package hu.kvcspt.ctreportingtoolbackend.mapper;

import hu.kvcspt.ctreportingtoolbackend.dto.ReportDTO;
import hu.kvcspt.ctreportingtoolbackend.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReportMapper {
    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    Report toEntity(ReportDTO reportDTO);

    ReportDTO fromEntity(Report report);
}
