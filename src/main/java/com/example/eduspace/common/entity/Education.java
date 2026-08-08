package com.example.eduspace.common.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    private String id;

    private String institution;

    private String degree;

    private String fieldOfStudy;

    private String board;

    private Integer startYear;

    private Integer endYear;
}