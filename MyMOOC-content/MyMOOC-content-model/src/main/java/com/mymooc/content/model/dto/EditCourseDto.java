package com.mymooc.content.model.dto;

import lombok.Data;

@Data
public class EditCourseDto extends AddCourseDto{
    private Long courseId;

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
