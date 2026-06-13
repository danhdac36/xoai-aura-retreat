package com.AuraMoon.auramoon.dashboard.service.impl;

import com.AuraMoon.auramoon.dashboard.dto.RevenueDashboardDTO;
import com.AuraMoon.auramoon.dashboard.service.DashboardService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class DashboardServiceImpl implements DashboardService {
    
    @Override
    public RevenueDashboardDTO getDashboardData(LocalDate startDate, LocalDate endDate, String category) {
        // TDD Skeleton - sẽ viết logic sau khi test chạy (RED phase)
        return null;
    }
}
