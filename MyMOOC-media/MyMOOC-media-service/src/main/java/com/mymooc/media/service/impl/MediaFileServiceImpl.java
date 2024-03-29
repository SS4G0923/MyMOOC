package com.mymooc.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.mymooc.base.exception.MyMoocException;
import com.mymooc.base.model.PageParams;
import com.mymooc.base.model.PageResult;
import com.mymooc.base.model.RestResponse;
import com.mymooc.media.mapper.MediaFilesMapper;
import com.mymooc.media.mapper.MediaProcessMapper;
import com.mymooc.media.model.dto.QueryMediaParamsDto;
import com.mymooc.media.model.dto.UploadFileParamsDto;
import com.mymooc.media.model.dto.UploadFileResultDto;
import com.mymooc.media.model.po.MediaFiles;
import com.mymooc.media.model.po.MediaProcess;
import com.mymooc.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MinioClient minioClient;

    @Autowired
    MediaFileService currentProxy;

    @Autowired
    MediaProcessMapper mediaProcessMapper;

    //普通文件桶
    @Value("${minio.bucket.files}")
    private String bucket_files;

    @Value("${minio.bucket.videofiles}")
    private String bucket_videos;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

   }


    //获取文件默认存储目录路径 年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/")+"/";
        return folder;
    }

    //获取文件的md5
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5Hex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private String getMimeType(String extension){
        if(extension==null)
            extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if(extensionMatch!=null){
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }
    /**
     * @description 将文件写入minIO
     * @param localFilePath  文件地址
     * @param bucket  桶
     * @param objectName 对象名称
     * @return void
     * @author Mr.M
     * @date 2022/10/12 21:22
     */
    public boolean addMediaFilesToMinIO(String localFilePath,String mimeType,String bucket, String objectName) {
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(localFilePath)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(testbucket);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}",bucket,objectName);
            System.out.println("上传成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio出错,bucket:{},objectName:{},错误原因:{}",bucket,objectName,e.getMessage(),e);
            MyMoocException.cast("上传文件到文件系统失败");
        }
        return false;
    }


    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName){
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert < 0) {
                log.error("保存文件信息到数据库失败,{}",mediaFiles.toString());
                MyMoocException.cast("保存文件信息失败");
                return null;
            }
            log.debug("保存文件信息到数据库成功,{}",mediaFiles.toString());

        }

        addWaitingTask(mediaFiles);
        log.debug("保存文件待处理信息到数据库成功");


        return mediaFiles;

    }

    private void addWaitingTask(MediaFiles mediaFiles){
        //文件名称
        String filename = mediaFiles.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //文件mimeType
        String mimeType = getMimeType(extension);
        //如果是avi视频添加到视频待处理表
        if(mimeType.equals("video/x-msvideo")){
            MediaProcess mediaProcess = new MediaProcess();

            BeanUtils.copyProperties(mediaFiles,mediaProcess);
            mediaProcess.setStatus("1");//未处理
            mediaProcess.setFailCount(0);//失败次数默认为0
            mediaProcess.setCreateDate(LocalDateTime.now());

            mediaProcessMapper.insert(mediaProcess);
        }
    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName) {
        File file = new File(localFilePath);
        if (!file.exists()) {
            MyMoocException.cast("文件不存在");
        }
        //文件名称
        String filename = uploadFileParamsDto.getFilename();
        //文件扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //文件mimeType
        String mimeType = getMimeType(extension);
        //文件的md5值
        String fileMd5 = getFileMd5(file);
        //文件的默认目录
        String defaultFolderPath = getDefaultFolderPath();
        //存储到minio中的对象名(带目录)
        if(StringUtils.isEmpty(objectName)){
            objectName = defaultFolderPath + fileMd5 + extension;
        }
        //将文件上传到minio
        boolean b = addMediaFilesToMinIO(localFilePath, mimeType, bucket_files, objectName);
        //将文件信息存储到数据库
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_files, objectName);
        if(mediaFiles == null){
            MyMoocException.cast("保存文件信息失败");
        }
        //准备返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;

    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {

        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);

        if (mediaFiles != null) {

            GetObjectArgs build = GetObjectArgs.builder().bucket(mediaFiles.getBucket()).object(mediaFiles.getFilePath()).build();
            try {
                GetObjectResponse object = minioClient.getObject(build);
                if (object != null) {
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {

        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);

        GetObjectArgs build = GetObjectArgs.builder()
                .bucket(bucket_videos)
                .object(chunkFileFolderPath + chunkIndex)
                .build();

        try {
            GetObjectResponse object = minioClient.getObject(build);
            if(object != null){
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return RestResponse.success(false);
    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {

        String mimeType = getMimeType(null);

        boolean b = addMediaFilesToMinIO(localChunkFilePath, mimeType, bucket_videos, getChunkFileFolderPath(fileMd5) + chunk);
        if(!b){
            return RestResponse.validfail(false, "上传分块失败");
        }

        return RestResponse.success(true);
    }

    @Override
    public RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {

        List<ComposeSource> sources = Stream.iterate(0, i -> ++i).limit(chunkTotal).map(i -> ComposeSource.builder().bucket(bucket_videos).object(getChunkFileFolderPath(fileMd5) + i).build()).collect(Collectors.toList());

        String object = getFilePathByMd5(fileMd5, uploadFileParamsDto.getFilename().substring(uploadFileParamsDto.getFilename().lastIndexOf(".")));
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket(bucket_videos)
                .object(object)
                .sources(sources)
                .build();

		try {
			minioClient.composeObject(composeObjectArgs);
		} catch (Exception e) {
            log.error("合并分块失败, bucket: {}, error message: {}", bucket_videos, e.getMessage());
            return RestResponse.validfail(false, "合并分块失败");
        }

        File file = downloadFileFromMinio(bucket_videos, object);
        try {

            String md5Hex = DigestUtils.md5Hex(Files.newInputStream(file.toPath()));
            if(!fileMd5.equals(md5Hex)){
                log.error("文件校验失败, fileMd5: {}, md5Hex: {}", fileMd5, md5Hex);
                return RestResponse.validfail(false, "文件校验失败");
            }

            uploadFileParamsDto.setFileSize(String.valueOf(file.length()));
        } catch(Exception e) {
            log.error("文件校验失败, error message: {}", e.getMessage());
            return RestResponse.validfail(false, "文件校验失败");
        }

        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videos, object);

        if(mediaFiles == null){
            return RestResponse.validfail(false, "保存文件信息失败");
        }

        cleanChunkFiles(getChunkFileFolderPath(fileMd5), chunkTotal);

        return RestResponse.success(true);
    }

    @Override
    public File downloadFileFromMinio(String bucket, String objectName){
        File minioFile = null;
        FileOutputStream outputStream = null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(objectName).build());
            minioFile = File.createTempFile("minio", ".merge");
            outputStream = new FileOutputStream(minioFile);
            IOUtils.copy(stream, outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    @Override
    public MediaFiles getFileById(String mediaId){
        return mediaFilesMapper.selectById(mediaId);
    }

    private String getFilePathByMd5(String fileMd5, String fileExt){
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

    private String getChunkFileFolderPath(String fileMd5){
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 +  "/" + "chunk" + "/";
    }

    private void cleanChunkFiles(String chunkFileFolderPath, int chunkTotal){

        Iterable<DeleteObject> objects = Stream.iterate(0, i -> ++i).limit(chunkTotal).map(i -> new DeleteObject(chunkFileFolderPath + i)).collect(Collectors.toList());
        RemoveObjectsArgs build = RemoveObjectsArgs.builder().bucket(bucket_videos).objects(objects).build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(build);
        results.forEach(f -> {
			try {
				f.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

    }
}
