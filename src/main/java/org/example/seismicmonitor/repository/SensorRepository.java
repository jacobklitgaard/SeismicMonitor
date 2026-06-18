package org.example.seismicmonitor.repository;

import org.example.seismicmonitor.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, String> {
}