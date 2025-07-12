package com.wukoba.bilibili.collect;


import com.wukoba.bilibili.dto.Data;
import com.wukoba.bilibili.dto.Item;
import com.wukoba.bilibili.dto.ReviewsResponse;
import com.wukoba.bilibili.gateway.BiliBiliApiService;
import com.wukoba.bilibili.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReviewsCollectService {
    private final ReviewRepository repository;
    private final BiliBiliApiService apiService;
    private final Logger logger = LoggerFactory.getLogger(ReviewsCollectService.class);
    private Long timestamp;

    public ReviewsCollectService(ReviewRepository repository, BiliBiliApiService apiService, @Value("${bilibili.cursor-timestamp:0}") Long baseTimestamp) {
        this.repository = repository;
        this.apiService = apiService;
        if (baseTimestamp == null) {
            throw new IllegalArgumentException("Base timestamp cannot be null");
        }
        this.timestamp = baseTimestamp;
    }

    /**
     * 收集并存储新的评论（包括普通评论与@我的评论）
     *
     * @throws IOException 如果在从远程API获取数据时发生错误
     */
    public void collectReviews() throws IOException {
        // 统一拉取评论数据（包括普通评论 + @我的评论）
        Data reviewData = fetchData(apiService.getReplies(), "replies");
        Data atData = fetchData(apiService.getAtList(), "@list");

        // 合并评论项
        List<Item> allItems = Stream.concat(
                Arrays.stream(reviewData.getItems()),
                Arrays.stream(atData.getItems())
        ).toList();

        // 过滤出新评论（replyTime > 上次记录的时间戳）
        Map<Long, Item> newReplyMap = allItems.stream()
                .filter(item -> item.getReplyTime() > timestamp || item.getAtTime() > timestamp)
                .collect(Collectors.toMap(Item::getId, Function.identity(), (a, b) -> b));

        // 更新本地最大时间戳
        this.timestamp = reviewData.getLastViewAt();

        logger.info("Collected {} new replies", newReplyMap.size());

        if (!newReplyMap.isEmpty()) {
            repository.addReview(newReplyMap);
        }
    }

    /**
     * 通用方法：通过API请求拉取评论数据
     *
     * @param call  Retrofit请求体
     * @param label 日志/错误信息标识
     * @return 响应中的评论数据
     * @throws IOException 网络错误或格式错误
     */
    private Data fetchData(Call<ReviewsResponse> call, String label) throws IOException {
        Response<ReviewsResponse> response = call.execute();
        if (response.isSuccessful()) {
            ReviewsResponse body = response.body();
            if (body != null && Objects.equals(body.getCode(), 0)) {
                return body.getData();
            }
        }
        throw new IOException("Failed to fetch Bilibili " + label + ": " + response.errorBody());
    }
}
