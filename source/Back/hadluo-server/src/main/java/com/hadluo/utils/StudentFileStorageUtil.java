package com.hadluo.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.hadluo.entity.XueshengEntity;

/**
 * 学生文件分层存储路径：年级 / 专业 / 班级 / 学号 / 业务类型 / 文件名
 * 学院全校统一，不参与目录区分。
 */
public final class StudentFileStorageUtil {

    public static final String STORAGE_PREFIX = "file/";
    public static final String DEFAULT_COLLEGE = "地理与空间信息学院";

    /** 学生毕设流程附件：每个业务目录仅保留一个文件 */
    private static final Set<String> SINGLE_FILE_WORKFLOW_BIZ_TYPES = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("kaitibaogao", "lunwenchugao", "zhongqijiancha", "dabianlunwen")));

    private StudentFileStorageUtil() {
    }

    /** 归一化毕业届别，仅保留 4 位年份，如 2026 */
    public static String normalizeBiyejie(String biyejie) {
        if (StringUtils.isBlank(biyejie)) {
            return "";
        }
        String digits = biyejie.replaceAll("[^0-9]", "");
        if (digits.length() >= 4) {
            return digits.substring(0, 4);
        }
        return digits;
    }

    /** 展示用：2026 -> 2026届 */
    public static String formatBiyejieLabel(String biyejie) {
        String year = normalizeBiyejie(biyejie);
        return year.isEmpty() ? "" : year + "届";
    }

    /** 归一化年级，仅保留 4 位入学年份，如 2022 */
    public static String normalizeNianji(String nianji) {
        if (StringUtils.isBlank(nianji)) {
            return "";
        }
        String digits = nianji.replaceAll("[^0-9]", "");
        if (digits.length() >= 4) {
            return digits.substring(0, 4);
        }
        return digits;
    }

    /** 展示用：2022 -> 2022级 */
    public static String formatNianjiLabel(String nianji) {
        String year = normalizeNianji(nianji);
        return year.isEmpty() ? "" : year + "级";
    }

    /** 从学号前缀推断入学年份（如 2022110110 -> 2022） */
    public static String inferNianjiFromXuehao(String xuehao) {
        if (StringUtils.isBlank(xuehao)) {
            return "";
        }
        String trimmed = xuehao.trim();
        if (trimmed.length() >= 4 && trimmed.startsWith("20")) {
            String prefix = trimmed.substring(0, 4);
            if (prefix.matches("20\\d{2}")) {
                return prefix;
            }
        }
        return "";
    }

    /** 根据毕业届别推算默认年级（默认四年制：届别 - 4） */
    public static String inferNianjiFromBiyejie(String biyejie) {
        String graduationYear = normalizeBiyejie(biyejie);
        if (graduationYear.length() != 4) {
            return "";
        }
        try {
            int year = Integer.parseInt(graduationYear);
            if (year >= 2000 && year <= 2100) {
                return String.valueOf(year - 4);
            }
        } catch (NumberFormatException ignore) {
        }
        return "";
    }

    /** 解析学生年级：显式字段 > 学号前缀 > 届别推算 */
    public static String resolveNianji(XueshengEntity student) {
        if (student == null) {
            return "";
        }
        String normalized = normalizeNianji(student.getNianji());
        if (StringUtils.isNotBlank(normalized)) {
            return normalized;
        }
        normalized = inferNianjiFromXuehao(student.getXuehao());
        if (StringUtils.isNotBlank(normalized)) {
            return normalized;
        }
        return inferNianjiFromBiyejie(student.getBiyejie());
    }

    /** 路径片段安全化，保留中文、字母、数字、下划线、短横线 */
    public static String slugify(String value) {
        if (StringUtils.isBlank(value)) {
            return "unknown";
        }
        String slug = value.trim()
                .replace('\\', '/')
                .replaceAll("[/]+", "-")
                .replaceAll("[\\x00-\\x1f<>:\"|?*]", "_")
                .replaceAll("\\s+", "_");
        if (slug.isEmpty()) {
            return "unknown";
        }
        return slug.length() > 80 ? slug.substring(0, 80) : slug;
    }

    /** MinIO 路径中的专业目录名：使用标准专业中文名，如「地理信息科学」 */
    public static String resolveMajorFolderName(XueshengEntity student) {
        if (student == null) {
            return "unknown-major";
        }
        String normalized = MajorConstants.normalizeMajor(student.getZhuanye());
        if (MajorConstants.isMajorValid(normalized)) {
            return slugify(normalized);
        }
        String slug = slugify(StringUtils.defaultIfBlank(normalized, student.getZhuanye()));
        return "unknown".equals(slug) ? "unknown-major" : slug;
    }

    /** 是否为「单文件」毕设流程业务目录 */
    public static boolean isSingleFileWorkflowBizType(String bizType) {
        if (StringUtils.isBlank(bizType)) {
            return false;
        }
        return SINGLE_FILE_WORKFLOW_BIZ_TYPES.contains(bizType.trim());
    }

    /**
     * 学生业务目录前缀（含 file/ 与末尾 /），用于列出或清空该目录下全部对象。
     * 示例：file/graduate/2022/major/城乡规划/class/1/student/111111/kaitibaogao/
     */
    public static String buildStudentBizFolderPrefix(XueshengEntity student, String bizType) {
        String nianji = slugify(resolveNianji(student));
        if ("unknown".equals(nianji)) {
            nianji = "unknown-grade";
        }
        String majorFolder = resolveMajorFolderName(student);
        String banji = slugify(student != null ? student.getBanji() : "unknown-class");
        String xuehao = slugify(student != null ? student.getXuehao() : "unknown-student");
        String biz = slugify(StringUtils.defaultIfBlank(bizType, "misc"));
        return STORAGE_PREFIX + "graduate/" + nianji + "/major/" + majorFolder + "/class/" + banji
                + "/student/" + xuehao + "/" + biz + "/";
    }

    /**
     * 构建学生业务文件对象键（含 file/ 前缀）。
     * 示例：file/graduate/2022/major/地理信息科学/class/1/student/110110/kaitibaogao/开题报告.docx
     */
    public static String buildStudentObjectKey(XueshengEntity student, String bizType, String storedFileName) {
        return buildStudentBizFolderPrefix(student, bizType) + storedFileName;
    }

    /** 非学生上传（头像、题目封面、模板等） */
    public static String buildSystemObjectKey(String category, String storedFileName) {
        String cat = slugify(StringUtils.defaultIfBlank(category, "misc"));
        return STORAGE_PREFIX + "system/" + cat + "/" + storedFileName;
    }

    /** 统一存储路径：确保以 file/ 开头，保留完整相对路径 */
    public static String normalizeStoredPath(String storedPath) {
        if (StringUtils.isBlank(storedPath)) {
            return null;
        }
        String normalized = storedPath.replace('\\', '/').trim();
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        int fileIdx = normalized.indexOf("file/");
        if (fileIdx > 0) {
            normalized = normalized.substring(fileIdx);
        }
        if (!normalized.startsWith(STORAGE_PREFIX)) {
            normalized = STORAGE_PREFIX + normalized;
        }
        return normalized;
    }

    /** 去掉 file/ 前缀后的相对路径，用于本地磁盘目录 */
    public static String toLocalRelativePath(String storedPath) {
        String normalized = normalizeStoredPath(storedPath);
        if (normalized == null) {
            return null;
        }
        return normalized.startsWith(STORAGE_PREFIX)
                ? normalized.substring(STORAGE_PREFIX.length())
                : normalized;
    }

    /** 从存储路径提取下载展示文件名（兼容旧数据的时间戳前缀） */
    public static String extractDisplayFileName(String storedPath) {
        if (StringUtils.isBlank(storedPath)) {
            return "";
        }
        String normalized = storedPath.replace('\\', '/');
        int slash = normalized.lastIndexOf('/');
        String name = slash >= 0 ? normalized.substring(slash + 1) : normalized;
        int underscore = name.indexOf('_');
        if (underscore > 0 && underscore < name.length() - 1) {
            String prefix = name.substring(0, underscore);
            if (prefix.matches("\\d{10,}")) {
                return name.substring(underscore + 1);
            }
        }
        return name;
    }

    /** 学生业务文件存储名：保留原始文件名（MinIO 分层路径已保证唯一） */
    public static String buildStoredFileName(String originalFileName) {
        String safe = sanitizeOriginalFileName(originalFileName);
        if (StringUtils.isBlank(safe)) {
            return System.currentTimeMillis() + ".bin";
        }
        return safe;
    }

    /** 生成唯一存储文件名：时间戳_原始文件名（用于头像、模板等非学生分层目录） */
    public static String buildUniqueStoredFileName(String originalFileName) {
        String safe = sanitizeOriginalFileName(originalFileName);
        if (StringUtils.isBlank(safe)) {
            return System.currentTimeMillis() + ".bin";
        }
        return System.currentTimeMillis() + "_" + safe;
    }

    public static String sanitizeOriginalFileName(String original) {
        if (StringUtils.isBlank(original)) {
            return null;
        }
        String name = original.replace('\\', '/').trim();
        int slash = name.lastIndexOf('/');
        if (slash >= 0) {
            name = name.substring(slash + 1);
        }
        name = name.replaceAll("[\\x00-\\x1f<>:\"|?*]", "_");
        if (name.length() > 180) {
            int dot = name.lastIndexOf('.');
            if (dot > 0) {
                name = name.substring(0, Math.min(dot, 170)) + name.substring(dot);
            } else {
                name = name.substring(0, 180);
            }
        }
        return StringUtils.isBlank(name) ? null : name;
    }
}
