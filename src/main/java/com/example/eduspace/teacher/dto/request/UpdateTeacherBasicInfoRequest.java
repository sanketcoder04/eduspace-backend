package com.example.eduspace.teacher.dto.request;

import com.example.eduspace.common.dto.AddressDto;
import com.example.eduspace.common.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTeacherBasicInfoRequest {

    @NotBlank(message = "Full name is required.")
    private String name;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number.")
    private String phoneNumber;

    @NotNull(message = "Address is required.")
    @Valid
    private AddressDto address;

    private Gender gender;

    private String headline;

    private String about;
}