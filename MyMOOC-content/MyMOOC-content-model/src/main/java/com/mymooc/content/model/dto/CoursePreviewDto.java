package com.mymooc.content.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoursePreviewDto {

	private CourseBaseInfoDto courseBase;

	private List<TeachPlanDto> teachplans;
}
