package com.spring.java.alibaba.oss.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectListing;
import com.spring.java.alibaba.oss.util.DemoOssConstants;
import org.apache.commons.codec.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class DemoOssController {

    @Autowired
    private OSS ossClient;

    @GetMapping("/buckets/list")
    public List<Bucket> listBuckets() {
        return ossClient.listBuckets();
    }

    @PostMapping("/buckets/create")
    public Bucket createBucket(@RequestParam String bucketName) throws InterruptedException{
        if(ossClient.doesBucketExist(bucketName)){
            return ossClient.getBucketInfo(bucketName).getBucket();
        }else{
            CreateBucketRequest cbr = new CreateBucketRequest(bucketName);
            cbr.setLocationConstraint(DemoOssConstants.BUCKET_LOCATION); // Se pueden agregar más propiedades al momento de crear el bucket
            cbr.setBucketName(bucketName);
            Bucket result = ossClient.createBucket(cbr);
            Thread.sleep(5000);
            return ossClient.getBucketInfo(result.getName()).getBucket();
        }
    }

    @DeleteMapping("/buckets/delete")
    public String deleteBucket(@RequestParam String bucketName) throws InterruptedException{
        if(ossClient.doesBucketExist(bucketName)){
            ossClient.deleteBucket(bucketName);
            return "delete success";
        }else{
            return "bucket not exists";
        }
    }

    @GetMapping("/buckets/exist/{bucketName}")
    public boolean existBucket(@PathVariable String bucketName) {
        return ossClient.doesBucketExist(bucketName);
    }

    @GetMapping("/objects/upload")
    public String upload() {
        try {
            ossClient.putObject(DemoOssConstants.BUCKET_NAME, "file-example.json", this
                    .getClass().getClassLoader().getResourceAsStream("file-example.json"));
        } catch (Exception e) {
            e.printStackTrace();
            return "upload fail: " + e.getMessage();
        }
        return "upload success";
    }

    @GetMapping("/objects/download")
    public String download() {
        try {
            OSSObject ossObject = ossClient.getObject(DemoOssConstants.BUCKET_NAME, "file-example.json");
            return "download success, content: \n" + IOUtils
                    .readStreamAsString(ossObject.getObjectContent(), CharEncoding.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "download fail: " + e.getMessage();
        }
    }

    @GetMapping("/objects/list")
    public ObjectListing listObjects() {
        return ossClient.listObjects(DemoOssConstants.BUCKET_NAME);
    }

    @GetMapping("/objects/exist/{objectName}")
    public boolean existObject(@PathVariable String objectName) {
        return ossClient.doesObjectExist(DemoOssConstants.BUCKET_NAME, objectName);
    }

}
