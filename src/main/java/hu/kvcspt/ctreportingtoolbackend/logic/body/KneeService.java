package hu.kvcspt.ctreportingtoolbackend.logic.body;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static hu.kvcspt.ctreportingtoolbackend.util.GeneralUtils.transformKeys;

@Service
@AllArgsConstructor
@Log4j2
public class KneeService extends BodyService {
    private SpringTemplateEngine templateEngine;

    public String generateHtml(Map<String, Object> formData) {
        Context context = new Context();
        context.setVariable("procedureInformationText", formData.get("procedureInformationText"));
        context.setVariable("clinicalInformationText", formData.get("clinicalInformationText"));
        context.setVariable("comparisonsText", formData.get("comparisonsText"));
        context.setVariable("findingsText", formData.get("findingsText"));
        context.setVariable("fractureType", transformKeys((Map<String, Object>) formData.get("fractureType")));
        context.setVariable("fractureFeatures", transformKeys((Map<String, Object>) formData.get("fractureFeatures")));
        context.setVariable("softTissueInjuries", transformKeys((Map<String, Object>) formData.get("softTissueInjuries")));
        context.setVariable("tibialPlateauColumns", transformKeys((Map<String, Object>) formData.get("tibialPlateauColumns")));
        context.setVariable("threeColumnClassification", transformKeys((Map<String, Object>) formData.get("threeColumnClassification")));
        context.setVariable("boneFindings", formData.get("boneFindings"));
        context.setVariable("alignment", formData.get("alignment"));
        context.setVariable("jointSpaces", formData.get("jointSpaces"));
        context.setVariable("kneeJointEffusion", formData.get("kneeJointEffusion"));
        context.setVariable("lipohemarthrosis", formData.get("lipohemarthrosis"));
        context.setVariable("extensorMechanism", formData.get("extensorMechanism"));
        context.setVariable("ligaments", formData.get("ligaments"));
        context.setVariable("bakersCyst", formData.get("bakersCyst"));
        context.setVariable("softTissue", formData.get("softTissue"));
        context.setVariable("impression", formData.get("impression"));

        return templateEngine.process("kneeReport", context);
    }

}
