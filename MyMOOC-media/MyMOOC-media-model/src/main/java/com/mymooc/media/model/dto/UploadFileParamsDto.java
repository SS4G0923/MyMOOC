package com.mymooc.media.model.dto;

import lombok.Data;

@Data
public class UploadFileParamsDto {

    private String filename;

    private String fileType;

    private String fileSize;

    private String tags;

    private String username;

    private String remark;

}
