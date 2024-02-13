package com.mymooc.content.service;

import com.mymooc.content.model.dto.TeachPlanDto;

import java.util.List;

public interface TeachPlanService {

    public List<TeachPlanDto> findTeachPlanTree(Long courseId);
}
