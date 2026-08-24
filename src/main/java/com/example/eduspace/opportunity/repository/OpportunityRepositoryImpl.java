package com.example.eduspace.opportunity.repository;

import com.example.eduspace.opportunity.dto.request.OpportunityFilterRequest;
import com.example.eduspace.opportunity.entity.Opportunity;
import com.example.eduspace.opportunity.enums.OpportunityStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OpportunityRepositoryImpl implements OpportunityRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Opportunity> search(OpportunityFilterRequest filter, Pageable pageable) {
        List<Criteria> criteriaList = new ArrayList<>();

        // Public feed only ever shows postings still accepting applicants,
        // unless the caller explicitly asks for a specific status (e.g. the
        // "my posts" view wants to see CLOSED ones too).
        if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
            criteriaList.add(Criteria.where("status").in(filter.getStatuses()));
        } else {
            criteriaList.add(Criteria.where("status")
                    .in(OpportunityStatus.OPEN, OpportunityStatus.PARTIALLY_FILLED));
        }

        if (filter.getPostType() != null) {
            criteriaList.add(Criteria.where("postType").is(filter.getPostType()));
        }

        if (filter.getCities() != null && !filter.getCities().isEmpty()) {
            criteriaList.add(Criteria.where("location.city").in(filter.getCities()));
        }

        if (filter.getModes() != null && !filter.getModes().isEmpty()) {
            criteriaList.add(Criteria.where("mode").in(filter.getModes()));
        }

        if (filter.getClassFormats() != null && !filter.getClassFormats().isEmpty()) {
            criteriaList.add(Criteria.where("classFormat").in(filter.getClassFormats()));
        }

        if (filter.getSubjects() != null && !filter.getSubjects().isEmpty()) {
            criteriaList.add(Criteria.where("subjects").in(filter.getSubjects()));
        }

        if (filter.getMinFee() != null) {
            criteriaList.add(Criteria.where("feeRange.max").gte(filter.getMinFee()));
        }
        if (filter.getMaxFee() != null) {
            criteriaList.add(Criteria.where("feeRange.min").lte(filter.getMaxFee()));
        }

        if (filter.getPostedAfter() != null) {
            criteriaList.add(Criteria.where("createdAt").gte(filter.getPostedAfter()));
        }

        if (filter.getAuthorId() != null) {
            criteriaList.add(Criteria.where("authorId").is(filter.getAuthorId()));
        }

        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, Opportunity.class);
        query.with(pageable);

        List<Opportunity> results = mongoTemplate.find(query, Opportunity.class);
        return new PageImpl<>(results, pageable, total);
    }
}