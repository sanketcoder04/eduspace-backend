package com.example.eduspace.application.entity;

import com.example.eduspace.application.enums.ApplicationStatus;
import com.example.eduspace.common.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "applications")
@CompoundIndex(name = "opportunity_applicant", def = "{'opportunityId': 1, 'applicantId': 1}")
public class Application extends BaseEntity {

    @Id
    private String id;

    @Indexed
    @Field(targetType = FieldType.OBJECT_ID)
    private String opportunityId;

    @Indexed
    @Field(targetType = FieldType.OBJECT_ID)
    private String applicantId; // whoever clicked Apply

    @Indexed
    @Field(targetType = FieldType.OBJECT_ID)
    private String authorId;    // owner of the opportunity — denormalized for "received applications" queries

    private String message; // optional note sent along with the application

    @Indexed
    private ApplicationStatus status;

    private String decisionReason; // optional — why an author rejected

    private Instant respondedAt; // when author approved/rejected, or applicant withdrew

    @Builder.Default
    private ContactShareConsent contactShareConsent = ContactShareConsent.builder().build();
}