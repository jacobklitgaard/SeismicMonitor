package org.example.seismicmonitor.service;

import org.example.seismicmonitor.dto.SensorReadingDTO;
import org.example.seismicmonitor.model.*;
import org.example.seismicmonitor.repository.EarthquakeAlertRepository;
import org.example.seismicmonitor.repository.SensorReadingRepository;
import org.example.seismicmonitor.service.estimation.EpicenterEstimator;
import org.example.seismicmonitor.service.estimation.MagnitudeEstimator;
import org.example.seismicmonitor.service.geocoding.GeocodingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EarthquakeAlertServiceTest {

    @Mock
    private EarthquakeAlertRepository alertRepository;
    @Mock
    private EpicenterEstimator epicenterEstimator;
    @Mock
    private MagnitudeEstimator magnitudeEstimator;
    @Mock
    private GeocodingService geocodingService;
    @Mock
    private SensorReadingRepository sensorReadingRepository;

    @InjectMocks
    private EarthquakeAlertService earthquakeAlertService;

    private SensorReadingDTO makeDTO(String readingId, double lat, double lon, double distance) {
        SensorReadingDTO dto = new SensorReadingDTO();
        dto.setReadingId(readingId);
        dto.setSensorId("SEN-001");
        SensorReadingDTO.SensorLocationDTO loc = new SensorReadingDTO.SensorLocationDTO();
        loc.setLatitude(lat);
        loc.setLongitude(lon);
        dto.setSensorLocation(loc);
        dto.setEstimatedDistanceToEpicenterKm(distance);
        dto.setEstimatedMagnitude(3.0);
        dto.setRecordedAt(LocalDateTime.now());
        return dto;
    }

    @Test
    void tryCreateAlertWithThreeReadingsCreatesAlert() {
        List<SensorReadingDTO> dtos = List.of(
                makeDTO("R1", 55.0, 12.0, 10.0),
                makeDTO("R2", 56.0, 12.0, 15.0),
                makeDTO("R3", 55.5, 13.0, 12.0)
        );
        List<SensorReading> readings = List.of(new SensorReading(), new SensorReading(), new SensorReading());

        when(epicenterEstimator.estimate(any())).thenReturn(new Location(55.5, 12.5));
        when(magnitudeEstimator.estimate(any())).thenReturn(3.5);

        earthquakeAlertService.tryCreateAlert(dtos, readings);

        verify(alertRepository).save(any());
    }

    @Test
    void tryCreateAlertWithFewerThanThreeReadingsDoesNotCreateAlert() {
        List<SensorReadingDTO> dtos = List.of(makeDTO("R1", 55.0, 12.0, 10.0));
        List<SensorReading> readings = List.of(new SensorReading());

        earthquakeAlertService.tryCreateAlert(dtos, readings);

        verify(alertRepository, never()).save(any());
    }

    @Test
    void tryCreateAlertWhenCalculationFailsDoesNotCreateAlert() {
        List<SensorReadingDTO> dtos = List.of(
                makeDTO("R1", 55.0, 12.0, 10.0),
                makeDTO("R2", 56.0, 12.0, 15.0),
                makeDTO("R3", 55.5, 13.0, 12.0)
        );
        List<SensorReading> readings = List.of(new SensorReading(), new SensorReading(), new SensorReading());

        when(epicenterEstimator.estimate(any())).thenThrow(new IllegalArgumentException("Fejl"));

        earthquakeAlertService.tryCreateAlert(dtos, readings);

        verify(alertRepository, never()).save(any());
    }
}