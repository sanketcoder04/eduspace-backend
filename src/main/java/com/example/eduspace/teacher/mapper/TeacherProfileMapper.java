package com.example.eduspace.teacher.mapper;

import com.example.eduspace.common.dto.AddressDto;
import com.example.eduspace.common.dto.EducationDto;
import com.example.eduspace.common.entity.Address;
import com.example.eduspace.common.entity.Education;
import com.example.eduspace.teacher.dto.response.SubjectOfferingResponse;
import com.example.eduspace.teacher.dto.response.TeacherProfileResponse;
import com.example.eduspace.teacher.dto.response.VerificationResponse;
import com.example.eduspace.teacher.entity.SubjectOffering;
import com.example.eduspace.teacher.entity.TeacherProfile;
import com.example.eduspace.teacher.entity.TeacherVerification;
import com.example.eduspace.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TeacherProfileMapper {

    AddressDto toAddressDto(Address address);

    Address toAddress(AddressDto dto);

    EducationDto toEducationDto(Education education);

    Education toEducation(EducationDto dto);

    SubjectOfferingResponse toSubjectOfferingResponse(SubjectOffering offering);

    VerificationResponse toVerificationResponse(TeacherVerification verification);

    @Mapping(target = "id", source = "profile.id")
    @Mapping(target = "name", source = "profile.name")
    @Mapping(target = "email", source = "user.email")
    TeacherProfileResponse toResponse(TeacherProfile profile, User user);

    List<EducationDto> toEducationDtoList(List<Education> education);

    List<Education> toEducationList(List<EducationDto> educationDtos);

    List<SubjectOfferingResponse> toSubjectOfferingResponseList(List<SubjectOffering> offerings);
}