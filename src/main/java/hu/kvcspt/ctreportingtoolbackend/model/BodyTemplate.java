package hu.kvcspt.ctreportingtoolbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "body_templates")
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
public class BodyTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;
    @Column(unique = true)
    @Setter
    private String title;
    @OneToMany(mappedBy = "bodyTemplate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BodyTemplateElement> bodyTemplateElements = null;

    public void setBodyTemplateElements(List<BodyTemplateElement> bodyTemplateElements) {
        if(this.bodyTemplateElements == null){
            this.bodyTemplateElements = bodyTemplateElements;

        } else {
            this.bodyTemplateElements.retainAll(bodyTemplateElements);
            this.bodyTemplateElements.addAll(bodyTemplateElements);
        }
    }
}
