package com.wukoba.bilibili.gateway;

import com.wukoba.bilibili.dto.ReviewsResponse;
import com.wukoba.bilibili.dto.Result;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface BiliBiliApiService {

    /**
     * 获取评论回复列表
     */
    @GET("x/msgfeed/reply")
    @Headers("referer=https://message.bilibili.com/")
    Call<ReviewsResponse> getReplies();

    /**
     * 获取@我的 列表
     */
    @GET("x/msgfeed/at")
    @Headers("referer=https://message.bilibili.com/")
    Call<ReviewsResponse> getAtList();

    /**
     * 向指定资源添加回复。
     */
    @POST("x/v2/reply/add")
    @FormUrlEncoded
    Call<ReviewsResponse> addReply(
            @Field("oid") String oid,
            @Field("type") String type,
            @Field("message") String message,
            @Field("root") String root,
            @Field("parent") String parent,
            @Field("csrf") String csrf
    );

    /**
     * 向指定资源添加回复。
     */
    @POST("x/v2/reply/add")
    @FormUrlEncoded
    Call<Result> addReply(
            @FieldMap Map<String, String> fields
    );
}