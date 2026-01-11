package com.store.popup.member.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address {
    private String city;
    private String dong;  // 동 (구보다 작은 행정구역)
    private String street;
    private String zipcode;
    private String detailAddress;
}
