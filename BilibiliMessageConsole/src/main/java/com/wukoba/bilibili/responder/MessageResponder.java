package com.wukoba.bilibili.responder;

import com.wukoba.bilibili.dto.Item;
import com.wukoba.bilibili.dto.ItemDetail;
import com.wukoba.bilibili.dto.Result;
import com.wukoba.bilibili.gateway.BiliBiliApiService;
import com.wukoba.bilibili.utils.CookieLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Service
public class MessageResponder {
    private final String csrf;
    private final BiliBiliApiService apiService;
    private final Logger logger = LoggerFactory.getLogger(MessageResponder.class);

    public MessageResponder(CookieLoader cookieLoader, BiliBiliApiService apiService) {
        Map<String, String> cookies = cookieLoader.getCookies();
        Assert.notNull(cookies, "Cookies cannot be null!");
        Assert.isTrue(cookies.containsKey("bili_jct"), "CSRF value not found in cookies!");
        this.csrf = cookies.get("bili_jct");
        this.apiService = apiService;
    }

    /**
     * 对给定的评论进行重试回复。
     *
     * @param item  评论项
     * @param reply 回复内容
     * @return 是否成功执行了回复操作
     * @throws IOException 如果网络请求失败
     */
    public boolean doRetry(Item item, String reply) throws IOException {
        ItemDetail itemDetail = item.getItem();
        Map<String, String> params = Map.of(
                "oid", itemDetail.getSubjectId().toString(),
                "type", itemDetail.getBusinessId().toString(),
                "message", buildMessage(item.getUser().getNickname(), reply),
                "root", itemDetail.getSourceId().toString(),
                "parent", itemDetail.getSourceId().toString(),
                "csrf", csrf
        );

        Result result = apiService.addReply(params).execute().body();
        if (result == null || !Objects.equals(result.getCode(), 0)) {
            logger.error("Failed to call Bilibili API, result: {}", result);
            return false;
        }
        return true;
    }

    /**
     * 构建回复消息。
     *
     * @param recipientName 接收者名称
     * @param reply         回复内容
     * @return 完整的消息文本
     */
    private String buildMessage(String recipientName, String reply) {
        return "回复@" + recipientName + ": " + reply;
    }
}