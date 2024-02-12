package com.mymooc.content.service;

import com.mymooc.base.model.PageParams;
import com.mymooc.base.model.PageResult;
import com.mymooc.content.model.dto.AddCourseDto;
import com.mymooc.content.model.dto.CourseBaseInfoDto;
import com.mymooc.content.model.dto.QueryCourseParamsDto;
import com.mymooc.content.model.po.CourseBase;

public interface CourseBaseInfoService {

    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);
}
