package com.wukoba.bilibili.job;

import com.wukoba.bilibili.collect.ReviewsCollectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReviewsCollectJob {
    @Autowired
    private ReviewsCollectService reviewsCollectService;

    private static final Logger logger = LoggerFactory.getLogger(ReviewsCollectJob.class);

    //    @Scheduled(cron = "0 */5 * * * *")
    @Scheduled(cron = "${bilibili.collect-cron:0/10 * * * * *}")
    public void execute() {
        try {
            logger.info("开始收集待回复消息");
            reviewsCollectService.collectReviews();
            logger.info("待回复信息收集完成");
        } catch (IOException e) {
            logger.error("收集待回复信息异常", e);
        }
    }
}
