package org.example.seismicmonitor.service.geocoding;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NominatimGeocodingServiceTest {

    @Autowired
    private NominatimGeocodingService geocodingService;

    @Test
    void getLocationNameReturnsLocationForKnownCoordinates() {
        String location = geocodingService.getLocationName(55.6761, 12.5683);
        assertNotNull(location);
        assertNotEquals("Unknown location", location);
    }

    @Test
    void getLocationNameReturnsUnknownForOceanCoordinates() {
        String location = geocodingService.getLocationName(0.0, 0.0);
        assertEquals("Unknown location", location);
    }
}