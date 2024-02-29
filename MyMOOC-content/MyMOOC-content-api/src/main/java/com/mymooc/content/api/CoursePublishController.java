package com.mymooc.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CoursePublishController {

	@GetMapping("/coursepreview/{courseId}")
	public ModelAndView preview(@PathVariable Long courseId){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("course_template");
		return modelAndView;
	}
}
