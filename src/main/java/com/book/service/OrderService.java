package com.book.service;
import com.book.entity.Order;
import com.book.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;

    public int createOrder(Order order){
        return orderMapper.insert(order);
    }
    public List<Order> getUserOrders(Integer userId){
        return orderMapper.selectByUserId(userId);
    }
}