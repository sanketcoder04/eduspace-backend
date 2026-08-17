package com.example.eduspace.common.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String line1;

    private String line2;

    private String city;

    private String state;

    private String pincode;

    private String country;

    private Double latitude;

    private Double longitude;
}