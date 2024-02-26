package com.mymooc.media.service.impl;

import com.mymooc.media.mapper.MediaProcessMapper;
import com.mymooc.media.model.po.MediaProcess;
import com.mymooc.media.service.MediaFileProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

	@Autowired
	MediaProcessMapper mediaProcessMapper;

	@Override
	public List<MediaProcess> getMediaProcessList(int shardTotal, int shardIndex, int count) {
		return mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
	}

	@Override
	public boolean startTask(long id) {
		int result = mediaProcessMapper.startTask(id);
		return result > 0;
	}

}
