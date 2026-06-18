package org.example.seismicmonitor.service;

import org.example.seismicmonitor.dto.SensorReadingDTO;
import org.example.seismicmonitor.model.Sensor;
import org.example.seismicmonitor.repository.SensorReadingRepository;
import org.example.seismicmonitor.repository.SensorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorDataServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private SensorReadingRepository sensorReadingRepository;

    @Mock
    private EarthquakeAlertService earthquakeAlertService;

    @InjectMocks
    private SensorDataService sensorDataService;

    private SensorReadingDTO makeDTO(String readingId, String sensorId) {
        SensorReadingDTO dto = new SensorReadingDTO();
        dto.setReadingId(readingId);
        dto.setSensorId(sensorId);
        SensorReadingDTO.SensorLocationDTO loc = new SensorReadingDTO.SensorLocationDTO();
        loc.setLatitude(55.0);
        loc.setLongitude(12.0);
        dto.setSensorLocation(loc);
        dto.setEstimatedDistanceToEpicenterKm(10.0);
        dto.setEstimatedMagnitude(3.0);
        dto.setRecordedAt(LocalDateTime.now());
        return dto;
    }

    @Test
    void processReadingsSavesSensorAndReadings() {
        SensorReadingDTO dto = makeDTO("READ-001", "SEN-001");
        when(sensorRepository.findById("SEN-001")).thenReturn(Optional.empty());
        when(sensorRepository.save(any())).thenReturn(new Sensor());

        sensorDataService.processReadings(List.of(dto));

        verify(sensorRepository).save(any());
        verify(sensorReadingRepository).save(any());
    }

    @Test
    void processReadingsReusesExistingSensor() {
        SensorReadingDTO dto = makeDTO("READ-002", "SEN-001");
        when(sensorRepository.findById("SEN-001")).thenReturn(Optional.of(new Sensor()));

        sensorDataService.processReadings(List.of(dto));

        verify(sensorRepository, never()).save(any());
        verify(sensorReadingRepository).save(any());
    }
}