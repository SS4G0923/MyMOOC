package com.mymooc.content;

import com.mymooc.content.mapper.CourseCategoryMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CourseCategoryMapperTests {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Test
    public void testCourseCategoryMapper(){
        courseCategoryMapper.selectTreeNodes("1");
    }
}
