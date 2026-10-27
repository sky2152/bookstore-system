package com.book.service;
import com.book.entity.Category;
import com.book.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> selectAll(){
        return categoryMapper.selectAll();
    }
    public Category selectById(Integer id){
        return categoryMapper.selectById(id);
    }
    public int add(Category category){
        return categoryMapper.insert(category);
    }
    public int edit(Category category){
        return categoryMapper.update(category);
    }
    public int delete(Integer id){
        int count = categoryMapper.countBooksByCategoryId(id);
        if(count > 0){
            return -1;
        }
        return categoryMapper.deleteById(id);
    }
}