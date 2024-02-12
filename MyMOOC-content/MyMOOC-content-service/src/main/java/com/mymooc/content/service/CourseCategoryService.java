package com.mymooc.content.service;

import com.mymooc.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

public interface CourseCategoryService {

    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
