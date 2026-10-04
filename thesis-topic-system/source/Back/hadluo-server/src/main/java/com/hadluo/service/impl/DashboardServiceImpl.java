package com.hadluo.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hadluo.entity.DabianlunwenEntity;
import com.hadluo.entity.KaitibaogaoEntity;
import com.hadluo.entity.LunwenchugaoEntity;
import com.hadluo.entity.XuantishenqingEntity;
import com.hadluo.entity.XueshengEntity;
import com.hadluo.entity.ZhongqijianchaEntity;
import com.hadluo.service.DabianlunwenService;
import com.hadluo.service.DashboardService;
import com.hadluo.service.JiaoshiService;
import com.hadluo.service.KaitibaogaoService;
import com.hadluo.service.LunwenchugaoService;
import com.hadluo.service.UsersService;
import com.hadluo.service.XuantishenqingService;
import com.hadluo.service.XueshengService;
import com.hadluo.service.ZhongqijianchaService;
import com.hadluo.config.RedisProperties;
import com.hadluo.service.RedisCacheService;
import com.hadluo.utils.ScopeMajorUtil;
import com.hadluo.utils.XuantishenqingStatusUtil;
import com.hadluo.utils.RedisCacheKeys;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("dashboardService")
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private XueshengService xueshengService;
	@Autowired
	private XuantishenqingService xuantishenqingService;
	@Autowired
	private KaitibaogaoService kaitibaogaoService;
	@Autowired
	private LunwenchugaoService lunwenchugaoService;
	@Autowired
	private ZhongqijianchaService zhongqijianchaService;
	@Autowired
	private DabianlunwenService dabianlunwenService;
	@Autowired
	private UsersService usersService;
	@Autowired
	private JiaoshiService jiaoshiService;
	@Autowired
	private RedisCacheService redisCacheService;
	@Autowired
	private RedisProperties redisProperties;

	private static class AuditCounts {
		int approved;
		int rejected;
		int pending;
	}

	@Override
	public Map<String, Object> buildStats(HttpServletRequest request, String zhuanyeParam) {
		String cacheKey = buildDashboardCacheKey(request, zhuanyeParam);
		return redisCacheService.getOrLoad(cacheKey, new TypeReference<Map<String, Object>>() {
		}.getType(), () -> buildStatsInternal(request, zhuanyeParam), redisProperties.getCache().getDashboardTtl());
	}

	private String buildDashboardCacheKey(HttpServletRequest request, String zhuanyeParam) {
		Object userId = request.getSession().getAttribute("userId");
		Object tableName = request.getSession().getAttribute("tableName");
		String teacherGonghao = ScopeMajorUtil.resolveTeacherGonghao(request);
		String sessionMajor = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);
		return RedisCacheKeys.DASHBOARD_STATS
				+ StringUtils.defaultString(String.valueOf(tableName), "anon") + ":"
				+ StringUtils.defaultString(String.valueOf(userId), "0") + ":"
				+ StringUtils.defaultString(sessionMajor, "-") + ":"
				+ StringUtils.defaultString(teacherGonghao, "-") + ":"
				+ StringUtils.defaultString(zhuanyeParam, "-");
	}

	private Map<String, Object> buildStatsInternal(HttpServletRequest request, String zhuanyeParam) {
		if (ScopeMajorUtil.isTeacherWithoutMajor(request, jiaoshiService)) {
			return emptyStats();
		}
		String teacherGonghao = ScopeMajorUtil.resolveTeacherGonghao(request);
		String sessionMajor = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);
		String effectiveMajor = StringUtils.isNotBlank(sessionMajor)
				? sessionMajor.trim()
				: (StringUtils.isNotBlank(zhuanyeParam) ? zhuanyeParam.trim() : null);

		List<XueshengEntity> students = listStudents(effectiveMajor);
		List<XuantishenqingEntity> applications = listApplications(effectiveMajor, teacherGonghao);
		List<KaitibaogaoEntity> kaitibaogaoList = listWorkflow(kaitibaogaoService, effectiveMajor, teacherGonghao);
		List<LunwenchugaoEntity> lunwenchugaoList = listWorkflow(lunwenchugaoService, effectiveMajor, teacherGonghao);
		List<ZhongqijianchaEntity> zhongqijianchaList = listWorkflow(zhongqijianchaService, effectiveMajor, teacherGonghao);
		List<DabianlunwenEntity> dabianlunwenList = listWorkflow(dabianlunwenService, effectiveMajor, teacherGonghao);

		Map<String, String> studentTopicStatus = buildStudentTopicStatus(students, applications);
		Set<String> approvedTopicXuehao = studentTopicStatus.entrySet().stream()
				.filter(e -> "selected".equals(e.getValue()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
		Set<String> completedXuehao = dabianlunwenList.stream()
				.filter(item -> XuantishenqingStatusUtil.isAppliedStatus(item.getShenhezhuangtai()))
				.map(DabianlunwenEntity::getXuehao)
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.toSet());

		int notSelected = 0;
		int selecting = 0;
		int selected = 0;
		for (String status : studentTopicStatus.values()) {
			if ("selected".equals(status)) {
				selected++;
			} else if ("selecting".equals(status)) {
				selecting++;
			} else {
				notSelected++;
			}
		}
		int total = students.size();

		int notStarted = notSelected;
		int completed = (int) students.stream().map(XueshengEntity::getXuehao).filter(completedXuehao::contains).count();
		int inProgress = (int) students.stream()
				.map(XueshengEntity::getXuehao)
				.filter(xh -> approvedTopicXuehao.contains(xh) && !completedXuehao.contains(xh))
				.count();

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("studentOverview", overviewMap(notSelected, selecting, selected, total));
		result.put("topicDistribution", buildTopicDistribution(applications));
		result.put("auditModules", buildAuditModules(applications, kaitibaogaoList, lunwenchugaoList,
				zhongqijianchaList, dabianlunwenList));
		result.put("progressOverview", progressMap(notStarted, inProgress, completed));
		result.put("majorProgress", buildMajorProgress(students, applications, effectiveMajor));
		return result;
	}

	private Map<String, Object> overviewMap(int notSelected, int selecting, int selected, int total) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("notSelected", notSelected);
		map.put("selecting", selecting);
		map.put("selected", selected);
		map.put("total", total);
		return map;
	}

	private Map<String, Object> progressMap(int notStarted, int inProgress, int completed) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("notStarted", notStarted);
		map.put("inProgress", inProgress);
		map.put("completed", completed);
		return map;
	}

	private Map<String, Object> emptyStats() {
		Map<String, Object> result = new LinkedHashMap<>();
		result.put("studentOverview", overviewMap(0, 0, 0, 0));
		result.put("topicDistribution", new ArrayList<>());
		result.put("auditModules", buildAuditModules(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), new ArrayList<>()));
		result.put("progressOverview", progressMap(0, 0, 0));
		result.put("majorProgress", new ArrayList<>());
		return result;
	}

	private List<XueshengEntity> listStudents(String major) {
		EntityWrapper<XueshengEntity> ew = new EntityWrapper<>();
		if (StringUtils.isNotBlank(major)) {
			ew.eq("zhuanye", major);
		}
		return xueshengService.selectList(ew);
	}

	private List<XuantishenqingEntity> listApplications(String major, String teacherGonghao) {
		EntityWrapper<XuantishenqingEntity> ew = new EntityWrapper<>();
		if (StringUtils.isNotBlank(major)) {
			ew.eq("zhuanye", major);
		}
		if (StringUtils.isNotBlank(teacherGonghao)) {
			ew.eq("jiaoshigonghao", teacherGonghao);
		}
		return xuantishenqingService.selectList(ew);
	}

	private <T> List<T> listWorkflow(com.baomidou.mybatisplus.service.IService<T> service, String major,
			String teacherGonghao) {
		EntityWrapper<T> ew = new EntityWrapper<>();
		if (StringUtils.isNotBlank(major)) {
			ew.eq("zhuanye", major);
		}
		if (StringUtils.isNotBlank(teacherGonghao)) {
			ew.eq("jiaoshigonghao", teacherGonghao);
		}
		return service.selectList(ew);
	}

	private Map<String, String> buildStudentTopicStatus(List<XueshengEntity> students,
			List<XuantishenqingEntity> applications) {
		Map<String, String> map = new LinkedHashMap<>();
		students.forEach(student -> map.put(student.getXuehao(), "none"));

		Map<String, List<String>> statusesByStudent = new HashMap<>();
		for (XuantishenqingEntity app : applications) {
			if (StringUtils.isBlank(app.getXuehao()) || !map.containsKey(app.getXuehao())) {
				continue;
			}
			statusesByStudent.computeIfAbsent(app.getXuehao(), key -> new ArrayList<>())
					.add(app.getShenhezhuangtai());
		}

		for (Map.Entry<String, List<String>> entry : statusesByStudent.entrySet()) {
			List<String> statuses = entry.getValue();
			if (statuses.stream().anyMatch(XuantishenqingStatusUtil::isAppliedStatus)) {
				map.put(entry.getKey(), "selected");
			} else if (statuses.stream().anyMatch(XuantishenqingStatusUtil::isPendingStatus)) {
				map.put(entry.getKey(), "selecting");
			} else {
				map.put(entry.getKey(), "none");
			}
		}
		return map;
	}

	private List<Map<String, Object>> buildTopicDistribution(List<XuantishenqingEntity> applications) {
		Map<String, Integer> counter = new LinkedHashMap<>();
		for (XuantishenqingEntity app : applications) {
			if (!XuantishenqingStatusUtil.isAppliedStatus(app.getShenhezhuangtai())) {
				continue;
			}
			String type = StringUtils.isNotBlank(app.getTimuleixing()) ? app.getTimuleixing().trim() : "未分类";
			counter.put(type, counter.getOrDefault(type, 0) + 1);
		}
		return counter.entrySet().stream()
				.map(entry -> {
					Map<String, Object> item = new LinkedHashMap<>();
					item.put("name", entry.getKey());
					item.put("value", entry.getValue());
					return item;
				})
				.sorted((a, b) -> Integer.compare((Integer) b.get("value"), (Integer) a.get("value")))
				.collect(Collectors.toList());
	}

	private List<Map<String, Object>> buildAuditModules(List<XuantishenqingEntity> applications,
			List<KaitibaogaoEntity> kaitibaogaoList, List<LunwenchugaoEntity> lunwenchugaoList,
			List<ZhongqijianchaEntity> zhongqijianchaList, List<DabianlunwenEntity> dabianlunwenList) {
		List<Map<String, Object>> modules = new ArrayList<>();
		modules.add(auditModule("选题申请", applications, XuantishenqingEntity::getShenhezhuangtai));
		modules.add(auditModule("开题报告", kaitibaogaoList, KaitibaogaoEntity::getShenhezhuangtai));
		modules.add(auditModule("论文初稿", lunwenchugaoList, LunwenchugaoEntity::getShenhezhuangtai));
		modules.add(auditModule("中期检查", zhongqijianchaList, ZhongqijianchaEntity::getShenhezhuangtai));
		modules.add(auditModule("论文答辩", dabianlunwenList, DabianlunwenEntity::getShenhezhuangtai));
		return modules;
	}

	private <T> Map<String, Object> auditModule(String module, List<T> records, Function<T, String> statusGetter) {
		AuditCounts counts = new AuditCounts();
		for (T record : records) {
			String normalized = XuantishenqingStatusUtil.normalizeDisplayStatus(statusGetter.apply(record));
			if (XuantishenqingStatusUtil.STATUS_APPROVED.equals(normalized)) {
				counts.approved++;
			} else if (XuantishenqingStatusUtil.STATUS_REJECTED.equals(normalized)) {
				counts.rejected++;
			} else {
				counts.pending++;
			}
		}
		Map<String, Object> item = new LinkedHashMap<>();
		item.put("module", module);
		item.put("approved", counts.approved);
		item.put("rejected", counts.rejected);
		item.put("pending", counts.pending);
		return item;
	}

	private List<Map<String, Object>> buildMajorProgress(List<XueshengEntity> students,
			List<XuantishenqingEntity> applications, String effectiveMajor) {
		Set<String> majors = new LinkedHashSet<>();
		if (StringUtils.isNotBlank(effectiveMajor)) {
			majors.add(effectiveMajor);
		} else {
			students.stream()
					.map(XueshengEntity::getZhuanye)
					.filter(StringUtils::isNotBlank)
					.map(String::trim)
					.forEach(majors::add);
		}

		Map<String, Set<String>> selectedByMajor = new HashMap<>();
		for (XuantishenqingEntity app : applications) {
			if (!XuantishenqingStatusUtil.isAppliedStatus(app.getShenhezhuangtai())
					|| StringUtils.isBlank(app.getXuehao())) {
				continue;
			}
			String major = StringUtils.defaultString(app.getZhuanye(), "").trim();
			selectedByMajor.computeIfAbsent(major, key -> new HashSet<>()).add(app.getXuehao().trim());
		}

		List<Map<String, Object>> list = new ArrayList<>();
		for (String major : majors) {
			List<XueshengEntity> majorStudents = students.stream()
					.filter(s -> major.equals(StringUtils.defaultString(s.getZhuanye(), "").trim()))
					.collect(Collectors.toList());
			if (majorStudents.isEmpty()) {
				continue;
			}
			Set<String> selectedXuehao = selectedByMajor.getOrDefault(major, new HashSet<>());
			int selected = (int) majorStudents.stream().map(XueshengEntity::getXuehao).filter(selectedXuehao::contains)
					.count();
			int total = majorStudents.size();
			Map<String, Object> item = new LinkedHashMap<>();
			item.put("name", major);
			item.put("total", total);
			item.put("selected", selected);
			item.put("percent", total > 0 ? Math.round(selected * 100.0 / total) : 0);
			list.add(item);
		}
		list.sort((a, b) -> Integer.compare((Integer) b.get("selected"), (Integer) a.get("selected")));
		return list;
	}
}
