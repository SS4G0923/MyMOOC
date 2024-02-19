package com.mymooc.media.api;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.mymooc.base.model.RestResponse;
import com.mymooc.media.model.dto.UploadFileParamsDto;
import com.mymooc.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class LargeFilesController {

	@Autowired
	MediaFileService mediaFileService;

	@PostMapping("/upload/checkfile")
	public RestResponse<Boolean> checkfile (@RequestParam("fileMd5") String fileMd5) throws Exception {
		return mediaFileService.checkFile(fileMd5);
	}


	@PostMapping("/upload/checkchunk")
	public RestResponse<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
											@RequestParam("chunk") int chunk) throws Exception {
		return mediaFileService.checkChunk(fileMd5, chunk);
	}

	@PostMapping("/upload/uploadchunk")
	public RestResponse uploadchunk(@RequestParam("file") MultipartFile file,
									@RequestParam("fileMd5") String fileMd5,
									@RequestParam("chunk") int chunk) throws Exception {

		return null;
	}

	@PostMapping("/upload/mergechunks")
	public RestResponse mergechunks(@RequestParam("fileMd5") String fileMd5,
									@RequestParam("fileName") String fileName,
									@RequestParam("chunkTotal") int chunkTotal) throws Exception {
		return null;

	}


}

