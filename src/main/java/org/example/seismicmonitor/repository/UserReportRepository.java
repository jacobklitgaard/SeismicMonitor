package org.example.seismicmonitor.repository;

import org.example.seismicmonitor.model.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    List<UserReport> findByAlertId(Long alertId);
    int countByAlertId(Long alertId);
}