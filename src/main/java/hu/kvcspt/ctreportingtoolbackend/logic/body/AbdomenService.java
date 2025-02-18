package hu.kvcspt.ctreportingtoolbackend.logic.body;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@AllArgsConstructor
public class AbdomenService extends BodyService {
    private SpringTemplateEngine templateEngine;

    public String generateHtml(Map<String, Object> formData) {
        Context context = new Context();
        context.setVariable("procedureInformationText", formData.get("procedureInformationText"));
        context.setVariable("techniqueText", formData.get("techniqueText"));
        context.setVariable("clinicalInformationText", formData.get("clinicalInformationText"));
        context.setVariable("comparisonsText", formData.get("comparisonsText"));
        context.setVariable("findingsText", formData.get("findingsText"));
        context.setVariable("distalEsophagus", formData.get("distalEsophagus"));
        context.setVariable("heartVessels", formData.get("heartVessels"));
        context.setVariable("other", formData.get("other"));
        context.setVariable("briefDescription", formData.get("briefDescription"));
        context.setVariable("location", formData.get("location"));
        context.setVariable("sizeInOrthogonalDimensionsInCm", formData.get("sizeInOrthogonalDimensionsInCm"));
        context.setVariable("composition", formData.get("composition"));
        context.setVariable("bosniakClassification", formData.get("bosniakClassification"));
        context.setVariable("margins", formData.get("margins"));
        context.setVariable("enhancement", formData.get("enhancement"));
        context.setVariable("nonenhancedPhaseAttenuation", formData.get("nonenhancedPhaseAttenuation"));
        context.setVariable("corticomedullaryPhaseAttenuation", formData.get("corticomedullaryPhaseAttenuation"));
        context.setVariable("nephrographicPhaseAttenuation", formData.get("nephrographicPhaseAttenuation"));
        context.setVariable("radius", formData.get("radius"));
        context.setVariable("exophyticExtent", formData.get("exophyticExtent"));
        context.setVariable("nearnessToSinus", formData.get("nearnessToSinus"));
        context.setVariable("polarLocation", formData.get("polarLocation"));
        context.setVariable("axialLocation", formData.get("axialLocation"));
        context.setVariable("hilarExtent", formData.get("hilarExtent"));
        context.setVariable("nephrometryScore", formData.get("nephrometryScore"));
        context.setVariable("invadesPerirenalFat", formData.get("invadesPerirenalFat"));
        context.setVariable("contactsPerirenalFascia", formData.get("contactsPerirenalFascia"));
        context.setVariable("invadesThroughPerirenalFascia", formData.get("invadesThroughPerirenalFascia"));
        context.setVariable("invadesCentralSinusFat", formData.get("invadesCentralSinusFat"));
        context.setVariable("invadesCollectingSystem", formData.get("invadesCollectingSystem"));
        context.setVariable("invadesIpsilateralAdrenal", formData.get("invadesIpsilateralAdrenal"));
        context.setVariable("invadesAdjacentOrgans", formData.get("invadesAdjacentOrgans"));
        context.setVariable("renalArtery", formData.get("renalArtery"));
        context.setVariable("renalVeinAnatomy", formData.get("renalVeinAnatomy"));
        context.setVariable("renalArteryStenosis", formData.get("renalArteryStenosis"));
        context.setVariable("renalVeinThrombus", formData.get("renalVeinThrombus"));
        context.setVariable("renalVeinThrombusText", formData.get("renalVeinThrombusText"));
        context.setVariable("ivcThrombus", formData.get("ivcThrombus"));
        context.setVariable("ivcThrombusText", formData.get("ivcThrombusText"));
        context.setVariable("otherRenalFindings", formData.get("otherRenalFindings"));
        context.setVariable("directInvasionsByRenalMass", formData.get("directInvasionsByRenalMass"));
        context.setVariable("adrenalNodule", formData.get("adrenalNodule"));
        context.setVariable("otherFindings", formData.get("otherFindings"));
        context.setVariable("retroperitonealAndPararenalLymphNodes", formData.get("retroperitonealAndPararenalLymphNodes"));
        context.setVariable("otherLymphNodes", formData.get("otherLymphNodes"));
        context.setVariable("liver", formData.get("liver"));
        context.setVariable("billiaryTract", formData.get("billiaryTract"));
        context.setVariable("gallbladder", formData.get("gallbladder"));
        context.setVariable("pancreas", formData.get("pancreas"));
        context.setVariable("spleen", formData.get("spleen"));
        context.setVariable("stomachAndAmallBowel", formData.get("stomachAndAmallBowel"));
        context.setVariable("colonAndAppendix", formData.get("colonAndAppendix"));
        context.setVariable("peritoneumAndMesentery", formData.get("peritoneumAndMesentery"));
        context.setVariable("retroperitoneum", formData.get("retroperitoneum"));
        context.setVariable("otherVessels", formData.get("otherVessels"));
        context.setVariable("bodyWall", formData.get("bodyWall"));
        context.setVariable("musculoskeletal", formData.get("musculoskeletal"));
        context.setVariable("tumorT", formData.get("tumorT"));
        context.setVariable("nodeN", formData.get("nodeN"));
        context.setVariable("metastasisM", formData.get("metastasisM"));
        context.setVariable("impression", formData.get("impression"));

        return templateEngine.process("abdomenReport", context);
    }
}
