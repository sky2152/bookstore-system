package com.book.entity;
import java.math.BigDecimal;
import java.util.Date;
public class Order {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private Integer buyNum;
    private BigDecimal totalMoney;
    private Date createTime;
    private String bookName;

    public Order(){}
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public Integer getUserId() {return userId;}
    public void setUserId(Integer userId) {this.userId = userId;}
    public Integer getBookId() {return bookId;}
    public void setBookId(Integer bookId) {this.bookId = bookId;}
    public Integer getBuyNum() {return buyNum;}
    public void setBuyNum(Integer buyNum) {this.buyNum = buyNum;}
    public BigDecimal getTotalMoney() {return totalMoney;}
    public void setTotalMoney(BigDecimal totalMoney) {this.totalMoney = totalMoney;}
    public Date getCreateTime() {return createTime;}
    public void setCreateTime(Date createTime) {this.createTime = createTime;}
    public String getBookName() {return bookName;}
    public void setBookName(String bookName) {this.bookName = bookName;}
}
