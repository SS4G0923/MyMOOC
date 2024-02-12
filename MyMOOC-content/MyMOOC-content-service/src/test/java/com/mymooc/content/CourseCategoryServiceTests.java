package com.mymooc.content;

import com.mymooc.base.model.PageParams;
import com.mymooc.base.model.PageResult;
import com.mymooc.content.model.dto.CourseCategoryTreeDto;
import com.mymooc.content.model.dto.QueryCourseParamsDto;
import com.mymooc.content.model.po.CourseBase;
import com.mymooc.content.model.po.CourseCategory;
import com.mymooc.content.service.CourseBaseInfoService;
import com.mymooc.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class CourseCategoryServiceTests {

    @Autowired
    CourseCategoryService courseCategoryServiceService;

    @Test
    public void testCourseBaseInfoService() {

        List<CourseCategoryTreeDto> list = courseCategoryServiceService.queryTreeNodes("1");

        System.out.println(list);
    }
}
