package com.store.popup.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendEmailDto {
    private String toMember;
    private String fromMember;
    private String code;
    private boolean isAuthentication;
}
