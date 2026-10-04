package com.hadluo.service.impl;

import java.io.InputStream;
import java.net.URI;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hadluo.config.MinioProperties;
import com.hadluo.service.ObjectStorageService;
import com.hadluo.utils.StudentFileStorageUtil;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.messages.Item;

@Service
public class MinioStorageServiceImpl implements ObjectStorageService {

    private static final Logger log = LoggerFactory.getLogger(MinioStorageServiceImpl.class);

    @Autowired
    private MinioProperties minioProperties;

    private MinioClient minioClient;

    /** 配置启用且已成功连上 MinIO */
    private volatile boolean ready = false;

    @PostConstruct
    public void init() {
        if (!minioProperties.isEnabled()) {
            log.info("MinIO 未启用，文件将使用本地 upload/file 目录");
            return;
        }
        try {
            URI uri = URI.create(minioProperties.getEndpoint());
            boolean secure = "https".equalsIgnoreCase(uri.getScheme());
            int port = uri.getPort() > 0 ? uri.getPort() : (secure ? 443 : 9000);
            minioClient = MinioClient.builder()
                    .endpoint(uri.getHost(), port, secure)
                    .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                    .build();
            ensureBucket();
            ready = true;
            log.info("MinIO 已连接: {} bucket={}", minioProperties.getEndpoint(), minioProperties.getBucketName());
        } catch (Exception ex) {
            ready = false;
            minioClient = null;
            log.warn("MinIO 连接失败，将回退到本地 upload/file 目录: {}", ex.getMessage());
        }
    }

    private void ensureBucket() throws Exception {
        String bucket = minioProperties.getBucketName();
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    @Override
    public boolean isAvailable() {
        return minioProperties.isEnabled() && ready && minioClient != null;
    }

    @Override
    public String upload(MultipartFile file, String fileName) throws Exception {
        if (!isAvailable()) {
            throw new IllegalStateException("MinIO storage is not available");
        }
        String objectName = toObjectName(fileName);
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(StringUtils.defaultIfBlank(file.getContentType(), "application/octet-stream"))
                        .build()
        );
        return objectName;
    }

    @Override
    public InputStream download(String storedPath) throws Exception {
        if (!isAvailable()) {
            return null;
        }
        String objectName = toObjectName(storedPath);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(objectName)
                            .build()
            );
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public boolean exists(String storedPath) {
        if (!isAvailable()) {
            return false;
        }
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(toObjectName(storedPath))
                            .build()
            );
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public void deleteObjectsByPrefix(String prefix) throws Exception {
        if (!isAvailable()) {
            return;
        }
        if (StringUtils.isBlank(prefix)) {
            return;
        }
        String objectPrefix = toObjectName(prefix);
        if (StringUtils.isBlank(objectPrefix)) {
            return;
        }
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(minioProperties.getBucketName())
                        .prefix(objectPrefix)
                        .recursive(true)
                        .build()
        );
        for (Result<Item> result : results) {
            Item item = result.get();
            if (item.isDir()) {
                continue;
            }
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(item.objectName())
                            .build()
            );
        }
    }

    /** 统一对象键，保留完整分层路径，兼容旧版扁平 file/xxx 文件名 */
    public String toObjectName(String storedPath) {
        String normalized = StudentFileStorageUtil.normalizeStoredPath(storedPath);
        if (normalized != null) {
            return normalized;
        }
        return storedPath;
    }
}
