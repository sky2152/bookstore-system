package com.book.service;

import com.book.entity.Notification;
import com.book.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    /** 消息类型：订单。 */
    public static final int TYPE_ORDER = 1;
    /** 消息类型：反馈。 */
    public static final int TYPE_FEEDBACK = 2;
    /** 消息类型：系统/评论。 */
    public static final int TYPE_SYSTEM = 3;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 推送一条站内消息。
     * <p>这里刻意不抛异常：通知属于旁路逻辑，推送失败不应该让下单/处理反馈这些主流程跟着回滚。
     * 真正需要保证送达时，应改为事务消息或落库后由定时任务重试。
     */
    public void push(Integer userId, int type, String title, String content, Integer relatedId) {
        if (userId == null || title == null) {
            return;
        }
        try {
            Notification n = new Notification();
            n.setUserId(userId);
            n.setType(type);
            n.setTitle(title);
            n.setContent(content);
            n.setRelatedId(relatedId);
            notificationMapper.insert(n);
        } catch (Exception e) {
            // 只记录，不影响调用方
            System.err.println("[notification] 推送失败 userId=" + userId + " title=" + title + " : " + e.getMessage());
        }
    }

    public List<Notification> list(Integer userId) {
        return notificationMapper.selectByUserId(userId);
    }

    public int unreadCount(Integer userId) {
        return notificationMapper.countUnread(userId);
    }

    public int markRead(Integer id, Integer userId) {
        return notificationMapper.markRead(id, userId);
    }

    public int markAllRead(Integer userId) {
        return notificationMapper.markAllRead(userId);
    }

    public int delete(Integer id, Integer userId) {
        return notificationMapper.deleteById(id, userId);
    }
}
