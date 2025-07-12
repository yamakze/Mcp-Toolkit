package com.wukoba.bilibili.tools;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wukoba.bilibili.dto.Item;
import com.wukoba.bilibili.repository.ReviewRepository;
import com.wukoba.bilibili.responder.MessageResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;


@Component
public class BilibiliTools {

    private static final Logger logger = LoggerFactory.getLogger(BilibiliTools.class);

    private final ReviewRepository repository;
    private final MessageResponder responder;

    @Autowired
    public BilibiliTools(ReviewRepository repository, MessageResponder responder) {
        this.repository = repository;
        this.responder = responder;
    }

    @Tool(name = "getReviews", description = "Get the latest messages that need to be replied to.")
    public List<ReviewResponse> getReviews() {
        List<Item> items = repository.getLastKReviews();
        return items.stream()
                .map(item -> new ReviewResponse(item.getId(),
                        item.getItem().getSourceContent().replaceAll("(回复\\s*)?@[^\\s:：]+[\\s:：]?", "")))
                .toList();
    }

    @Async
    @Tool(name = "replyToMessage", description = "Batch send a reply to a specific message by ID.")
    public void replyToMessage(List<MessageReplyRequest> messageReplyRequestList) {
        for (MessageReplyRequest messageReplyRequest : messageReplyRequestList) {
            try {
                Item item = repository.getCollectedItem(messageReplyRequest.id);
                if (responder.doRetry(item, messageReplyRequest.replyContent)) {
                    repository.removeReviews(item.getId());
                }
                // 延迟 500 毫秒，避免限流
                Thread.sleep(500);
            } catch (IOException e) {
                logger.error("Exception while replying to message (id={})", messageReplyRequest.id, e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 恢复中断标志
                logger.warn("Reply task was interrupted", e);
                break;
            }
        }
    }

    public record ReviewResponse(
            @JsonProperty("messageId") Long id,
            @JsonProperty("messageContent") String content
    ) {
    }

    public record MessageReplyRequest(@JsonProperty("messageId") Long id,
                                      @JsonProperty("replyContent") String replyContent) {
    }
}

