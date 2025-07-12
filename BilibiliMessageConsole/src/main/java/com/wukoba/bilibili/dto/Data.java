package com.wukoba.bilibili.dto;

public class Data {
    private Item[] items;
    private long lastViewAt;

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public long getLastViewAt() {
        return lastViewAt;
    }

    public void setLastViewAt(long lastViewAt) {
        this.lastViewAt = lastViewAt;
    }

}
