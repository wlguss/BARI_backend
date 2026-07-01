package com.bari.user.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponseWrapper<T> {
    private T data;
}
