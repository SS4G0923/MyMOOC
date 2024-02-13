package com.mymooc.content.service.impl;

import com.mymooc.content.mapper.TeachplanMapper;
import com.mymooc.content.model.dto.TeachPlanDto;
import com.mymooc.content.service.TeachPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeachPlanServiceImpl implements TeachPlanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Override
    public List<TeachPlanDto> findTeachPlanTree(Long courseId) {
        List<TeachPlanDto> res =  teachplanMapper.selectTreeNodes(courseId);
        return res;
    }
}
