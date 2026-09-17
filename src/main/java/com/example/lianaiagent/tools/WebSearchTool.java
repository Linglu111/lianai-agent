package com.example.lianaiagent.tools;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class WebSearchTool {

    private static final String SEARCH_API_BASE_URL = "https://www.searchapi.io";
    private static final String DEFAULT_ENGINE = "bing";
    private static final int RESULT_LIMIT = 5;

    private final RestClient restClient;
    private final String apiKey;

    public WebSearchTool(@Value("${search-api.api-key:}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(SEARCH_API_BASE_URL)
                .build();
    }

    @Tool(description = "搜索互联网中的实时信息。当问题涉及新闻、近期事件或本地知识库之外的信息时使用")
    public String searchWeb(@ToolParam(description = "需要在百度中查询的搜索关键词") String query) {
        if (!StringUtils.hasText(query)) {
            return "联网搜索失败：搜索关键词不能为空。";
        }
        if (!StringUtils.hasText(apiKey)) {
            return "联网搜索不可用：未配置 SEARCHAPI_API_KEY。";
        }

        try {
            JsonNode response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/search")
                            .queryParam("engine", DEFAULT_ENGINE)
                            .queryParam("q", query)
                            .queryParam("num", RESULT_LIMIT)
                            .build())
                    // 使用请求头传递密钥，避免密钥出现在 URL 和访问日志中。
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .retrieve()
                    .body(JsonNode.class);

            return formatSearchResults(response);
        }
        catch (RestClientResponseException exception) {
            log.warn("SearchAPI request failed with HTTP status {}", exception.getStatusCode());
            return "联网搜索失败：SearchAPI 返回 HTTP " + exception.getStatusCode().value() + "。";
        }
        catch (Exception exception) {
            log.error("SearchAPI request failed", exception);
            return "联网搜索失败：暂时无法访问搜索服务。";
        }
    }

    private String formatSearchResults(JsonNode response) {
        if (response == null) {
            return "没有搜索到相关结果。";
        }

        List<String> results = new ArrayList<>();

        JsonNode answerBox = response.path("answer_box");
        String directAnswer = answerBox.path("answer").asText();
        if (answerBox.isObject() && StringUtils.hasText(directAnswer)) {
            results.add("即时答案：" + directAnswer);
        }

        JsonNode organicResults = response.path("organic_results");
        if (organicResults.isArray()) {
            int count = Math.min(RESULT_LIMIT, organicResults.size());
            for (int index = 0; index < count; index++) {
                JsonNode item = organicResults.get(index);
                results.add("""
                        %d. %s
                        摘要：%s
                        链接：%s
                        """.formatted(
                        index + 1,
                        item.path("title").asText("无标题"),
                        item.path("snippet").asText("无摘要"),
                        item.path("link").asText("无链接")
                ).trim());
            }
        }

        return results.isEmpty() ? "没有搜索到相关结果。" : String.join("\n\n", results);
    }
}
