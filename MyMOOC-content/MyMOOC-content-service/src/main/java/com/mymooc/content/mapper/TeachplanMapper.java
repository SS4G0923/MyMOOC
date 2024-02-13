package com.mymooc.content.mapper;

import com.mymooc.content.model.dto.TeachPlanDto;
import com.mymooc.content.model.po.Teachplan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    public List<TeachPlanDto> selectTreeNodes(Long courseId);
}
