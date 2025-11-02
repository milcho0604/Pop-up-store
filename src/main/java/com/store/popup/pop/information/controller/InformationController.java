package com.store.popup.pop.information.controller;

import com.store.popup.common.dto.CommonErrorDto;
import com.store.popup.common.dto.CommonResDto;
import com.store.popup.pop.information.domain.Information;
import com.store.popup.pop.information.domain.InformationStatus;
import com.store.popup.pop.information.dto.InformationDetailDto;
import com.store.popup.pop.information.dto.InformationListDto;
import com.store.popup.pop.information.dto.InformationSaveDto;
import com.store.popup.pop.information.service.AdminInformationService;
import com.store.popup.pop.information.service.InformationConvertService;
import com.store.popup.pop.information.service.InformationService;
import com.store.popup.pop.post.domain.Post;
import com.store.popup.pop.post.service.PostService;
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
public class InformationController {

    private final InformationService informationService;
    private final AdminInformationService adminInformationService;
    private final InformationConvertService informationConvertService;
    private final PostService postService;

    // 고객이 팝업 스토어 제보
    @PostMapping("/create")
    public ResponseEntity<?> createInformation(@ModelAttribute InformationSaveDto dto) {
        try {
            Information information = informationService.create(dto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "팝업 스토어 제보가 등록되었습니다.", information.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, "제보 등록 실패: " + e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    // 관리자가 제보 목록 조회 (페이지네이션, 상태별 필터링 가능)
    @GetMapping("/list")
    public ResponseEntity<?> getInformationList(
            Pageable pageable,
            @RequestParam(required = false) InformationStatus status) {
        try {
            Page<InformationListDto> informations = adminInformationService.getInformationList(pageable, status);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "제보 목록을 조회합니다.", informations);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, "제보 목록 조회 실패: " + e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    // 관리자가 제보 상세 조회
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getInformationDetail(@PathVariable Long id) {
        try {
            InformationDetailDto information = adminInformationService.getInformationDetail(id);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "제보 상세정보를 조회합니다.", information);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, "제보 상세 조회 실패: " + e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    // 제보자 본인이 자신의 제보 목록 조회
    @GetMapping("/my/list")
    public ResponseEntity<?> getMyInformationList() {
        try {
            List<InformationListDto> informations = informationService.getMyInformationList();
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "내 제보 목록을 조회합니다.", informations);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, "내 제보 목록 조회 실패: " + e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    // 제보자 본인이 자신의 제보 상세 조회
    @GetMapping("/my/detail/{id}")
    public ResponseEntity<?> getMyInformationDetail(@PathVariable Long id) {
        try {
            InformationDetailDto information = informationService.getMyInformationDetail(id);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "내 제보 상세정보를 조회합니다.", information);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, "내 제보 상세 조회 실패: " + e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    // 관리자가 단일 제보를 Post로 변환
    @PostMapping("/convert/{id}")
    public ResponseEntity<?> convertInformationToPost(@PathVariable Long id) {
        try {
            Post post = informationConvertService.convertInformationToPost(id);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "제보가 Post로 변환되었습니다.", post.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, "제보 변환 실패: " + e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }

    // 관리자가 여러 제보를 Post로 일괄 변환
    @PostMapping("/convert/batch")
    public ResponseEntity<?> convertInformationsToPosts(@RequestBody List<Long> informationIds) {
        try {
            List<Post> posts = informationConvertService.convertInformationsToPosts(informationIds);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, 
                    posts.size() + "개의 제보가 Post로 변환되었습니다.", 
                    posts.stream().map(Post::getId).toList());
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, "제보 일괄 변환 실패: " + e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }
}

