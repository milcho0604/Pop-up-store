package com.store.popup.information.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.information.domain.InformationStatus;
import com.store.popup.information.dto.InformationDetailDto;
import com.store.popup.information.dto.InformationListDto;
import com.store.popup.information.service.AdminInformationService;
import com.store.popup.information.service.InformationConvertService;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.dto.PostUpdateReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/info")
public class InformationAdminController {

    private final AdminInformationService adminInformationService;
    private final InformationConvertService informationConvertService;

    // 관리자가 제보 목록 조회 (페이지네이션, 상태별 필터링 가능)
    @GetMapping("/list")
    public ResponseEntity<CommonResDto> getInformationList(
            Pageable pageable,
            @RequestParam(required = false) InformationStatus status) {
        Page<InformationListDto> informations = adminInformationService.getInformationList(pageable, status);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "제보 목록을 조회합니다.", informations);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 관리자가 제보 상세 조회
    @GetMapping("/detail/{id}")
    public ResponseEntity<CommonResDto> getInformationDetail(@PathVariable Long id) {
        InformationDetailDto information = adminInformationService.getInformationDetail(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "제보 상세정보를 조회합니다.", information);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 관리자가 단일 제보를 Post로 변환
    @PostMapping("/convert/{id}")
    public ResponseEntity<CommonResDto> convertInformationToPost(@PathVariable Long id) {
        Post post = informationConvertService.convertInformationToPost(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "제보가 Post로 변환되었습니다.", post.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 관리자가 여러 제보를 Post로 일괄 변환
    @PostMapping("/convert/batch")
    public ResponseEntity<CommonResDto> convertInformationsToPosts(@RequestBody List<Long> informationIds) {
        List<Post> posts = informationConvertService.convertInformationsToPosts(informationIds);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,
                posts.size() + "개의 제보가 Post로 변환되었습니다.",
                posts.stream().map(Post::getId).toList());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 관리자가 단일 제보를 정보 수정 후 Post로 변환
    @PostMapping("/convert/{id}/with-update")
    public ResponseEntity<CommonResDto> convertInformationToPostWithUpdate(
            @PathVariable Long id,
            @ModelAttribute PostUpdateReqDto dto) {
        Post post = informationConvertService.convertInformationToPostWithUpdate(id, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "제보가 수정 후 Post로 변환되었습니다.", post.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 관리자가 여러 제보를 정보 수정 후 Post로 일괄 변환
    @PostMapping("/convert/batch/with-update")
    public ResponseEntity<CommonResDto> convertInformationsToPostsWithUpdate(
            @RequestParam List<Long> informationIds,
            @RequestBody List<PostUpdateReqDto> updateDtos) {
        List<Post> posts = informationConvertService.convertInformationsToPostsWithUpdate(informationIds, updateDtos);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,
                posts.size() + "개의 제보가 수정 후 Post로 변환되었습니다.",
                posts.stream().map(Post::getId).toList());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 관리자가 팝업 제보를 거절
    @PostMapping("/reject/{id}")
    public ResponseEntity<CommonResDto> rejectInformation(@PathVariable Long id) {
        InformationDetailDto information = adminInformationService.rejectInformation(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "제보가 거절되었습니다.", information);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 관리자가 팝업 제보를 삭제
    @PostMapping("/delete/{id}")
    public ResponseEntity<CommonResDto> deleteInformation(@PathVariable Long id) {
        adminInformationService.deleteInformation(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "제보가 삭제되었습니다.", id);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 관리자가 승인을 취소
    @PostMapping("/cancel-approval/{id}")
    public ResponseEntity<CommonResDto> cancelApproval(@PathVariable Long id) {
        InformationDetailDto information = informationConvertService.cancelApproval(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "승인이 취소되었습니다.", information);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}

