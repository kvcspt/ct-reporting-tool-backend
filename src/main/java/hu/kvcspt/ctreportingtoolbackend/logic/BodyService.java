package hu.kvcspt.ctreportingtoolbackend.logic;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import hu.kvcspt.ctreportingtoolbackend.dto.BodyReportDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.BodyTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.mapper.BodyTemplateElementMapper;
import hu.kvcspt.ctreportingtoolbackend.mapper.BodyTemplateMapper;
import hu.kvcspt.ctreportingtoolbackend.model.BodyTemplate;
import hu.kvcspt.ctreportingtoolbackend.model.repository.BodyTemplateElementRepository;
import hu.kvcspt.ctreportingtoolbackend.model.repository.BodyTemplateRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BodyService {
    private SpringTemplateEngine templateEngine;
    private BodyTemplateRepository bodyTemplateRepository;
    private BodyTemplateElementRepository bodyTemplateElementRepository;

    public String generateHtml(List<BodyReportDTO> formData){
        Context context = new Context();
        context.setVariable("formData", formData);

        return templateEngine.process("genericReport", context);
    }
    public byte[] generatePdfFromHtml(String htmlContent) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ConverterProperties properties = new ConverterProperties();
        HtmlConverter.convertToPdf(htmlContent, outputStream, properties);

        return outputStream.toByteArray();
    }

    public BodyTemplateDTO createBodyTemplateDTO(BodyTemplateDTO bodyTemplateDTO){
        BodyTemplate bodyTemplate = BodyTemplateMapper.INSTANCE.toEntity(bodyTemplateDTO);
        bodyTemplate.setBodyTemplateElements(bodyTemplateDTO.getBodyTemplateElementDTOs().stream().map(BodyTemplateElementMapper.INSTANCE::toEntity).toList());
        bodyTemplate.getBodyTemplateElements().forEach(bodyTemplateElement -> bodyTemplateElement.setBodyTemplate(bodyTemplate));
        BodyTemplate savedTemplate = bodyTemplateRepository.save(bodyTemplate);
        return BodyTemplateMapper.INSTANCE.fromEntity(savedTemplate);
    }

    public List<BodyTemplateDTO> getTemplates() {
        List<BodyTemplate> templates = bodyTemplateRepository.findAll();
        return templates.stream().map(BodyTemplateMapper.INSTANCE::fromEntity).collect(Collectors.toList());
    }
}
