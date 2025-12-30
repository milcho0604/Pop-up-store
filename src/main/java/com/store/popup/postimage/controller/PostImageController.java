package com.store.popup.postimage.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.postimage.dto.PostImageDto;
import com.store.popup.postimage.service.PostImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Post 이미지 갤러리 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/post/{postId}/images")
public class PostImageController {

    private final PostImageService postImageService;

    /**
     * Post에 이미지 추가 (여러 장 가능)
     */
    @PostMapping
    public ResponseEntity<CommonResDto> addImages(
            @PathVariable Long postId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "descriptions", required = false) List<String> descriptions) {
        List<PostImageDto> images = postImageService.addImages(postId, files, descriptions);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "이미지가 추가되었습니다.", images);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }

    /**
     * Post의 모든 이미지 조회
     */
    @GetMapping
    public ResponseEntity<CommonResDto> getPostImages(@PathVariable Long postId) {
        List<PostImageDto> images = postImageService.getPostImages(postId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "이미지 목록을 조회합니다.", images);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    /**
     * 이미지 삭제
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<CommonResDto> deleteImage(
            @PathVariable Long postId,
            @PathVariable Long imageId) {
        postImageService.deleteImage(imageId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "이미지가 삭제되었습니다.", imageId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
