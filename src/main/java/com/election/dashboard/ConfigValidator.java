package com.election.dashboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConfigValidator implements CommandLineRunner {

    @Value("${GOOGLE_MAPS_API_KEY:#{environment.GOOGLE_MAPS_API_KEY}}")
    private String googleMapsApiKey;

    @Value("${GEMINI_API_KEY:#{environment.GEMINI_API_KEY}}")
    private String geminiApiKey;

    @Override
    public void run(String... args) throws Exception {
        if (googleMapsApiKey == null || googleMapsApiKey.trim().isEmpty()) {
            System.err.println("WARNING: GOOGLE_MAPS_API_KEY is missing. Using fallback/dummy mode.");
        }
        if (geminiApiKey == null || geminiApiKey.trim().isEmpty()) {
            System.err.println("WARNING: GEMINI_API_KEY is missing. Using fallback/dummy mode.");
        }
        System.out.println("ConfigValidator completed.");
    }
}
