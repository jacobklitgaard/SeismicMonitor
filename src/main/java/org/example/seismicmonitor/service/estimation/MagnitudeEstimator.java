package org.example.seismicmonitor.service.estimation;

import org.example.seismicmonitor.dto.SensorReadingDTO;
import java.util.List;

public interface MagnitudeEstimator {
    double estimate(List<SensorReadingDTO> readings);
}