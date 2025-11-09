package com.store.popup.pop.post.mapper;

import com.store.popup.pop.post.domain.Post;
import com.store.popup.pop.post.dto.PostUpdateReqDto;
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
