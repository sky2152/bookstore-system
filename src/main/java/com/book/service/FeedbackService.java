package com.book.service;
import com.book.entity.Feedback;
import com.book.mapper.FeedbackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private NotificationService notificationService;

    public int createFeedback(Feedback feedback) {
        return feedbackMapper.insert(feedback);
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackMapper.selectAll();
    }

    public List<Feedback> getUserFeedbacks(Integer userId) {
        return feedbackMapper.selectByUserId(userId);
    }

    public Feedback getFeedbackById(Integer id) {
        return feedbackMapper.selectById(id);
    }

    /**
     * 处理反馈并给提交人发一条站内消息。
     * <p>重复点击「处理」不会重复推送：只在状态从待处理变为已处理时通知一次。
     */
    @Transactional
    public int handleFeedback(Integer id, String handleRemark) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            return 0;
        }
        int res = feedbackMapper.updateStatus(id, 1, handleRemark);
        if (res > 0 && feedback.getStatus() != null && feedback.getStatus() != 1) {
            String reply = (handleRemark == null || handleRemark.trim().isEmpty())
                    ? "管理员已为您跟进处理"
                    : handleRemark.trim();
            notificationService.push(feedback.getUserId(), NotificationService.TYPE_FEEDBACK,
                    "缺货反馈已处理",
                    "《" + feedback.getBookName() + "》：" + reply,
                    id);
        }
        return res;
    }
}
