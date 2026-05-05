package com.gtawp.certgen.util;

import com.gtawp.certgen.dto.CertificateRequest;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CsvParser {

    public List<CertificateRequest> parseCsv(MultipartFile file) {
        List<CertificateRequest> requests = new ArrayList<>();
        
        log.info("Processing upload. Filename: {}, ContentType: {}, Size: {} bytes", 
                file.getOriginalFilename(), file.getContentType(), file.getSize());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Read lines manually first to detect the best delimiter
            List<String> lines = reader.lines().collect(Collectors.toList());
            if (lines.isEmpty()) return requests;

            char delimiter = detectDelimiter(lines.get(0));
            log.info("Detected delimiter: '{}'", delimiter == '\t' ? "\\t" : delimiter);

            boolean firstLine = true;
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                // Split by detected delimiter
                String[] record = splitLine(line, delimiter);

                if (firstLine) {
                    firstLine = false;
                    if (isHeader(record)) {
                        log.debug("Skipping header line: {}", line);
                        continue;
                    }
                }

                if (record.length >= 2) { // Allow name and email at minimum
                    CertificateRequest request = new CertificateRequest();
                    request.setStudentName(record[0].trim());
                    request.setEmail(record[1].trim());
                    
                    // Webinar name might be in index 2 or mapped to a default
                    if (record.length >= 3) {
                        request.setWebinarName(record[2].trim());
                    } else if (record.length == 2 && isEmail(record[1])) {
                        request.setWebinarName("Webinar Participant"); // Default fallback
                    }

                    if (!request.getStudentName().isEmpty() && isEmail(request.getEmail())) {
                        requests.add(request);
                    }
                }
            }
        } catch (Exception e) {
            log.error("CSV parsing failed for file {}: {}", file.getOriginalFilename(), e.getMessage());
            throw new RuntimeException("Could not parse file: " + e.getMessage(), e);
        }
        
        log.info("Successfully parsed {} valid records from {}", requests.size(), file.getOriginalFilename());
        return requests;
    }

    private char detectDelimiter(String line) {
        if (line.contains("\t")) return '\t';
        if (line.contains(";")) return ';';
        return ','; // Default
    }

    private String[] splitLine(String line, char delimiter) {
        if (delimiter == '\t') return line.split("\t");
        if (delimiter == ';') return line.split(";");
        // For commas, use a simple split or more complex regex if quotes are needed
        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    }

    private boolean isEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    private boolean isHeader(String[] record) {
        if (record == null || record.length == 0) return false;
        String first = record[0].toLowerCase();
        String second = record.length > 1 ? record[1].toLowerCase() : "";
        return first.contains("name") || second.contains("email") || first.contains("webinar");
    }
}
