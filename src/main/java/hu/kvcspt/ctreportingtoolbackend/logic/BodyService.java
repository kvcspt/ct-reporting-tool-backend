package hu.kvcspt.ctreportingtoolbackend.logic;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import hu.kvcspt.ctreportingtoolbackend.dto.BodyReportDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.BodyTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.mapper.BodyTemplateMapper;
import hu.kvcspt.ctreportingtoolbackend.model.BodyTemplate;
import hu.kvcspt.ctreportingtoolbackend.model.repository.BodyTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class BodyService {
    private SpringTemplateEngine templateEngine;
    private BodyTemplateRepository bodyTemplateRepository;

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
        if(bodyTemplateRepository.existsByTitle(bodyTemplateDTO.getTitle())){
            throw new IllegalArgumentException("There is another template with this title!");
        }

        BodyTemplate bodyTemplate = BodyTemplateMapper.INSTANCE.toEntity(bodyTemplateDTO);
        bodyTemplate.getBodyTemplateElements().forEach(bodyTemplateElement -> bodyTemplateElement.setBodyTemplate(bodyTemplate));
        BodyTemplate savedTemplate = bodyTemplateRepository.save(bodyTemplate);
        return BodyTemplateMapper.INSTANCE.fromEntity(savedTemplate);
    }

    public List<BodyTemplateDTO> getTemplates() {
        List<BodyTemplate> templates = bodyTemplateRepository.findAll();
        return templates.stream().map(BodyTemplateMapper.INSTANCE::fromEntity).collect(Collectors.toList());
    }

    public BodyTemplateDTO updateTemplate(BodyTemplateDTO bodyTemplateDTO) {
        BodyTemplate existingBodyTemplate = bodyTemplateRepository
                .findByTitle(bodyTemplateDTO.getTitle())
                .orElseThrow(() -> new IllegalArgumentException("BodyTemplate not found!"));

        BodyTemplate newTemplate = BodyTemplateMapper.INSTANCE.toEntity(bodyTemplateDTO);
        newTemplate.getBodyTemplateElements().forEach(bodyTemplateElement -> bodyTemplateElement.setBodyTemplate(existingBodyTemplate));

        existingBodyTemplate.setTitle(newTemplate.getTitle());
        existingBodyTemplate.setBodyTemplateElements(newTemplate.getBodyTemplateElements());

        bodyTemplateRepository.save(existingBodyTemplate);
        return BodyTemplateMapper.INSTANCE.fromEntity(existingBodyTemplate);
    }

    public void deleteBodyTemplate(@NonNull String title){
        if (bodyTemplateRepository.existsByTitle(title)) {
            BodyTemplate existingBodyTemplate = bodyTemplateRepository
                    .findByTitle(title)
                    .orElseThrow(() -> new IllegalArgumentException("BodyTemplate not found!"));
            bodyTemplateRepository.delete(existingBodyTemplate);
            bodyTemplateRepository.deleteByTitle(title);
            log.debug("BodyTemplate is deleted successfully");
        } else {
            log.debug("BodyTemplate with title " + title + " not found.");
            throw new IllegalArgumentException("BodyTemplate with title " + title + " not found.");
        }
    }
}
