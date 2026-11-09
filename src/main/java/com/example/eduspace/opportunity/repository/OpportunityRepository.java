package com.example.eduspace.opportunity.repository;

import com.example.eduspace.opportunity.entity.Opportunity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OpportunityRepository extends MongoRepository<Opportunity, String>, OpportunityRepositoryCustom {

    Slice<Opportunity> findByTitleContainingIgnoreCase(String titlePart, Pageable pageable);
}