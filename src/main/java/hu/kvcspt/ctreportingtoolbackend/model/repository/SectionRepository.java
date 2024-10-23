package hu.kvcspt.ctreportingtoolbackend.model.repository;

import hu.kvcspt.ctreportingtoolbackend.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
}
