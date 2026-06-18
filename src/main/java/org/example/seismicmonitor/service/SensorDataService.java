package org.example.seismicmonitor.service;

import org.example.seismicmonitor.dto.SensorReadingDTO;
import org.example.seismicmonitor.model.Sensor;
import org.example.seismicmonitor.model.SensorReading;
import org.example.seismicmonitor.repository.SensorReadingRepository;
import org.example.seismicmonitor.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SensorDataService {

    private final SensorRepository sensorRepository;
    private final SensorReadingRepository sensorReadingRepository;
    private final EarthquakeAlertService earthquakeAlertService;

    public SensorDataService(SensorRepository sensorRepository,
                             SensorReadingRepository sensorReadingRepository,
                             EarthquakeAlertService earthquakeAlertService) {
        this.sensorRepository = sensorRepository;
        this.sensorReadingRepository = sensorReadingRepository;
        this.earthquakeAlertService = earthquakeAlertService;
    }

    public void processReadings(List<SensorReadingDTO> dtos) {
        List<SensorReading> savedReadings = new ArrayList<>();

        for (SensorReadingDTO dto : dtos) {
            Sensor sensor = sensorRepository.findById(dto.getSensorId())
                    .orElseGet(() -> {
                        Sensor s = new Sensor();
                        s.setSensorId(dto.getSensorId());
                        s.setLatitude(dto.getSensorLocation().getLatitude());
                        s.setLongitude(dto.getSensorLocation().getLongitude());
                        return sensorRepository.save(s);
                    });

            SensorReading reading = new SensorReading();
            reading.setReadingId(dto.getReadingId());
            reading.setSensor(sensor);
            reading.setEstimatedDistanceToEpicenterKm(dto.getEstimatedDistanceToEpicenterKm());
            reading.setEstimatedMagnitude(dto.getEstimatedMagnitude());
            reading.setRecordedAt(dto.getRecordedAt());
            savedReadings.add(sensorReadingRepository.save(reading));
        }

        earthquakeAlertService.tryCreateAlert(dtos, savedReadings);
    }

    public List<SensorReading> getAllReadings() {
        return sensorReadingRepository.findAll();
    }
}