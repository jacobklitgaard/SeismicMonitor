package org.example.seismicmonitor.service.estimation;

import org.example.seismicmonitor.dto.SensorReadingDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AverageMagnitudeEstimator implements MagnitudeEstimator {

    @Override
    public double estimate(List<SensorReadingDTO> readings) {
        return readings.stream()
                .mapToDouble(SensorReadingDTO::getEstimatedMagnitude)
                .average()
                .orElse(0.0);
    }
}