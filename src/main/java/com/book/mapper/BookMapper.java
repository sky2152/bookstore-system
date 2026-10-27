package com.book.mapper;

import com.book.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BookMapper {
    List<Book> selectByCategoryId(Integer cid);
    List<Book> searchBook(String keyword);
    List<Book> selectAll();
    Book selectById(Integer id);
    int insert(Book book);
    int update(Book book);
    int deleteById(Integer id);
    int deductStock(Integer id, Integer num);
    List<Book> selectHotBooks(int limit);
    List<Book> selectSalesStats(String keyword);
}