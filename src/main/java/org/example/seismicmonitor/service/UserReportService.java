package org.example.seismicmonitor.service;

import org.example.seismicmonitor.model.AlertStatus;
import org.example.seismicmonitor.model.EarthquakeAlert;
import org.example.seismicmonitor.model.UserReport;
import org.example.seismicmonitor.repository.EarthquakeAlertRepository;
import org.example.seismicmonitor.repository.UserReportRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserReportService {

    private final UserReportRepository userReportRepository;
    private final EarthquakeAlertRepository alertRepository;

    public UserReportService(UserReportRepository userReportRepository,
                             EarthquakeAlertRepository alertRepository) {
        this.userReportRepository = userReportRepository;
        this.alertRepository = alertRepository;
    }

    public UserReport createReport(Long alertId, int intensity, String username) {

        // find alert
        EarthquakeAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        // check if active
        if (alert.getStatus() != AlertStatus.ACTIVE) {
            throw new IllegalStateException("Cannot report on a non-active alert");
        }

        // check if already reported on
        if (userReportRepository.existsByAlertIdAndReportedBy(alertId, username)) {
            throw new IllegalStateException("You have already reported this alert");
        }

        // build report
        UserReport report = new UserReport();
        report.setAlert(alert);
        report.setIntensity(intensity);
        report.setReportedBy(username);
        report.setCreatedAt(LocalDateTime.now());
        return userReportRepository.save(report);
    }

    public List<UserReport> getReportsForAlert(Long alertId) {
        return userReportRepository.findByAlertId(alertId);
    }

    // for admin and user
    public int countReportsForAlert(Long alertId) {
        return userReportRepository.countByAlertId(alertId);
    }
}