package com.book.mapper;

import com.book.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NotificationMapper {
    int insert(Notification notification);

    List<Notification> selectByUserId(Integer userId);

    int countUnread(Integer userId);

    /** 只能标记自己的消息，避免越权改别人的已读状态。 */
    int markRead(@Param("id") Integer id, @Param("userId") Integer userId);

    int markAllRead(Integer userId);

    int deleteById(@Param("id") Integer id, @Param("userId") Integer userId);
}
