package com.store.popup.favorite.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.favorite.dto.FavoriteResDto;
import com.store.popup.favorite.service.FavoriteService;
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
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 찜하기 추가
    @PostMapping("/add/{postId}")
    public ResponseEntity<CommonResDto> addFavorite(@PathVariable Long postId) {
        FavoriteResDto favorite = favoriteService.addFavorite(postId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "찜하기가 추가되었습니다.", favorite);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 찜하기 취소
    @DeleteMapping("/remove/{postId}")
    public ResponseEntity<CommonResDto> removeFavorite(@PathVariable Long postId) {
        favoriteService.removeFavorite(postId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "찜하기가 취소되었습니다.", postId);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 내 찜 목록 조회 (페이징)
    @GetMapping("/my/list")
    public ResponseEntity<CommonResDto> getMyFavorites(Pageable pageable) {
        Page<FavoriteResDto> favorites = favoriteService.getMyFavorites(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "찜 목록을 조회합니다.", favorites);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 내 찜 목록 조회 (전체 리스트)
    @GetMapping("/my/list/all")
    public ResponseEntity<CommonResDto> getMyFavoritesList() {
        List<FavoriteResDto> favorites = favoriteService.getMyFavoritesList();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "전체 찜 목록을 조회합니다.", favorites);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 찜 여부 확인
    @GetMapping("/check/{postId}")
    public ResponseEntity<CommonResDto> isFavorite(@PathVariable Long postId) {
        boolean isFavorite = favoriteService.isFavorite(postId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "찜 여부를 확인합니다.", isFavorite);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 내 찜 개수
    @GetMapping("/my/count")
    public ResponseEntity<CommonResDto> getMyFavoriteCount() {
        long count = favoriteService.getMyFavoriteCount();
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "내 찜 개수를 조회합니다.", count);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // Post의 찜 개수
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<CommonResDto> getPostFavoriteCount(@PathVariable Long postId) {
        long count = favoriteService.getPostFavoriteCount(postId);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "포스트의 찜 개수를 조회합니다.", count);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
