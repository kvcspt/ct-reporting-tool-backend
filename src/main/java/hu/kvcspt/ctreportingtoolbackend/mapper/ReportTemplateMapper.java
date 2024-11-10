package hu.kvcspt.ctreportingtoolbackend.mapper;

import hu.kvcspt.ctreportingtoolbackend.dto.ReportTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.model.ReportTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReportTemplateMapper {
    ReportTemplateMapper INSTANCE = Mappers.getMapper(ReportTemplateMapper.class);

    ReportTemplate toEntity(ReportTemplateDTO reportTemplateDTO);

    ReportTemplateDTO fromEntity(ReportTemplate reportTemplate);
}
