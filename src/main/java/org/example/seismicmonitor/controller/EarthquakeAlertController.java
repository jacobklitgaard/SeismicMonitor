package org.example.seismicmonitor.controller;

import org.example.seismicmonitor.model.AlertStatus;
import org.example.seismicmonitor.model.EarthquakeAlert;
import org.example.seismicmonitor.model.SensorReading;
import org.example.seismicmonitor.service.EarthquakeAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin("*")
public class EarthquakeAlertController {

    private final EarthquakeAlertService alertService;

    public EarthquakeAlertController(EarthquakeAlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<List<EarthquakeAlert>> getAllAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @GetMapping("/active")
    public ResponseEntity<List<EarthquakeAlert>> getActiveAlerts() {
        return ResponseEntity.ok(alertService.getActiveAlerts());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<EarthquakeAlert> updateStatus(@PathVariable Long id,
                                                        @RequestParam AlertStatus status) {
        return ResponseEntity.ok(alertService.updateStatus(id, status));
    }

    @GetMapping("/{id}/readings")
    public ResponseEntity<List<SensorReading>> getReadingsForAlert(@PathVariable Long id) {
        return ResponseEntity.ok(alertService.getReadingsForAlert(id));
    }
}