package org.example.seismicmonitor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Sensor {

    @Id
    private String sensorId;

    private double latitude;
    private double longitude;
}