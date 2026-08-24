package com.example.eduspace.opportunity.entity;

import com.example.eduspace.common.entity.Address;
import com.example.eduspace.common.entity.BaseEntity;
import com.example.eduspace.common.enums.Role;
import com.example.eduspace.opportunity.enums.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "opportunities")
@CompoundIndexes({
        @CompoundIndex(name = "feed_default", def = "{'postType': 1, 'status': 1, 'createdAt': -1}"),
        @CompoundIndex(name = "feed_location_mode", def = "{'location.city': 1, 'mode': 1, 'status': 1}")
})
public class Opportunity extends BaseEntity {

    @Id
    private String id;

    @Field(targetType = FieldType.OBJECT_ID)
    @Indexed
    private String authorId;

    private Role authorRole; // TEACHER | STUDENT — denormalized so the feed never has to join

    @Indexed
    private PostType postType;

    private String title;

    @Indexed
    private List<String> subjects;

    private String gradeLevel; // e.g. "Class 11-12", "JEE", "NEET"

    private String board;      // e.g. "CBSE", "ICSE", "State Board", "IB"

    private String description;

    @Indexed
    private Mode mode;

    private ClassFormat classFormat;

    /** Required/shown only when mode != ONLINE. */
    private Address location;

    /** Offline only. */
    private TuitionLocationType tuitionLocationType;

    private FeeRange feeRange;

    private Double sessionDurationHours;

    private Integer sessionsPerWeek;

    private LocalDate preferredStartDate;

    @Indexed
    private OpportunityStatus status;

    @Builder.Default
    private int applicationsCount = 0;

    // ---- postType == TEACHING_OPENING ----
    private TeachingOpeningDetails teachingOpeningDetails;

    // ---- postType == TUITION_REQUIREMENT ----
    private TuitionRequirementDetails tuitionRequirementDetails;
}