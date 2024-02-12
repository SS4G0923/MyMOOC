package com.mymooc.content.api;

import com.mymooc.content.model.dto.CourseCategoryTreeDto;
import com.mymooc.content.service.CourseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CourseCategoryController {

    @Autowired
    CourseCategoryService courseCategoryService;

    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return courseCategoryService.queryTreeNodes("1");
    }
}
