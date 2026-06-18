package org.example.seismicmonitor.service.estimation;

import org.example.seismicmonitor.model.Location;
import org.example.seismicmonitor.model.LocationWithDistance;
import java.util.List;

public interface EpicenterEstimator {
    Location estimate(List<LocationWithDistance> measurements);
}