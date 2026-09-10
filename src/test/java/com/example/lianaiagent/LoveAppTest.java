package com.example.lianaiagent;

import cn.hutool.core.lang.UUID;
import com.example.lianaiagent.app.LoveApp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class LoveAppTest {

    @Autowired
    private LoveApp loveApp;

    @Test
    void testChat(){
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好， 我是linglu";
        String response = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(response);
        // 第二轮
        message = "我谈了个女朋友，她叫hhh，我想让她更爱我";
        response = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(response);
        // 第三轮
        message = "我的对象叫什么？告诉我";
        response = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(response);
    }

    @Test
    void testChatWithReport(){
        String chatId = UUID.randomUUID().toString();
        String message = "你好， 我是linglu，我想让另一半（涵涵）更爱我，但我不知道怎么办";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer =  loveApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }

}
