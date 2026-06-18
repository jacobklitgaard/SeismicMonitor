package org.example.seismicmonitor.repository;

import org.example.seismicmonitor.model.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    List<UserReport> findByAlertId(Long alertId);
    int countByAlertId(Long alertId);
    boolean existsByAlertIdAndReportedBy(Long alertId, String reportedBy);

    @Query("SELECT AVG(r.intensity) FROM UserReport r WHERE r.alert.id = :alertId")
    Double averageIntensityByAlertId(@Param("alertId") Long alertId);
}