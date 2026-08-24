package com.incubyte.salary.service;

import com.incubyte.salary.dto.AnalyticsDtos.DashboardResponse;

public interface AnalyticsService {

    /** Builds the full set of aggregates the HR-manager dashboard needs in one call. */
    DashboardResponse getDashboard();
}
