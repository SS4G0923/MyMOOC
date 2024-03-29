package com.mymooc.content.service;

import com.mymooc.content.model.dto.BindTeachplanMediaDto;
import com.mymooc.content.model.dto.SaveTeachPlanDto;
import com.mymooc.content.model.dto.TeachPlanDto;
import com.mymooc.content.model.po.TeachplanMedia;

import java.util.List;

public interface TeachPlanService {

    public List<TeachPlanDto> findTeachPlanTree(Long courseId);

    public void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto);

    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}
