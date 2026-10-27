package com.book.entity;
import java.util.Date;

public class Feedback {
    private Integer id;
    private Integer userId;
    private Integer bookId;
    private String bookName;
    private String username;
    private String remark;
    private Integer status; // 0:待处理 1:已处理
    private Date createTime;
    private Date handleTime;
    private String handleRemark;

    public Feedback() {}

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public Integer getUserId() {return userId;}
    public void setUserId(Integer userId) {this.userId = userId;}
    public Integer getBookId() {return bookId;}
    public void setBookId(Integer bookId) {this.bookId = bookId;}
    public String getBookName() {return bookName;}
    public void setBookName(String bookName) {this.bookName = bookName;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getRemark() {return remark;}
    public void setRemark(String remark) {this.remark = remark;}
    public Integer getStatus() {return status;}
    public void setStatus(Integer status) {this.status = status;}
    public Date getCreateTime() {return createTime;}
    public void setCreateTime(Date createTime) {this.createTime = createTime;}
    public Date getHandleTime() {return handleTime;}
    public void setHandleTime(Date handleTime) {this.handleTime = handleTime;}
    public String getHandleRemark() {return handleRemark;}
    public void setHandleRemark(String handleRemark) {this.handleRemark = handleRemark;}

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                ", username='" + username + '\'' +
                ", remark='" + remark + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", handleTime=" + handleTime +
                ", handleRemark='" + handleRemark + '\'' +
                '}';
    }
}
