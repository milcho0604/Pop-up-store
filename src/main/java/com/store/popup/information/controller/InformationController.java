package com.store.popup.information.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.information.domain.Information;
import com.store.popup.information.dto.InformationDetailDto;
import com.store.popup.information.dto.InformationListDto;
import com.store.popup.information.dto.InformationSaveDto;
import com.store.popup.information.dto.InformationUpdateReqDto;
import com.store.popup.information.service.InformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<CommonResDto> createInformation(@ModelAttribute InformationSaveDto dto) {
        Information information = informationService.create(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "팝업 스토어 제보가 등록되었습니다.", information.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 제보자 본인이 자신의 제보 목록 조회
    @GetMapping("/my/list")
    public ResponseEntity<CommonResDto> getMyInformationList() {
        List<InformationListDto> informations = informationService.getMyInformationList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "내 제보 목록을 조회합니다.", informations);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 제보자 본인이 자신의 제보 상세 조회
    @GetMapping("/my/detail/{id}")
    public ResponseEntity<CommonResDto> getMyInformationDetail(@PathVariable Long id) {
        InformationDetailDto information = informationService.getMyInformationDetail(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "내 제보 상세정보를 조회합니다.", information);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 제보자 본인이 자신의 제보 수정
    @PostMapping("/my/update/{id}")
    public ResponseEntity<CommonResDto> updateMyInformation(
            @PathVariable Long id,
            @ModelAttribute InformationUpdateReqDto dto) {
        InformationDetailDto information = informationService.update(id, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "제보가 수정되었습니다.", information);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}

