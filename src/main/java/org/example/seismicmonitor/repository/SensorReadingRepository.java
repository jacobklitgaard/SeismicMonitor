package org.example.seismicmonitor.repository;

import org.example.seismicmonitor.model.SensorReading;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorReadingRepository extends JpaRepository<SensorReading, String> {
}