package com.gtawp.certgen.config;

import com.gtawp.certgen.entity.WebinarSequence;
import com.gtawp.certgen.repository.WebinarSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final WebinarSequenceRepository sequenceRepository;
    private static final String GLOBAL_KEY = "GLOBAL";
    private static final int TARGET_START_COUNTER = 8249;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        log.info("Checking global certificate sequence starting point...");
        
        WebinarSequence sequence = sequenceRepository.findByDateKeyForUpdate(GLOBAL_KEY)
                .orElseGet(() -> {
                    WebinarSequence newSeq = new WebinarSequence();
                    newSeq.setDateKey(GLOBAL_KEY);
                    newSeq.setLastCounter(TARGET_START_COUNTER);
                    log.info("Initializing new global sequence with counter: {}", TARGET_START_COUNTER);
                    return newSeq;
                });

        if (sequence.getLastCounter() < TARGET_START_COUNTER) {
            log.info("Updating existing global certificate counter from {} to {}", sequence.getLastCounter(), TARGET_START_COUNTER);
            sequence.setLastCounter(TARGET_START_COUNTER);
            sequenceRepository.save(sequence);
        } else {
            log.info("Global certificate counter is already at or above target: {}", sequence.getLastCounter());
        }
    }
}
