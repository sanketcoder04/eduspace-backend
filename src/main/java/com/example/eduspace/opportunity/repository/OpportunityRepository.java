package com.example.eduspace.opportunity.repository;

import com.example.eduspace.opportunity.entity.Opportunity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OpportunityRepository
        extends MongoRepository<Opportunity, String>, OpportunityRepositoryCustom {
}