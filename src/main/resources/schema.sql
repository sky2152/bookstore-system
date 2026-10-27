-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS book_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE book_db;

-- 先删除所有表
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;

-- 用户表
CREATE TABLE user (
  id INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL,
  -- BCrypt 哈希固定 60 字符，留足余量到 100
  password VARCHAR(100) NOT NULL,
  role INT NOT NULL DEFAULT 2,
  balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 图书分类表
CREATE TABLE category (
  id INT NOT NULL AUTO_INCREMENT,
  category_name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 图书表
CREATE TABLE book (
  id INT NOT NULL AUTO_INCREMENT,
  book_name VARCHAR(100) NOT NULL,
  author VARCHAR(50) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  category_id INT NOT NULL,
  sales INT NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 订单表
CREATE TABLE orders (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  book_id INT NOT NULL,
  buy_num INT NOT NULL DEFAULT 1,
  total_money DECIMAL(10,2) NOT NULL,
  status INT NOT NULL DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_user_id (user_id),
  KEY idx_book_id (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 缺货反馈表
CREATE TABLE feedback (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  book_id INT NOT NULL,
  book_name VARCHAR(100) NOT NULL,
  username VARCHAR(50) NOT NULL,
  remark TEXT,
  status INT NOT NULL DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  handle_time DATETIME,
  handle_remark TEXT,
  KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 图书评论表
CREATE TABLE comment (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  book_id INT NOT NULL,
  order_id INT NOT NULL,
  rating INT NOT NULL,
  content TEXT,
  is_pinned INT NOT NULL DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_book_id (book_id),
  KEY idx_user_id (user_id),
  -- 一单一评在数据库层再兜一次，服务层校验之外的第二道防线
  UNIQUE KEY uk_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 消息通知表
CREATE TABLE notification (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  type INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  content TEXT,
  related_id INT,
  is_read INT NOT NULL DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_user_read (user_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
-- 两个账号的密码都是 123456，此处存的是 BCrypt 哈希（$2a$10$...），不能直接改回明文
INSERT INTO user (username, password, role, balance) VALUES 
('admin', '$2a$10$OyqwDb.cu1YnYmFL/D4fjOaNpPLCqvZnFV5zM8z.VXIvMQH2ltTP2', 1, 9999.00),
('testuser', '$2a$10$OyqwDb.cu1YnYmFL/D4fjOaNpPLCqvZnFV5zM8z.VXIvMQH2ltTP2', 2, 1000.00);

INSERT INTO category (category_name) VALUES 
('计算机'),
('文学小说'),
('历史传记'),
('科学技术'),
('儿童读物');

INSERT INTO book (book_name, author, price, stock, category_id, sales) VALUES 
('Java编程思想', 'Bruce Eckel', 108.00, 50, 1, 120),
('Spring实战', 'Craig Walls', 89.00, 30, 1, 85),
('三体', '刘慈欣', 68.00, 100, 2, 256),
('活着', '余华', 39.00, 80, 2, 198),
('明朝那些事儿', '当年明月', 98.00, 60, 3, 145),
('时间简史', '史蒂芬霍金', 55.00, 45, 4, 112),
('小王子', '圣埃克苏佩里', 32.00, 200, 5, 345);
