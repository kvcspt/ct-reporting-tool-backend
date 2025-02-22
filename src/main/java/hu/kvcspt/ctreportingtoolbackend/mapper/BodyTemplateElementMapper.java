package hu.kvcspt.ctreportingtoolbackend.mapper;

import hu.kvcspt.ctreportingtoolbackend.dto.BodyTemplateElementDTO;
import hu.kvcspt.ctreportingtoolbackend.model.BodyTemplateElement;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BodyTemplateElementMapper {
    BodyTemplateElementMapper INSTANCE = Mappers.getMapper(BodyTemplateElementMapper.class);

    BodyTemplateElement toEntity(BodyTemplateElementDTO bodyTemplateDTO);

    BodyTemplateElementDTO fromEntity(BodyTemplateElement bodyTemplate);
}
