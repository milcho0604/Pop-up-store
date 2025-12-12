package com.store.popup.pop.controller;

import com.store.popup.common.dto.CommonResDto;
import com.store.popup.pop.domain.Post;
import com.store.popup.pop.dto.PostSaveDto;
import com.store.popup.pop.dto.PostUpdateReqDto;
import com.store.popup.pop.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/post")
public class PostAdminController {

    private final PostService postService;

    // 관리자가 팝업 직접 생성
    @PostMapping("/create")
    public ResponseEntity<CommonResDto> register(@ModelAttribute PostSaveDto dto) throws java.nio.file.AccessDeniedException {
        Post post = postService.create(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "post 등록 성공", post);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 관리자가 팝업 수정
    @PostMapping("/update/{id}")
    public ResponseEntity<CommonResDto> updatePost (@PathVariable Long id, @ModelAttribute PostUpdateReqDto dto){
        postService.updatePost(id, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "post가 성공적으로 업데이트 되었습니다.", id);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 관리자가 팝업 삭제
    @PostMapping("/delete/{id}")
    public ResponseEntity<CommonResDto> deletePost(@PathVariable Long id){
        postService.deletePost(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "post가 삭제되었습니다.",id);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
