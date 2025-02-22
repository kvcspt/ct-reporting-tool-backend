package hu.kvcspt.ctreportingtoolbackend.model.repository;

import hu.kvcspt.ctreportingtoolbackend.model.BodyTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BodyTemplateRepository extends JpaRepository<BodyTemplate, Long> {
    Optional<BodyTemplate> findByTitle(String title);
    boolean existsByTitle(String title);
    void deleteByTitle(String title);

}
