package com.election.dashboard;

import com.google.cloud.discoveryengine.v1.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * RagService – searches the Vertex AI Search datastore for election content.
 * Falls back gracefully with rich mock data when not configured.
 *
 * FIX: Uses @Value injection instead of System.getenv() for consistency.
 */
@Service
public class RagService {

    @Value("${gcp.project.id:}")
    private String projectId;

    @Value("${gcp.region:us-central1}")
    private String location;

    @Value("${gcp.vertex.datastore.id:}")
    private String dataStoreId;

    public List<String> searchElectionDates(String query) {
        List<String> results = new ArrayList<>();

        if (projectId == null || projectId.isBlank() || dataStoreId == null || dataStoreId.isBlank()) {
            // Rich mock data with real election info
            results.add("📅 Lok Sabha 2024: Phase 1 — April 19, Phase 7 — June 1, Results — June 4, 2024.");
            results.add("🗓️ Summary Revision (SIR) 2025: Draft roll — October 2025, Final roll — January 5, 2026.");
            results.add("📋 Form 6 deadline: Typically 30 days before the last date of summary publication.");
            results.add("🌐 Latest schedule: eci.gov.in/candidatenoticesandaffidevits/gen-election/");
            return results;
        }

        try (SearchServiceClient client = SearchServiceClient.create()) {
            String servingConfig = String.format(
                "projects/%s/locations/%s/collections/default_collection/dataStores/%s/servingConfigs/default_config",
                projectId, location, dataStoreId
            );

            SearchRequest request = SearchRequest.newBuilder()
                    .setServingConfig(servingConfig)
                    .setQuery(query)
                    .setPageSize(5)
                    .build();

            SearchServiceClient.SearchPagedResponse response = client.search(request);
            for (SearchResponse.SearchResult result : response.iterateAll()) {
                results.add(result.getDocument().getName());
            }
        } catch (Exception e) {
            System.err.println("RagService: Vertex Search error: " + e.getMessage());
            results.add("⚠️ Search service temporarily unavailable. Please visit eci.gov.in for the latest information.");
        }

        return results;
    }
}
