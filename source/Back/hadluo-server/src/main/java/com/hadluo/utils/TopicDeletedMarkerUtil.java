package com.hadluo.utils;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hadluo.entity.TimuxinxiEntity;
import com.hadluo.entity.UsersEntity;
import com.hadluo.service.JiaoshiService;
import com.hadluo.service.TimuxinxiService;
import com.hadluo.service.UsersService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 标记流程模块记录关联的题目是否在当前账号可见的题目信息中不存在（含已删除或不在权限范围）。
 */
@Component
public class TopicDeletedMarkerUtil {

	@Autowired
	private TimuxinxiService timuxinxiService;

	@Autowired
	private UsersService usersService;

	@Autowired
	private JiaoshiService jiaoshiService;

	public void markPage(PageUtils page, HttpServletRequest request) {
		if (page == null || page.getList() == null || page.getList().isEmpty()) {
			return;
		}

		List<String> topicCodes = new ArrayList<>();
		for (Object row : page.getList()) {
			String code = getTopicCode(row);
			if (StringUtils.isNotBlank(code)) {
				topicCodes.add(code.trim());
			}
		}
		Set<String> visibleCodes = loadVisibleTopicCodes(request, topicCodes);

		for (Object row : page.getList()) {
			String code = getTopicCode(row);
			boolean deleted = StringUtils.isBlank(code) || !visibleCodes.contains(code.trim());
			setDeletedFlag(row, deleted);
		}
	}

	private Set<String> loadVisibleTopicCodes(HttpServletRequest request, List<String> topicCodes) {
		Set<String> visible = new HashSet<>();
		if (topicCodes == null || topicCodes.isEmpty()) {
			return visible;
		}
		EntityWrapper<TimuxinxiEntity> ew = new EntityWrapper<>();
		ew.in("timubianhao", topicCodes);
		applyTopicScope(ew, request);
		List<TimuxinxiEntity> topics = timuxinxiService.selectList(ew);
		if (topics != null) {
			for (TimuxinxiEntity topic : topics) {
				if (topic != null && StringUtils.isNotBlank(topic.getTimubianhao())) {
					visible.add(topic.getTimubianhao().trim());
				}
			}
		}
		return visible;
	}

	private void applyTopicScope(EntityWrapper<TimuxinxiEntity> ew, HttpServletRequest request) {
		if (request == null || ew == null) {
			return;
		}
		if (ScopeMajorUtil.isTeacherWithoutMajor(request, jiaoshiService)) {
			ew.eq("id", -1L);
			return;
		}
		String tableName = String.valueOf(request.getSession().getAttribute("tableName"));
		if ("jiaoshi".equals(tableName)) {
			String teacherNo = ScopeMajorUtil.resolveTeacherGonghao(request);
			if (StringUtils.isNotBlank(teacherNo)) {
				ew.eq("jiaoshigonghao", teacherNo);
			}
			return;
		}
		if ("users".equals(tableName)) {
			Object userIdObj = request.getSession().getAttribute("userId");
			if (userIdObj instanceof Long) {
				UsersEntity admin = usersService.selectById((Long) userIdObj);
				if (admin != null && StringUtils.isNotBlank(admin.getZhuanye())) {
					ew.eq("zhuanye", admin.getZhuanye().trim());
				}
			}
		}
	}

	private String getTopicCode(Object row) {
		if (row == null) {
			return "";
		}
		try {
			Method method = row.getClass().getMethod("getTimubianhao");
			Object value = method.invoke(row);
			return value == null ? "" : value.toString();
		} catch (Exception ignored) {
			return "";
		}
	}

	private void setDeletedFlag(Object row, boolean deleted) {
		if (row == null) {
			return;
		}
		try {
			Method method = row.getClass().getMethod("setTimuyishanchu", Boolean.class);
			method.invoke(row, deleted);
		} catch (Exception ignored) {
			// 实体未支持标记字段时忽略
		}
	}
}
