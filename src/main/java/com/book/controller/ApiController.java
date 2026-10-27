package com.book.controller;

import com.book.entity.Book;
import com.book.entity.Category;
import com.book.entity.Order;
import com.book.entity.User;
import com.book.service.BookService;
import com.book.service.CategoryService;
import com.book.service.OrderService;
import com.book.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;

    @GetMapping("/book/list")
    public List<Book> bookList(@RequestParam(required = false) String keyword){
        return bookService.searchBook(keyword);
    }

    @GetMapping("/book/hot")
    public List<Book> hotBooks(){
        return bookService.getHotBooks(6);
    }

    @GetMapping("/book/stats")
    public List<Book> bookStats(@RequestParam(required = false) String keyword){
        return bookService.getSalesStats(keyword);
    }

    @GetMapping("/category/list")
    public List<Category> categoryList(){
        return categoryService.selectAll();
    }

    @GetMapping("/user/balance")
    public Object userBalance(HttpSession session){
        User user = (User) session.getAttribute("loginUser");
        if(user == null){
            return java.util.Map.of("error", "未登录");
        }
        return java.util.Map.of("balance", user.getBalance());
    }

    @GetMapping("/user/orders")
    public Object userOrders(HttpSession session){
        User user = (User) session.getAttribute("loginUser");
        if(user == null){
            return java.util.Map.of("error", "未登录");
        }
        return orderService.getUserOrders(user.getId());
    }
    
    @GetMapping("/user/check-username")
    public Object checkUsername(@RequestParam String username){
        if("admin".equals(username)){
            return java.util.Map.of("exists", true, "message", "该用户名不可使用");
        }
        User exist = userService.selectByUsername(username);
        if(exist != null){
            return java.util.Map.of("exists", true, "message", "该用户名已被使用");
        }
        return java.util.Map.of("exists", false, "message", "用户名可用");
    }
}
