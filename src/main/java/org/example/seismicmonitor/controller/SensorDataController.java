package org.example.seismicmonitor.controller;

import org.example.seismicmonitor.dto.SensorReadingDTO;
import org.example.seismicmonitor.model.SensorReading;
import org.example.seismicmonitor.service.SensorDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @PostMapping("/sensor-data")
    public ResponseEntity<Void> receiveSensorData(@RequestBody List<SensorReadingDTO> readings) {
        sensorDataService.processReadings(readings);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/sensor-readings")
    public ResponseEntity<List<SensorReading>> getAllReadings() {
        return ResponseEntity.ok(sensorDataService.getAllReadings());
    }
}