package com.book.service;
import com.book.entity.User;
import com.book.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 登录校验。
     * <p>密码在库里是 BCrypt 哈希，无法再用 SQL 的 {@code password = #{password}} 直接比对，
     * 因此改为：按用户名取出用户，再用 {@link PasswordEncoder#matches} 校验哈希。
     * 返回前把 password 置空，避免哈希被放进 HttpSession 或随接口返回给前端。
     */
    public User login(User input){
        if(input == null || isBlank(input.getUsername()) || isBlank(input.getPassword())){
            return null;
        }
        User db = userMapper.selectByUsername(input.getUsername().trim());
        if(db == null || isBlank(db.getPassword())){
            return null;
        }
        if(!passwordEncoder.matches(input.getPassword(), db.getPassword())){
            return null;
        }
        db.setPassword(null);
        return db;
    }

    public User selectByUsername(String username){
        return userMapper.selectByUsername(username);
    }

    public User selectById(Integer id){
        return userMapper.selectById(id);
    }

    @Transactional
    public int register(User user){
        if(user == null || isBlank(user.getUsername()) || isBlank(user.getPassword())){
            return 0;
        }
        user.setUsername(user.getUsername().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.insert(user);
    }

    @Transactional
    public int recharge(Integer uid, BigDecimal money){
        return userMapper.recharge(uid,money);
    }

    private static boolean isBlank(String s){
        return s == null || s.trim().isEmpty();
    }
}
