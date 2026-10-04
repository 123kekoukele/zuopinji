package com.hadluo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /** 是否启用 MinIO 存储（本地默认 false，生产环境在 application-prod.yml 中开启） */
    private boolean enabled = false;

    /** 例如 http://127.0.0.1:9000 或 http://127.0.0.1:9000 */
    private String endpoint = "http://127.0.0.1:9000";

    private String accessKey = "hadluo";

    private String secretKey = "HadluoMinio@2026";

    private String bucketName = "hadluo-files";

    /** 对象名前缀，与数据库 file/ 路径保持一致 */
    private String objectPrefix = "file/";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectPrefix() {
        return objectPrefix;
    }

    public void setObjectPrefix(String objectPrefix) {
        this.objectPrefix = objectPrefix;
    }
}
