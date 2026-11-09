package com.example.eduspace.application.repository;

import com.example.eduspace.application.dto.request.ApplicationSearchRequest;
import com.example.eduspace.application.entity.Application;
import com.example.eduspace.application.enums.ApplicationBox;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class ApplicationRepositoryImpl implements ApplicationRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Application> search(String userId, ApplicationSearchRequest filter, String keyword,
                                    List<String> matchedOpportunityIds, List<String> matchedUserIds,
                                    Pageable pageable) {
        boolean sent = filter.getBox() == ApplicationBox.SENT;
        String ownField = sent ? "applicantId" : "authorId";
        String otherPartyField = sent ? "authorId" : "applicantId";

        List<Criteria> all = new ArrayList<>();

        all.add(Criteria.where(ownField).is(userId));

        if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
            all.add(Criteria.where("status").in(filter.getStatuses()));
        }

        if (filter.getPostedAfter() != null) {
            all.add(Criteria.where("createdAt").gte(filter.getPostedAfter()));
        }

        if (keyword != null) {
            List<Criteria> anyOf = new ArrayList<>();
            anyOf.add(Criteria.where("message").regex(Pattern.quote(keyword), "i"));
            if (!matchedOpportunityIds.isEmpty()) {
                anyOf.add(Criteria.where("opportunityId").in(matchedOpportunityIds));
            }
            if (!matchedUserIds.isEmpty()) {
                anyOf.add(Criteria.where(otherPartyField).in(matchedUserIds));
            }
            all.add(new Criteria().orOperator(anyOf.toArray(new Criteria[0])));
        }

        Query query = new Query(new Criteria().andOperator(all.toArray(new Criteria[0])));

        long total = mongoTemplate.count(query, Application.class);
        query.with(pageable);

        return new PageImpl<>(mongoTemplate.find(query, Application.class), pageable, total);
    }
}