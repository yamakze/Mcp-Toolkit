package com.wukoba.bilibili.dto;

public class Item {
    private Long id;
    private User user;
    private ItemDetail item;
    private long replyTime;
    private long atTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ItemDetail getItem() {
        return item;
    }

    public void setItem(ItemDetail item) {
        this.item = item;
    }

    public Long getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Long replyTime) {
        this.replyTime = replyTime;
    }

    public long getAtTime() {
        return atTime;
    }

    public void setAtTime(long atTime) {
        this.atTime = atTime;
    }
}
