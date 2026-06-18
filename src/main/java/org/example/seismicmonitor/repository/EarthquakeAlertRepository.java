package org.example.seismicmonitor.repository;

import org.example.seismicmonitor.model.AlertStatus;
import org.example.seismicmonitor.model.EarthquakeAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EarthquakeAlertRepository extends JpaRepository<EarthquakeAlert, Long> {
    List<EarthquakeAlert> findByStatus(AlertStatus status);
}