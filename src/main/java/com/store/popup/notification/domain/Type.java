package com.store.popup.notification.domain;

public enum Type {
//    회원 가입 시 토닥 admin에게 가는 알림
    REGISTER,
//    팝업 제보 발생 시 병원 admin에게 가는 알림 NOTIFICATION
    REPORT_NOTIFICATION,
//    팝업 등록 완료 시 admin에게 가는 알림
    POST_NOTIFICATION,
//    게시글에 댓글이 달렸을 경우 게시글 작성자에게 가는 알림
    POST,
//    댓글에 대한 답글이 달렸을 경우 기존 댓글 작성자에게 가는 알림
    COMMENT,
//    결제 알림
    PAYMENT,
//    채팅 알림
    CHAT,
//    팔로우 알림
    FOLLOW,
//    좋아요 알림
    LIKE,
//    투표 알림
    VOTE,
//    공지사항
    NOTICE,
//    QnA
    QNA,
//    리뷰에 대한 알림
    REVIEW
}
