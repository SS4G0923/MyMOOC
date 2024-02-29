package com.mymooc.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mymooc.base.exception.MyMoocException;
import com.mymooc.content.mapper.TeachplanMapper;
import com.mymooc.content.mapper.TeachplanMediaMapper;
import com.mymooc.content.model.dto.BindTeachplanMediaDto;
import com.mymooc.content.model.dto.SaveTeachPlanDto;
import com.mymooc.content.model.dto.TeachPlanDto;
import com.mymooc.content.model.po.Teachplan;
import com.mymooc.content.model.po.TeachplanMedia;
import com.mymooc.content.service.TeachPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeachPlanServiceImpl implements TeachPlanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachPlanDto> findTeachPlanTree(Long courseId) {
        List<TeachPlanDto> res =  teachplanMapper.selectTreeNodes(courseId);
        return res;
    }

    @Override
    public void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto) {
        Long id = saveTeachPlanDto.getId();
        if(id == null){
            Teachplan teachPlan = new Teachplan();
            BeanUtils.copyProperties(saveTeachPlanDto,teachPlan);
            Long parentId = teachPlan.getParentid();
            Long courseId = teachPlan.getCourseId();
            int order = getTeachPlanOrder(parentId, courseId);
            teachPlan.setOrderby(order);

            teachplanMapper.insert(teachPlan);
        }else {
            Teachplan teachPlan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(saveTeachPlanDto,teachPlan);
            teachplanMapper.updateById(teachPlan);
        }
    }

    private int getTeachPlanOrder(Long parentId, Long courseId) {
        LambdaQueryWrapper<Teachplan> wrapper = new LambdaQueryWrapper<Teachplan>().eq(Teachplan::getParentid, parentId).eq(Teachplan::getCourseId, courseId);
        Integer i = teachplanMapper.selectCount(wrapper);
        return i+1;
    }

    @Override
    @Transactional
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto){

        Long teachplanID = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanID);
        if(teachplan == null){
            MyMoocException.cast("课程计划不存在");
            return null;
        }

        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId()));
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        BeanUtils.copyProperties(bindTeachplanMediaDto,teachplanMedia);
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);

        return teachplanMedia;
    }
}
