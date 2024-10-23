package hu.kvcspt.ctreportingtoolbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Table(name = "sections")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @ElementCollection
    @CollectionTable(name = "section_field_content", joinColumns = @JoinColumn(name = "section_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    private Map<String, String> fieldContent;

    @ManyToOne
    @JoinColumn(name = "section_template_id")
    private SectionTemplate sectionTemplate;

    @ManyToOne
    @JoinColumn(name = "report_id")
    @JsonIgnore
    private Report report;

    public void setFieldContent(String field, String value) {
        if (sectionTemplate == null) {
            return;
        }
        if (sectionTemplate.getRequiredFields().contains(field)) {
            fieldContent.put(field, value);
        } else {
            throw new IllegalArgumentException("Field " + field + " is not required for this section.");
        }
    }
    public String getContent() {
        if (sectionTemplate == null) {
            return "No section template available.";
        }
        StringBuilder contentBuilder = new StringBuilder();
        for (String field : sectionTemplate.getRequiredFields()) {
            contentBuilder.append(field)
                    .append(": ")
                    .append(fieldContent.getOrDefault(field, "Not Provided"))
                    .append("\n");
        }
        return contentBuilder.toString();
    }
}
