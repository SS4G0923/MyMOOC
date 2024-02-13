package com.mymooc.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mymooc.base.exception.MyMoocException;
import com.mymooc.base.model.PageParams;
import com.mymooc.base.model.PageResult;
import com.mymooc.content.mapper.CourseBaseMapper;
import com.mymooc.content.mapper.CourseMarketMapper;
import com.mymooc.content.model.dto.AddCourseDto;
import com.mymooc.content.model.dto.CourseBaseInfoDto;
import com.mymooc.content.model.dto.EditCourseDto;
import com.mymooc.content.model.dto.QueryCourseParamsDto;
import com.mymooc.content.model.po.CourseBase;
import com.mymooc.content.model.po.CourseMarket;
import com.mymooc.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto) {

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()), CourseBase::getName, courseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, courseParamsDto.getAuditStatus());

        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        List<CourseBase> items = pageResult.getRecords();
        long total = pageResult.getTotal();

        PageResult<CourseBase> courseBasePageResult = new PageResult<CourseBase>(items, total, pageParams.getPageNo(), pageParams.getPageSize());

        return courseBasePageResult;
    }
    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {

        CourseBase courseBean = new CourseBase();
        BeanUtils.copyProperties(addCourseDto, courseBean);
        courseBean.setCompanyId(companyId);
        courseBean.setCreateDate(LocalDateTime.now());
        courseBean.setAuditStatus("202002");
        courseBean.setStatus("203001");

        int insert = courseBaseMapper.insert(courseBean);
        if(insert <=0 ) throw new RuntimeException("Insert Course Base Info FAILED");

        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        courseMarket.setId(courseBean.getId());
        saveCourseMarket(courseMarket);

        return getCourseBaseInfo(courseBean.getId());
    }
    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null) return null;

        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if(courseMarket != null)
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);

        return courseBaseInfoDto;

    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {

        Long courseId = editCourseDto.getCourseId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null) MyMoocException.cast("Course Not Found");

        if(!companyId.equals(courseBase.getCompanyId())) MyMoocException.cast("You are not allowed to modify this course");

        BeanUtils.copyProperties(editCourseDto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());

        int i = courseBaseMapper.updateById(courseBase);
        if(i <= 0) MyMoocException.cast("Update Course Base Info FAILED");

        CourseBaseInfoDto courseBaseInfo = getCourseBaseInfo(courseId);
        return courseBaseInfo;
    }

    private int saveCourseMarket(CourseMarket courseMarket){
        String charge = courseMarket.getCharge();
        if(StringUtils.isEmpty(charge)){
            throw new RuntimeException("Charge Rule is Empty");
        }

        if(charge.equals("201001")){
            Float price = courseMarket.getPrice();
            if(price == null || price <= 0){
                throw new RuntimeException("Price is Empty or Invalid");
            }
        }

        CourseMarket courseMarketById = courseMarketMapper.selectById(courseMarket.getId());
        if(courseMarketById == null){
            return courseMarketMapper.insert(courseMarket);
        }else {
            BeanUtils.copyProperties(courseMarket, courseMarketById);
            courseMarketById.setId(courseMarket.getId());
            return courseMarketMapper.updateById(courseMarketById);
        }
    }
}
