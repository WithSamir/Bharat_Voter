package com.election.dashboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * ConfigValidator – validates startup configuration.
 *
 * FIX: Removed broken Spring EL #{environment.VAR} syntax which throws
 * ELException at startup. Replaced with clean @Value with safe defaults.
 */
@Component
public class ConfigValidator implements CommandLineRunner {

    @Value("${google.maps.api.key:}")
    private String googleMapsApiKey;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Override
    public void run(String... args) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🇮🇳  BHARAT VOTER DASHBOARD — CONFIG CHECK");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        if (googleMapsApiKey == null || googleMapsApiKey.isBlank()) {
            System.out.println("⚠️  GOOGLE_MAPS_API_KEY: Not set → Leaflet/OpenStreetMap fallback active.");
        } else {
            System.out.println("✅  GOOGLE_MAPS_API_KEY: Configured (Google Maps mode active).");
        }

        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            System.out.println("⚠️  GEMINI_API_KEY: Not set → Mock chatbot responses active.");
        } else {
            System.out.println("✅  GEMINI_API_KEY: Configured (Gemini 1.5 Flash AI mode active).");
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🚀  Server starting at http://localhost:8080");
        System.out.println("    Login: voter / voter123");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
