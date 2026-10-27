# 书店管理系统（双端 Web 系统）

一个基于 Spring Boot 3 的书店管理系统，分为**管理员端**与**用户端**，覆盖图书管理、分类管理、注册登录、余额充值、下单、订单查询、图书推荐、缺货反馈、图书评论、站内消息等完整业务闭环。

前后端不分离：页面为内嵌 CSS/JavaScript 的原生 HTML，由 Spring Boot 直接托管在 `src/main/resources/static/`，通过 `fetch` 调用后端 JSON 接口。

---

## 一、功能概览

### 管理员端

| 功能 | 说明 |
| --- | --- |
| 图书管理 | 新增、编辑、删除、按书名/作者搜索 |
| 批量导入 | 粘贴多行文本批量建书，按行容错并返回成功/失败条数 |
| 分类管理 | 新增、删除（分类下有图书时拒绝删除） |
| 缺货反馈处理 | 查看全部反馈，填写处理意见 |
| 评论管理 | 查看全部评论，置顶/取消置顶、删除 |

### 用户端

| 功能 | 说明 |
| --- | --- |
| 注册 / 登录 / 退出 | 密码 BCrypt 加盐哈希存储 |
| 图书浏览 | 分页列表、分类筛选、关键字搜索、热销榜 |
| 购买 | 校验库存与余额，条件 UPDATE 扣减，全程事务 |
| 充值 | 更新账户余额 |
| 订单查询 | 订单列表 + 消费统计（订单数、购书数、累计消费） |
| 智能推荐 | 4 种推荐策略，见下文 |
| 缺货反馈 | 对缺货图书提交反馈，处理结果以站内消息通知 |
| 图书评论 | 购买后评价（1~5 星 + 文字），一单一评 |
| 消息中心 | 下单成功、反馈处理结果等站内通知，支持已读/全部已读/删除 |

---

## 二、技术栈

| 层次 | 技术 |
| --- | --- |
| 语言 / 运行时 | Java 17 |
| 框架 | Spring Boot 3.3.0、Spring MVC |
| 持久层 | MyBatis 3.0.3（接口 + XML 映射） |
| 数据库 | MySQL 8.0，Druid 1.2.20 连接池 |
| 安全 | spring-security-crypto（仅 BCrypt，未引入完整安全过滤链） |
| 构建 | Maven（含 mvnw wrapper） |
| 前端 | 原生 HTML / CSS / JavaScript（fetch） |
| 设计文档 | PlantUML（`uml-diagrams/`，14 张图） |

---

## 三、项目结构

```
book/
├── src/main/java/com/book/
│   ├── BookApplication.java          # 启动类，@MapperScan("com.book.mapper")
│   ├── config/PasswordConfig.java    # BCryptPasswordEncoder Bean
│   ├── controller/                   # 9 个控制器
│   ├── service/                      # 业务逻辑
│   ├── mapper/                       # MyBatis 接口
│   └── entity/                       # 7 个实体
├── src/main/resources/
│   ├── application.properties        # 公共配置（不含账号密码）
│   ├── application-local.properties.example  # 本地配置模板
│   ├── schema.sql                    # 建表 + 种子数据
│   ├── mapper/*.xml                  # 7 个 SQL 映射文件
│   └── static/                       # 14 个页面
├── uml-diagrams/                     # 用例图、类图、ER 图、时序图、活动图
├── bookstore-demo/                   # 单页项目演示（架构与流程展示）
└── pom.xml
```

---

## 四、数据库设计

共 7 张表，均为 InnoDB + utf8mb4：

| 表名 | 说明 | 主要字段 |
| --- | --- | --- |
| `user` | 用户 | id, username(唯一), password(BCrypt), role(1=管理员 2=普通用户), balance |
| `category` | 图书分类 | id, category_name |
| `book` | 图书 | id, book_name, author, price, stock, category_id, sales |
| `orders` | 订单 | id, user_id, book_id, buy_num, total_money, status, create_time |
| `feedback` | 缺货反馈 | id, user_id, book_id, book_name, remark, status(0=待处理 1=已处理), handle_remark |
| `comment` | 图书评论 | id, user_id, book_id, order_id, rating(1~5), content, is_pinned |
| `notification` | 站内消息 | id, user_id, type(1=订单 2=反馈 3=系统), title, content, related_id, is_read |

> `book.sales` 在库中不落值，热销与统计一律由 `orders` 实时聚合（`coalesce(sum(o.buy_num), 0)`），避免两份数据不同步。

---

## 五、几个实现要点

### 1. 下单：事务 + 条件更新，不靠先查后改

`BookService.buyBook` 用 `@Transactional` 包住整个流程，库存与余额都用**带条件的 UPDATE 并判断影响行数**：

```sql
-- BookMapper.deductStock
update book  set stock   = stock   - #{num}   where id = #{id} and stock   >= #{num}
-- UserMapper.deductBalance
update user  set balance = balance - #{money} where id = #{id} and balance >= #{money}
```

任一步影响行数为 0 即判定失败并整体回滚。这样即使并发下单也不会把库存扣成负数，比「先 select 再判断再 update」更安全。

### 2. 推荐模块：4 种策略，全部带降级

| 策略 | 接口 | 做法 | 兜底 |
| --- | --- | --- | --- |
| 热销 | `GET /recommend/hot` | 按订单聚合销量排序 | — |
| 历史类目偏好 | `GET /recommend/history` | 统计用户历史购买的类目分布，推荐同类目未买过的书 | 无购买记录 → 热销 |
| 用户协同过滤 | `GET /recommend/collaborative` | Jaccard 相似度计算用户间相似度，加权聚合邻居买过而目标用户没买过的书 | 无相似用户 → 热销 |
| 混合 | `GET /recommend/hybrid` | 历史推荐与热销各取一半，按 id 去重 | — |

Jaccard 相似度：`|A ∩ B| / |A ∪ B|`，A、B 分别为两个用户的购书集合。

> **实话说在前面**：当前是种子数据（几十条订单），用户数少导致协同过滤很难找到相似邻居，绝大多数情况会走热销兜底。这一点是刻意设计的降级路径，不是缺陷；但数据量上来之前，协同过滤的实际效果有限。

### 3. 密码：BCrypt，不引入整套 Spring Security

只引 `spring-security-crypto`，注册时 `encode`、登录时 `matches`，避免引入 starter 后多出一套默认登录页和过滤器链。登录成功后会把 `password` 置空再放进 session，哈希不落到前端。

### 4. 站内消息：旁路逻辑，不拖垮主流程

`NotificationService.push` 内部捕获异常只打日志——**消息推送失败不会让下单事务回滚**。目前触发点两个：下单成功、缺货反馈被处理（重复处理不会重复推送）。

这是个简化取舍：真正要求可靠送达需要事务消息或补偿重试，当前实现是「尽力而为」。

### 5. 评论：必须挂在订单上

评论以 `order_id` 为锚，服务端校验「订单存在 → 属于当前用户 → 该订单未评价过」，因此「没买过却能评价」和「刷评」在数据层面就无法成立。

---

## 六、接口清单

### 会话与用户

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/login` | 登录，成功写入 session |
| POST | `/register` | 注册 |
| GET | `/logout` | 退出 |
| GET | `/api/user/balance` | 当前用户余额 |
| GET | `/api/user/check-username` | 用户名可用性检查 |

### 图书与分类

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/book/list?keyword=` | 图书列表 / 搜索 |
| GET | `/api/book/hot` | 热销榜（6 条） |
| GET | `/api/book/stats?keyword=` | 销量统计 |
| POST | `/book/add` | 新增图书（管理员） |
| POST | `/book/edit` | 编辑图书（管理员） |
| GET | `/book/delete?id=` | 删除图书（管理员） |
| POST | `/book/batch` | 批量导入（`batchText` 每行 `书名,作者,价格,库存,分类名`） |
| GET | `/api/category/list` | 分类列表 |
| POST | `/category/add` | 新增分类（管理员） |
| GET | `/category/del?id=` | 删除分类（管理员） |

### 交易

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/buy` | 下单（`bookId`, `num`） |
| POST | `/user/recharge` | 充值（`money`） |
| GET | `/api/user/orders` | 我的订单 |

### 推荐

| 方法 | 路径 |
| --- | --- |
| GET | `/recommend/hot`、`/recommend/history`、`/recommend/collaborative`、`/recommend/hybrid` |

### 反馈

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/feedback/create` | 提交缺货反馈 |
| GET | `/feedback/my` | 我的反馈 |
| GET | `/feedback/all` | 全部反馈 |
| POST | `/feedback/handle` | 处理反馈（`id`, `handleRemark`） |

### 评论

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/comment/create` | 提交评价（`orderId`, `rating`, `content`） |
| GET | `/comment/book?bookId=` | 某本书的评论 + 平均分 + 条数 |
| GET | `/comment/my` | 我的评价 |
| GET | `/comment/pending` | 我还没评价的订单 |
| GET | `/comment/all` | 全部评论（管理员） |
| POST | `/comment/pin` | 置顶/取消置顶（管理员，`id`, `isPinned`） |
| POST | `/comment/delete` | 删除（本人或管理员） |

### 通知

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/notification/list` | 消息列表 + 未读数 |
| GET | `/notification/unread-count` | 未读数 |
| POST | `/notification/read` | 单条已读 |
| POST | `/notification/read-all` | 全部已读 |
| POST | `/notification/delete` | 删除 |

---

## 七、快速开始

### 环境要求

- JDK 17
- MySQL 8.0
- Maven（可直接用自带的 `mvnw`，无需预装）

### 1. 建库

```bash
mysql -u root -p < src/main/resources/schema.sql
```

> ⚠️ `schema.sql` 开头有 `DROP TABLE IF EXISTS`，**会清空同名表**。只用于初始化本地开发库。

### 2. 配置数据库账号

数据库密码**不写进仓库**。二选一：

**方式一（推荐）：本地配置文件**

```bash
cp src/main/resources/application-local.properties.example \
   src/main/resources/application-local.properties
```

然后编辑 `application-local.properties`，填入自己的账号密码。该文件已被 `.gitignore` 忽略。

**方式二：环境变量**

```bash
export DB_USERNAME=root
export DB_PASSWORD=你的密码
```

> `application.properties` 里的连接地址是 `jdbc:mysql://localhost:3306/book_db`，如果库名或端口不同，直接改那一行即可。
>
> Spring Boot 默认只对内嵌数据库执行 `schema.sql`，对 MySQL 不会自动执行，所以建表必须手动跑第 1 步的命令。

### 3. 启动

```bash
# Linux / macOS / Git Bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Windows CMD
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

> 如果用的是方式二（环境变量）而不是 local profile，可以去掉 `-Dspring-boot.run.profiles=local`。

启动后访问 <http://localhost:8080/login.html>

### 测试账号

| 用户名 | 密码 | 角色 |
| --- | --- | --- |
| admin | 123456 | 管理员 |
| testuser | 123456 | 普通用户 |

---

## 八、已知限制

写在这里是为了避免面试时被问住，也说明自己清楚边界：

1. **会话是单机内存 session**，多实例部署需要换成 Spring Session + Redis。
2. **鉴权逻辑分散在各控制器里**，没有统一的登录拦截器。管理端接口（图书增删改、分类、反馈、评论）都已逐个做了 `role == 1` 校验，但静态页面本身没有访问控制——直接输入 `/bookList.html` 能打开页面，只是页面里的接口调用会被拒绝。后续应改为 `HandlerInterceptor` 统一收敛。
3. **评论、反馈列表未分页**，数据量大时需要补分页。
4. **推荐算法是内存计算**，每次请求全量查订单，数据量大时应改为离线计算 + 缓存。
5. 缺少单元测试与接口自动化测试。

---

## 九、相关文档

- `uml-diagrams/` — 用例图、类图、ER 图、时序图、活动图共 14 张 PlantUML 源文件，`README_使用说明.html` 说明如何预览
- `bookstore-demo/bookstore-demo.html` — 单页项目演示，浏览器直接打开即可
