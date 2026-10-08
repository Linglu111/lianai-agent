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
        String answer = loveApp.doChatWithRag(message, chatId, "已婚");
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithRagWhenNoDocumentIsRetrieved() {
        String chatId = UUID.randomUUID().toString();
        String message = "如何学习 Java？";

        // 使用不存在的状态，确保 VectorStoreDocumentRetriever 无法召回文档。
        String answer = loveApp.doChatWithRag(message, chatId, "不存在的状态");

        Assertions.assertNotNull(answer);
        Assertions.assertTrue(
                answer.contains("只能回答恋爱相关的问题"),
                () -> "模型没有输出自定义兜底内容，实际回答：" + answer
        );
    }

    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        // 测试地图 MCP
        String message = "我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点";
        String answer =  loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }

}
