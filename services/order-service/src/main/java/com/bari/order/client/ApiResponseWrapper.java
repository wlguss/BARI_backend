package com.bari.order.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 내부 서비스 RestClient 응답의 ApiResponse 래퍼 역직렬화용 DTO.
 * 각 서비스는 { "status": 200, "message": "...", "data": {...} } 형태로 응답하므로
 * data 필드만 추출하기 위해 사용합니다.
 */
@Getter
@NoArgsConstructor
public class ApiResponseWrapper<T> {
    private T data;
}
