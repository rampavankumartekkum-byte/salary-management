package com.incubyte.salary.controller;

import com.incubyte.salary.dto.AnalyticsDtos.DashboardResponse;
import com.incubyte.salary.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return analyticsService.getDashboard();
    }
}
