package com.book.controller;

import com.book.entity.Comment;
import com.book.entity.User;
import com.book.service.CommentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /** 提交评价：只能评价自己的订单，一单一评。 */
    @PostMapping("/create")
    @ResponseBody
    public Map<String, Object> create(@RequestParam(required = false) Integer orderId,
                                      @RequestParam(required = false) Integer rating,
                                      @RequestParam(required = false) String content,
                                      HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = currentUser(session);
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        String code = commentService.createComment(loginUser.getId(), orderId, rating, content);
        if ("success".equals(code)) {
            result.put("success", true);
            result.put("message", "评价提交成功");
        } else {
            result.put("success", false);
            result.put("message", messageOf(code));
        }
        return result;
    }

    /** 某本书的公开评论列表（含平均分）。 */
    @GetMapping("/book")
    @ResponseBody
    public Map<String, Object> bookComments(@RequestParam Integer bookId) {
        return commentService.getBookComments(bookId);
    }

    @GetMapping("/my")
    @ResponseBody
    public Map<String, Object> myComments(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = currentUser(session);
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        result.put("success", true);
        result.put("data", commentService.getMyComments(loginUser.getId()));
        return result;
    }

    /** 待评价订单：买过但还没写评价的订单。 */
    @GetMapping("/pending")
    @ResponseBody
    public Map<String, Object> pending(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = currentUser(session);
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }
        result.put("success", true);
        result.put("data", commentService.getUncommentedOrders(loginUser.getId()));
        return result;
    }

    /** 管理端：全部评论。 */
    @GetMapping("/all")
    @ResponseBody
    public Map<String, Object> allComments(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = currentUser(session);
        if (!isAdmin(loginUser)) {
            result.put("success", false);
            result.put("message", "需要管理员权限");
            return result;
        }
        result.put("success", true);
        result.put("data", commentService.getAllComments());
        return result;
    }

    /** 管理端：置顶 / 取消置顶。 */
    @PostMapping("/pin")
    @ResponseBody
    public Map<String, Object> pin(@RequestParam Integer id,
                                   @RequestParam(defaultValue = "1") Integer isPinned,
                                   HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User loginUser = currentUser(session);
        if (!isAdmin(loginUser)) {
            result.put("success", false);
            result.put("message", "需要管理员权限");
            return result;
        }
        int res = commentService.pinComment(id, isPinned == 1 ? 1 : 0);
        result.put("success", res > 0);
        result.put("message", res > 0 ? "操作成功" : "操作失败");
        return result;
    }

    /** 删除评论：本人或管理员。 */
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
        Comment comment = commentService.getById(id);
        if (comment == null) {
            result.put("success", false);
            result.put("message", "评论不存在");
            return result;
        }
        boolean allowed = isAdmin(loginUser) || loginUser.getId().equals(comment.getUserId());
        if (!allowed) {
            result.put("success", false);
            result.put("message", "只能删除自己的评论");
            return result;
        }
        int res = commentService.deleteComment(id);
        result.put("success", res > 0);
        result.put("message", res > 0 ? "删除成功" : "删除失败");
        return result;
    }

    @GetMapping("/page")
    public String toMyComments() {
        return "redirect:/myComments.html";
    }

    @GetMapping("/admin/page")
    public String toAdminComments() {
        return "redirect:/adminComments.html";
    }

    private static User currentUser(HttpSession session) {
        return (User) session.getAttribute("loginUser");
    }

    private static boolean isAdmin(User user) {
        return user != null && user.getRole() != null && user.getRole() == 1;
    }

    private static String messageOf(String code) {
        switch (code) {
            case "error_order_required": return "请选择要评价的订单";
            case "error_invalid_rating": return "评分需要在 1~5 星之间";
            case "error_order_not_found": return "订单不存在";
            case "error_not_owner": return "只能评价自己的订单";
            case "error_already_commented": return "该订单已经评价过了";
            case "error_insert_failed": return "评价提交失败，请重试";
            default: return "评价提交失败";
        }
    }
}
