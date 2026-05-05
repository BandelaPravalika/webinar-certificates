package com.gtawp.certgen.repository;

import com.gtawp.certgen.entity.Webinar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebinarRepository extends JpaRepository<Webinar, Long> {
}
