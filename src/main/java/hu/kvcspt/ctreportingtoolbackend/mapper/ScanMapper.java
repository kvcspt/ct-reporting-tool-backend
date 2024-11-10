package hu.kvcspt.ctreportingtoolbackend.mapper;

import hu.kvcspt.ctreportingtoolbackend.dto.ScanDTO;
import hu.kvcspt.ctreportingtoolbackend.model.Scan;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScanMapper {
    ScanMapper INSTANCE = Mappers.getMapper(ScanMapper.class);

    Scan toEntity(ScanDTO scanDTO);

    ScanDTO fromEntity(Scan scan);
}
