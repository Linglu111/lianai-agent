package com.example.lianaiimagesearchmcp.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 同时激活 stdio（与 application.yaml 保持一致）和 local（加载本机私密配置 application-local.yaml，
 * 里面才有 pexels.api-key）。缺 application-local.yaml 时 key 会退化为环境变量/空串，测试仍可运行。
 */
@SpringBootTest
class ImageSearchToolTest {
    @Resource
    private ImageSearchTool imageSearchTool;

    @Test
    void searchImage() {
        String result = imageSearchTool.searchImage("cat");
        assertNotNull(result);
    }
}