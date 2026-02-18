package com.ayush.jobtracker.service;

import org.springframework.stereotype.Service;

import com.ayush.jobtracker.dto.DashboardResponseDto;
import com.ayush.jobtracker.entity.ApplicationStatus;
import com.ayush.jobtracker.repository.ApplicationRepository;

@Service
public class DashboardService {
    private final ApplicationRepository applicationrepo;
    public DashboardService(ApplicationRepository applicationrepo){
        this.applicationrepo = applicationrepo;
    }
    public DashboardResponseDto toUser(Long id){
        DashboardResponseDto drt = new DashboardResponseDto();
        long total_applications = applicationrepo.countByUserId(id);
        long totalOffered = 0;
        long totalRejected = 0;
        double offerRate = 0.0;
        double rejectionRate = 0.0;
        if(total_applications != 0){
            totalOffered = applicationrepo.countByUserIdAndStatus(id, ApplicationStatus.OFFERED);
            totalRejected = applicationrepo.countByUserIdAndStatus(id, ApplicationStatus.REJECTED);
            offerRate = (totalOffered * 100.0)/total_applications;
            rejectionRate = (totalRejected * 100.0)/total_applications;
        } 
        drt.setTotalApplications(total_applications);
        drt.setTotalOffered(totalOffered);
        drt.setTotalRejected(totalRejected);
        drt.setRejectionRate(rejectionRate);
        drt.setOfferRate(offerRate);
        return drt;

    }
}
