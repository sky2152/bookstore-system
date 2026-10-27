package com.book.controller;

import com.book.entity.Feedback;
import com.book.entity.User;
import com.book.service.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/create")
    @ResponseBody
    public Map<String, Object> createFeedback(@RequestParam Integer bookId,
                                              @RequestParam String bookName,
                                              @RequestParam String remark,
                                              HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("=== 收到反馈请求 ===");
            System.out.println("bookId: " + bookId);
            System.out.println("bookName: " + bookName);
            System.out.println("remark: " + remark);
            
            User loginUser = (User) session.getAttribute("loginUser");
            System.out.println("loginUser: " + loginUser);
            
            if (loginUser == null) {
                result.put("success", false);
                result.put("message", "请先登录");
                return result;
            }

            Feedback feedback = new Feedback();
            feedback.setUserId(loginUser.getId());
            feedback.setBookId(bookId);
            feedback.setBookName(bookName);
            feedback.setUsername(loginUser.getUsername());
            feedback.setRemark(remark);
            feedback.setStatus(0);

            System.out.println("准备插入反馈: " + feedback);
            
            int res = feedbackService.createFeedback(feedback);
            System.out.println("插入结果: " + res);
            
            if (res > 0) {
                result.put("success", true);
                result.put("message", "反馈提交成功");
            } else {
                result.put("success", false);
                result.put("message", "反馈提交失败");
            }
        } catch (Exception e) {
            System.err.println("创建反馈出错:");
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "服务器错误: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/my")
    @ResponseBody
    public Map<String, Object> getMyFeedbacks(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = (User) session.getAttribute("loginUser");

        List<Feedback> feedbacks = feedbackService.getUserFeedbacks(loginUser.getId());
        result.put("success", true);
        result.put("data", feedbacks);
        return result;
    }

    @GetMapping("/all")
    @ResponseBody
    public Map<String, Object> getAllFeedbacks() {
        Map<String, Object> result = new HashMap<>();

        List<Feedback> feedbacks = feedbackService.getAllFeedbacks();
        result.put("success", true);
        result.put("data", feedbacks);
        return result;
    }

    @PostMapping("/handle")
    @ResponseBody
    public Map<String, Object> handleFeedback(@RequestParam Integer id,
                                              @RequestParam String handleRemark) {
        Map<String, Object> result = new HashMap<>();

        int res = feedbackService.handleFeedback(id, handleRemark);
        if (res > 0) {
            result.put("success", true);
            result.put("message", "处理成功");
        } else {
            result.put("success", false);
            result.put("message", "处理失败");
        }
        return result;
    }

    @GetMapping("/page")
    public String toFeedbackPage() {
        return "redirect:/feedback.html";
    }

    @GetMapping("/admin/page")
    public String toAdminFeedbackPage() {
        return "redirect:/admin-feedback.html";
    }
}
