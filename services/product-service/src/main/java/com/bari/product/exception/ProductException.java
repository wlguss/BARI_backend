package com.bari.product.exception;

import com.bari.common.exception.BusinessException;

public class ProductException extends BusinessException {

    public ProductException(ProductErrorCode errorCode) {
        super(errorCode);
    }
}
