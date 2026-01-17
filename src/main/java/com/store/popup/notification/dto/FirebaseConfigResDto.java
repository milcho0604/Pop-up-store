package com.store.popup.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseConfigResDto {
    @JsonProperty("apiKey")
    private String apiKey;

    @JsonProperty("authDomain")
    private String authDomain;

    @JsonProperty("projectId")
    private String projectId;

    @JsonProperty("storageBucket")
    private String storageBucket;

    @JsonProperty("messagingSenderId")
    private String messagingSenderId;

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("measurementId")
    private String measurementId;

    @JsonProperty("vapidKey")
    private String vapidKey;
}
