package com.example.lianaiagent.rag;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.util.Assert;

/** 根据用户的情感状态创建带元数据过滤的 RAG Advisor。 */
public final class LoveAppRagCustomAdvisorFactory {

    private LoveAppRagCustomAdvisorFactory() {
    }

    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {
        Assert.notNull(vectorStore, "vectorStore 不能为空");
        Assert.hasText(status, "status 不能为空");

        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();

        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(expression)
                .similarityThreshold(0.5)
                .topK(3)
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();
    }
}
