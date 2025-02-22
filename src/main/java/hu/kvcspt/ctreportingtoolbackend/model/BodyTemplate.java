package hu.kvcspt.ctreportingtoolbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "body_templates")
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class BodyTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "bodyTemplate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BodyTemplateElement> bodyTemplateElements;
}
