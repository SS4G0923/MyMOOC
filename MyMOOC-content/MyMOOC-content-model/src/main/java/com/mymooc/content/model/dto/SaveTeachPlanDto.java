package com.mymooc.content.model.dto;

import lombok.Data;

@Data
public class SaveTeachPlanDto {

    private Long id;

    private String pname;

    private String parentId;

    private Integer grade;

    private String mediaTyoe;

    private Long courseId;

    private Long coursePubId;

    private String isPreview;
}
