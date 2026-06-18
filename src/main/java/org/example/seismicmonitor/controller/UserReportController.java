package org.example.seismicmonitor.controller;

import org.example.seismicmonitor.model.UserReport;
import org.example.seismicmonitor.service.UserReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alerts/{alertId}/reports")
@CrossOrigin("*")
public class UserReportController {

    private final UserReportService userReportService;

    public UserReportController(UserReportService userReportService) {
        this.userReportService = userReportService;
    }

    @PostMapping
    public ResponseEntity<UserReport> createReport(@PathVariable Long alertId,
                                                   @RequestParam int intensity) {
        return ResponseEntity.ok(userReportService.createReport(alertId, intensity));
    }

    @GetMapping
    public ResponseEntity<List<UserReport>> getReports(@PathVariable Long alertId) {
        return ResponseEntity.ok(userReportService.getReportsForAlert(alertId));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCount(@PathVariable Long alertId) {
        return ResponseEntity.ok(userReportService.countReportsForAlert(alertId));
    }
}