package com.mymooc.content.api;

import com.mymooc.content.model.dto.CoursePreviewDto;
import com.mymooc.content.service.CourseBaseInfoService;
import com.mymooc.content.service.CoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/open")
@RestController
public class CourseOpenController {

	@Autowired
	private CourseBaseInfoService courseBaseInfoService;

	@Autowired
	private CoursePublishService coursePublishService;


	@GetMapping("/course/whole/{courseId}")
	public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId) {
		//获取课程预览信息
		return coursePublishService.getCoursePreviewInfo(courseId);

	}

}
