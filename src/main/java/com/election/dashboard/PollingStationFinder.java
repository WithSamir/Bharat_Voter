package com.election.dashboard;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;

public class PollingStationFinder extends Div {

    private GeoApiContext geoApiContext;

    public PollingStationFinder(String apiKey) {
        setId("map");
        getStyle().set("width", "100%").set("height", "400px").set("border-radius", "8px");
        
        if (apiKey != null && !apiKey.isEmpty() && !apiKey.equals("dummy_key") && !apiKey.equals("your_key")) {
            UI.getCurrent().getPage().addJavaScript("https://maps.googleapis.com/maps/api/js?key=" + apiKey);
            UI.getCurrent().getPage().executeJs(
                "setTimeout(function() {" +
                "  window.markers = [];" +
                "  window.map = new google.maps.Map(document.getElementById('map'), {center: {lat: 28.4744, lng: 77.5040}, zoom: 14});" +
                "}, 1000);"
            );
            
            this.geoApiContext = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
        } else {
            setText("🗺️ Simulation Mode Active: Displaying Default Coordinates (Greater Noida). Please provide a valid Google Maps API Key.");
            getStyle()
                .set("background-color", "#E2E8F0")
                .set("color", "#475569")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");
        }
    }

    public void renderPollingStations(double lat, double lng) {
        // Clear previous markers to avoid UI clutter
        UI.getCurrent().getPage().executeJs(
            "if (window.markers) {" +
            "  window.markers.forEach(function(marker) { marker.setMap(null); });" +
            "  window.markers = [];" +
            "}"
        );

        if (geoApiContext != null) {
            try {
                // Google Maps SDK Request with Try-Catch block for resilience
                PlacesSearchResponse response = PlacesApi.nearbySearchQuery(geoApiContext, new LatLng(lat, lng))
                        .radius(5000)
                        .keyword("polling station")
                        .await();
                        
                if (response.results != null) {
                    for (int i = 0; i < Math.min(response.results.length, 5); i++) {
                        double rLat = response.results[i].geometry.location.lat;
                        double rLng = response.results[i].geometry.location.lng;
                        
                        UI.getCurrent().getPage().executeJs(
                            "var marker = new google.maps.Marker({position: {lat: $0, lng: $1}, map: window.map});" +
                            "window.markers.push(marker);", rLat, rLng
                        );
                    }
                }
            } catch (Exception e) {
                System.err.println("Google Maps SDK Error: Bhai, server thoda busy hai, please ek minute baad try karo. " + e.getMessage());
            }
        }
    }
}
