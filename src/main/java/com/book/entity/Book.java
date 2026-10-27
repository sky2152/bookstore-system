package com.book.entity;
import java.math.BigDecimal;
public class Book {
    private Integer id;
    private String bookName;
    private String author;
    private BigDecimal price;
    private Integer stock;
    private Integer categoryId;
    private String categoryName;
    private Integer sales;

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}

    public String getBookName() {return bookName;}
    public void setBookName(String bookName) {this.bookName = bookName;}

    public String getAuthor() {return author;}
    public void setAuthor(String author) {this.author = author;}

    public BigDecimal getPrice() {return price;}
    public void setPrice(BigDecimal price) {this.price = price;}

    public Integer getStock() {return stock;}
    public void setStock(Integer stock) {this.stock = stock;}

    public Integer getCategoryId() {return categoryId;}
    public void setCategoryId(Integer categoryId) {this.categoryId = categoryId;}

    public String getCategoryName() {return categoryName;}
    public void setCategoryName(String categoryName) {this.categoryName = categoryName;}

    public Integer getSales() {return sales;}
    public void setSales(Integer sales) {this.sales = sales;}
}
