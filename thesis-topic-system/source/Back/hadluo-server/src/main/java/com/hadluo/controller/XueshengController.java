package com.hadluo.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import com.hadluo.utils.ValidatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hadluo.annotation.IgnoreAuth;

import com.hadluo.entity.XueshengEntity;
import com.hadluo.entity.view.XueshengView;
import com.hadluo.entity.XuantishenqingEntity;
import com.hadluo.entity.ConfigEntity;

import com.hadluo.service.JiaoshiService;
import com.hadluo.service.XueshengService;
import com.hadluo.service.TokenService;
import com.hadluo.service.JiaoshiService;
import com.hadluo.service.UsersService;
import com.hadluo.service.XuantishenqingService;
import com.hadluo.service.ConfigService;
import com.hadluo.entity.UsersEntity;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.ExcelImportUtil;
import com.hadluo.utils.MajorConstants;
import com.hadluo.utils.PasswordUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.ScopeMajorUtil;
import com.hadluo.utils.StudentFileStorageUtil;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 学生
 * 后端接口
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@RestController
@RequestMapping("/xuesheng")
public class XueshengController {
    private static final String SYSTEM_OPEN_START = "system_open_start";
    private static final String SYSTEM_OPEN_END = "system_open_end";
    private static final String SYSTEM_GRADUATION_YEAR = "system_graduation_year";
    private static final String MAJOR_OPEN_START_PREFIX = "system_open_start_major";
    private static final String MAJOR_OPEN_END_PREFIX = "system_open_end_major";

    @Autowired
    private XueshengService xueshengService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private JiaoshiService jiaoshiService;

    @Autowired
    private ConfigService configService;

    private R validateMajorField(String major) {
        String normalizedMajor = MajorConstants.normalizeMajor(major);
        if (!MajorConstants.isMajorValid(normalizedMajor)) {
            return R.error("专业必须为以下之一：" + String.join("、", MajorConstants.MAJOR_OPTIONS));
        }
        return null;
    }

    private String getConfigValue(String name) {
        ConfigEntity cfg = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", name));
        return cfg != null ? cfg.getValue() : null;
    }

    private void applyStudentProfileDefaults(XueshengEntity xuesheng) {
        if (xuesheng == null) {
            return;
        }
        if (StringUtils.isBlank(xuesheng.getBiyejie())) {
            xuesheng.setBiyejie(StudentFileStorageUtil.normalizeBiyejie(getConfigValue(SYSTEM_GRADUATION_YEAR)));
        } else {
            xuesheng.setBiyejie(StudentFileStorageUtil.normalizeBiyejie(xuesheng.getBiyejie()));
        }
        if (StringUtils.isBlank(xuesheng.getNianji())) {
            String inferred = StudentFileStorageUtil.inferNianjiFromXuehao(xuesheng.getXuehao());
            if (StringUtils.isBlank(inferred)) {
                inferred = StudentFileStorageUtil.inferNianjiFromBiyejie(xuesheng.getBiyejie());
            }
            xuesheng.setNianji(inferred);
        } else {
            xuesheng.setNianji(StudentFileStorageUtil.normalizeNianji(xuesheng.getNianji()));
        }
        xuesheng.setXueyuan(StudentFileStorageUtil.DEFAULT_COLLEGE);
    }

    private long nextStudentImportId(int rowIndex) {
        return System.currentTimeMillis() * 10000L + (rowIndex % 10000);
    }

    private int resolveStudentHeaderColumn(Row headerRow, String... aliases) {
        if (headerRow == null || aliases == null) {
            return -1;
        }
        int lastCellNum = headerRow.getLastCellNum();
        for (int i = 0; i < lastCellNum; i++) {
            String title = ExcelImportUtil.normalizeHeader(ExcelImportUtil.getCellString(headerRow.getCell(i)));
            for (String alias : aliases) {
                if (alias.equals(title)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int locateStudentHeaderRow(Sheet sheet) {
        int scanLimit = Math.min(sheet.getLastRowNum(), 9);
        for (int rowIndex = 0; rowIndex <= scanLimit; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            if (resolveStudentHeaderColumn(row, "学号") >= 0
                    && resolveStudentHeaderColumn(row, "学生姓名", "姓名") >= 0) {
                return rowIndex;
            }
        }
        return 0;
    }

    private String normalizeStudentNo(String studentNo) {
        if (StringUtils.isBlank(studentNo)) {
            return "";
        }
        String normalized = studentNo.replace('\u00A0', ' ').trim();
        if (normalized.endsWith(".0")) {
            normalized = normalized.substring(0, normalized.length() - 2);
        }
        if (normalized.matches("\\d+\\.\\d+[Ee][+-]?\\d+")) {
            try {
                normalized = String.valueOf((long) Double.parseDouble(normalized));
            } catch (NumberFormatException ignore) {
            }
        }
        return normalized;
    }

    private String normalizeMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return "";
        }
        String digits = mobile.replaceAll("[^0-9]", "");
        if (digits.length() >= 11) {
            return digits.substring(digits.length() - 11);
        }
        return digits;
    }

    private String buildStudentImportErrorMessage(List<String> errorMessages, int maxLines) {
        StringBuilder sb = new StringBuilder();
        int limit = maxLines <= 0 ? errorMessages.size() : Math.min(maxLines, errorMessages.size());
        for (int i = 0; i < limit; i++) {
            if (i > 0) {
                sb.append("；");
            }
            sb.append(errorMessages.get(i));
        }
        if (errorMessages.size() > limit) {
            sb.append("；其余 ").append(errorMessages.size() - limit).append(" 条请修正后重试");
        }
        return sb.toString();
    }

    /** 解析待更新学生主键：请求体 id → 登录 Token 会话 → 学号查库 */
    private void resolveStudentIdForUpdate(XueshengEntity xuesheng, HttpServletRequest request) {
        if (xuesheng == null || xuesheng.getId() != null) {
            return;
        }
        Long sessionUserId = (Long) request.getSession().getAttribute("userId");
        if (sessionUserId != null) {
            xuesheng.setId(sessionUserId);
            return;
        }
        if (StringUtils.isNotBlank(xuesheng.getXuehao())) {
            XueshengEntity existing = xueshengService.selectOne(
                    new EntityWrapper<XueshengEntity>().eq("xuehao", xuesheng.getXuehao()));
            if (existing != null) {
                xuesheng.setId(existing.getId());
            }
        }
    }

    private String buildMajorConfigKey(String prefix, String major) {
        String majorCode = MajorConstants.toMajorCode(major);
        if (majorCode.isEmpty()) {
            return "";
        }
        return prefix + "_" + majorCode;
    }

    private Date parseDateTime(String dateText) {
        if (dateText == null || dateText.trim().isEmpty()) {
            return null;
        }
        String[] patterns = new String[] {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss.SSS"
        };
        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                return sdf.parse(dateText.trim());
            } catch (ParseException ignore) {
            }
        }
        return null;
    }

    private boolean hasAnyOpenWindowConfig(String startKey, String endKey) {
        String startText = getConfigValue(startKey);
        String endText = getConfigValue(endKey);
        return StringUtils.isNotBlank(startText) || StringUtils.isNotBlank(endText);
    }

    private R checkOpenTimeByKeys(String startKey, String endKey, String scopeLabel) {
        String startText = getConfigValue(startKey);
        String endText = getConfigValue(endKey);
        boolean hasStart = StringUtils.isNotBlank(startText);
        boolean hasEnd = StringUtils.isNotBlank(endText);
        if (!hasStart && !hasEnd) {
            return null;
        }
        if (!hasStart || !hasEnd) {
            return R.error(scopeLabel + "开放时间未完整设置，暂无法登录");
        }
        Date start = parseDateTime(startText);
        Date end = parseDateTime(endText);
        if (start == null || end == null) {
            return R.error(scopeLabel + "开放时间配置格式错误，请联系管理员");
        }
        Date now = new Date();
        if (now.before(start)) {
            return R.error("还未到" + scopeLabel + "登录时间，开放时间：" + startText);
        }
        if (now.after(end)) {
            return R.error(scopeLabel + "登录已关闭，结束时间：" + endText);
        }
        return null;
    }

    private R checkSystemOpenTimeForStudent(String studentMajor) {
        String normalizedMajor = MajorConstants.normalizeMajor(studentMajor);
        if (MajorConstants.isMajorValid(normalizedMajor)) {
            String majorStartKey = buildMajorConfigKey(MAJOR_OPEN_START_PREFIX, normalizedMajor);
            String majorEndKey = buildMajorConfigKey(MAJOR_OPEN_END_PREFIX, normalizedMajor);
            if (hasAnyOpenWindowConfig(majorStartKey, majorEndKey)) {
                return checkOpenTimeByKeys(majorStartKey, majorEndKey, normalizedMajor + "专业学生");
            }
        }
        return checkOpenTimeByKeys(SYSTEM_OPEN_START, SYSTEM_OPEN_END, "系统");
    }

    @Autowired
    private XuantishenqingService xuantishenqingService;


    
	@Autowired
	private TokenService tokenService;
	
	/**
	 * 登录
	 */
	@IgnoreAuth
	@RequestMapping(value = "/login")
	public R login(String username, String password, String captcha, HttpServletRequest request) {
		XueshengEntity u = xueshengService.selectOne(new EntityWrapper<XueshengEntity>().eq("xuehao", username));
		R openCheck = checkSystemOpenTimeForStudent(u != null ? u.getZhuanye() : null);
		if (openCheck != null) {
			return openCheck;
		}
		if (u == null || !PasswordUtils.matches(password, u.getMima())) {
			return R.error("账号或密码不正确");
		}
		if (!PasswordUtils.isEncoded(u.getMima())) {
			u.setMima(PasswordUtils.encode(password));
			xueshengService.updateById(u);
		}
		String token = tokenService.generateToken(u.getId(), username, "xuesheng", "学生");
		return R.ok().put("token", token);
	}


	
	/**
     * 未登录场景下的修改密码（学生）
     * 需要提供：学号(username) + 原密码 + 新密码
     */
	@IgnoreAuth
	@RequestMapping("/changePassword")
	public R changePassword(String username, String oldPassword, String newPassword, HttpServletRequest request) {
		if (username == null || username.trim().isEmpty()
				|| oldPassword == null || oldPassword.isEmpty()
				|| newPassword == null || newPassword.isEmpty()) {
			return R.error("学号、原密码和新密码均不能为空");
		}
		XueshengEntity u = xueshengService.selectOne(new EntityWrapper<XueshengEntity>().eq("xuehao", username));
		if (u == null) {
			return R.error("账号不存在");
		}
		if (!PasswordUtils.matches(oldPassword, u.getMima())) {
			return R.error("原密码不正确");
		}
		u.setMima(PasswordUtils.encode(newPassword));
		xueshengService.updateById(u);
		return R.ok("密码修改成功，请使用新密码登录");
	}

	/**
     * 注册
     */
	@IgnoreAuth
    @RequestMapping("/register")
    public R register(@RequestBody XueshengEntity xuesheng){
    	//ValidatorUtils.validateEntity(xuesheng);
    	XueshengEntity u = xueshengService.selectOne(new EntityWrapper<XueshengEntity>().eq("xuehao", xuesheng.getXuehao()));
		if(u!=null) {
			return R.error("注册用户已存在");
		}
		Long uId = new Date().getTime();
		R majorCheck = validateMajorField(xuesheng.getZhuanye());
		if (majorCheck != null) {
			return majorCheck;
		}
		xuesheng.setZhuanye(MajorConstants.normalizeMajor(xuesheng.getZhuanye()));
		applyStudentProfileDefaults(xuesheng);
		if (StringUtils.isNotBlank(xuesheng.getMima()) && !PasswordUtils.isEncoded(xuesheng.getMima())) {
			xuesheng.setMima(PasswordUtils.encode(xuesheng.getMima()));
		}
		xuesheng.setId(uId);
        xueshengService.insert(xuesheng);
        return R.ok();
    }

	
	/**
	 * 退出
	 */
	@RequestMapping("/logout")
	public R logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return R.ok("退出成功");
	}
	
	/**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request){
    	Long id = (Long)request.getSession().getAttribute("userId");
        XueshengEntity u = xueshengService.selectById(id);
        return R.ok().put("data", u);
    }
    
    /**
     * 密码重置
     */
    @IgnoreAuth
	@RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request){
    	XueshengEntity u = xueshengService.selectOne(new EntityWrapper<XueshengEntity>().eq("xuehao", username));
    	if (u == null) {
    		return R.error("账号不存在");
    	}
        u.setMima(PasswordUtils.encode("123456"));
        xueshengService.updateById(u);
        return R.ok("密码已重置为：123456");
    }


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,XueshengEntity xuesheng,
		HttpServletRequest request){
        EntityWrapper<XueshengEntity> ew = new EntityWrapper<XueshengEntity>();

		if (ScopeMajorUtil.isTeacherWithoutMajor(request, jiaoshiService)) {
			int limit = 10;
			int pageNum = 1;
			try {
				if (params.get("limit") != null) {
					limit = Integer.parseInt(params.get("limit").toString());
				}
				if (params.get("page") != null) {
					pageNum = Integer.parseInt(params.get("page").toString());
				}
			} catch (Exception ignore) {
			}
			return R.ok().put("data", new PageUtils(new ArrayList<>(), 0, limit, pageNum));
		}
		String scopedMajor = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);
		ScopeMajorUtil.applyMajorToQuery(scopedMajor, xuesheng, params);

		PageUtils page = xueshengService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, xuesheng), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,XueshengEntity xuesheng, 
		HttpServletRequest request){
        EntityWrapper<XueshengEntity> ew = new EntityWrapper<XueshengEntity>();

		PageUtils page = xueshengService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, xuesheng), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( XueshengEntity xuesheng){
       	EntityWrapper<XueshengEntity> ew = new EntityWrapper<XueshengEntity>();
      	ew.allEq(MPUtil.allEQMapPre( xuesheng, "xuesheng")); 
        return R.ok().put("data", xueshengService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(XueshengEntity xuesheng){
        EntityWrapper< XueshengEntity> ew = new EntityWrapper< XueshengEntity>();
 		ew.allEq(MPUtil.allEQMapPre( xuesheng, "xuesheng")); 
		XueshengView xueshengView =  xueshengService.selectView(ew);
		return R.ok("查询学生成功").put("data", xueshengView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        XueshengEntity xuesheng = xueshengService.selectById(id);
		xuesheng = xueshengService.selectView(new EntityWrapper<XueshengEntity>().eq("id", id));
		String scopedMajor = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);
		R denied = ScopeMajorUtil.denyIfOutOfScope(scopedMajor, xuesheng);
		if (denied != null) {
			return denied;
		}
        return R.ok().put("data", xuesheng);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        XueshengEntity xuesheng = xueshengService.selectById(id);
		xuesheng = xueshengService.selectView(new EntityWrapper<XueshengEntity>().eq("id", id));
        return R.ok().put("data", xuesheng);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody XueshengEntity xuesheng, HttpServletRequest request){
    	R scopeDenied = validateStudentMutateScope(xuesheng, request, true);
    	if (scopeDenied != null) {
    		return scopeDenied;
    	}
    	xuesheng.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(xuesheng);
    	XueshengEntity u = xueshengService.selectOne(new EntityWrapper<XueshengEntity>().eq("xuehao", xuesheng.getXuehao()));
		if (u != null) {
			return R.error("用户已存在");
		}
		if (xuesheng.getMima() != null && !xuesheng.getMima().isEmpty()) {
			xuesheng.setMima(PasswordUtils.encode(xuesheng.getMima()));
		}
		R majorCheck = validateMajorField(xuesheng.getZhuanye());
		if (majorCheck != null) {
			return majorCheck;
		}
		xuesheng.setZhuanye(MajorConstants.normalizeMajor(xuesheng.getZhuanye()));
		applyStudentProfileDefaults(xuesheng);
		xuesheng.setId(new Date().getTime());
        xueshengService.insert(xuesheng);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody XueshengEntity xuesheng, HttpServletRequest request){
    	xuesheng.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(xuesheng);
    	XueshengEntity u = xueshengService.selectOne(new EntityWrapper<XueshengEntity>().eq("xuehao", xuesheng.getXuehao()));
		if(u!=null) {
			return R.error("用户已存在");
		}
		R majorCheck = validateMajorField(xuesheng.getZhuanye());
		if (majorCheck != null) {
			return majorCheck;
		}
		xuesheng.setZhuanye(MajorConstants.normalizeMajor(xuesheng.getZhuanye()));
		applyStudentProfileDefaults(xuesheng);
		xuesheng.setId(new Date().getTime());
        xueshengService.insert(xuesheng);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody XueshengEntity xuesheng, HttpServletRequest request){
    	R scopeDenied = validateStudentMutateScope(xuesheng, request, false);
    	if (scopeDenied != null) {
    		return scopeDenied;
    	}
        //ValidatorUtils.validateEntity(xuesheng);
        if (xuesheng.getMima() != null && !xuesheng.getMima().isEmpty() && !PasswordUtils.isEncoded(xuesheng.getMima())) {
            xuesheng.setMima(PasswordUtils.encode(xuesheng.getMima()));
        }
		R majorCheck = validateMajorField(xuesheng.getZhuanye());
		if (majorCheck != null) {
			return majorCheck;
		}
		xuesheng.setZhuanye(MajorConstants.normalizeMajor(xuesheng.getZhuanye()));
		applyStudentProfileDefaults(xuesheng);
		resolveStudentIdForUpdate(xuesheng, request);
		if (xuesheng.getId() == null) {
			return R.error("更新失败：缺少用户标识，请重新登录后重试");
		}
        boolean updated = xueshengService.updateById(xuesheng);
        if (!updated) {
            return R.error("更新失败，请确认账号状态或重新登录后重试");
        }
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
    	R scopeDenied = validateStudentDeleteScope(ids, request);
    	if (scopeDenied != null) {
    		return scopeDenied;
    	}
        xueshengService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 已选题学生列表
     * - 超级管理员（未配置专业）：全部学生
     * - 专业管理员 / 教师：仅本专业学生
     */
    @RequestMapping("/selectedByMajor")
    public R selectedByMajor(HttpServletRequest request) {
        if (ScopeMajorUtil.isTeacherWithoutMajor(request, jiaoshiService)) {
            return R.ok().put("data", new ArrayList<>()).put("total", 0).put("selectedCount", 0);
        }
        String major = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);

        EntityWrapper<XueshengEntity> stuWrapper = new EntityWrapper<XueshengEntity>();
        if (major != null && !major.trim().isEmpty()) {
            stuWrapper.eq("zhuanye", major.trim());
        }

        List<XueshengEntity> all = xueshengService.selectList(stuWrapper);
        if (all == null || all.isEmpty()) {
            return R.ok().put("data", new ArrayList<>())
                    .put("total", 0)
                    .put("selectedCount", 0);
        }

        List<String> xuehaos = new ArrayList<>();
        for (XueshengEntity s : all) {
            if (s != null && s.getXuehao() != null && !s.getXuehao().trim().isEmpty()) {
                xuehaos.add(s.getXuehao());
            }
        }

        if (xuehaos.isEmpty()) {
            return R.ok().put("data", new ArrayList<>())
                    .put("total", all.size())
                    .put("selectedCount", 0);
        }

        List<XuantishenqingEntity> applied = xuantishenqingService.selectList(
                new EntityWrapper<XuantishenqingEntity>()
                        .in("xuehao", xuehaos)
                        .in("shenhezhuangtai", com.hadluo.utils.XuantishenqingStatusUtil.getAppliedStatuses())
        );

        Set<String> selectedSet = new HashSet<>();
        for (XuantishenqingEntity a : applied) {
            if (a != null && a.getXuehao() != null && !a.getXuehao().trim().isEmpty()) {
                selectedSet.add(a.getXuehao());
            }
        }

        List<XueshengEntity> selectedStudents = new ArrayList<>();
        for (XueshengEntity s : all) {
            if (s != null && selectedSet.contains(s.getXuehao())) {
                selectedStudents.add(s);
            }
        }

        return R.ok()
                .put("data", selectedStudents)
                .put("total", all.size())
                .put("selectedCount", selectedStudents.size());
    }

    /**
     * 未选题学生列表
     * - 超级管理员（未配置专业）：全部学生
     * - 专业管理员 / 教师：仅本专业学生
     */
    @RequestMapping("/unselectedByMajor")
    public R unselectedByMajor(HttpServletRequest request) {
        if (ScopeMajorUtil.isTeacherWithoutMajor(request, jiaoshiService)) {
            return R.ok().put("data", new ArrayList<>()).put("total", 0).put("unselectedCount", 0);
        }
        String major = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);

        EntityWrapper<XueshengEntity> stuWrapper = new EntityWrapper<XueshengEntity>();
        if (major != null && !major.trim().isEmpty()) {
            stuWrapper.eq("zhuanye", major.trim());
        }

        List<XueshengEntity> all = xueshengService.selectList(stuWrapper);
        if (all == null || all.isEmpty()) {
            return R.ok().put("data", new ArrayList<>())
                    .put("total", 0)
                    .put("unselectedCount", 0);
        }

        List<String> xuehaos = new ArrayList<>();
        for (XueshengEntity s : all) {
            if (s != null && s.getXuehao() != null && !s.getXuehao().trim().isEmpty()) {
                xuehaos.add(s.getXuehao());
            }
        }

        if (xuehaos.isEmpty()) {
            return R.ok().put("data", all)
                    .put("total", all.size())
                    .put("unselectedCount", all.size());
        }

        List<XuantishenqingEntity> applied = xuantishenqingService.selectList(
                new EntityWrapper<XuantishenqingEntity>()
                        .in("xuehao", xuehaos)
                        .in("shenhezhuangtai", com.hadluo.utils.XuantishenqingStatusUtil.getAppliedStatuses())
        );

        Set<String> selectedSet = new HashSet<>();
        for (XuantishenqingEntity a : applied) {
            if (a != null && a.getXuehao() != null && !a.getXuehao().trim().isEmpty()) {
                selectedSet.add(a.getXuehao());
            }
        }

        List<XueshengEntity> unselectedStudents = new ArrayList<>();
        for (XueshengEntity s : all) {
            if (s != null && !selectedSet.contains(s.getXuehao())) {
                unselectedStudents.add(s);
            }
        }

        return R.ok()
                .put("data", unselectedStudents)
                .put("total", all.size())
                .put("unselectedCount", unselectedStudents.size());
    }

    /**
     * Excel 批量导入学生信息
     * 表头字段：学号、密码、学生姓名、联系电话、性别、专业
     * 密码有值则按表中值导入，密码为空则取联系电话后六位
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public R importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            return R.error("请选择要导入的 Excel 文件");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
            return R.error("仅支持 .xls 或 .xlsx 格式文件");
        }

        String tableName = String.valueOf(request.getSession().getAttribute("tableName"));
        String scopedMajor = "";
        boolean isSuperAdmin = false;
        if ("users".equals(tableName)) {
            Long userId = (Long) request.getSession().getAttribute("userId");
            UsersEntity admin = userId != null ? usersService.selectById(userId) : null;
            scopedMajor = admin != null ? MajorConstants.normalizeMajor(admin.getZhuanye()) : "";
            isSuperAdmin = scopedMajor.isEmpty();
            if (!isSuperAdmin && !MajorConstants.isMajorValid(scopedMajor)) {
                return R.error("当前专业管理员专业配置不合法，请先修正后再导入");
            }
        } else if ("jiaoshi".equals(tableName)) {
            if (ScopeMajorUtil.isTeacherWithoutMajor(request, jiaoshiService)) {
                return R.error("当前教师专业未配置，无法导入学生");
            }
            scopedMajor = MajorConstants.normalizeMajor(
                    ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService));
            if (!MajorConstants.isMajorValid(scopedMajor)) {
                return R.error("当前教师专业未配置或不合法，无法导入学生");
            }
        } else {
            return R.error("无权执行学生批量导入");
        }

        List<XueshengEntity> toInsert = new ArrayList<>();
        int skippedDuplicateCount = 0;
        int skippedEmptyRows = 0;
        List<String> errorMessages = new ArrayList<>();
        List<String> duplicateMessages = new ArrayList<>();
        Set<String> studentNoInFile = new HashSet<>();
        try (InputStream is = file.getInputStream()) {
            Workbook wb = fileName.endsWith(".xlsx") ? new XSSFWorkbook(is) : new HSSFWorkbook(is);
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                wb.close();
                return R.error("Excel 工作表为空");
            }
            int headerRowIndex = locateStudentHeaderRow(sheet);
            Row headerRow = sheet.getRow(headerRowIndex);
            if (headerRow == null) {
                wb.close();
                return R.error("Excel 表头为空");
            }

            int colStudentNo = resolveStudentHeaderColumn(headerRow, "学号");
            int colPassword = resolveStudentHeaderColumn(headerRow, "密码");
            int colStudentName = resolveStudentHeaderColumn(headerRow, "学生姓名", "姓名");
            int colMobile = resolveStudentHeaderColumn(headerRow, "联系电话", "手机号码", "手机号");
            int colGender = resolveStudentHeaderColumn(headerRow, "性别");
            int colMajor = resolveStudentHeaderColumn(headerRow, "专业");
            int colNianji = resolveStudentHeaderColumn(headerRow, "年级");
            int colBanji = resolveStudentHeaderColumn(headerRow, "班级");

            if (colStudentNo < 0 || colStudentName < 0 || colMobile < 0 || colGender < 0 || colMajor < 0) {
                wb.close();
                return R.error("Excel 表头必须包含：学号、学生姓名、联系电话、性别、专业（密码列可选，为空时取电话后六位）");
            }

            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String studentNo = normalizeStudentNo(ExcelImportUtil.getCellString(sheet, row, colStudentNo));
                String rawPassword = colPassword >= 0 ? ExcelImportUtil.getCellString(sheet, row, colPassword) : "";
                String studentName = ExcelImportUtil.getCellString(sheet, row, colStudentName);
                String mobile = normalizeMobile(ExcelImportUtil.getCellString(sheet, row, colMobile));
                String gender = ExcelImportUtil.getCellString(sheet, row, colGender);
                String studentMajor = MajorConstants.normalizeMajor(ExcelImportUtil.getCellString(sheet, row, colMajor));
                String nianji = colNianji >= 0 ? ExcelImportUtil.getCellString(sheet, row, colNianji) : "";
                String banji = colBanji >= 0 ? ExcelImportUtil.getCellString(sheet, row, colBanji) : "";
                if (banji.endsWith(".0")) {
                    banji = banji.substring(0, banji.length() - 2);
                }

                if (studentNo.isEmpty() && studentName.isEmpty() && mobile.isEmpty()
                        && rawPassword.isEmpty() && gender.isEmpty() && studentMajor.isEmpty()) {
                    skippedEmptyRows++;
                    continue;
                }
                int excelRowNo = i + 1;
                if (studentNo.isEmpty() || studentName.isEmpty() || mobile.isEmpty() || studentMajor.isEmpty()) {
                    errorMessages.add("第 " + excelRowNo + " 行：学号、学生姓名、联系电话、专业不能为空");
                    continue;
                }
                if (!"男".equals(gender) && !"女".equals(gender)) {
                    errorMessages.add("第 " + excelRowNo + " 行：性别仅支持“男”或“女”");
                    continue;
                }
                if (!MajorConstants.isMajorValid(studentMajor)) {
                    errorMessages.add("第 " + excelRowNo + " 行：专业不在允许范围内（"
                            + String.join("、", MajorConstants.MAJOR_OPTIONS) + "）");
                    continue;
                }
                if (!isSuperAdmin && !scopedMajor.equals(studentMajor)) {
                    errorMessages.add("第 " + excelRowNo + " 行：仅可导入本专业学生（当前专业：" + scopedMajor + "）");
                    continue;
                }
                if (studentNoInFile.contains(studentNo)) {
                    errorMessages.add("第 " + excelRowNo + " 行：学号在 Excel 中重复（" + studentNo + "）");
                    continue;
                }
                studentNoInFile.add(studentNo);

                if (rawPassword.isEmpty()) {
                    String mobileDigits = mobile.replaceAll("[^0-9]", "");
                    if (mobileDigits.length() < 6) {
                        errorMessages.add("第 " + excelRowNo + " 行：密码为空时联系电话需至少 6 位数字");
                        continue;
                    }
                    rawPassword = mobileDigits.substring(mobileDigits.length() - 6);
                }
                if (rawPassword.length() != 6) {
                    errorMessages.add("第 " + excelRowNo + " 行：密码长度必须为 6 位");
                    continue;
                }

                XueshengEntity exists = xueshengService.selectOne(new EntityWrapper<XueshengEntity>().eq("xuehao", studentNo));
                if (exists != null) {
                    skippedDuplicateCount++;
                    String existMajor = MajorConstants.normalizeMajor(exists.getZhuanye());
                    String existName = StringUtils.defaultString(exists.getXueshengxingming(), "未知姓名");
                    if (!isSuperAdmin && StringUtils.isNotBlank(scopedMajor) && !scopedMajor.equals(existMajor)) {
                        duplicateMessages.add("学号 " + studentNo + " 已存在于【"
                                + (existMajor.isEmpty() ? "未设置专业" : existMajor) + "】（" + existName
                                + "），当前账号仅管理【" + scopedMajor + "】，列表中不会显示该学生");
                    } else {
                        duplicateMessages.add("学号 " + studentNo + " 已存在（" + existName + "，专业："
                                + (existMajor.isEmpty() ? "未设置" : existMajor) + "）");
                    }
                    continue;
                }

                XueshengEntity entity = new XueshengEntity();
                entity.setId(nextStudentImportId(i));
                entity.setXuehao(studentNo);
                entity.setXueshengxingming(studentName);
                entity.setShoujihaoma(mobile);
                entity.setXingbie(gender);
                entity.setZhuanye(studentMajor);
                entity.setNianji(nianji);
                entity.setBanji(banji);
                applyStudentProfileDefaults(entity);
                entity.setMima(PasswordUtils.encode(rawPassword));
                toInsert.add(entity);
            }
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败：" + e.getMessage());
        }

        if (toInsert.isEmpty()) {
            if (!errorMessages.isEmpty()) {
                return R.error("导入失败：" + buildStudentImportErrorMessage(errorMessages, 8));
            }
            if (skippedDuplicateCount > 0) {
                StringBuilder msg = new StringBuilder("未导入新学生：共 ").append(skippedDuplicateCount)
                        .append(" 条学号在系统中已存在。");
                if (!duplicateMessages.isEmpty()) {
                    msg.append("已存在明细：").append(buildStudentImportErrorMessage(duplicateMessages, 20));
                }
                return R.ok(msg.toString());
            }
            if (skippedEmptyRows > 0) {
                return R.error("Excel 未解析到有效数据行，请检查表头是否为：学号、学生姓名、联系电话、性别、专业，且数据从表头下一行开始填写");
            }
            return R.error("Excel 中没有有效的数据行");
        }

        for (XueshengEntity entity : toInsert) {
            xueshengService.insert(entity);
        }

        StringBuilder msg = new StringBuilder("导入成功：").append(toInsert.size()).append(" 条");
        if (skippedDuplicateCount > 0) {
            msg.append("，已跳过已存在学号 ").append(skippedDuplicateCount).append(" 条");
            if (!duplicateMessages.isEmpty()) {
                msg.append("（").append(buildStudentImportErrorMessage(duplicateMessages, 10)).append("）");
            }
        }
        if (!errorMessages.isEmpty()) {
            msg.append("，").append(errorMessages.size()).append(" 行未导入。问题明细：")
                    .append(buildStudentImportErrorMessage(errorMessages, 3));
        }
        return R.ok(msg.toString());
    }

    /** 教师/专业管理员新增或修改学生时，强制限定在本专业范围内 */
    private R validateStudentMutateScope(XueshengEntity xuesheng, HttpServletRequest request, boolean isNew) {
        if (ScopeMajorUtil.isTeacherWithoutMajor(request, jiaoshiService)) {
            return R.error("当前教师专业未配置，无法维护学生信息");
        }
        String scopedMajor = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);
        if (StringUtils.isNotBlank(scopedMajor)) {
            String normalized = MajorConstants.normalizeMajor(scopedMajor);
            if (!MajorConstants.isMajorValid(normalized)) {
                return R.error("当前账号专业未配置或不合法，无法维护学生信息");
            }
            if (xuesheng != null) {
                xuesheng.setZhuanye(normalized);
            }
            if (!isNew) {
                XueshengEntity existing = null;
                if (xuesheng != null && xuesheng.getId() != null) {
                    existing = xueshengService.selectById(xuesheng.getId());
                } else if (xuesheng != null && StringUtils.isNotBlank(xuesheng.getXuehao())) {
                    existing = xueshengService.selectOne(
                            new EntityWrapper<XueshengEntity>().eq("xuehao", xuesheng.getXuehao()));
                }
                R denied = ScopeMajorUtil.denyIfOutOfScope(normalized, existing);
                if (denied != null) {
                    return denied;
                }
            }
        }
        return null;
    }

    private R validateStudentDeleteScope(Long[] ids, HttpServletRequest request) {
        if (ids == null || ids.length == 0) {
            return R.error("请选择要删除的学生");
        }
        if (ScopeMajorUtil.isTeacherWithoutMajor(request, jiaoshiService)) {
            return R.error("当前教师专业未配置，无法删除学生");
        }
        String scopedMajor = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);
        if (StringUtils.isBlank(scopedMajor)) {
            return null;
        }
        for (Long id : ids) {
            if (id == null) {
                continue;
            }
            XueshengEntity existing = xueshengService.selectById(id);
            R denied = ScopeMajorUtil.denyIfOutOfScope(scopedMajor, existing);
            if (denied != null) {
                return denied;
            }
        }
        return null;
    }

}
