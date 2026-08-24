package com.example.eduspace.opportunity.mapper;

import com.example.eduspace.common.dto.AddressDto;
import com.example.eduspace.opportunity.dto.request.CreateTeachingOpeningRequest;
import com.example.eduspace.opportunity.dto.request.CreateTuitionRequirementRequest;
import com.example.eduspace.opportunity.dto.response.*;
import com.example.eduspace.opportunity.entity.*;
import com.example.eduspace.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OpportunityMapper {

    // ---------------------------------------------------------------
    // Request -> Entity (creation)
    // ---------------------------------------------------------------

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorRole", constant = "TEACHER")
    @Mapping(target = "postType", ignore = true)      // set explicitly in the service
    @Mapping(target = "status", ignore = true)        // set explicitly in the service
    @Mapping(target = "applicationsCount", ignore = true)
    @Mapping(target = "tuitionRequirementDetails", ignore = true)
    @Mapping(target = "teachingOpeningDetails.seatsFilled", ignore = true)
    Opportunity toOpportunity(CreateTeachingOpeningRequest request, User author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorRole", constant = "STUDENT")
    @Mapping(target = "postType", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "applicationsCount", ignore = true)
    @Mapping(target = "teachingOpeningDetails", ignore = true)
    Opportunity toOpportunity(CreateTuitionRequirementRequest request, User author);

    // Nested request -> entity value objects (MapStruct wires these in automatically
    // wherever the field names line up, e.g. request.getFeeRange() -> Opportunity.feeRange)
    FeeRange toFeeRange(com.example.eduspace.opportunity.entity.FeeRange feeRange);

    TimeSlot toTimeSlot(TimeSlot timeSlot);

    List<TimeSlot> toTimeSlots(List<TimeSlot> timeSlots);

    @Mapping(target = "seatsFilled", ignore = true)
    TeachingOpeningDetails toTeachingOpeningDetails(CreateTeachingOpeningRequest request);

    TuitionRequirementDetails toTuitionRequirementDetails(CreateTuitionRequirementRequest request);

    // ---------------------------------------------------------------
    // Entity -> Response
    // ---------------------------------------------------------------

    @Mapping(target = "authorName", ignore = true)
    @Mapping(target = "authorAvatarUrl", ignore = true)
    OpportunityResponse toResponse(Opportunity opportunity);

    List<OpportunityResponse> toResponseList(List<Opportunity> opportunities);

    FeeRangeResponse toFeeRangeResponse(FeeRange feeRange);

    TimeSlotResponse toTimeSlotResponse(TimeSlot timeSlot);

    List<TimeSlotResponse> toTimeSlotResponseList(List<TimeSlot> timeSlots);

    TeachingOpeningDetailsResponse toTeachingOpeningDetailsResponse(TeachingOpeningDetails details);

    TuitionRequirementDetailsResponse toTuitionRequirementDetailsResponse(TuitionRequirementDetails details);

    AddressDto toAddressDto(com.example.eduspace.common.entity.Address address);
}