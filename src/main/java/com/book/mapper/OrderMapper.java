package com.book.mapper;
import com.book.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface OrderMapper {
    int insert(Order order);
    List<Order> selectByUserId(Integer userId);
    List<Order> selectAll();
}