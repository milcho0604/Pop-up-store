package com.store.popup.information.controller;

import com.store.popup.common.dto.CommonErrorDto;
import com.store.popup.common.dto.CommonResDto;
import com.store.popup.information.domain.Information;
import com.store.popup.information.domain.InformationStatus;
import com.store.popup.information.dto.InformationDetailDto;
import com.store.popup.information.dto.InformationListDto;
import com.store.popup.information.dto.InformationSaveDto;
import com.store.popup.information.dto.InformationUpdateReqDto;
import com.store.popup.information.service.AdminInformationService;
import com.store.popup.information.service.InformationConvertService;
import com.store.popup.information.service.InformationService;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.dto.PostUpdateReqDto;
import com.store.popup.pop.service.PostService;
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

    // 제보자 본인이 자신의 제보 수정
    @PutMapping("/my/update/{id}")
    public ResponseEntity<?> updateMyInformation(
            @PathVariable Long id,
            @ModelAttribute InformationUpdateReqDto dto) {
        try {
            InformationDetailDto information = informationService.update(id, dto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "제보가 수정되었습니다.", information);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST, "제보 수정 실패: " + e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }
}

