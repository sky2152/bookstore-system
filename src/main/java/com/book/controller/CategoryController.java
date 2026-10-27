package com.book.controller;
import com.book.entity.Category;
import com.book.entity.User;
import com.book.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String categoryList(){
        return "redirect:/categoryList.html";
    }

    @ResponseBody
    @PostMapping("/add")
    public Map<String, Object> add(Category category, HttpSession session){
        User u = (User) session.getAttribute("loginUser");
        Map<String, Object> result = new HashMap<>();
        if(u == null || u.getRole() != 1){
            result.put("success", false);
            result.put("message", "未登录或无权限");
            return result;
        }
        int code = categoryService.add(category);
        if(code > 0){
            result.put("success", true);
            result.put("message", "新增分类成功");
        } else {
            result.put("success", false);
            result.put("message", "新增失败，请重试");
        }
        return result;
    }

    @ResponseBody
    @GetMapping("/del")
    public Map<String, Object> del(Integer id, HttpSession session){
        User u = (User) session.getAttribute("loginUser");
        Map<String, Object> result = new HashMap<>();
        if(u == null || u.getRole() != 1){
            result.put("success", false);
            result.put("message", "未登录或无权限");
            return result;
        }
        int result_code = categoryService.delete(id);
        if(result_code == -1){
            result.put("success", false);
            result.put("message", "该分类下还有图书，无法删除，请先移除或转移该分类下的图书");
        } else if(result_code > 0){
            result.put("success", true);
            result.put("message", "删除成功");
        } else {
            result.put("success", false);
            result.put("message", "删除失败，分类可能不存在");
        }
        return result;
    }
}