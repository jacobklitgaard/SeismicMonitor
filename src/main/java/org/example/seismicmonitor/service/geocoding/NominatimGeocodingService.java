package org.example.seismicmonitor.service.geocoding;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NominatimGeocodingService implements GeocodingService {

    private final WebClient webClient;

    // configures webclient with Nominatim as base-URL and a User-Agent header
    public NominatimGeocodingService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader("User-Agent", "SeismicMonitor/1.0")
                .build();
    }

    // GET-request for Nominatim's reverse geocoding endpoint
    @Override
    public String getLocationName(double latitude, double longitude) {
        NominatimResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reverse")
                        .queryParam("format", "json")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .build())
                .retrieve()
                .bodyToMono(NominatimResponse.class)
                .block();

        if (response != null && response.getDisplayName() != null) {
            return response.getDisplayName();
        }
        return "Unknown location";
    }
}