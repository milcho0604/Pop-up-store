package com.store.popup.favoritefolder.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.favoritefolder.dto.FavoriteFolderDto;
import com.store.popup.favoritefolder.dto.FavoriteFolderSaveDto;
import com.store.popup.favoritefolder.service.FavoriteFolderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 찜하기 폴더 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/favorite/folder")
public class FavoriteFolderController {

    private final FavoriteFolderService favoriteFolderService;

    /**
     * 폴더 생성
     */
    @PostMapping
    public ResponseEntity<CommonResDto> createFolder(@RequestBody FavoriteFolderSaveDto dto) {
        FavoriteFolderDto folder = favoriteFolderService.createFolder(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "폴더가 생성되었습니다.", folder);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    /**
     * 내 폴더 목록 조회
     */
    @GetMapping
    public ResponseEntity<CommonResDto> getMyFolders() {
        List<FavoriteFolderDto> folders = favoriteFolderService.getMyFolders();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "폴더 목록을 조회합니다.", folders);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 폴더 수정
     */
    @PutMapping("/{folderId}")
    public ResponseEntity<CommonResDto> updateFolder(
            @PathVariable Long folderId,
            @RequestBody FavoriteFolderSaveDto dto) {
        FavoriteFolderDto folder = favoriteFolderService.updateFolder(folderId, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "폴더가 수정되었습니다.", folder);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 폴더 삭제
     */
    @DeleteMapping("/{folderId}")
    public ResponseEntity<CommonResDto> deleteFolder(@PathVariable Long folderId) {
        favoriteFolderService.deleteFolder(folderId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "폴더가 삭제되었습니다.", folderId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 찜을 폴더로 이동
     */
    @PutMapping("/move/{favoriteId}")
    public ResponseEntity<CommonResDto> moveFavoriteToFolder(
            @PathVariable Long favoriteId,
            @RequestParam(required = false) Long folderId) {
        favoriteFolderService.moveFavoriteToFolder(favoriteId, folderId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "찜이 폴더로 이동되었습니다.", favoriteId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
