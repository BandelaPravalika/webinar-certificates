package com.gtawp.certgen.repository;

import com.gtawp.certgen.entity.Certificate;
import com.gtawp.certgen.entity.CertificateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByStatus(CertificateStatus status);
    
    Optional<Certificate> findByCertificateId(String certificateId);
    
    @Query("SELECT COUNT(c) FROM Certificate c WHERE c.status = :status")
    Long countByStatus(CertificateStatus status);
    
    List<Certificate> findByStatusAndRetryCountLessThan(CertificateStatus status, Integer maxRetries);
}
