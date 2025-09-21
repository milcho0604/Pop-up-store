package com.store.popup.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Component
public class S3ClientFileUpload {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    public S3ClientFileUpload(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String upload(MultipartFile file) {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            // S3에 파일 업로드
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .build(), RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }

        // 업로드된 파일의 URL 반환
        return String.format("https://%s.s3.amazonaws.com/%s", bucket, fileName);
    }
}