package com.book.controller;
import com.book.entity.User;
import com.book.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index(){
        return "redirect:/login.html";
    }

    @PostMapping("/login")
    public String login(User user, HttpSession session){
        User loginUser = userService.login(user);
        if(loginUser == null){
            return "redirect:/login.html";
        }
        session.setAttribute("loginUser",loginUser);
        if(loginUser.getRole() != null && loginUser.getRole() == 1){
            return "redirect:/bookList.html";
        }else{
            return "redirect:/userIndex.html";
        }
    }

    @PostMapping("/register")
    public String reg(User user){
        if("admin".equals(user.getUsername())){
            return "redirect:/register.html?error=admin";
        }
        User exist = userService.selectByUsername(user.getUsername());
        if(exist != null){
            return "redirect:/register.html?error=duplicate";
        }
        int res = userService.register(user);
        if(res > 0){
            return "redirect:/login.html?success=register";
        }
        return "redirect:/register.html?error=fail";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login.html";
    }
}
