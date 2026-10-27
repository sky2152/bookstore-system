package com.book.service;
import com.book.entity.Feedback;
import com.book.mapper.FeedbackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackMapper feedbackMapper;

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

    public int handleFeedback(Integer id, String handleRemark) {
        return feedbackMapper.updateStatus(id, 1, handleRemark);
    }
}
