package com.book.mapper;

import com.book.entity.User;
import org.apache.ibatis.annotations.Mapper;
// 缺少这行导入，加上
import java.math.BigDecimal;

@Mapper
public interface UserMapper {
    /** 按用户名查询完整用户（含 password 哈希），登录时用于比对。 */
    User selectByUsername(String username);
    int insert(User user);
    int recharge(Integer id, BigDecimal money);
    User selectById(Integer id);
    int deductBalance(Integer id, BigDecimal money);
}