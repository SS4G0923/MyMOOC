package com.mymooc.content.api;

import com.mymooc.content.model.dto.CoursePreviewDto;
import com.mymooc.content.service.CoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CoursePublishController {

	@Autowired
	CoursePublishService coursePublishService;

	@GetMapping("/coursepreview/{courseId}")
	public ModelAndView preview(@PathVariable Long courseId){
		ModelAndView modelAndView = new ModelAndView();

		CoursePreviewDto coursePreviewDto = coursePublishService.getCoursePreviewInfo(courseId);
		modelAndView.addObject("model", coursePreviewDto);
		modelAndView.setViewName("course_template");

		return modelAndView;
	}
}
