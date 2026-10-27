package com.book.entity;

import java.util.Date;

/**
 * 图书评论。
 * <p>评论必须挂在一条订单上（orderId），用于「一单一评」和「已购才可评」的约束；
 * username / bookName 是联表查询出来的展示字段，不落库。
 */
public class Comment {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private Integer orderId;
    private Integer rating;      // 1~5
    private String content;
    private Integer isPinned;    // 0:普通 1:置顶
    private Date createTime;

    // 展示字段（联表查出，非表字段）
    private String username;
    private String bookName;

    public Comment() {}

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public Integer getUserId() {return userId;}
    public void setUserId(Integer userId) {this.userId = userId;}
    public Integer getBookId() {return bookId;}
    public void setBookId(Integer bookId) {this.bookId = bookId;}
    public Integer getOrderId() {return orderId;}
    public void setOrderId(Integer orderId) {this.orderId = orderId;}
    public Integer getRating() {return rating;}
    public void setRating(Integer rating) {this.rating = rating;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
    public Integer getIsPinned() {return isPinned;}
    public void setIsPinned(Integer isPinned) {this.isPinned = isPinned;}
    public Date getCreateTime() {return createTime;}
    public void setCreateTime(Date createTime) {this.createTime = createTime;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getBookName() {return bookName;}
    public void setBookName(String bookName) {this.bookName = bookName;}

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", orderId=" + orderId +
                ", rating=" + rating +
                ", isPinned=" + isPinned +
                ", createTime=" + createTime +
                '}';
    }
}
