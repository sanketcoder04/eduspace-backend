package com.example.eduspace.student.mapper;

import com.example.eduspace.common.dto.AddressDto;
import com.example.eduspace.common.dto.CertificateResponse;
import com.example.eduspace.common.dto.EducationDto;
import com.example.eduspace.common.dto.VerificationResponse;
import com.example.eduspace.common.entity.Address;
import com.example.eduspace.common.entity.Certificate;
import com.example.eduspace.common.entity.Education;
import com.example.eduspace.common.entity.ProfileVerification;
import com.example.eduspace.student.dto.response.StudentProfileResponse;
import com.example.eduspace.student.entity.StudentProfile;
import com.example.eduspace.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface StudentProfileMapper {

    AddressDto toAddressDto(Address address);

    Address toAddress(AddressDto dto);

    EducationDto toEducationDto(Education education);

    Education toEducation(EducationDto dto);

    VerificationResponse toVerificationResponse(ProfileVerification verification);

    @Mapping(target = "id", source = "profile.id")
    @Mapping(target = "name", source = "profile.name")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "createdAt", source = "profile.createdAt")
    @Mapping(target = "updatedAt", source = "profile.updatedAt")
    @Mapping(target = "lastLoginAt", source = "user.lastLoginAt")
    StudentProfileResponse toResponse(StudentProfile profile, User user);

    List<EducationDto> toEducationDtoList(List<Education> education);

    List<Education> toEducationList(List<EducationDto> educationDtos);

    CertificateResponse toCertificateResponse(Certificate certificate);

    List<CertificateResponse> toCertificateResponseList(List<Certificate> certificates);
}