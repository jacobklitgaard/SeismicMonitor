package org.example.seismicmonitor.service;

import org.example.seismicmonitor.dto.SensorReadingDTO;
import org.example.seismicmonitor.model.*;
import org.example.seismicmonitor.repository.EarthquakeAlertRepository;
import org.example.seismicmonitor.service.estimation.EpicenterEstimator;
import org.example.seismicmonitor.service.estimation.MagnitudeEstimator;
import org.example.seismicmonitor.service.geocoding.GeocodingService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EarthquakeAlertService {

    private final EarthquakeAlertRepository alertRepository;
    private final EpicenterEstimator epicenterEstimator;
    private final MagnitudeEstimator magnitudeEstimator;
    private final GeocodingService geocodingService;

    public EarthquakeAlertService(EarthquakeAlertRepository alertRepository,
                                  EpicenterEstimator epicenterEstimator,
                                  MagnitudeEstimator magnitudeEstimator,
                                  GeocodingService geocodingService) {
        this.alertRepository = alertRepository;
        this.epicenterEstimator = epicenterEstimator;
        this.magnitudeEstimator = magnitudeEstimator;
        this.geocodingService = geocodingService;
    }

    public void tryCreateAlert(List<SensorReadingDTO> readings, List<SensorReading> savedReadings) {
        if (readings.size() != 3) return;

        List<LocationWithDistance> measurements = readings.stream()
                .map(r -> new LocationWithDistance(
                        new Location(r.getSensorLocation().getLatitude(),
                                r.getSensorLocation().getLongitude()),
                        r.getEstimatedDistanceToEpicenterKm()))
                .toList();

        try {
            Location epicenter = epicenterEstimator.estimate(measurements);
            double magnitude = magnitudeEstimator.estimate(readings);
            String locationName = geocodingService.getLocationName(epicenter.latitude(), epicenter.longitude());

            EarthquakeAlert alert = new EarthquakeAlert();
            alert.setStatus(AlertStatus.UNDER_REVIEW);
            alert.setEpicenterLatitude(epicenter.latitude());
            alert.setEpicenterLongitude(epicenter.longitude());
            alert.setEstimatedMagnitude(magnitude);
            alert.setCreatedAt(LocalDateTime.now());
            alert.setLocationName(locationName);
            alert.setReadings(savedReadings);
            alertRepository.save(alert);

        } catch (IllegalArgumentException e) {
            // Beregning fejlede – gem målinger men opret ikke varsel
        }
    }

    public List<EarthquakeAlert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public List<EarthquakeAlert> getActiveAlerts() {
        return alertRepository.findByStatus(AlertStatus.ACTIVE);
    }

    public List<SensorReading> getReadingsForAlert(Long id) {
        EarthquakeAlert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        return alert.getReadings();
    }

    public EarthquakeAlert updateStatus(Long id, AlertStatus newStatus) {
        EarthquakeAlert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        validateTransition(alert.getStatus(), newStatus);
        alert.setStatus(newStatus);
        return alertRepository.save(alert);
    }

    private void validateTransition(AlertStatus current, AlertStatus next) {
        boolean valid = switch (current) {
            case UNDER_REVIEW -> next == AlertStatus.ACTIVE || next == AlertStatus.FALSE_ALARM;
            case ACTIVE -> next == AlertStatus.NOT_ACTIVE;
            case FALSE_ALARM, NOT_ACTIVE -> false;
        };
        if (!valid) {
            throw new IllegalStateException("Invalid transition: " + current + " -> " + next);
        }
    }

}