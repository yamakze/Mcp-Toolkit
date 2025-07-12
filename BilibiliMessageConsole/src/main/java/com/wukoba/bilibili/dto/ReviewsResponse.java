package com.wukoba.bilibili.dto;

import org.springframework.stereotype.Repository;

public class ReviewsResponse extends Result{
    private com.wukoba.bilibili.dto.Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
