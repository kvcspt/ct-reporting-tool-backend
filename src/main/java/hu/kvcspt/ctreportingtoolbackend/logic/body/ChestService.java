package hu.kvcspt.ctreportingtoolbackend.logic.body;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static hu.kvcspt.ctreportingtoolbackend.util.GeneralUtils.transformKeys;
@Service
@AllArgsConstructor
public class ChestService extends BodyService {
    private SpringTemplateEngine templateEngine;

    @Override
    public String generateHtml(Map<String, Object> formData) {
        Context context = new Context();
        context.setVariable("procedureInformationText", formData.get("procedureInformationText"));
        context.setVariable("techniqueText", formData.get("techniqueText"));
        context.setVariable("clinicalInformationText", formData.get("clinicalInformationText"));
        context.setVariable("comparisonsText", formData.get("comparisonsText"));
        context.setVariable("presenceOfGroundGlassOpacity", formData.get("presenceOfGroundGlassOpacity"));
        context.setVariable("lateralityOfGroundGlassOpacity", formData.get("lateralityOfGroundGlassOpacity"));
        context.setVariable("locationOfGroundGlassOpacity", formData.get("locationOfGroundGlassOpacity"));
        context.setVariable("predominantDistributionOfGroundGlassOpacity", formData.get("predominantDistributionOfGroundGlassOpacity"));
        context.setVariable("quantityOfGroundGlassOpacity", formData.get("quantityOfGroundGlassOpacity"));
        context.setVariable("patternOfGroundGlassOpacity", transformKeys((Map<String, Object>) formData.get("patternOfGroundGlassOpacity")));
        context.setVariable("morphologyOfGroundGlassOpacity", formData.get("morphologyOfGroundGlassOpacity"));
        context.setVariable("centrilobularNodules", formData.get("centrilobularNodules"));
        context.setVariable("solidNodules", formData.get("solidNodules"));
        context.setVariable("airSpaceConsolidation", formData.get("airSpaceConsolidation"));
        context.setVariable("presenceOfLymphadenopathy", formData.get("presenceOfLymphadenopathy"));
        context.setVariable("locationOfLymphadenopathy", transformKeys((Map<String, Object>) formData.get("locationOfLymphadenopathy")));
        context.setVariable("pleuralEffusionSize", formData.get("pleuralEffusionSize"));
        context.setVariable("presenceOfMucoidImpaction", formData.get("presenceOfMucoidImpaction"));
        context.setVariable("presenceOfBronchialWallThickening", formData.get("presenceOfBronchialWallThickening"));
        context.setVariable("smoothInterlobularSeptalThickeningSeverity", formData.get("smoothInterlobularSeptalThickeningSeverity"));
        context.setVariable("presenceOfPulmonaryCavities", formData.get("presenceOfPulmonaryCavities"));
        context.setVariable("endotrachealTube", formData.get("endotrachealTube"));
        context.setVariable("studyQuality", formData.get("studyQuality"));
        context.setVariable("impressionText", formData.get("impressionText"));
        context.setVariable("covidClassification", formData.get("covidClassification"));

        return templateEngine.process("chestReport", context);
    }
}
