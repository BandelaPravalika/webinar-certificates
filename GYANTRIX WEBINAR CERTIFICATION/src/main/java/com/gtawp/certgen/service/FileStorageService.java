package com.gtawp.certgen.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileStorageService {

    private final String uploadDir = "generated-certificates";
    private Path rootPath;

    @PostConstruct
    public void init() {
        try {
            rootPath = Paths.get(uploadDir);
            if (!Files.exists(rootPath)) {
                Files.createDirectories(rootPath);
                log.info("Created directory: {}", rootPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Could not initialize storage directory", e);
            throw new RuntimeException("Could not create storage directory", e);
        }
    }

    public String saveCertificate(String certificateId, byte[] content) {
        String filename = "certificate_" + certificateId + ".pdf";
        try {
            Path targetPath = rootPath.resolve(filename);
            Files.write(targetPath, content);
            log.info("Saved certificate to: {}", targetPath.toAbsolutePath());
            return targetPath.toString();
        } catch (IOException e) {
            log.error("Failed to store certificate {}", filename, e);
            throw new RuntimeException("Failed to store certificate", e);
        }
    }

    public byte[] getFile(String storagePath) {
        try {
            return Files.readAllBytes(Paths.get(storagePath));
        } catch (IOException e) {
            log.error("Failed to read certificate from path: {}", storagePath, e);
            throw new RuntimeException("Certificate file not found or unreadable", e);
        }
    }
}
