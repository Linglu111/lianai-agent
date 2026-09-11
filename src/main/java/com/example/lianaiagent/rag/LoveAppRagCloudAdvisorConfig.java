package com.example.lianaiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 云知识库配置
 */
@Configuration
public class LoveAppRagCloudAdvisorConfig {

    @Bean
    public DocumentRetriever loveAppCloudDocumentRetriever(
            @Value("${spring.ai.dashscope.api-key}") String apiKey,
            @Value("${spring.ai.dashscope.workspace-id:}") String workspaceId,
            @Value("${love-app.rag.cloud.index-name}") String indexName,
            @Value("${love-app.rag.cloud.rerank-top-n:5}") int rerankTopN,
            @Value("${love-app.rag.cloud.rerank-min-score:0.01}") float rerankMinScore) {

        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(apiKey)
                .workSpaceId(workspaceId)
                .build();

        DashScopeDocumentRetrieverOptions options = DashScopeDocumentRetrieverOptions.builder()
                .indexName(indexName)
                .enableReranking(true)
                .rerankTopN(rerankTopN)
                .rerankMinScore(rerankMinScore)
                .build();

        return new DashScopeDocumentRetriever(dashScopeApi, options);
    }

    @Bean
    public Advisor loveAppRagCloudAdvisor(DocumentRetriever loveAppCloudDocumentRetriever) {
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(loveAppCloudDocumentRetriever)
                .build();
    }
}

