package com.example.eduspace.application.repository;

import com.example.eduspace.application.dto.request.ApplicationSearchRequest;
import com.example.eduspace.application.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicationRepositoryCustom {

    Page<Application> search(String userId, ApplicationSearchRequest filter, String keyword,
                             List<String> matchedOpportunityIds, List<String> matchedUserIds,
                             Pageable pageable);
}