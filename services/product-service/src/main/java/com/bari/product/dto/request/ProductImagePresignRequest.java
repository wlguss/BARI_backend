package com.bari.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductImagePresignRequest {

    @NotBlank(message = "파일명은 필수입니다.")
    private String fileName;

    @NotBlank(message = "Content-Type은 필수입니다.")
    @Pattern(
            regexp = "image/(jpeg|jpg|png|webp)",
            message = "지원 가능한 이미지 형식은 jpeg, jpg, png, webp 입니다."
    )
    private String contentType;
}