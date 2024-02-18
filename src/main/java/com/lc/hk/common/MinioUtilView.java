package com.lc.hk.common;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.lc.hk.config.MinioConfigView;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lc
 */
@Log4j2
@Component
public class MinioUtilView {

    @Autowired
    MinioConfigView minioConfig;
    @Autowired
    MinioClient minioClientView;

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        boolean flag = false;
        flag = minioClientView.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (flag) {
            return true;
        }
        return false;
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     */
    @SneakyThrows
    public boolean makeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            minioClientView.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 列出所有存储桶名称
     *
     * @return
     */
    @SneakyThrows
    public List<String> listBucketNames() {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    /**
     * 列出所有存储桶
     *
     * @return
     */
    @SneakyThrows
    public List<Bucket> listBuckets() {
        return minioClientView.listBuckets();
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public boolean removeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                // 有对象文件，则删除失败
                if (item.size() > 0) {
                    return false;
                }
            }
            // 删除存储桶，注意，只有存储桶为空时才能删除成功。
            minioClientView.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            flag = bucketExists(bucketName);
            if (!flag) {
                return true;
            }

        }
        return false;
    }

    /**
     * 列出存储桶中的所有对象名称
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public List<String> listObjectNames(String bucketName) {
        List<String> listObjectNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        }
        return listObjectNames;
    }

    /**
     * 列出存储桶中的所有对象
     *
     * @param bucketName 存储桶名称
     * @return
     */
    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            return minioClientView.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }


    /**
     * 删除对象tag信息
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     */
    @SneakyThrows
    public void deleteObjectTags(String bucketName, String objectName) {
        minioClientView.deleteObjectTags(DeleteObjectTagsArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 文件url前半段
     *
     * @param bucket 桶
     * @return 前半段
     */
    public String getObjectPrefixUrl(String bucket) {
        return String.format("%s/%s/", minioConfig.getEndpoint(), bucket);
    }

    /**
     * @Description： 生成新文件名
     *
     * @param oldName
     * @return java.lang.String
     */
    private String getNewFileName(String oldName){
        String suffix = oldName.substring(oldName.lastIndexOf("."));
        String name = IdUtil.simpleUUID();
        String timePrefix = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        return timePrefix + "/" + name + suffix;
    }


    /**
     * 下载文件
     *
     * @param bucket    桶
     * @param objectKey 文件key
     * @return 文件流
     */
    public InputStream download(String bucket, String objectKey) throws Exception {
        return minioClientView.getObject(GetObjectArgs.builder().bucket(bucket).object(objectKey).build());
    }

    /**
     * 文件复制
     *
     * @param sourceBucket    源桶
     * @param sourceObjectKey 源文件key
     * @param bucket          桶
     * @param objectKey       文件key
     * @return 新文件url
     */
    public String copyFile(String sourceBucket, String sourceObjectKey, String bucket, String objectKey) throws Exception {
        CopySource source = CopySource.builder().bucket(sourceBucket).object(sourceObjectKey).build();
        minioClientView.copyObject(CopyObjectArgs.builder().bucket(bucket).object(objectKey).source(source).build());
        return getObjectPrefixUrl(bucket) + objectKey;
    }

    /**
     * 删除文件
     *
     * @param bucket    桶
     * @param objectKey 文件key
     */
    public void deleteFile(String bucket, String objectKey) throws Exception {
        minioClientView.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(objectKey).build());
    }

    /**
     * 获取文件签名url
     *
     * @param bucket    桶
     * @param objectKey 文件key
     * @param expires   签名有效时间  单位秒
     * @return 文件签名地址
     */
    public String getSignedUrl(String bucket, String objectKey, int expires){
        if(!StrUtil.isEmpty(objectKey)){
            try{
                return minioClientView.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucket.toLowerCase()).object(objectKey).expiry(expires).build());
            }catch (Exception e){
                log.error("获取文件签名url异常：", e);
            }
        }
        return "";
    }

    /**
     * @Description：获取桶下对象访问路径
     *
     * @param bucket
     * @param objectKey
     * @return java.lang.String
     */
    public String getUrl(String bucket, String objectKey) {
        if(StrUtil.isEmpty(objectKey)){
            return "";
        }
        return getObjectPrefixUrl(bucket) + objectKey;
    }

}