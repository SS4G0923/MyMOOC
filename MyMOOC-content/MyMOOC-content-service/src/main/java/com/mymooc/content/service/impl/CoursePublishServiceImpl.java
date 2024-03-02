package com.mymooc.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.mymooc.base.exception.MyMoocException;
import com.mymooc.content.mapper.CourseBaseMapper;
import com.mymooc.content.mapper.CourseMarketMapper;
import com.mymooc.content.mapper.CoursePublishPreMapper;
import com.mymooc.content.model.dto.CourseBaseInfoDto;
import com.mymooc.content.model.dto.CoursePreviewDto;
import com.mymooc.content.model.dto.TeachPlanDto;
import com.mymooc.content.model.po.CourseBase;
import com.mymooc.content.model.po.CourseMarket;
import com.mymooc.content.model.po.CoursePublishPre;
import com.mymooc.content.service.CourseBaseInfoService;
import com.mymooc.content.service.CoursePublishService;
import com.mymooc.content.service.TeachPlanService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoursePublishServiceImpl implements CoursePublishService {

	@Autowired
	CourseBaseInfoService courseBaseInfoService;

	@Autowired
	TeachPlanService teachPlanService;

	@Autowired
	CourseMarketMapper courseMarketMapper;

	@Autowired
	CoursePublishPreMapper coursePublishPreMapper;

	@Autowired
	CourseBaseMapper courseBaseMapper;

	@Override
	public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
		CoursePreviewDto coursePreviewDto = new CoursePreviewDto();

		CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
		List<TeachPlanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
		coursePreviewDto.setCourseBase(courseBaseInfo);
		coursePreviewDto.setTeachplans(teachPlanTree);

		return coursePreviewDto;
	}

	@Override
	@Transactional
	public void commitAudit(Long companyId, Long courseId) {

		CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
		if(courseBaseInfo == null)
			MyMoocException.cast("Course not found");

		String auditStatus = courseBaseInfo.getAuditStatus();
		if(auditStatus.equals("202003"))
			MyMoocException.cast("Course already committed");

		String pic = courseBaseInfo.getPic();
		if(StringUtils.isEmpty(pic))
			MyMoocException.cast("Course picture not found");

		List<TeachPlanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
		if(teachPlanTree == null || teachPlanTree.isEmpty())
			MyMoocException.cast("Course Teach Plan not found");

		CoursePublishPre coursePublishPre = new CoursePublishPre();
		BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
		coursePublishPre.setCompanyId(companyId);

		CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
		String courseMarketJson = JSON.toJSONString(courseMarket);
		coursePublishPre.setMarket(courseMarketJson);

		String teachPlanJson = JSON.toJSONString(teachPlanTree);
		coursePublishPre.setTeachplan(teachPlanJson);

		coursePublishPre.setStatus("202003");
		coursePublishPre.setCreateDate(LocalDateTime.now());

		CoursePublishPre coursePublishPre1 = coursePublishPreMapper.selectById(courseId);
		if(coursePublishPre1 == null)
			coursePublishPreMapper.insert(coursePublishPre);
		else
			coursePublishPreMapper.updateById(coursePublishPre);

		CourseBase courseBase = courseBaseMapper.selectById(courseId);
		courseBase.setStatus("202003");
		courseBaseMapper.updateById(courseBase);
	}
}
