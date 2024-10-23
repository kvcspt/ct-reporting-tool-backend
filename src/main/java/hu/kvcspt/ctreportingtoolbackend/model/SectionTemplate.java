package hu.kvcspt.ctreportingtoolbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "section_templates")
public class SectionTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;
    private String description;

    @ElementCollection
    private List<String> requiredFields;
    @OneToMany(mappedBy = "sectionTemplate", cascade = CascadeType.ALL)
    private List<Section> sections;
    @ManyToOne
    @JoinColumn(name = "report_template_id")
    private ReportTemplate reportTemplate;
}
