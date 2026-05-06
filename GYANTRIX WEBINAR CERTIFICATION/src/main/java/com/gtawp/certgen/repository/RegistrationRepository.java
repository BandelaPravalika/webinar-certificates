package com.gtawp.certgen.repository;

import com.gtawp.certgen.entity.Registration;
import com.gtawp.certgen.entity.Webinar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByEmailAndWebinar(String email, Webinar webinar);
    Optional<Registration> findByPhoneAndWebinar(String phone, Webinar webinar);
}
