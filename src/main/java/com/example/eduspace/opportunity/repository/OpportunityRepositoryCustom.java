package com.example.eduspace.opportunity.repository;

import com.example.eduspace.opportunity.dto.request.OpportunityFilterRequest;
import com.example.eduspace.opportunity.entity.Opportunity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OpportunityRepositoryCustom {

    Page<Opportunity> search(OpportunityFilterRequest filter, String keyword, List<String> keywordAuthorIds, Pageable pageable);
}