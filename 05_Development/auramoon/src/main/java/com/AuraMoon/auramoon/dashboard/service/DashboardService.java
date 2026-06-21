package com.AuraMoon.auramoon.dashboard.service;

import com.AuraMoon.auramoon.dashboard.dto.RevenueDashboardDTO;
import java.time.LocalDate;

public interface DashboardService {
    RevenueDashboardDTO getDashboardData(LocalDate startDate, LocalDate endDate, String category);
}
