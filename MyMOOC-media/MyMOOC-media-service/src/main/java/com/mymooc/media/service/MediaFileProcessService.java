package com.mymooc.media.service;

import com.mymooc.media.model.po.MediaProcess;

import java.util.List;

public interface MediaFileProcessService {

	public List<MediaProcess> getMediaProcessList(int shardTotal, int shardIndex, int count);
}
