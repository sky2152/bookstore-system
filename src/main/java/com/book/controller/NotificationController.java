package com.book.controller;

import com.book.entity.User;
import com.book.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/list")
    @ResponseBody
    public Map<String, Object> list(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = currentUser(session);
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        result.put("success", true);
        result.put("data", notificationService.list(loginUser.getId()));
        result.put("unread", notificationService.unreadCount(loginUser.getId()));
        return result;
    }

    @GetMapping("/unread-count")
    @ResponseBody
    public Map<String, Object> unreadCount(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = currentUser(session);
        result.put("count", loginUser == null ? 0 : notificationService.unreadCount(loginUser.getId()));
        return result;
    }

    @PostMapping("/read")
    @ResponseBody
    public Map<String, Object> read(@RequestParam Integer id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = currentUser(session);
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        int res = notificationService.markRead(id, loginUser.getId());
        result.put("success", res > 0);
        return result;
    }

    @PostMapping("/read-all")
    @ResponseBody
    public Map<String, Object> readAll(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = currentUser(session);
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        int res = notificationService.markAllRead(loginUser.getId());
        result.put("success", true);
        result.put("updated", res);
        return result;
    }

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestParam Integer id, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = currentUser(session);
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        int res = notificationService.delete(id, loginUser.getId());
        result.put("success", res > 0);
        return result;
    }

    @GetMapping("/page")
    public String toNotificationPage() {
        return "redirect:/notifications.html";
    }

    private static User currentUser(HttpSession session) {
        return (User) session.getAttribute("loginUser");
    }
}
