package com.example.lianaiagent;

import org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class LianaiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(LianaiAgentApplication.class, args);
    }

}
