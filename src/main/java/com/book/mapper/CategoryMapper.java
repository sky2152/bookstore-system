package com.book.mapper;
import com.book.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface CategoryMapper {
    List<Category> selectAll();
    Category selectById(Integer id);
    int insert(Category category);
    int update(Category category);
    int deleteById(Integer id);
    int countBooksByCategoryId(Integer id);
}