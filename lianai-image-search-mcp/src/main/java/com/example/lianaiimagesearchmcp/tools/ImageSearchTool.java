package com.example.lianaiimagesearchmcp.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ImageSearchTool {

    private String API_KEY = "F6bw2FV143QtgOQ5wKqsY5faRxEJYwI6LiD4y4ZRPMZcLtLBELCvjhmn";

    private static final String API_URL = "https://api.pexels.com/v1/search";

    @Tool(description = "Search images from web")
    public String searchImage(@ToolParam(description = "Search query keyword") String query) {
        try {
            return String.join(",", searchMediumImages(query));
        } catch (Exception e) {
            return "Error search image: " + e.getMessage();
        }
    }

    /**
     * 搜索中等尺寸图片
     * @param query
     * @return
     */
    public List<String> searchMediumImages(String query) {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException("未配置 pexels.api-key：请在 application-local.yaml 中配置并以 local profile 启动，"
                    + "或设置环境变量 PEXELS_API_KEY");
        }
        // 设置请求头
        Map<String, String> headers = Map.of("Authorization", API_KEY);
        // 设置请求参数
        Map<String, Object> params = Map.of("query", query);

        // 发送get请求
        String response = HttpUtil.createGet(API_URL)
                .addHeaders(headers)
                .form(params)
                .execute()
                .body();

        // 解析响应
        return JSONUtil.parseObj(response)
                .getJSONArray("photos")
                .stream()
                .map(photoObj -> (JSONObject) photoObj)
                .map(photoObj -> photoObj.getJSONObject("src"))
                .map(photo -> photo.getStr("medium"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }
}
