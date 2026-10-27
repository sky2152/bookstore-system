package com.book.controller;

import com.book.entity.User;
import com.book.service.BookService;
import com.book.service.OrderService;
import com.book.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@Controller
public class UserShopController {
    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/login")
    public String toLogin(){
        return "redirect:/login.html";
    }

    @GetMapping("/register")
    public String toReg(){
        return "redirect:/register.html";
    }

    @GetMapping("/user/index")
    public String index(){
        return "redirect:/userIndex.html";
    }

    @PostMapping("/buy")
    public String buyBook(Integer bookId, Integer num, HttpSession session){
        User loginUser = (User) session.getAttribute("loginUser");
        if(loginUser == null){
            return "redirect:/login.html";
        }
        if(bookId == null || num == null || num <= 0){
            return "redirect:/userIndex.html?error=" + encode("购买数量无效");
        }
        String result = bookService.buyBook(loginUser.getId(), bookId, num);
        if(!"success".equals(result)){
            String msg = mapBuyError(result);
            return "redirect:/userIndex.html?error=" + encode(msg);
        }
        User updated = userService.selectById(loginUser.getId());
        session.setAttribute("loginUser", updated);
        return "redirect:/userIndex.html?msg=" + encode("购买成功");
    }

    private static String encode(String text){
        return java.net.URLEncoder.encode(text, java.nio.charset.StandardCharsets.UTF_8);
    }

    private String mapBuyError(String code){
        switch(code){
            case "error_book_not_found": return "图书不存在";
            case "error_invalid_num": return "购买数量无效";
            case "error_stock_insufficient": return "库存不足，无法购买";
            case "error_user_not_found": return "用户信息异常";
            case "error_balance_insufficient": return "余额不足，请先充值";
            case "error_stock_update_failed": return "库存扣减失败，请重试";
            case "error_balance_update_failed": return "余额扣减失败，请重试";
            case "error_order_create_failed": return "订单创建失败，请重试";
            default: return "购买失败，请重试";
        }
    }

    @GetMapping("/user/recharge")
    public String toRecharge(){
        return "redirect:/recharge.html";
    }

    @GetMapping("/user/orders")
    public String toOrders(){
        return "redirect:/myOrders.html";
    }

    @PostMapping("/user/recharge")
    public String recharge(Integer money, HttpSession session){
        User loginUser = (User) session.getAttribute("loginUser");
        if(loginUser == null){
            return "redirect:/login.html";
        }
        if(money == null || money <= 0){
            return "redirect:/recharge.html?error=" + encode("充值金额无效");
        }
        userService.recharge(loginUser.getId(), new BigDecimal(money));
        User updated = userService.selectById(loginUser.getId());
        session.setAttribute("loginUser", updated);
        return "redirect:/userIndex.html";
    }
}
