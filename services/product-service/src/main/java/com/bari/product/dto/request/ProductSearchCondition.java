package com.bari.product.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchCondition {

    private Long storeId;
    private String category;
    private String keyword;
    private Boolean active;
}
