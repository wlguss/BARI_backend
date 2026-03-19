package com.bari.product.service;

import com.bari.product.dto.request.ProductImagePresignRequest;
import com.bari.product.dto.response.ProductImagePresignResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public ProductImagePresignResponse generatePresignedUploadUrl(ProductImagePresignRequest request) {
        String key = generateImageKey(request.getFileName());

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(request.getContentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        String imageUrl = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + key;

        return ProductImagePresignResponse.builder()
                .presignedUrl(presignedRequest.url().toString())
                .key(key)
                .imageUrl(imageUrl)
                .expiresIn(300L)
                .build();
    }

    private String generateImageKey(String originalFileName) {
        String extension = extractExtension(originalFileName);
        return "products/" + UUID.randomUUID() + "." + extension;
    }

    private String extractExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            throw new IllegalArgumentException("파일 확장자가 올바르지 않습니다.");
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
