package com.qingqiu.aigeek;

import dev.langchain4j.rag.RetrievalAugmentor;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = {
//        PgVectorStoreAutoConfiguration.class
         DataSourceAutoConfiguration.class
})
public class AiAgentApplication implements CommandLineRunner {

    @Autowired
    ApplicationContext ctx;

    @Override
    public void run(String... args) {
        Map<String, RetrievalAugmentor> beans = ctx.getBeansOfType(RetrievalAugmentor.class);
        System.out.println("-----------------"+beans.keySet());
    }

    public static void main(String[] args) {
        SpringApplication.run(AiAgentApplication.class, args);
    }

}
