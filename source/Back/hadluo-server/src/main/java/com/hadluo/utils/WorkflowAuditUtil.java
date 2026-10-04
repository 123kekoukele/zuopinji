package com.hadluo.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 毕业设计流程模块（开题报告、论文初稿、中期检查、答辩论文）审核状态
 */
public final class WorkflowAuditUtil {

	public static final String STATUS_PENDING = "未审核";
	public static final String STATUS_APPROVED = "通过";
	public static final String STATUS_REJECTED = "驳回";

	private WorkflowAuditUtil() {
	}

	public static boolean isStudentSession(HttpServletRequest request) {
		if (request == null) {
			return false;
		}
		Object tableNameObj = request.getSession().getAttribute("tableName");
		return tableNameObj != null && "xuesheng".equals(tableNameObj.toString());
	}

	public static String normalizeStatus(String status) {
		if (StringUtils.isBlank(status)) {
			return STATUS_PENDING;
		}
		return status.trim();
	}

	/** 学生新提交或管理员未填时默认「未审核」 */
	public static void applyOnInsert(String status, java.util.function.Consumer<String> setter, HttpServletRequest request) {
		if (isStudentSession(request) || StringUtils.isBlank(status)) {
			setter.accept(STATUS_PENDING);
		}
	}

	/** 学生重新提交重置为「未审核」；教师/管理员可修改审核结果 */
	public static void applyOnUpdate(String status, java.util.function.Consumer<String> setter, HttpServletRequest request) {
		if (isStudentSession(request)) {
			setter.accept(STATUS_PENDING);
		} else if (StringUtils.isBlank(status)) {
			setter.accept(STATUS_PENDING);
		}
	}

	/** 学生重新提交时清空教师填写的审核意见 */
	public static void clearAuditReasonOnStudentResubmit(HttpServletRequest request,
			java.util.function.Consumer<String> reasonSetter) {
		if (isStudentSession(request) && reasonSetter != null) {
			reasonSetter.accept(null);
		}
	}
}
