package com.wukoba.bilibili.repository;

import com.wukoba.bilibili.dto.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ReviewRepository {
    private final Map<Long, Item> reviewItems = new ConcurrentHashMap<>();

    @Value("${bilibili.max-retrieve-count:5}")
    private Integer retrieveCount;

    /**
     * 根据ID获取已收集的项目信息。
     *
     * @param id 项目的唯一标识符
     * @return 项目信息
     */
    public Item getCollectedItem(Long id) {
        return reviewItems.get(id);
    }

    /**
     * 获取最近k条评论。
     *
     * @return 最近5条评论列表
     */
    public List<Item> getLastKReviews() {
        return reviewItems.entrySet()
                .stream()
                .sorted((e1, e2) -> (int) (e1.getValue().getReplyTime() - e2.getValue().getReplyTime()))
                .limit(retrieveCount)
                .map(Map.Entry::getValue)
                .toList();
    }

    /**
     * 移除指定ID的评论。
     *
     * @param ids 要移除的评论ID列表
     */
    public void removeReviews(Long... ids) {
        for (Long id : ids) {
            reviewItems.remove(id);
        }
    }

    /**
     * 添加多条评论。
     *
     * @param reviews 待添加的评论映射
     */
    public void addReview(Map<Long, Item> reviews) {
        reviewItems.putAll(reviews);
    }
}
