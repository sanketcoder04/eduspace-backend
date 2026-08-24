package com.example.eduspace.application.repository;

import com.example.eduspace.application.entity.Application;
import com.example.eduspace.application.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends MongoRepository<Application, String> {

    Page<Application> findByApplicantId(String applicantId, Pageable pageable);

    Page<Application> findByAuthorId(String authorId, Pageable pageable);

    Page<Application> findByOpportunityId(String opportunityId, Pageable pageable);

    Optional<Application> findByOpportunityIdAndApplicantIdAndStatusIn(
            String opportunityId, String applicantId, List<ApplicationStatus> statuses);

    Optional<Application> findByIdAndAuthorId(String id, String authorId);

    Optional<Application> findByIdAndApplicantId(String id, String applicantId);
}