package com.example.lianaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RAG 系统文档加载器
 */
@Component
@Slf4j
public class LoveAppDocumentLoader {

    private static final Pattern STATUS_PATTERN = Pattern.compile("-\\s*(单身|恋爱|已婚)篇\\.md$");

    private final ResourcePatternResolver resourcePatternResolver;

    public LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadMarkdowns() {
        List<Document> allDocuments = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath*:documents/*.md");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                String status = extractStatus(fileName);
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", fileName)
                        .withAdditionalMetadata("status", status)
                        .build();
                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                allDocuments.addAll(reader.get());
            }
        } catch (IOException e) {
            log.error("Markdown 文档加载失败", e);
        }
        return allDocuments;
    }

    private String extractStatus(String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("知识库文档文件名不能为空");
        }
        Matcher matcher = STATUS_PATTERN.matcher(fileName);
        if (!matcher.find()) {
            throw new IllegalArgumentException("无法从知识库文档文件名识别 status: " + fileName);
        }
        return matcher.group(1);
    }
}
