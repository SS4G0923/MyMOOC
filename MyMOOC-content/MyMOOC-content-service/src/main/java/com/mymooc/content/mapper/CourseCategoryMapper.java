package com.mymooc.content.mapper;

import com.mymooc.content.model.dto.CourseCategoryTreeDto;
import com.mymooc.content.model.po.CourseCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    public List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
