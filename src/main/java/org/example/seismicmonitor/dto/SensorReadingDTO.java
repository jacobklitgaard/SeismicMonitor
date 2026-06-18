package org.example.seismicmonitor.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class SensorReadingDTO {

    private String readingId;
    private String sensorId;
    private SensorLocationDTO sensorLocation;
    private double estimatedDistanceToEpicenterKm;
    private double estimatedMagnitude;
    private LocalDateTime recordedAt;

    // nested class
    @Getter @Setter @NoArgsConstructor
    public static class SensorLocationDTO {
        private double latitude;
        private double longitude;
    }
}