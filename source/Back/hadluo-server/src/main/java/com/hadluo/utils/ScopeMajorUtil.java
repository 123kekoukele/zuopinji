package com.hadluo.utils;

import com.hadluo.entity.BanjixinxiEntity;
import com.hadluo.entity.JiaoshiEntity;
import com.hadluo.entity.UsersEntity;
import com.hadluo.entity.XueshengEntity;
import com.hadluo.service.JiaoshiService;
import com.hadluo.service.UsersService;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 管理端按专业范围过滤：学院级管理员（users 未配置专业）可见全部；
 * 专业管理员（users.zhuanye）与教师（jiaoshi.zhuanye）仅可见本专业数据。
 */
public final class ScopeMajorUtil {

	private ScopeMajorUtil() {
	}

	public static String resolveScopedMajor(HttpServletRequest request, UsersService usersService,
			JiaoshiService jiaoshiService) {
		if (request == null) {
			return null;
		}
		Object userIdObj = request.getSession().getAttribute("userId");
		if (!(userIdObj instanceof Long)) {
			return null;
		}
		Long userId = (Long) userIdObj;
		String tableName = String.valueOf(request.getSession().getAttribute("tableName"));
		if ("users".equals(tableName)) {
			UsersEntity admin = usersService.selectById(userId);
			if (admin != null && StringUtils.isNotBlank(admin.getZhuanye())) {
				return admin.getZhuanye().trim();
			}
			return null;
		}
		if ("jiaoshi".equals(tableName)) {
			JiaoshiEntity teacher = jiaoshiService.selectById(userId);
			if (teacher != null && StringUtils.isNotBlank(teacher.getZhuanye())) {
				return teacher.getZhuanye().trim();
			}
		}
		return null;
	}

	public static boolean isTeacherSession(HttpServletRequest request) {
		return request != null && "jiaoshi".equals(String.valueOf(request.getSession().getAttribute("tableName")));
	}

	/** 教师账号未配置专业时，不应看到任何学生/班级 */
	public static boolean isTeacherWithoutMajor(HttpServletRequest request, JiaoshiService jiaoshiService) {
		if (!isTeacherSession(request)) {
			return false;
		}
		Object userIdObj = request.getSession().getAttribute("userId");
		if (!(userIdObj instanceof Long)) {
			return true;
		}
		JiaoshiEntity teacher = jiaoshiService.selectById((Long) userIdObj);
		return teacher == null || StringUtils.isBlank(teacher.getZhuanye());
	}

	public static String resolveTeacherGonghao(HttpServletRequest request) {
		if (!isTeacherSession(request)) {
			return null;
		}
		Object username = request.getSession().getAttribute("username");
		if (username == null) {
			return null;
		}
		String gonghao = username.toString().trim();
		return StringUtils.isBlank(gonghao) ? null : gonghao;
	}

	public static void applyMajorToQuery(String scopedMajor, XueshengEntity xuesheng, Map<String, Object> params) {
		if (StringUtils.isBlank(scopedMajor)) {
			return;
		}
		if (xuesheng != null) {
			xuesheng.setZhuanye(scopedMajor.trim());
		}
		if (params != null) {
			params.put("zhuanye", scopedMajor.trim());
		}
	}

	public static void applyMajorToQuery(String scopedMajor, BanjixinxiEntity banjixinxi, Map<String, Object> params) {
		if (StringUtils.isBlank(scopedMajor)) {
			return;
		}
		if (banjixinxi != null) {
			banjixinxi.setZhuanye(scopedMajor.trim());
		}
		if (params != null) {
			params.put("zhuanye", scopedMajor.trim());
		}
	}

	public static boolean matchesScopedMajor(String scopedMajor, String recordMajor) {
		if (StringUtils.isBlank(scopedMajor)) {
			return true;
		}
		if (StringUtils.isBlank(recordMajor)) {
			return false;
		}
		return scopedMajor.trim().equals(recordMajor.trim());
	}

	public static R denyIfTeacherMutate(HttpServletRequest request) {
		if (isTeacherSession(request)) {
			return R.error("教师账号仅可查看本专业学生与班级信息");
		}
		return null;
	}

	public static R denyIfOutOfScope(String scopedMajor, XueshengEntity student) {
		if (student == null) {
			return R.error("记录不存在");
		}
		if (!matchesScopedMajor(scopedMajor, student.getZhuanye())) {
			return R.error("无权访问其他专业学生信息");
		}
		return null;
	}

	public static R denyIfOutOfScope(String scopedMajor, BanjixinxiEntity banji) {
		if (banji == null) {
			return R.error("记录不存在");
		}
		if (!matchesScopedMajor(scopedMajor, banji.getZhuanye())) {
			return R.error("无权访问其他专业班级信息");
		}
		return null;
	}
}
