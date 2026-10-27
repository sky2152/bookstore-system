package com.book.service;

import com.book.entity.Book;
import com.book.entity.Order;
import com.book.entity.User;
import com.book.mapper.BookMapper;
import com.book.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderService orderService;

    public List<Book> selectByCid(Integer cid){
        return bookMapper.selectByCategoryId(cid);
    }

    public List<Book> searchBook(String keyword){
        if(keyword == null || keyword.trim().isEmpty()){
            return bookMapper.selectAll();
        }
        return bookMapper.searchBook(keyword.trim());
    }

    public int add(Book book){
        return bookMapper.insert(book);
    }

    public int edit(Book book){
        return bookMapper.update(book);
    }

    public int delete(Integer id){
        return bookMapper.deleteById(id);
    }

    @Transactional
    public String buyBook(Integer userId, Integer bookId, Integer num){
        Book book = bookMapper.selectById(bookId);
        if(book == null){
            return "error_book_not_found";
        }
        if(num <= 0){
            return "error_invalid_num";
        }
        if(book.getStock() < num){
            return "error_stock_insufficient";
        }

        User user = userMapper.selectById(userId);
        if(user == null){
            return "error_user_not_found";
        }

        BigDecimal totalMoney = book.getPrice().multiply(BigDecimal.valueOf(num));
        if(user.getBalance().compareTo(totalMoney) < 0){
            return "error_balance_insufficient";
        }

        int stockResult = bookMapper.deductStock(bookId, num);
        if(stockResult <= 0){
            return "error_stock_update_failed";
        }

        int balanceResult = userMapper.deductBalance(userId, totalMoney);
        if(balanceResult <= 0){
            return "error_balance_update_failed";
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setBookId(book.getId());
        order.setBuyNum(num);
        order.setTotalMoney(totalMoney);
        order.setBookName(book.getBookName());

        int orderResult = orderService.createOrder(order);
        if(orderResult <= 0){
            return "error_order_create_failed";
        }

        return "success";
    }

    public Book selectById(Integer bookId){
        return bookMapper.selectById(bookId);
    }

    public List<Book> getHotBooks(int limit){
        return bookMapper.selectHotBooks(limit);
    }

    public List<Book> getSalesStats(String keyword){
        return bookMapper.selectSalesStats(keyword);
    }
}