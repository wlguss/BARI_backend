package com.bari.inventory.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponseWrapper<T> {
    private T data;
}
