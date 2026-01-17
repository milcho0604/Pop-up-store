package com.store.popup.notification.controller;

import com.store.popup.config.FirebaseProperties;
import com.store.popup.notification.dto.FirebaseConfigResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/firebase")
public class FirebaseConfigController {

    private final FirebaseProperties firebaseProperties;

    @GetMapping("/config")
    public ResponseEntity<FirebaseConfigResDto> getFirebaseConfig() {
        FirebaseConfigResDto config = FirebaseConfigResDto.builder()
                .apiKey(firebaseProperties.getApiKey())
                .authDomain(firebaseProperties.getAuthDomain())
                .projectId(firebaseProperties.getProjectId())
                .storageBucket(firebaseProperties.getStorageBucket())
                .messagingSenderId(firebaseProperties.getMessagingSenderId())
                .appId(firebaseProperties.getAppId())
                .measurementId(firebaseProperties.getMeasurementId())
                .vapidKey(firebaseProperties.getVapidKey())
                .build();

        return ResponseEntity.ok(config);
    }
}
