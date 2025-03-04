package hu.kvcspt.ctreportingtoolbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "body_template_elements")
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class BodyTemplateElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String label;

    @NotNull
    private String name;

    @NotNull
    private String type;
    private boolean duplicate;

    @ElementCollection
    @CollectionTable(name = "body_template_element_options", joinColumns = @JoinColumn(name = "element_id"))
    @Column(name = "option_value")
    private List<String> options;

    @ManyToOne
    @JoinColumn(name = "body_template_id", nullable = false)
    private BodyTemplate bodyTemplate;
}
