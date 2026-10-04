package com.hadluo.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hadluo.annotation.IgnoreAuth;
import com.hadluo.entity.ConfigEntity;
import com.hadluo.entity.EIException;
import com.hadluo.entity.TokenEntity;
import com.hadluo.entity.XueshengEntity;
import com.hadluo.service.ConfigService;
import com.hadluo.service.ObjectStorageService;
import com.hadluo.service.TokenService;
import com.hadluo.service.XueshengService;
import com.hadluo.utils.R;
import com.hadluo.utils.StudentFileStorageUtil;

/**
 * 上传文件映射表
 */
@RestController
@RequestMapping("file")
@SuppressWarnings({"unchecked","rawtypes"})
public class FileController{
	@Autowired
    private ConfigService configService;
	@Autowired
	private ResourceLoader resourceLoader;
	@Autowired
	private ObjectStorageService objectStorageService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private XueshengService xueshengService;

	/** 候选上传目录：兼容从 Back 或 Back/hadluo-server 启动时路径不一致的问题 */
	private File[] getUploadDirCandidates() throws IOException {
		String base = System.getProperty("user.dir");
		File current = new File(base, "upload/file").getCanonicalFile();
		File parent = new File(base, "../upload/file").getCanonicalFile();
		return new File[] { parent, current };
	}

	/** 获取持久化上传目录，优先使用已有文件的上传目录 */
	private File getUploadDir() throws IOException {
		File[] candidates = getUploadDirCandidates();
		for (File candidate : candidates) {
			if (candidate.exists() && candidate.isDirectory()) {
				File[] files = candidate.listFiles();
				if (files != null && files.length > 0) {
					return candidate;
				}
			}
		}
		String base = System.getProperty("user.dir").replace('\\', '/');
		File preferred = base.endsWith("/hadluo-server") || base.endsWith("\\hadluo-server")
				? candidates[0]
				: candidates[1];
		if (!preferred.exists()) {
			preferred.mkdirs();
		}
		return preferred;
	}

	private String extractBaseName(String storedPath) {
		if (StringUtils.isBlank(storedPath)) {
			return null;
		}
		String normalized = storedPath.replace('\\', '/');
		int slash = normalized.lastIndexOf('/');
		return slash >= 0 ? normalized.substring(slash + 1) : normalized;
	}

	private File resolveLocalUploadedFile(String storedPath) throws IOException {
		String normalized = StudentFileStorageUtil.normalizeStoredPath(storedPath);
		if (StringUtils.isBlank(normalized)) {
			return null;
		}
		String relative = StudentFileStorageUtil.toLocalRelativePath(normalized);
		for (File dir : getUploadDirCandidates()) {
			File nested = new File(dir, relative.replace('/', File.separatorChar));
			if (nested.exists() && nested.isFile()) {
				return nested;
			}
		}
		String baseName = extractBaseName(relative);
		if (StringUtils.isNotBlank(baseName)) {
			for (File dir : getUploadDirCandidates()) {
				File flat = new File(dir, baseName);
				if (flat.exists() && flat.isFile()) {
					return flat;
				}
			}
		}
		return null;
	}

	private byte[] readStoredFileBytes(String storedPath) throws Exception {
		String normalized = StudentFileStorageUtil.normalizeStoredPath(storedPath);
		if (objectStorageService.isAvailable()) {
			if (normalized != null && objectStorageService.exists(normalized)) {
				InputStream in = objectStorageService.download(normalized);
				if (in != null) {
					try {
						return IOUtils.toByteArray(in);
					} finally {
						in.close();
					}
				}
			}
			String baseName = extractBaseName(normalized);
			if (StringUtils.isNotBlank(baseName) && !baseName.equals(normalized)
					&& objectStorageService.exists(baseName)) {
				InputStream in = objectStorageService.download(baseName);
				if (in != null) {
					try {
						return IOUtils.toByteArray(in);
					} finally {
						in.close();
					}
				}
			}
		}
		File local = resolveLocalUploadedFile(storedPath);
		if (local != null) {
			return FileUtils.readFileToByteArray(local);
		}
		String baseName = extractBaseName(normalized);
		if (StringUtils.isNotBlank(baseName)) {
			Resource resource = resourceLoader.getResource("classpath:static/file/" + baseName);
			if (resource.isReadable()) {
				return FileUtils.readFileToByteArray(resource.getFile());
			}
		}
		return null;
	}

	private String resolveUploadExtension(MultipartFile file) {
		String originalName = file.getOriginalFilename();
		if (StringUtils.isNotBlank(originalName)) {
			int dot = originalName.lastIndexOf('.');
			if (dot >= 0 && dot < originalName.length() - 1) {
				return originalName.substring(dot + 1);
			}
		}
		return "bin";
	}

	private XueshengEntity resolveStudent(HttpServletRequest request) {
		Object tableName = request.getSession().getAttribute("tableName");
		if ("xuesheng".equals(tableName)) {
			Object userIdObj = request.getSession().getAttribute("userId");
			if (userIdObj instanceof Long) {
				return xueshengService.selectById((Long) userIdObj);
			}
		}
		String token = request.getHeader("Token");
		if (StringUtils.isNotBlank(token)) {
			TokenEntity tokenEntity = tokenService.getTokenEntity(token);
			if (tokenEntity != null && "xuesheng".equals(tokenEntity.getTablename())) {
				return xueshengService.selectById(tokenEntity.getUserid());
			}
		}
		return null;
	}

	private String resolveSystemCategory(String type, String bizType) {
		if (StringUtils.isNotBlank(bizType)) {
			return bizType;
		}
		if (StringUtils.isNotBlank(type)) {
			if (type.contains("_template")) {
				return "template";
			}
			if ("1".equals(type)) {
				return "face";
			}
		}
		return "misc";
	}

	private String buildObjectKey(HttpServletRequest request, MultipartFile file, String type, String bizType) {
		if (StringUtils.isNotBlank(type) && type.contains("_template")) {
			return StudentFileStorageUtil.buildSystemObjectKey("template",
					type + "." + resolveUploadExtension(file));
		}
		XueshengEntity student = resolveStudent(request);
		if (student != null) {
			String storedName = StudentFileStorageUtil.buildStoredFileName(file.getOriginalFilename());
			return StudentFileStorageUtil.buildStudentObjectKey(student, bizType, storedName);
		}
		String uniqueName = StudentFileStorageUtil.buildUniqueStoredFileName(file.getOriginalFilename());
		return StudentFileStorageUtil.buildSystemObjectKey(resolveSystemCategory(type, bizType), uniqueName);
	}

	private void saveLocalFile(MultipartFile file, String objectKey) throws IOException {
		File upload = getUploadDir();
		String relative = StudentFileStorageUtil.toLocalRelativePath(objectKey);
		File dest = new File(upload, relative.replace('/', File.separatorChar));
		File parent = dest.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		if (file.isEmpty()) {
			if (!dest.exists()) {
				dest.createNewFile();
			}
		} else {
			file.transferTo(dest);
		}
	}

	/** 清空学生毕设流程业务目录中的已有文件（MinIO 或本地） */
	private void clearStudentWorkflowFolder(String folderPrefix) throws Exception {
		if (StringUtils.isBlank(folderPrefix)) {
			return;
		}
		if (objectStorageService.isAvailable()) {
			objectStorageService.deleteObjectsByPrefix(folderPrefix);
			return;
		}
		String relative = StudentFileStorageUtil.toLocalRelativePath(folderPrefix);
		if (StringUtils.isBlank(relative)) {
			return;
		}
		String relativePath = relative.replace('/', File.separatorChar);
		for (File uploadRoot : getUploadDirCandidates()) {
			File folder = new File(uploadRoot, relativePath);
			if (folder.exists() && folder.isDirectory()) {
				FileUtils.cleanDirectory(folder);
			}
		}
	}

	/**
	 * 上传文件（允许 0 字节空文件，仅拒绝未选择文件的情况）
	 */
	@RequestMapping("/upload")
    @IgnoreAuth
	public R upload(@RequestParam("file") MultipartFile file, String type, String bizType,
			HttpServletRequest request) throws Exception {
		if (file == null || (file.isEmpty() && StringUtils.isBlank(file.getOriginalFilename()))) {
			throw new EIException("请选择要上传的文件");
		}
		XueshengEntity student = resolveStudent(request);
		if (student != null && StudentFileStorageUtil.isSingleFileWorkflowBizType(bizType)) {
			clearStudentWorkflowFolder(StudentFileStorageUtil.buildStudentBizFolderPrefix(student, bizType));
		}
		String objectKey = buildObjectKey(request, file, type, bizType);

		if (objectStorageService.isAvailable()) {
			objectStorageService.upload(file, objectKey);
		} else {
			saveLocalFile(file, objectKey);
		}

		if(StringUtils.isNotBlank(type) && type.equals("1")) {
			ConfigEntity configEntity = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", "faceFile"));
			if(configEntity==null) {
				configEntity = new ConfigEntity();
				configEntity.setName("faceFile");
				configEntity.setValue(objectKey);
			} else {
				configEntity.setValue(objectKey);
			}
			configService.insertOrUpdate(configEntity);
		}
		String displayName = StudentFileStorageUtil.extractDisplayFileName(objectKey);
		return R.ok().put("file", objectKey).put("originalFilename", displayName);
	}
	
	/**
	 * 访问/预览图片（GET /file/**），优先 MinIO，其次本地目录，最后 classpath:static
	 */
	@IgnoreAuth
	@GetMapping("/**")
	public ResponseEntity<byte[]> getFile(HttpServletRequest request) {
		try {
			String uri = request.getRequestURI();
			String marker = "/file/";
			int idx = uri.indexOf(marker);
			if (idx < 0) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			String storedPath = uri.substring(idx + marker.length());
			if (StringUtils.isBlank(storedPath) || storedPath.startsWith("download") || storedPath.startsWith("upload")) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			byte[] body = readStoredFileBytes(storedPath);
			if (body != null) {
				String displayName = StudentFileStorageUtil.extractDisplayFileName(storedPath);
				return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(getContentType(displayName)))
					.body(body);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	private String getContentType(String fileName) {
		String lower = fileName == null ? "" : fileName.toLowerCase();
		if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
		if (lower.endsWith(".png")) return "image/png";
		if (lower.endsWith(".gif")) return "image/gif";
		if (lower.endsWith(".pdf")) return "application/pdf";
		if (lower.endsWith(".doc")) return "application/msword";
		if (lower.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		if (lower.endsWith(".ppt")) return "application/vnd.ms-powerpoint";
		if (lower.endsWith(".pptx")) return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
		return "application/octet-stream";
	}

	private HttpHeaders buildAttachmentHeaders(String displayFileName) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.set(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + java.net.URLEncoder.encode(displayFileName, "UTF-8").replace("+", "%20") + "\"; filename*=UTF-8''"
						+ java.net.URLEncoder.encode(displayFileName, "UTF-8").replace("+", "%20"));
		return headers;
	}

	@IgnoreAuth
	@RequestMapping("/download")
	public ResponseEntity<byte[]> download(@RequestParam String fileName) {
		try {
			byte[] body = readStoredFileBytes(fileName);
			if (body != null) {
				String displayName = StudentFileStorageUtil.extractDisplayFileName(fileName);
				HttpHeaders headers = buildAttachmentHeaders(displayName);
			    return new ResponseEntity<byte[]>(body, headers, HttpStatus.OK);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
	}
	
}
