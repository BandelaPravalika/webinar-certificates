package com.gtawp.certgen.util;

import com.gtawp.certgen.entity.WebinarSequence;
import com.gtawp.certgen.repository.WebinarSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
public class CertificateIdGenerator {

    private static final String PREFIX = "GTAWP";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMM");

    // Fixed global key — counter NEVER resets across days
    private static final String GLOBAL_KEY = "GLOBAL";

    private final WebinarSequenceRepository sequenceRepository;

    @Transactional
    public synchronized String generateCertificateId() {
        // Always use the same GLOBAL key so counter is continuous
        WebinarSequence sequence = sequenceRepository.findByDateKeyForUpdate(GLOBAL_KEY)
                .orElseGet(() -> {
                    WebinarSequence newSeq = new WebinarSequence();
                    newSeq.setDateKey(GLOBAL_KEY);
                    newSeq.setLastCounter(0);
                    return newSeq;
                });

        int nextCounter = sequence.getLastCounter() + 1;
        sequence.setLastCounter(nextCounter);
        sequenceRepository.save(sequence);

        // Date is today's date (just for display in the ID), counter is global
        String dateKey = LocalDate.now().format(DATE_FORMATTER);
        return String.format("%s%s%04d", PREFIX, dateKey, nextCounter);
    }
}
