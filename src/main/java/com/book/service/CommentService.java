package com.book.service;

import com.book.entity.Comment;
import com.book.entity.Order;
import com.book.mapper.CommentMapper;
import com.book.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    /** 单条评论正文的字数上限。 */
    private static final int MAX_CONTENT_LENGTH = 500;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 创建评论。返回成功/失败编码，由 controller 翻译成中文提示。
     * <p>约束：必须登录（调用方保证）、必须是自己买过的订单、一单一评、评分 1~5。
     */
    public String createComment(Integer userId, Integer orderId, Integer rating, String content) {
        if (orderId == null) {
            return "error_order_required";
        }
        if (rating == null || rating < 1 || rating > 5) {
            return "error_invalid_rating";
        }

        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return "error_order_not_found";
        }
        if (!userId.equals(order.getUserId())) {
            return "error_not_owner";
        }
        if (commentMapper.selectByOrderId(orderId) != null) {
            return "error_already_commented";
        }

        String text = content == null ? "" : content.trim();
        if (text.length() > MAX_CONTENT_LENGTH) {
            text = text.substring(0, MAX_CONTENT_LENGTH);
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setBookId(order.getBookId());
        comment.setOrderId(orderId);
        comment.setRating(rating);
        comment.setContent(text);

        return commentMapper.insert(comment) > 0 ? "success" : "error_insert_failed";
    }

    /** 某本书的评论列表 + 平均分 + 条数。 */
    public Map<String, Object> getBookComments(Integer bookId) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", commentMapper.selectByBookId(bookId));
        result.put("avgRating", commentMapper.selectAvgRating(bookId));
        result.put("count", commentMapper.countByBookId(bookId));
        return result;
    }

    public List<Comment> getMyComments(Integer userId) {
        return commentMapper.selectByUserId(userId);
    }

    public List<Comment> getAllComments() {
        return commentMapper.selectAll();
    }

    public List<Order> getUncommentedOrders(Integer userId) {
        return orderMapper.selectUncommentedOrders(userId);
    }

    public Comment getById(Integer id) {
        return commentMapper.selectById(id);
    }

    public int pinComment(Integer id, Integer isPinned) {
        return commentMapper.updatePinned(id, isPinned);
    }

    public int deleteComment(Integer id) {
        return commentMapper.deleteById(id);
    }
}
