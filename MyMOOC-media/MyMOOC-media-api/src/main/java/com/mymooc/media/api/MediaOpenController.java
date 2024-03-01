package com.mymooc.media.api;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.mymooc.base.exception.MyMoocException;
import com.mymooc.base.model.RestResponse;
import com.mymooc.media.model.po.MediaFiles;
import com.mymooc.media.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open")
public class MediaOpenController {

	@Autowired
	MediaFileService mediaFileService;

	@GetMapping("/preview/{mediaId}")
	public RestResponse<String> getPlayUrlByMediaId(@PathVariable String mediaId){

		MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
		if(mediaFiles == null || StringUtils.isEmpty(mediaFiles.getUrl())){
			MyMoocException.cast("视频还没有转码处理");
		}
		return RestResponse.success(mediaFiles.getUrl());

	}

}
