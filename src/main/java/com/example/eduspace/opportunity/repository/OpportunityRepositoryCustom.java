package com.example.eduspace.opportunity.repository;

import com.example.eduspace.opportunity.dto.request.OpportunityFilterRequest;
import com.example.eduspace.opportunity.entity.Opportunity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OpportunityRepositoryCustom {
    Page<Opportunity> search(OpportunityFilterRequest filter, Pageable pageable);
}