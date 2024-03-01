package com.mymooc.content.service.impl;

import com.mymooc.content.model.dto.CourseBaseInfoDto;
import com.mymooc.content.model.dto.CoursePreviewDto;
import com.mymooc.content.model.dto.TeachPlanDto;
import com.mymooc.content.service.CourseBaseInfoService;
import com.mymooc.content.service.CoursePublishService;
import com.mymooc.content.service.TeachPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoursePublishServiceImpl implements CoursePublishService {

	@Autowired
	CourseBaseInfoService courseBaseInfoService;

	@Autowired
	TeachPlanService teachPlanService;

	@Override
	public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
		CoursePreviewDto coursePreviewDto = new CoursePreviewDto();

		CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
		List<TeachPlanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
		coursePreviewDto.setCourseBase(courseBaseInfo);
		coursePreviewDto.setTeachplans(teachPlanTree);

		return coursePreviewDto;
	}
}
