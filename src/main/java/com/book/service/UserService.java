package com.book.service;
import com.book.entity.User;
import com.book.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User login(User user){
        return userMapper.login(user);
    }
    public User selectByUsername(String username){
        return userMapper.selectByUsername(username);
    }
    public User selectById(Integer id){
        return userMapper.selectById(id);
    }
    @Transactional
    public int register(User user){
        return userMapper.insert(user);
    }
    @Transactional
    public int recharge(Integer uid, BigDecimal money){
        return userMapper.recharge(uid,money);
    }
}