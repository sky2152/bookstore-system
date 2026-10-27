package com.book.controller;

import com.book.entity.Book;
import com.book.entity.Category;
import com.book.service.BookService;
import com.book.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String bookList(){
        return "redirect:/bookList.html";
    }

    @PostMapping("/add")
    public String addBook(Book book){
        int result = bookService.add(book);
        if(result > 0){
            return "redirect:/bookList.html?msg=" + java.net.URLEncoder.encode("新增图书成功", java.nio.charset.StandardCharsets.UTF_8);
        }
        return "redirect:/bookList.html?error=" + java.net.URLEncoder.encode("新增失败，请重试", java.nio.charset.StandardCharsets.UTF_8);
    }

    @GetMapping("/delete")
    public String deleteBook(Integer id){
        int result = bookService.delete(id);
        if(result > 0){
            return "redirect:/bookList.html?msg=" + java.net.URLEncoder.encode("删除成功", java.nio.charset.StandardCharsets.UTF_8);
        }
        return "redirect:/bookList.html?error=" + java.net.URLEncoder.encode("删除失败，请重试", java.nio.charset.StandardCharsets.UTF_8);
    }

    @PostMapping("/edit")
    public String editBook(Book book){
        int result = bookService.edit(book);
        if(result > 0){
            return "redirect:/bookList.html?msg=" + java.net.URLEncoder.encode("修改成功", java.nio.charset.StandardCharsets.UTF_8);
        }
        return "redirect:/bookList.html?error=" + java.net.URLEncoder.encode("修改失败，请重试", java.nio.charset.StandardCharsets.UTF_8);
    }

    @ResponseBody
    @GetMapping
    public List<Book> apiList(){
        return bookService.searchBook(null);
    }

    @ResponseBody
    @PostMapping("/batch")
    public Map<String, Object> batchAdd(String batchText){
        Map<String, Object> result = new HashMap<>();

        List<Category> categories = categoryService.selectAll();
        Map<String, Integer> categoryMap = new HashMap<>();
        for(Category c : categories){
            categoryMap.put(c.getCategoryName(), c.getId());
        }

        int success = 0, fail = 0;
        String[] lines = batchText.split("\n");
        for(String line : lines){
            line = line.trim();
            if(line.isEmpty()) continue;
            line = line.replace("，", ",");
            String[] parts = line.split(",");
            if(parts.length < 5){ fail++; continue; }
            try {
                Book book = new Book();
                book.setBookName(parts[0].trim());
                book.setAuthor(parts[1].trim());
                book.setPrice(new BigDecimal(parts[2].trim()));
                book.setStock(Integer.parseInt(parts[3].trim()));
                String catName = parts[4].trim();
                Integer cid = categoryMap.get(catName);
                if(cid == null){ fail++; continue; }
                book.setCategoryId(cid);
                bookService.add(book);
                success++;
            } catch(Exception e){
                fail++;
            }
        }

        result.put("success", true);
        String msg = "批量导入完成：成功 " + success + " 条";
        if(fail > 0) msg += "，失败 " + fail + " 条";
        result.put("message", msg);
        result.put("successCount", success);
        result.put("failCount", fail);
        return result;
    }
}
