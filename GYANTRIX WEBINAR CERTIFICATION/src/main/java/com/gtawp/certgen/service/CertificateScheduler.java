package com.gtawp.certgen.service;

import com.gtawp.certgen.entity.Certificate;
import com.gtawp.certgen.entity.CertificateStatus;
import com.gtawp.certgen.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CertificateScheduler {

    private final CertificateRepository certificateRepository;
    private final CertificateService certificateService;

    /**
     * Scheduled job to re-process certificates in RETRY status.
     * Runs every 5 minutes (300,000 milliseconds).
     */
    @Scheduled(fixedRate = 300000)
    public void reprocessRetryCertificates() {
        List<Certificate> retryRecords = certificateRepository.findByStatus(CertificateStatus.RETRY);
        
        if (retryRecords.isEmpty()) {
            return;
        }

        log.info("Scheduler picked up {} records in RETRY status for re-processing", retryRecords.size());

        for (Certificate certificate : retryRecords) {
            try {
                log.info("Re-processing certificate ID: {} (Retry count: {})", 
                        certificate.getCertificateId(), certificate.getRetryCount());
                
                // Move back to processing flow
                certificateService.processCertificate(certificate);
                
            } catch (Exception e) {
                log.error("Critical error during scheduled re-processing of certificate {}: {}", 
                        certificate.getCertificateId(), e.getMessage());
            }
        }
    }
}
