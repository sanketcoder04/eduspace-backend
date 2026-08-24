package com.example.eduspace.application.mapper;

import com.example.eduspace.application.dto.response.ApplicationResponse;
import com.example.eduspace.application.dto.response.ContactShareConsentResponse;
import com.example.eduspace.application.entity.Application;
import com.example.eduspace.application.entity.ContactShareConsent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ApplicationMapper {

    @Mapping(target = "opportunityTitle", ignore = true)     // enriched: OpportunityRepository lookup
    @Mapping(target = "applicantName", ignore = true)        // enriched: User lookup
    @Mapping(target = "applicantAvatarUrl", ignore = true)   // enriched: Teacher/StudentProfile lookup
    ApplicationResponse toResponse(Application application);

    List<ApplicationResponse> toResponseList(List<Application> applications);

    ContactShareConsentResponse toContactShareConsentResponse(ContactShareConsent consent);
}