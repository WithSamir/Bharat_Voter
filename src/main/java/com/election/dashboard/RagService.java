package com.election.dashboard;

import com.google.cloud.discoveryengine.v1.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RagService {

    public List<String> searchElectionDates(String query) {
        List<String> results = new ArrayList<>();
        String projectId = System.getenv("GCP_PROJECT_ID");
        String location = System.getenv("GCP_REGION");
        String dataStoreId = System.getenv("VERTEX_SEARCH_DATASTORE_ID");
        
        if (projectId == null || dataStoreId == null) {
            results.add("Mock Result: SIR 2026 starts Nov 2025. Final roll on Jan 5, 2026.");
            return results;
        }

        try (SearchServiceClient client = SearchServiceClient.create()) {
            String servingConfig = String.format("projects/%s/locations/%s/collections/default_collection/dataStores/%s/servingConfigs/default_config", 
                projectId, location, dataStoreId);
                
            SearchRequest request = SearchRequest.newBuilder()
                    .setServingConfig(servingConfig)
                    .setQuery(query)
                    .setPageSize(3)
                    .build();
                    
            SearchServiceClient.SearchPagedResponse response = client.search(request);
            for (SearchResponse.SearchResult result : response.iterateAll()) {
                results.add(result.getDocument().getName());
            }
        } catch (Exception e) {
            results.add("Error fetching RAG context: " + e.getMessage());
        }
        return results;
    }
}
