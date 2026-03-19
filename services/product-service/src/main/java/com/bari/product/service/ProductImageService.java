package com.bari.product.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bari.product.config.AwsS3Config.AwsProperties;
import com.bari.product.dto.request.ProductImagePresignRequest;
import com.bari.product.dto.response.ProductImagePresignResponse;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductImageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final S3Presigner s3Presigner;
    private final AwsProperties awsProperties;

    public ProductImagePresignResponse generatePresignedUploadUrl(ProductImagePresignRequest request) {
        validateContentType(request.getContentType());

        String key = createObjectKey(request.getFileName());
        String bucket = awsProperties.getS3().getBucket();
        String region = awsProperties.getRegion();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(request.getContentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(presignRequest);

        String imageUrl = "https://%s.s3.%s.amazonaws.com/%s"
                .formatted(bucket, region, key);

        return ProductImagePresignResponse.builder()
                .uploadUrl(presignedRequest.url().toString())
                .imageUrl(imageUrl)
                .key(key)
                .build();
    }

    private void validateContentType(String contentType) {
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("허용되지 않는 이미지 형식입니다.");
        }
    }

    private String createObjectKey(String originalFileName) {
        String safeFileName = sanitizeFileName(originalFileName);
        LocalDate today = LocalDate.now();

        return "products/%d/%02d/%02d/%s_%s"
                .formatted(
                        today.getYear(),
                        today.getMonthValue(),
                        today.getDayOfMonth(),
                        UUID.randomUUID(),
                        safeFileName
                );
    }

    private String sanitizeFileName(String fileName) {
        String normalized = fileName.replaceAll("\\s+", "_");
        return URLEncoder.encode(normalized, StandardCharsets.UTF_8);
    }
}
