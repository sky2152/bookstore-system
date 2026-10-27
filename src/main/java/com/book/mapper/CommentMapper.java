package com.book.mapper;

import com.book.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
    int insert(Comment comment);

    /** 某本书的全部评论：置顶在前，其余按时间倒序。 */
    List<Comment> selectByBookId(Integer bookId);

    List<Comment> selectByUserId(Integer userId);

    List<Comment> selectAll();

    Comment selectById(Integer id);

    /** 用于「一单一评」校验。 */
    Comment selectByOrderId(Integer orderId);

    int updatePinned(@Param("id") Integer id, @Param("isPinned") Integer isPinned);

    int deleteById(Integer id);

    /** 某本书的平均分，无评论时返回 0。 */
    Double selectAvgRating(Integer bookId);

    int countByBookId(Integer bookId);
}
