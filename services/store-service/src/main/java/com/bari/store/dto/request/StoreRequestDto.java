package com.bari.store.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequestDto {
    private String name;
    private String description;
    private String address;
    private String phone;
    private String businessHours;
    private String category;
    private String imageUrl;
}