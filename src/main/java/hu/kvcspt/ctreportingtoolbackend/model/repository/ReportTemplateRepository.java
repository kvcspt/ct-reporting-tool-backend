package hu.kvcspt.ctreportingtoolbackend.model.repository;

import hu.kvcspt.ctreportingtoolbackend.model.ReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {
}
