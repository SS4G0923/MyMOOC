package com.mymooc.media.api;

import com.mymooc.base.model.PageParams;
import com.mymooc.base.model.PageResult;
import com.mymooc.media.model.dto.QueryMediaParamsDto;
import com.mymooc.media.model.dto.UploadFileParamsDto;
import com.mymooc.media.model.dto.UploadFileResultDto;
import com.mymooc.media.model.po.MediaFiles;
import com.mymooc.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @description 媒资文件管理接口
 * @author Mr.M
 * @date 2022/9/6 11:29
 * @version 1.0
 */
@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {


    @Autowired
    MediaFileService mediaFileService;


    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto){
    Long companyId = 1232141425L;
    return mediaFileService.queryMediaFiels(companyId,pageParams,queryMediaParamsDto);

    }

    @RequestMapping(value = "upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto upload(@RequestPart("filedata")MultipartFile filedata,
                                      @RequestParam(value = "objectName", required = false) String objectName) throws IOException {

        Long companyId = 1232141425L;

        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());
        uploadFileParamsDto.setFileSize(String.valueOf(filedata.getSize()));
        uploadFileParamsDto.setFileType("001001");

        File tempFile = File.createTempFile("minio", ".temp");
        filedata.transferTo(tempFile);
        String localFilePath = tempFile.getAbsolutePath();

		return mediaFileService.uploadFile(companyId, uploadFileParamsDto, localFilePath, objectName);
    }

}
