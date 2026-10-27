package com.book.entity;

import java.util.Date;

/**
 * 站内消息通知。
 * <p>type 约定：1 = 订单类，2 = 反馈类，3 = 系统/评论类。
 * relatedId 指向触发该消息的业务主键（订单号 / 反馈号 / 评论号），便于前端跳转。
 */
public class Notification {
    private Integer id;
    private Integer userId;
    private Integer type;
    private String title;
    private String content;
    private Integer relatedId;
    private Integer isRead;   // 0:未读 1:已读
    private Date createTime;

    public Notification() {}

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public Integer getUserId() {return userId;}
    public void setUserId(Integer userId) {this.userId = userId;}
    public Integer getType() {return type;}
    public void setType(Integer type) {this.type = type;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
    public Integer getRelatedId() {return relatedId;}
    public void setRelatedId(Integer relatedId) {this.relatedId = relatedId;}
    public Integer getIsRead() {return isRead;}
    public void setIsRead(Integer isRead) {this.isRead = isRead;}
    public Date getCreateTime() {return createTime;}
    public void setCreateTime(Date createTime) {this.createTime = createTime;}

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", relatedId=" + relatedId +
                ", isRead=" + isRead +
                ", createTime=" + createTime +
                '}';
    }
}
