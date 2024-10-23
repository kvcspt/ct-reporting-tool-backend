package hu.kvcspt.ctreportingtoolbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hl7.fhir.r5.model.ElementDefinition;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.StructureDefinition;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "reportTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SectionTemplate> sectionTemplates = new ArrayList<>();

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;
    public StructureDefinition toFhirStructureDefinition() {
        StructureDefinition structureDefinition = new StructureDefinition();

        structureDefinition.setId(String.valueOf(id));
        structureDefinition.setName(name);
        structureDefinition.setStatus(Enumerations.PublicationStatus.ACTIVE);
        structureDefinition.setKind(StructureDefinition.StructureDefinitionKind.LOGICAL);

        List<ElementDefinition> elementDefinitions = getElementDefinitions();

        structureDefinition.setSnapshot(new StructureDefinition.StructureDefinitionSnapshotComponent());
        structureDefinition.getSnapshot().setElement(elementDefinitions);

        return structureDefinition;
    }

    private List<ElementDefinition> getElementDefinitions() {
        List<ElementDefinition> elementDefinitions = new ArrayList<>();

        for (SectionTemplate sectionTemplate : sectionTemplates) {
            ElementDefinition elementDefinition = new ElementDefinition();
            elementDefinition.setPath(sectionTemplate.getName());
            elementDefinition.setShort(sectionTemplate.getDescription());

            elementDefinitions.add(elementDefinition);
        }
        return elementDefinitions;
    }
}
