package com.store.popup.pop.mapper;

import com.store.popup.pop.domain.Post;
import com.store.popup.pop.dto.PostUpdateReqDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PostMapper {
    void update(@MappingTarget Post target, PostUpdateReqDto source);
}
