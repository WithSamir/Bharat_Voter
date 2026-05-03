package com.election.service;

import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

@Service
public class ElectionAssistantService {

    private final VertexAiGeminiChatModel chatModel;

    public ElectionAssistantService() {
        this.chatModel = VertexAiGeminiChatModel.builder()
                .project(System.getenv("GCP_PROJECT_ID"))
                .location(System.getenv("GCP_REGION"))
                .modelName("gemini-3-flash")
                .build();
    }

    public String processIntent(String userInput) {
        String systemPrompt = "You are a supportive college senior from KCCITM. Help users with Form 6/8 using 40% Hinglish.";
        
        // Generate response
        return chatModel.generate(systemPrompt + "\nUser Input: " + userInput);
    }
}
