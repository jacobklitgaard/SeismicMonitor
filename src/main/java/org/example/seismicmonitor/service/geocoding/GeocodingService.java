package org.example.seismicmonitor.service.geocoding;

public interface GeocodingService {
    String getLocationName(double latitude, double longitude);
}