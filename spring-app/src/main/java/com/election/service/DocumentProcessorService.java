package com.election.service;

import com.google.cloud.documentai.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class DocumentProcessorService {

    public Map<String, String> processDocument(byte[] fileBytes) {
        Map<String, String> extractedData = new HashMap<>();
        String projectId = System.getenv("GCP_PROJECT_ID");
        String location = System.getenv("GCP_REGION");
        String processorId = System.getenv("DOCAI_PROCESSOR_ID");
        
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
            
            for (Document.Entity entity : document.getEntitiesList()) {
                String type = entity.getType();
                String text = entity.getMentionText();
                if (type.contains("Name")) extractedData.put("name", text);
                if (type.contains("Address")) extractedData.put("address", text);
            }
        } catch (Exception e) {
            extractedData.put("error", "Error processing document: " + e.getMessage());
        }
        
        return extractedData;
    }
}
