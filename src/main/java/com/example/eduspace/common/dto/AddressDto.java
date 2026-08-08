package com.example.eduspace.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    @NotBlank(message = "Address line 1 is required.")
    private String line1;

    private String line2;

    @NotBlank(message = "City is required.")
    private String city;

    @NotBlank(message = "State is required.")
    private String state;

    @NotBlank(message = "Pincode is required.")
    private String pincode;

    @NotBlank(message = "Country is required.")
    private String country;

    private Double latitude;

    private Double longitude;
}