package com.mymooc.content.service;

import com.mymooc.content.model.dto.CoursePreviewDto;

import java.io.File;

public interface CoursePublishService {

	CoursePreviewDto getCoursePreviewInfo(Long courseId);

	void commitAudit(Long companyId, Long courseId);

	void publish (Long companyId, Long courseId);

	File generateCourseHtml(Long courseId);

	void uploadCourseHtml(Long courseId, File file);
}
