package com.book.mapper;
import com.book.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface OrderMapper {
    int insert(Order order);
    List<Order> selectByUserId(Integer userId);
    List<Order> selectAll();
    /** 该用户尚未评价的订单，供「待评价」列表使用。 */
    List<Order> selectUncommentedOrders(Integer userId);
    Order selectById(Integer id);
}
