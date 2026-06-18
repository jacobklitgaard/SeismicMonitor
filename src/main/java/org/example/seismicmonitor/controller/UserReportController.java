package org.example.seismicmonitor.controller;

import org.example.seismicmonitor.model.UserReport;
import org.example.seismicmonitor.repository.UserReportRepository;
import org.example.seismicmonitor.service.UserReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts/{alertId}/reports")
@CrossOrigin("*")
public class UserReportController {

    private final UserReportService userReportService;
    private final UserReportRepository userReportRepository;

    public UserReportController(UserReportService userReportService, UserReportRepository userReportRepository) {
        this.userReportService = userReportService;
        this.userReportRepository = userReportRepository;
    }

    @PostMapping
    public ResponseEntity<?> createReport(@PathVariable Long alertId,
                                          @RequestParam int intensity,
                                          Authentication authentication) {
        try {
            return ResponseEntity.ok(userReportService.createReport(alertId, intensity, authentication.getName()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<UserReport>> getReports(@PathVariable Long alertId) {
        return ResponseEntity.ok(userReportService.getReportsForAlert(alertId));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCount(@PathVariable Long alertId) {
        return ResponseEntity.ok(userReportService.countReportsForAlert(alertId));
    }

    @GetMapping("/average-intensity")
    public ResponseEntity<Double> getAverageIntensity(@PathVariable Long alertId) {
        Double avg = userReportRepository.averageIntensityByAlertId(alertId);
        return ResponseEntity.ok(avg != null ? avg : 0.0);
    }
}