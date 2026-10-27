package com.book.controller;

import com.book.entity.Book;
import com.book.entity.User;
import com.book.service.RecommendService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/hot")
    @ResponseBody
    public Map<String, Object> getHotRecommendations() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Book> hotBooks = recommendService.getHotBooks(10);
            result.put("success", true);
            result.put("data", hotBooks);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/history")
    @ResponseBody
    public Map<String, Object> getHistoryRecommendations(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        try {
            List<Book> recommendations = recommendService.recommendByUserHistory(loginUser.getId(), 10);
            result.put("success", true);
            result.put("data", recommendations);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/collaborative")
    @ResponseBody
    public Map<String, Object> getCollaborativeRecommendations(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        try {
            List<Book> recommendations = recommendService.recommendByCollaborativeFiltering(loginUser.getId(), 10);
            result.put("success", true);
            result.put("data", recommendations);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/hybrid")
    @ResponseBody
    public Map<String, Object> getHybridRecommendations(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        try {
            List<Book> recommendations = recommendService.getHybridRecommendations(loginUser.getId(), 10);
            result.put("success", true);
            result.put("data", recommendations);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/page")
    public String toRecommendPage() {
        return "redirect:/recommend.html";
    }
}
