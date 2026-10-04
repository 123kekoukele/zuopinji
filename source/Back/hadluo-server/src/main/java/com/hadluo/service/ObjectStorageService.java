package com.hadluo.service;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {

    /** 上传并返回对象键，例如 file/1773588097956.docx */
    String upload(MultipartFile file, String fileName) throws Exception;

    /** 读取对象流，不存在时返回 null */
    InputStream download(String storedPath) throws Exception;

    /** 对象是否存在 */
    boolean exists(String storedPath);

    /** 删除指定前缀下的全部对象（用于单文件业务目录替换） */
    void deleteObjectsByPrefix(String prefix) throws Exception;

    /** MinIO 已配置且连接可用 */
    boolean isAvailable();
}
