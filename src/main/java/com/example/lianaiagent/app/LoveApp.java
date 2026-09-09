package com.example.lianaiagent.app;

import com.example.lianaiagent.advisor.MyLoggerAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private final PromptTemplate loveReportPromptTemplate;

    public LoveApp(ChatModel dashscopeChatModel,
                   ChatMemory chatMemory,
                   @Value("classpath:/prompts/love-system.st") Resource systemPrompt,
                   @Value("classpath:/prompts/love-report.st") Resource loveReportPrompt) {
        this.loveReportPromptTemplate = new PromptTemplate(loveReportPrompt);

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        // 自定义拦截器
                        new MyLoggerAdvisor()
//                        // 自定义推理增强
//                        ,new ReReadingAdvisor()
                )
                .build();

    }

    public String doChat(String message, String chatId){
        ChatResponse response = this.chatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(message)
                .call()
                .chatResponse();

        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * 图片理解对话。图片只参与当前请求；JDBC ChatMemory 1.1.2 不会持久化图片二进制。
     */
    public String doChatWithImage(String message,
                                  String chatId,
                                  byte[] imageData,
                                  String contentType) {
        Assert.hasText(message, "message 不能为空");
        Assert.hasText(chatId, "chatId 不能为空");
        Assert.isTrue(imageData != null && imageData.length > 0, "图片不能为空");
        Assert.hasText(contentType, "图片 Content-Type 不能为空");

        MimeType mimeType = MimeTypeUtils.parseMimeType(contentType);
        Assert.isTrue("image".equalsIgnoreCase(mimeType.getType()), "仅支持图片文件");

        Media image = Media.builder()
                .mimeType(mimeType)
                .data(imageData)
                .build();

        String content = this.chatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(user -> user.text(message).media(image))
                .call()
                .content();

        log.info("multimodal content: {}", content);
        return content;
    }

    public record LoveReport(String title, List<String> suggestions) {
    }

    /**
     * AI 恋爱报告 (结构化输出)
     * @param message
     * @param chatId
     * @return
     */
    public LoveReport doChatWithReport(String message, String chatId){
        String reportPrompt = loveReportPromptTemplate.render(Map.of("message", message));

        LoveReport loveReport = this.chatClient
                .prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(reportPrompt)
                .call()
                .entity(LoveReport.class);

        log.info("loveReport: {}", loveReport);
        return loveReport;
    }
}
