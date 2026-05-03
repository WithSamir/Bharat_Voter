package com.election.dashboard;

import com.google.cloud.documentai.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import java.util.HashMap;
import java.util.Map;

@Service
public class DocumentService {

    private VertexAiGeminiChatModel geminiFlashModel;

    public DocumentService() {
        String projectId = System.getenv("GCP_PROJECT_ID");
        if (projectId != null && !projectId.isEmpty()) {
            this.geminiFlashModel = VertexAiGeminiChatModel.builder()
                    .project(projectId)
                    .location(System.getenv("GCP_REGION"))
                    .modelName("gemini-3-flash")
                    .build();
        }
    }

    public Map<String, String> processAadhaar(byte[] fileBytes) {
        Map<String, String> extractedData = new HashMap<>();
        String projectId = System.getenv("GCP_PROJECT_ID");
        String location = System.getenv("GCP_REGION");
        String processorId = System.getenv("DOCAI_PROCESSOR_ID");
        
        if (projectId == null || processorId == null) {
            extractedData.put("error", "Document AI not configured");
            return extractedData;
        }

        try (DocumentProcessorServiceClient client = DocumentProcessorServiceClient.create()) {
            String name = String.format("projects/%s/locations/%s/processors/%s", projectId, location, processorId);
            
            RawDocument rawDocument = RawDocument.newBuilder()
                    .setContent(ByteString.copyFrom(fileBytes))
                    .setMimeType("image/jpeg")
                    .build();
                    
            ProcessRequest request = ProcessRequest.newBuilder()
                    .setName(name)
                    .setRawDocument(rawDocument)
                    .build();
                    
            ProcessResponse result = client.processDocument(request);
            Document document = result.getDocument();
            
            boolean lowConfidence = false;

            for (Document.Entity entity : document.getEntitiesList()) {
                String type = entity.getType();
                String text = entity.getMentionText();
                float confidence = entity.getConfidence();
                
                if (confidence < 0.7f) {
                    lowConfidence = true;
                }

                if (type.contains("Name")) extractedData.put("name", text);
                if (type.contains("Address")) extractedData.put("address", text);
            }

            if (lowConfidence) {
                String promptMessage = "The Aadhaar image quality is too low or blurry. Please prompt the user kindly to upload a clearer photo.";
                String aiMessage = geminiFlashModel != null ? 
                    geminiFlashModel.generate(promptMessage) : 
                    "Image quality is too low. Please upload a clearer photo.";
                extractedData.put("error", aiMessage);
            }

        } catch (Exception e) {
            extractedData.put("error", "Error processing document: " + e.getMessage());
        }
        return extractedData;
    }
}
