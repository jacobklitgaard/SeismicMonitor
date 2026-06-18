package org.example.seismicmonitor.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class EarthquakeAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlertStatus status;

    private String locationName;
    private double epicenterLatitude;
    private double epicenterLongitude;
    private double estimatedMagnitude;
    private LocalDateTime createdAt;

    // mirrors the relation
    @JsonManagedReference
    @OneToMany(mappedBy = "alert")
    private List<SensorReading> readings;
}