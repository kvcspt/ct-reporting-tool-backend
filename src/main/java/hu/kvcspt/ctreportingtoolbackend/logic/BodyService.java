package hu.kvcspt.ctreportingtoolbackend.logic;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import hu.kvcspt.ctreportingtoolbackend.dto.BodyReportDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.BodyTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.DicomSRDTO;
import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.mapper.BodyTemplateMapper;
import hu.kvcspt.ctreportingtoolbackend.model.BodyTemplate;
import hu.kvcspt.ctreportingtoolbackend.model.repository.BodyTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.dcm4che3.data.*;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
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

    public File generateDicomSR(DicomSRDTO body) {
        Attributes attrs = new Attributes();
        ScanDTO scan = body.getScan();
        List<BodyReportDTO> report = body.getForm();

        attrs.setString(Tag.SOPClassUID, VR.UI, UID.BasicTextSRStorage);
        attrs.setString(Tag.SOPInstanceUID, VR.UI, UIDUtils.createUID());
        attrs.setString(Tag.StudyInstanceUID, VR.UI, scan.getStudyUid());
        attrs.setString(Tag.SeriesInstanceUID, VR.UI, scan.getSeriesUid());
        attrs.setString(Tag.Modality, VR.CS, "SR");
        attrs.setString(Tag.PatientID, VR.LO, scan.getPatient().getId());
        attrs.setString(Tag.PatientName, VR.PN, scan.getPatient().getName());
        attrs.setDate(Tag.StudyDate, VR.DA, Date.valueOf(scan.getScanDate().toLocalDate()));

        Sequence refStudySeq = attrs.newSequence(Tag.ReferencedStudySequence, 1);
        Attributes refStudy = new Attributes();
        refStudy.setString(Tag.StudyInstanceUID, VR.UI, scan.getStudyUid());
        refStudySeq.add(refStudy);

        Sequence refSeriesSeq = attrs.newSequence(Tag.ReferencedSeriesSequence, 1);
        Attributes refSeries = new Attributes();
        refSeries.setString(Tag.SeriesInstanceUID, VR.UI, scan.getSeriesUid());
        refSeriesSeq.add(refSeries);

        Attributes root = new Attributes();
        root.setString(Tag.ValueType, VR.CS, "CONTAINER");
        root.setString(Tag.ContinuityOfContent, VR.CS, "SEPARATE");
        root.setString(Tag.CodeMeaning, VR.LO, "Report Root");
        root.setString(Tag.CodeValue, VR.SH, "111700");
        root.setString(Tag.CodingSchemeDesignator, VR.SH, "DCM");

        Sequence contentSequence = root.newSequence(Tag.ContentSequence, report.size());

        Sequence conceptNameCodeSeq = root.newSequence(Tag.ConceptNameCodeSequence, 1);
        Attributes titleAttr = new Attributes();
        titleAttr.setString(Tag.CodeValue, VR.ST, "121144");
        titleAttr.setString(Tag.CodingSchemeDesignator, VR.SH, "DCM");
        titleAttr.setString(Tag.CodeMeaning, VR.LO, body.getTitle());
        conceptNameCodeSeq.add(titleAttr);

        report.forEach(element -> {
            Attributes item = new Attributes();
            item.setString(Tag.ValueType, VR.CS, "TEXT");
            item.setString(Tag.CodeMeaning, VR.LO, element.getLabel());
            item.setString(Tag.TextValue, VR.UT, element.getValue());
            contentSequence.add(item);
        });

        attrs.newSequence(Tag.ContentSequence, 1).add(root);

        File file = new File("dicom_sr.dcm");
        try (DicomOutputStream dos = new DicomOutputStream(file)) {
            dos.writeDataset(null, attrs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }
}
