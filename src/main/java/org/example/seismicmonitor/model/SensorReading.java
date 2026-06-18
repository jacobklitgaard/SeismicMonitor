package org.example.seismicmonitor.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
public class SensorReading {

    @Id
    private String readingId;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToOne
    @JoinColumn(name = "alert_id")
    @JsonBackReference
    private EarthquakeAlert alert;

    private double estimatedDistanceToEpicenterKm;
    private double estimatedMagnitude;
    private LocalDateTime recordedAt;
}