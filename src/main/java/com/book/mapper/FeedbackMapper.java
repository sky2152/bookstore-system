package com.book.mapper;
import com.book.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface FeedbackMapper {
    int insert(Feedback feedback);
    List<Feedback> selectAll();
    List<Feedback> selectByUserId(Integer userId);
    Feedback selectById(Integer id);
    int updateStatus(Integer id, Integer status, String handleRemark);
}
