package com.election.dashboard;

import com.google.cloud.documentai.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * DocumentService – processes uploaded Aadhaar/identity documents via
 * Google Cloud Document AI.
 *
 * FIX: Removed broken VertexAiGeminiChatModel with wrong model "gemini-3-flash".
 * Now uses GeminiApiService for the low-confidence message.
 * FIX: Uses @Value for config instead of System.getenv() directly.
 */
@Service
public class DocumentService {

    private final GeminiApiService geminiApiService;

    @Value("${gcp.project.id:}")
    private String projectId;

    @Value("${gcp.region:us-central1}")
    private String location;

    @Value("${gcp.docai.processor.id:}")
    private String processorId;

    public DocumentService(GeminiApiService geminiApiService) {
        this.geminiApiService = geminiApiService;
    }

    /**
     * Processes an Aadhaar or identity document using Google Cloud Document AI.
     * Falls back gracefully if Document AI is not configured.
     *
     * @param fileBytes Raw bytes of the uploaded image/PDF
     * @return Map with extracted fields (name, address) or error message
     */
    public Map<String, String> processAadhaar(byte[] fileBytes) {
        Map<String, String> extractedData = new HashMap<>();

        if (projectId == null || projectId.isBlank() || processorId == null || processorId.isBlank()) {
            // Document AI not configured — return helpful mock data
            extractedData.put("name", "N/A (Document AI not configured)");
            extractedData.put("address", "N/A (Set GCP_PROJECT_ID and DOCAI_PROCESSOR_ID in .env)");
            extractedData.put("info", "Document AI is not configured. Your application will still be processed manually.");
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
                String prompt = "The Aadhaar image quality is too low or blurry. Please tell the user kindly (in a friendly, warm tone) to upload a clearer photo.";
                String aiMsg = geminiApiService.isConfigured()
                        ? geminiApiService.generate(prompt)
                        : "Image quality is too low. Please upload a clearer, well-lit photo of your document.";
                extractedData.put("warning", aiMsg != null ? aiMsg : "Please upload a clearer photo.");
            }

        } catch (Exception e) {
            System.err.println("DocumentService error: " + e.getMessage());
            extractedData.put("error", "Document processing error. Please try again or contact support.");
        }

        return extractedData;
    }
}
