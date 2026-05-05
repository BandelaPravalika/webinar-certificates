package com.gtawp.certgen.repository;

import com.gtawp.certgen.entity.WebinarSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface WebinarSequenceRepository extends JpaRepository<WebinarSequence, String> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ws FROM WebinarSequence ws WHERE ws.dateKey = :dateKey")
    Optional<WebinarSequence> findByDateKeyForUpdate(String dateKey);
}
