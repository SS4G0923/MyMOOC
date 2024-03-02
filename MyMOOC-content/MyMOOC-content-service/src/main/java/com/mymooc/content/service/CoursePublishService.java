package com.mymooc.content.service;

import com.mymooc.content.model.dto.CoursePreviewDto;

public interface CoursePublishService {

	CoursePreviewDto getCoursePreviewInfo(Long courseId);

	void commitAudit(Long companyId, Long courseId);

	void publish (Long companyId, Long courseId);
}
