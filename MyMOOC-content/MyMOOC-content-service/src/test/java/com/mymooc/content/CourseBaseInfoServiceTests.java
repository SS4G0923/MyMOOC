package com.mymooc.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mymooc.base.model.PageParams;
import com.mymooc.base.model.PageResult;
import com.mymooc.content.mapper.CourseBaseMapper;
import com.mymooc.content.model.dto.QueryCourseParamsDto;
import com.mymooc.content.model.po.CourseBase;
import com.mymooc.content.service.CourseBaseInfoService;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CourseBaseInfoServiceTests {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Test
    public void testCourseBaseInfoService() {

        QueryCourseParamsDto courseParamsDto = new QueryCourseParamsDto();
        courseParamsDto.setCourseName("java");
        courseParamsDto.setAuditStatus("202004");

        PageResult<CourseBase> courseBasePageResult = courseBaseInfoService.queryCourseBaseList(new PageParams(1, 2), courseParamsDto);

        System.out.println(courseBasePageResult);
    }
}
