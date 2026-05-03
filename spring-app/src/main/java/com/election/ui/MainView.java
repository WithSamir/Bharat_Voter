package com.election.ui;

import com.flowingcode.vaadin.addons.googlemaps.GoogleMap;
import com.flowingcode.vaadin.addons.googlemaps.GoogleMapMarker;
import com.flowingcode.vaadin.addons.googlemaps.LatLon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.lineawesome.LineAwesomeIcon;

@Route("")
public class MainView extends VerticalLayout {

    public MainView(@Value("${google.maps.api-key}") String apiKey) {
        // Icon Fix: Use Vaadin Line Awesome icons to ensure 100% visibility
        add(LineAwesomeIcon.VOTE_YEA.create());
        
        // Booth Finder logic via GoogleMap addon
        GoogleMap gmaps = new GoogleMap(apiKey, null, null);
        gmaps.setMapType(GoogleMap.MapType.ROADMAP);
        gmaps.setSizeFull();
        
        // Center on Greater Noida
        LatLon center = new LatLon(28.4744, 77.5040);
        gmaps.setCenter(center);
        gmaps.setZoom(14);
        
        GoogleMapMarker marker = new GoogleMapMarker("KCCITM Polling Booth", center, false);
        gmaps.addMarker(marker);
        
        add(gmaps);
    }
}
