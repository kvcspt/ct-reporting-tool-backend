package hu.kvcspt.ctreportingtoolbackend.logic.body;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public abstract class BodyService {
    public abstract String generateHtml(Map<String, Object> formData);
    public byte[] generatePdfFromHtml(String htmlContent) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ConverterProperties properties = new ConverterProperties();
        HtmlConverter.convertToPdf(htmlContent, outputStream, properties);

        return outputStream.toByteArray();
    }
}
