package com.election.dashboard;

import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

@Service
public class ElectionService {

    private VertexAiGeminiChatModel chatModel;

    public ElectionService() {
        String projectId = System.getenv("GCP_PROJECT_ID");
        if (projectId != null && !projectId.isEmpty()) {
            this.chatModel = VertexAiGeminiChatModel.builder()
                    .project(projectId)
                    .location(System.getenv("GCP_REGION"))
                    .modelName("gemini-3-flash")
                    .build();
        }
    }

    public String processIntent(String userInput) {
        if (chatModel == null) {
            return getMockResponse(userInput);
        }
        
        String systemPrompt = "You are an expert assistant on the Indian Election System. Help users understand the election process, timelines, and steps in an interactive and easy-to-follow way.";
        try {
            return chatModel.generate(systemPrompt + "\nUser Input: " + userInput);
        } catch (Exception e) {
            return getMockResponse(userInput);
        }
    }

    private String getMockResponse(String userInput) {
        String input = userInput.toLowerCase();
        if (input.contains("process") || input.contains("step") || input.contains("how")) {
            return "Here is the complete process of the Indian Election System:\n\n" +
                   "1️⃣ Voter Registration: Fill Form 6 to get your Voter ID.\n" +
                   "2️⃣ Notification: The ECI announces election dates.\n" +
                   "3️⃣ Nomination: Candidates file their nomination papers.\n" +
                   "4️⃣ Campaigning: Parties campaign (ends 48 hrs before voting).\n" +
                   "5️⃣ Voting Day: Use EVM and VVPAT at your polling booth.\n" +
                   "6️⃣ Counting & Results: Votes are counted and the winner forms the government!\n\n" +
                   "Want to test your knowledge with a quick quiz? Just type 'quiz'!";
        } else if (input.contains("quiz")) {
            return "Awesome! Here's a quiz: What is the minimum age to vote in India?\n(a) 16\n(b) 18\n(c) 21\nReply with a, b, or c.";
        } else if (input.equals("b") || input.equals("18") || input.contains("18")) {
            return "Correct! 18 years is the minimum voting age. Ready for another question?";
        } else if (input.contains("form 6")) {
            return "Form 6 is for new voter registration. You need proof of age and residence. You can apply online at the NVSP portal. Bhai, form bhar de time se!";
        }
        return "Namaste! I am your Election Assistant. You can ask me to explain the 'election process', ask about 'Form 6', or say 'quiz' to test your knowledge!";
    }
}
