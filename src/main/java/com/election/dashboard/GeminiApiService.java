package com.election.dashboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * GeminiApiService – calls the Gemini 1.5 Flash REST API directly.
 * This avoids needing GCP Application Default Credentials (ADC) and
 * works with just the GEMINI_API_KEY from the .env file.
 */
@Service
public class GeminiApiService {

    private static final String GEMINI_API_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    private static final String SYSTEM_PROMPT =
        "You are 'Bharat Bot', a friendly and knowledgeable AI assistant for Indian elections and democracy. " +
        "Your personality is like a helpful college senior — warm, encouraging, and sometimes uses light Hinglish " +
        "(a mix of Hindi and English) like 'Bhai', 'Yaar', 'Bilkul sahi', 'Tension mat lo'. " +
        "You know everything about: voter registration (Form 6), EVMs, VVPAT, NOTA, Model Code of Conduct, " +
        "Election Commission of India (ECI), polling booths, constituency boundaries, election dates, " +
        "Lok Sabha, Rajya Sabha, State Assembly elections, how to check voter ID status, and democratic rights. " +
        "Keep responses concise, clear, and helpful. Use emojis occasionally. " +
        "If asked about something unrelated to elections or Indian democracy, politely steer back to your domain.";

    @Value("${gemini.api.key:}")
    private String apiKey;

    /**
     * Generates a response from Gemini 1.5 Flash using the REST API.
     * Falls back to null if the API key is missing or the call fails.
     */
    public String generate(String userMessage) {
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }
        try {
            String requestBody = buildRequestBody(userMessage);
            String apiUrl = GEMINI_API_URL + apiKey;

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(20000);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8)) {
                    String response = scanner.useDelimiter("\\A").next();
                    return parseGeminiResponse(response);
                }
            } else {
                try (Scanner scanner = new Scanner(conn.getErrorStream(), StandardCharsets.UTF_8)) {
                    String errorBody = scanner.useDelimiter("\\A").next();
                    System.err.println("Gemini API Error [" + responseCode + "]: " + errorBody);
                }
                return null;
            }
        } catch (Exception e) {
            System.err.println("GeminiApiService error: " + e.getMessage());
            return null;
        }
    }

    private String buildRequestBody(String userMessage) {
        // Escape special characters for JSON
        String escapedSystem = escapeJson(SYSTEM_PROMPT);
        String escapedUser = escapeJson(userMessage);

        return "{\n" +
               "  \"contents\": [\n" +
               "    {\"role\": \"user\", \"parts\": [{\"text\": \"" + escapedSystem + "\\n\\nUser: " + escapedUser + "\"}]}\n" +
               "  ],\n" +
               "  \"generationConfig\": {\n" +
               "    \"temperature\": 0.7,\n" +
               "    \"maxOutputTokens\": 512,\n" +
               "    \"topP\": 0.95\n" +
               "  },\n" +
               "  \"safetySettings\": [\n" +
               "    {\"category\": \"HARM_CATEGORY_HARASSMENT\", \"threshold\": \"BLOCK_MEDIUM_AND_ABOVE\"},\n" +
               "    {\"category\": \"HARM_CATEGORY_HATE_SPEECH\", \"threshold\": \"BLOCK_MEDIUM_AND_ABOVE\"}\n" +
               "  ]\n" +
               "}";
    }

    private String parseGeminiResponse(String json) {
        // Simple JSON extraction for "text" field within candidates[0].content.parts[0]
        try {
            int textIdx = json.indexOf("\"text\":");
            if (textIdx == -1) return null;
            int start = json.indexOf("\"", textIdx + 7) + 1;
            int end = start;
            boolean escaped = false;
            while (end < json.length()) {
                char c = json.charAt(end);
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    break;
                }
                end++;
            }
            return json.substring(start, end)
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\t", "\t");
        } catch (Exception e) {
            return null;
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
