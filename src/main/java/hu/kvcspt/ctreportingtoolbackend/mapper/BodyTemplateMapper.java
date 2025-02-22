package hu.kvcspt.ctreportingtoolbackend.mapper;

import hu.kvcspt.ctreportingtoolbackend.dto.BodyTemplateDTO;
import hu.kvcspt.ctreportingtoolbackend.model.BodyTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BodyTemplateMapper {
    BodyTemplateMapper INSTANCE = Mappers.getMapper(BodyTemplateMapper.class);
    @Mapping(target = "bodyTemplateElements", source = "bodyTemplateElementDTOs")
    BodyTemplate toEntity(BodyTemplateDTO bodyTemplateDTO);
    @Mapping(target = "bodyTemplateElementDTOs", source = "bodyTemplateElements")
    BodyTemplateDTO fromEntity(BodyTemplate bodyTemplate);
}
