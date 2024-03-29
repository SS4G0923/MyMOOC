package com.mymooc.content.service.jobhandler;

import com.mymooc.base.exception.MyMoocException;
import com.mymooc.content.feignclient.SearchServiceClient;
import com.mymooc.content.mapper.CoursePublishMapper;
import com.mymooc.content.model.dto.CoursePreviewDto;
import com.mymooc.content.model.po.CoursePublish;
import com.mymooc.content.service.CoursePublishService;
import com.mymooc.messagesdk.model.po.MqMessage;
import com.mymooc.messagesdk.service.MessageProcessAbstract;
import com.mymooc.messagesdk.service.MqMessageService;
import com.mymooc.search.po.CourseIndex;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

	@Autowired
	CoursePublishService coursePublishService;

	@Autowired
	SearchServiceClient searchServiceClient;

	@Autowired
	CoursePublishMapper coursePublishMapper;

	@XxlJob("CoursePublishJobHandler")
	public void coursePublishJobHandler() throws Exception{
		int shardIndex = XxlJobHelper.getShardIndex();
		int shardTotal = XxlJobHelper.getShardTotal();
		log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
		//参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
		process(shardIndex,shardTotal,"course_publish",30,60);

	}

	//课程发布任务处理
	@Override
	public boolean execute(MqMessage mqMessage) {
		//获取消息相关的业务信息
		String businessKey1 = mqMessage.getBusinessKey1();
		Long courseId = Long.parseLong(businessKey1);
		//课程静态化
		generateCourseHtml(mqMessage,courseId);
		//课程索引
		saveCourseIndex(mqMessage,courseId);
		//课程缓存
		saveCourseCache(mqMessage,courseId);
		return true;
	}


	//生成课程静态化页面并上传至文件系统
	public void generateCourseHtml(MqMessage mqMessage,long courseId){

		log.debug("开始进行课程静态化,课程id:{}",courseId);
		//消息id
		Long id = mqMessage.getId();
		//消息处理的service
		MqMessageService mqMessageService = this.getMqMessageService();
		//消息幂等性处理
		int stageOne = mqMessageService.getStageOne(id);
		if(stageOne >0){
			log.debug("课程静态化已处理直接返回，课程id:{}",courseId);
			return ;
		}

		//开始课程静态化
		File file = coursePublishService.generateCourseHtml(courseId);
		if(file == null)
			MyMoocException.cast("生成的静态页面为空");

		coursePublishService.uploadCourseHtml(courseId, file);

		//任务处理完成，保存第一阶段状态
		mqMessageService.completedStageOne(id);

	}

	//将课程信息缓存至redis
	public void saveCourseCache(MqMessage mqMessage,long courseId){
		log.debug("将课程信息缓存至redis,课程id:{}",courseId);
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}


	}
	//保存课程索引信息
	public void saveCourseIndex(MqMessage mqMessage,long courseId){
		log.debug("保存课程索引信息,课程id:{}",courseId);
		Long taskId = mqMessage.getId();
		MqMessageService mqMessageService = this.getMqMessageService();
		int stageTwo = mqMessageService.getStageTwo(taskId);
		if(stageTwo >0){
			log.debug("课程索引已处理直接返回，课程id:{}",courseId);
			return;
		}
		//查询课程信息，调用搜索服务添加索引
		CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
		CourseIndex courseIndex = new CourseIndex();
		BeanUtils.copyProperties(coursePublish,courseIndex);
		//调用搜索服务添加索引
		Boolean add = searchServiceClient.add(courseIndex);
		if(!add){
			MyMoocException.cast("添加课程索引失败");
		}

		mqMessageService.completedStageTwo(taskId);
	}

}

