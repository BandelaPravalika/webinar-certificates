package com.gtawp.certgen.controller;

import com.gtawp.certgen.dto.RegistrationRequest;
import com.gtawp.certgen.dto.CertificateRequest;
import com.gtawp.certgen.entity.Registration;
import com.gtawp.certgen.entity.Webinar;
import com.gtawp.certgen.repository.RegistrationRepository;
import com.gtawp.certgen.repository.WebinarRepository;
import com.gtawp.certgen.service.CertificateService;
import com.gtawp.certgen.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/form")
@Tag(name = "Student Registration", description = "Public endpoints for webinar registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationRepository registrationRepository;
    private final WebinarRepository webinarRepository;
    private final CertificateService certificateService;

    @PostMapping("/submit")
    @Operation(summary = "Submit a student registration form")
    public ResponseEntity<?> submitRegistration(@RequestBody RegistrationRequest request) {
        // 1. Validation: Required Fields
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Full Name is required"));
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
        }
        if (!request.isConfirmation()) {
            return ResponseEntity.badRequest().body(Map.of("error", "You must confirm your details"));
        }

        // 2. Validate Webinar
        Webinar webinar = webinarRepository.findById(request.getWebinarId())
                .orElseThrow(() -> new ResourceNotFoundException("Webinar not found with id: " + request.getWebinarId()));

        // 3. Expiry Check: If now > expiry_time
        if (java.time.LocalDateTime.now().isAfter(webinar.getExpiryTime())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Form link expired"));
        }

        // 4. Check for Duplicate Registration
        if (registrationRepository.findByEmailAndWebinar(request.getEmail(), webinar).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Already submitted"));
        }

        // 5. Save Registration
        Registration registration = new Registration();
        registration.setWebinar(webinar);
        registration.setFullName(request.getFullName());
        registration.setEmail(request.getEmail());
        registration.setPhone(request.getPhone());
        registration.setCollegeName(request.getCollegeName());
        registration.setDepartment(request.getDepartment());
        registration.setYearOfStudy(request.getYearOfStudy());
        registration.setReferralSource(request.getReferralSource());
        registration.setCrName(request.getCrName());
        registration.setCrPhone(request.getCrPhone());
        registration.setFriendName(request.getFriendName());
        registration.setFriendPhone(request.getFriendPhone());
        registration.setFriendCollege(request.getFriendCollege());
        registrationRepository.save(registration);

        // 6. Trigger Asynchronous Certificate Generation
        CertificateRequest certRequest = new CertificateRequest();
        certRequest.setStudentName(request.getFullName());
        certRequest.setEmail(request.getEmail());
        certRequest.setWebinarName(webinar.getTitle());
        
        certificateService.processSingleRequest(certRequest, registration);

        return ResponseEntity.ok(Map.of("message", "Submission successful. Certificate sent to your email."));
    }
}
