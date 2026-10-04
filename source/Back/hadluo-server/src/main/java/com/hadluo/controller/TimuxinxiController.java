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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Date;
import java.util.List;
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

import com.hadluo.entity.TimuxinxiEntity;
import com.hadluo.entity.JiaoshiEntity;
import com.hadluo.entity.view.TimuxinxiView;
import com.hadluo.entity.XuantishenqingEntity;
import com.hadluo.entity.TokenEntity;

import com.hadluo.service.TimuxinxiService;
import com.hadluo.service.TokenService;
import com.hadluo.service.XuantishenqingService;
import com.hadluo.service.XueshengService;
import com.hadluo.service.UsersService;
import com.hadluo.service.JiaoshiService;
import com.hadluo.entity.XueshengEntity;
import com.hadluo.entity.UsersEntity;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.ExcelImportUtil;
import com.hadluo.utils.MajorConstants;
import com.hadluo.utils.R;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.CommonUtil;
import java.io.IOException;
import java.io.InputStream;
import com.hadluo.service.StoreupService;
import com.hadluo.entity.StoreupEntity;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 题目信息
 * 后端接口
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@RestController
@RequestMapping("/timuxinxi")
public class TimuxinxiController {
    @Autowired
    private TimuxinxiService timuxinxiService;

    @Autowired
    private StoreupService storeupService;

    @Autowired
    private XuantishenqingService xuantishenqingService;

    @Autowired
    private XueshengService xueshengService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private JiaoshiService jiaoshiService;

    @Autowired
    private TokenService tokenService;

    private void ensureTimubianhao(TimuxinxiEntity timuxinxi) {
        if (timuxinxi == null || timuxinxi.getId() == null) {
            return;
        }
        if (StringUtils.isBlank(timuxinxi.getTimubianhao())) {
            timuxinxi.setTimubianhao(String.valueOf(timuxinxi.getId()));
        } else {
            timuxinxi.setTimubianhao(timuxinxi.getTimubianhao().trim());
        }
    }

    private long nextImportId(int rowIndex) {
        return System.currentTimeMillis() * 10000L + (rowIndex % 10000);
    }

    private void ensureUniqueTimubianhao(TimuxinxiEntity timuxinxi, Set<String> usedInFile, int rowIndex) {
        ensureTimubianhao(timuxinxi);
        int retry = 0;
        while (retry < 5) {
            String code = timuxinxi.getTimubianhao();
            if (StringUtils.isBlank(code)) {
                timuxinxi.setId(nextImportId(rowIndex + retry));
                ensureTimubianhao(timuxinxi);
                retry++;
                continue;
            }
            if (usedInFile.contains(code)) {
                timuxinxi.setId(nextImportId(rowIndex + retry));
                timuxinxi.setTimubianhao(null);
                ensureTimubianhao(timuxinxi);
                retry++;
                continue;
            }
            TimuxinxiEntity exists = timuxinxiService.selectOne(
                    new EntityWrapper<TimuxinxiEntity>().eq("timubianhao", code));
            if (exists != null) {
                timuxinxi.setId(nextImportId(rowIndex + retry));
                timuxinxi.setTimubianhao(null);
                ensureTimubianhao(timuxinxi);
                retry++;
                continue;
            }
            usedInFile.add(code);
            return;
        }
        throw new IllegalStateException("无法生成唯一题目编号");
    }

    private int resolveHeaderColumn(Row headerRow, String... aliases) {
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

    private int locateTimuxinxiHeaderRow(Sheet sheet) {
        int scanLimit = Math.min(sheet.getLastRowNum(), 9);
        for (int rowIndex = 0; rowIndex <= scanLimit; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            if (resolveHeaderColumn(row, "课题名称", "题目名称", "论文题目", "课题") >= 0
                    && resolveHeaderColumn(row, "题目类型") >= 0) {
                return rowIndex;
            }
        }
        return 0;
    }

    private String normalizeTeacherNo(String teacherNo) {
        if (teacherNo == null) {
            return "";
        }
        String normalized = teacherNo.replace('\u00A0', ' ').trim();
        if (normalized.endsWith(".0")) {
            normalized = normalized.substring(0, normalized.length() - 2);
        }
        return normalized;
    }

    private R applyMajorByTeacher(TimuxinxiEntity timuxinxi, HttpServletRequest request) {
        String tableName = String.valueOf(request.getSession().getAttribute("tableName"));
        String normalizedMajor = MajorConstants.normalizeMajor(timuxinxi.getZhuanye());
        String teacherNo = timuxinxi.getJiaoshigonghao() == null ? "" : timuxinxi.getJiaoshigonghao().trim();
        if ("jiaoshi".equals(tableName)) {
            String loginTeacherNo = (String) request.getSession().getAttribute("username");
            JiaoshiEntity teacher = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("jiaoshigonghao", loginTeacherNo));
            if (teacher == null) {
                return R.error("教师信息不存在，无法保存题目");
            }
            String teacherMajor = MajorConstants.normalizeMajor(teacher.getZhuanye());
            if (!MajorConstants.isMajorValid(teacherMajor)) {
                return R.error("教师专业未配置或不合法，请先维护教师专业");
            }
            timuxinxi.setJiaoshigonghao(loginTeacherNo);
            timuxinxi.setJiaoshixingming(teacher.getJiaoshixingming());
            timuxinxi.setZhuanye(teacherMajor);
            return null;
        }

        if (!teacherNo.isEmpty()) {
            JiaoshiEntity teacher = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("jiaoshigonghao", teacherNo));
            if (teacher == null) {
                return R.error("教师工号不存在，无法关联专业");
            }
            String teacherMajor = MajorConstants.normalizeMajor(teacher.getZhuanye());
            if (!MajorConstants.isMajorValid(teacherMajor)) {
                return R.error("该教师专业未配置或不合法，请先维护教师专业");
            }
            if (normalizedMajor.isEmpty()) {
                normalizedMajor = teacherMajor;
            }
            if (!teacherMajor.equals(normalizedMajor)) {
                return R.error("题目专业必须与教师专业一致");
            }
            timuxinxi.setZhuanye(teacherMajor);
            return null;
        }

        if (!MajorConstants.isMajorValid(normalizedMajor)) {
            return R.error("专业必须为以下之一：" + String.join("、", MajorConstants.MAJOR_OPTIONS));
        }
        timuxinxi.setZhuanye(normalizedMajor);
        return null;
    }


    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,TimuxinxiEntity timuxinxi,
		HttpServletRequest request){
		String tableName = String.valueOf(request.getSession().getAttribute("tableName"));
		// 教师：只看自己的题目
		if("jiaoshi".equals(tableName)) {
			timuxinxi.setJiaoshigonghao((String)request.getSession().getAttribute("username"));
		}
		// 专业管理员：按 users.zhuanye 过滤题目
		if("users".equals(tableName)) {
			Long userId = (Long) request.getSession().getAttribute("userId");
			if (userId != null) {
				UsersEntity admin = usersService.selectById(userId);
				if (admin != null && admin.getZhuanye() != null && !admin.getZhuanye().trim().isEmpty()) {
					String major = admin.getZhuanye().trim();
					timuxinxi.setZhuanye(major);
					if (params != null) {
						params.put("zhuanye", major);
					}
				}
			}
		}
        EntityWrapper<TimuxinxiEntity> ew = new EntityWrapper<TimuxinxiEntity>();

		PageUtils page = timuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, timuxinxi), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表（用户端题目列表，标记已被选的题目）
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,TimuxinxiEntity timuxinxi, 
		HttpServletRequest request){
        EntityWrapper<TimuxinxiEntity> ew = new EntityWrapper<TimuxinxiEntity>();

        // 学生端：只展示本专业教师发布的题目（按学生表的 zhuanye 精确匹配）
        // 注意：此接口标了 @IgnoreAuth，不会走 AuthorizationInterceptor 来设置 session。
        // 但后续页面切换（admin/学生）可能会导致 session 被覆盖，因此这里改为基于请求头 Token 判定。
        String token = request.getHeader("Token");
        TokenEntity tokenEntity = null;
        if (StringUtils.isNotBlank(token)) {
            tokenEntity = tokenService.getTokenEntity(token);
        }
        if (tokenEntity != null && "xuesheng".equals(tokenEntity.getTablename())) {
            String xuehao = tokenEntity.getUsername();
            if (xuehao != null) {
                XueshengEntity stu = xueshengService.selectOne(
                        new EntityWrapper<XueshengEntity>().eq("xuehao", xuehao)
                );
                if (stu != null && stu.getZhuanye() != null && !stu.getZhuanye().trim().isEmpty()) {
                    String major = stu.getZhuanye().trim();
                    if (params != null) {
                        params.put("zhuanye", major);
                    }
                    timuxinxi.setZhuanye(major);
                }
            }
        }

		PageUtils page = timuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, timuxinxi), params), params));
        // 为每条题目标记是否已被学生选中（选题申请中该题目编号已有“已审核”记录）
        if (page.getList() != null) {
            for (Object row : page.getList()) {
                TimuxinxiEntity t = (TimuxinxiEntity) row;
                if (StringUtils.isNotBlank(t.getTimubianhao())) {
                    int count = xuantishenqingService.selectCount(
                        new EntityWrapper<XuantishenqingEntity>()
                            .eq("timubianhao", t.getTimubianhao())
                            .in("shenhezhuangtai", com.hadluo.utils.XuantishenqingStatusUtil.getAppliedStatuses()));
                    t.setYibeixuan(count > 0);
                } else {
                    t.setYibeixuan(false);
                }
            }
        }
        return R.ok().put("data", page);
    }

    /**
     * 用户端首页图表统计（题目类型分布、近7天选题趋势）
     */
    @IgnoreAuth
    @RequestMapping("/homeChartStats")
    public R homeChartStats(HttpServletRequest request) {
        String major = null;
        String token = request.getHeader("Token");
        if (StringUtils.isNotBlank(token)) {
            TokenEntity tokenEntity = tokenService.getTokenEntity(token);
            if (tokenEntity != null && "xuesheng".equals(tokenEntity.getTablename())) {
                String xuehao = tokenEntity.getUsername();
                if (xuehao != null) {
                    XueshengEntity stu = xueshengService.selectOne(
                            new EntityWrapper<XueshengEntity>().eq("xuehao", xuehao)
                    );
                    if (stu != null && stu.getZhuanye() != null && !stu.getZhuanye().trim().isEmpty()) {
                        major = stu.getZhuanye().trim();
                    }
                }
            }
        }

        EntityWrapper<TimuxinxiEntity> topicEw = new EntityWrapper<TimuxinxiEntity>();
        if (major != null) {
            topicEw.eq("zhuanye", major);
        }
        List<TimuxinxiEntity> topics = timuxinxiService.selectList(topicEw);
        Map<String, Integer> typeMap = new LinkedHashMap<String, Integer>();
        for (TimuxinxiEntity topic : topics) {
            String type = topic.getTimuleixing();
            if (type == null || type.trim().isEmpty()) {
                type = "未分类";
            } else {
                type = type.trim();
            }
            typeMap.put(type, typeMap.getOrDefault(type, 0) + 1);
        }
        List<Map<String, Object>> typeDistribution = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, Integer> entry : typeMap.entrySet()) {
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            typeDistribution.add(item);
        }

        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        startCal.add(Calendar.DAY_OF_MONTH, -6);
        Date startDate = startCal.getTime();

        EntityWrapper<XuantishenqingEntity> appEw = new EntityWrapper<XuantishenqingEntity>();
        appEw.ge("shenqingshijian", startDate);
        if (major != null) {
            appEw.eq("zhuanye", major);
        }
        List<XuantishenqingEntity> applications = xuantishenqingService.selectList(appEw);

        SimpleDateFormat dayKeyFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dayLabelFormat = new SimpleDateFormat("M/d");
        Map<String, Integer> dayCounts = new LinkedHashMap<String, Integer>();
        Calendar dayCal = Calendar.getInstance();
        dayCal.set(Calendar.HOUR_OF_DAY, 0);
        dayCal.set(Calendar.MINUTE, 0);
        dayCal.set(Calendar.SECOND, 0);
        dayCal.set(Calendar.MILLISECOND, 0);
        dayCal.add(Calendar.DAY_OF_MONTH, -6);
        for (int i = 0; i < 7; i++) {
            dayCounts.put(dayKeyFormat.format(dayCal.getTime()), 0);
            dayCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        for (XuantishenqingEntity application : applications) {
            if (application.getShenqingshijian() == null) {
                continue;
            }
            String dayKey = dayKeyFormat.format(application.getShenqingshijian());
            if (dayCounts.containsKey(dayKey)) {
                dayCounts.put(dayKey, dayCounts.get(dayKey) + 1);
            }
        }

        List<String> trendDates = new ArrayList<String>();
        List<Integer> trendCounts = new ArrayList<Integer>();
        for (Map.Entry<String, Integer> entry : dayCounts.entrySet()) {
            try {
                trendDates.add(dayLabelFormat.format(dayKeyFormat.parse(entry.getKey())));
            } catch (ParseException e) {
                trendDates.add(entry.getKey());
            }
            trendCounts.add(entry.getValue());
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("typeDistribution", typeDistribution);
        data.put("trendDates", trendDates);
        data.put("trendCounts", trendCounts);
        return R.ok().put("data", data);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( TimuxinxiEntity timuxinxi){
       	EntityWrapper<TimuxinxiEntity> ew = new EntityWrapper<TimuxinxiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( timuxinxi, "timuxinxi")); 
        return R.ok().put("data", timuxinxiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(TimuxinxiEntity timuxinxi){
        EntityWrapper< TimuxinxiEntity> ew = new EntityWrapper< TimuxinxiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( timuxinxi, "timuxinxi")); 
		TimuxinxiView timuxinxiView =  timuxinxiService.selectView(ew);
		return R.ok("查询题目信息成功").put("data", timuxinxiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        TimuxinxiEntity timuxinxi = timuxinxiService.selectById(id);
		timuxinxi = timuxinxiService.selectView(new EntityWrapper<TimuxinxiEntity>().eq("id", id));
        return R.ok().put("data", timuxinxi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        TimuxinxiEntity timuxinxi = timuxinxiService.selectById(id);
		timuxinxi = timuxinxiService.selectView(new EntityWrapper<TimuxinxiEntity>().eq("id", id));
        return R.ok().put("data", timuxinxi);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody TimuxinxiEntity timuxinxi, HttpServletRequest request){
    	timuxinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	ensureTimubianhao(timuxinxi);
		R majorCheck = applyMajorByTeacher(timuxinxi, request);
		if (majorCheck != null) {
			return majorCheck;
		}
        timuxinxiService.insert(timuxinxi);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody TimuxinxiEntity timuxinxi, HttpServletRequest request){
    	timuxinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	ensureTimubianhao(timuxinxi);
		R majorCheck = applyMajorByTeacher(timuxinxi, request);
		if (majorCheck != null) {
			return majorCheck;
		}
        timuxinxiService.insert(timuxinxi);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody TimuxinxiEntity timuxinxi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(timuxinxi);
		R majorCheck = applyMajorByTeacher(timuxinxi, request);
		if (majorCheck != null) {
			return majorCheck;
		}
        timuxinxiService.updateById(timuxinxi);//全部更新
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        timuxinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
    /**
     * Excel 批量导入题目信息
     * 教师导入：Excel 仅需包含「课题名称、题目类型、课题性质、题目范围」四列；
     * 题目编号可省略（系统自动生成），专业与教师信息按当前登录教师账号自动关联。
     * 管理员导入：除上述四列外，需额外提供「教师工号」列。
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class)
    public R importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            return R.error("请选择要导入的文件");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
            return R.error("仅支持 .xls 或 .xlsx 格式的 Excel 文件");
        }
        String tableName = String.valueOf(request.getSession().getAttribute("tableName"));
        boolean teacherImport = "jiaoshi".equals(tableName);
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
        } else if (!teacherImport) {
            return R.error("无权执行题目批量导入");
        }
        String loginTeacherNo = null;
        String loginTeacherName = null;
        JiaoshiEntity loginTeacher = null;
        if (teacherImport) {
            loginTeacherNo = (String) request.getSession().getAttribute("username");
            if (StringUtils.isBlank(loginTeacherNo)) {
                return R.error("未获取到教师工号，请重新登录后再试");
            }
            loginTeacher = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("jiaoshigonghao", loginTeacherNo));
            if (loginTeacher == null) {
                return R.error("教师信息不存在，无法导入题目");
            }
            loginTeacherName = loginTeacher.getJiaoshixingming();
            String teacherMajor = MajorConstants.normalizeMajor(loginTeacher.getZhuanye());
            if (!MajorConstants.isMajorValid(teacherMajor)) {
                return R.error("当前教师专业未配置或不合法，请先维护教师专业");
            }
        }
        List<TimuxinxiEntity> toInsert = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        List<String> skippedScopeMessages = new ArrayList<>();
        Set<String> usedTimubianhao = new HashSet<>();
        int skippedScopeCount = 0;
        int skippedEmptyRows = 0;
        try (InputStream is = file.getInputStream()) {
            Workbook wb = fileName.endsWith(".xlsx") ? new XSSFWorkbook(is) : new HSSFWorkbook(is);
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                wb.close();
                return R.error("Excel 工作表为空");
            }
            int headerRowIndex = locateTimuxinxiHeaderRow(sheet);
            Row headerRow = sheet.getRow(headerRowIndex);
            if (headerRow == null) {
                wb.close();
                return R.error("Excel 表头为空");
            }
            int colTimubianhao = resolveHeaderColumn(headerRow, "题目编号", "编号");
            int colKetimingcheng = resolveHeaderColumn(headerRow, "课题名称", "题目名称", "论文题目", "课题");
            int colTimuleixing = resolveHeaderColumn(headerRow, "题目类型");
            int colKetixingzhi = resolveHeaderColumn(headerRow, "课题性质");
            int colTimufanwei = resolveHeaderColumn(headerRow, "题目范围", "研究内容");
            int colJiaoshigonghao = resolveHeaderColumn(headerRow, "教师工号", "指导教师工号");
            if (colKetimingcheng < 0 || colTimuleixing < 0 || colKetixingzhi < 0 || colTimufanwei < 0) {
                wb.close();
                return R.error("Excel 表头必须包含：课题名称、题目类型、课题性质、题目范围");
            }
            if (!teacherImport && colJiaoshigonghao < 0) {
                wb.close();
                return R.error("管理员导入时 Excel 表头必须额外包含：教师工号");
            }
            for (int i = headerRowIndex + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                String title = ExcelImportUtil.getCellString(sheet, row, colKetimingcheng);
                String type = ExcelImportUtil.getCellString(sheet, row, colTimuleixing);
                String nature = ExcelImportUtil.getCellString(sheet, row, colKetixingzhi);
                String scope = ExcelImportUtil.getCellString(sheet, row, colTimufanwei);
                if (title.isEmpty() && type.isEmpty() && nature.isEmpty() && scope.isEmpty()) {
                    skippedEmptyRows++;
                    continue;
                }
                int excelRowNo = i + 1;
                if (title.isEmpty()) {
                    errorMessages.add("第 " + excelRowNo + " 行：课题名称不能为空");
                    continue;
                }
                if (type.isEmpty()) {
                    errorMessages.add("第 " + excelRowNo + " 行：题目类型不能为空");
                    continue;
                }
                if (nature.isEmpty()) {
                    errorMessages.add("第 " + excelRowNo + " 行：课题性质不能为空");
                    continue;
                }
                if (scope.isEmpty()) {
                    errorMessages.add("第 " + excelRowNo + " 行：题目范围不能为空");
                    continue;
                }
                TimuxinxiEntity entity = new TimuxinxiEntity();
                entity.setId(nextImportId(i));
                String code = colTimubianhao >= 0 ? ExcelImportUtil.getCellString(sheet, row, colTimubianhao) : "";
                if (!code.isEmpty()) {
                    entity.setTimubianhao(code);
                }
                try {
                    ensureUniqueTimubianhao(entity, usedTimubianhao, i);
                } catch (IllegalStateException ex) {
                    errorMessages.add("第 " + excelRowNo + " 行：无法生成唯一题目编号");
                    continue;
                }
                entity.setKetimingcheng(title);
                entity.setTimuleixing(type);
                entity.setKetixingzhi(nature);
                entity.setTimufanwei(scope);
                if (teacherImport) {
                    entity.setJiaoshigonghao(loginTeacherNo);
                    entity.setJiaoshixingming(loginTeacherName);
                    entity.setZhuanye(MajorConstants.normalizeMajor(loginTeacher.getZhuanye()));
                } else {
                    String rowTeacherNo = normalizeTeacherNo(
                            colJiaoshigonghao >= 0 ? ExcelImportUtil.getCellString(sheet, row, colJiaoshigonghao) : "");
                    if (rowTeacherNo.isEmpty()) {
                        errorMessages.add("第 " + excelRowNo + " 行：教师工号不能为空");
                        continue;
                    }
                    JiaoshiEntity rowTeacher = jiaoshiService.selectOne(
                            new EntityWrapper<JiaoshiEntity>().eq("jiaoshigonghao", rowTeacherNo));
                    if (rowTeacher == null) {
                        errorMessages.add("第 " + excelRowNo + " 行：教师工号不存在（" + rowTeacherNo + "）");
                        continue;
                    }
                    String rowTeacherMajor = MajorConstants.normalizeMajor(rowTeacher.getZhuanye());
                    if (!MajorConstants.isMajorValid(rowTeacherMajor)) {
                        errorMessages.add("第 " + excelRowNo + " 行：对应教师专业未配置或不合法（" + rowTeacherNo + "）");
                        continue;
                    }
                    if (!isSuperAdmin && !scopedMajor.equals(rowTeacherMajor)) {
                        skippedScopeCount++;
                        skippedScopeMessages.add("第 " + excelRowNo + " 行：教师 " + rowTeacherNo + "（"
                                + StringUtils.defaultString(rowTeacher.getJiaoshixingming(), "未知姓名")
                                + "）属于【" + rowTeacherMajor + "】，当前账号仅管理【" + scopedMajor
                                + "】，已跳过（导入后不会出现在当前专业列表）");
                        continue;
                    }
                    entity.setJiaoshigonghao(rowTeacherNo);
                    entity.setJiaoshixingming(rowTeacher.getJiaoshixingming());
                    entity.setZhuanye(isSuperAdmin ? rowTeacherMajor : scopedMajor);
                }
                entity.setFabushijian(new Date());
                entity.setAddtime(new Date());
                toInsert.add(entity);
            }
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败：" + e.getMessage());
        }
        if (toInsert.isEmpty()) {
            if (!errorMessages.isEmpty()) {
                return R.error(buildImportErrorMessage(errorMessages, 8));
            }
            if (skippedScopeCount > 0) {
                StringBuilder msg = new StringBuilder("未导入新题目：共 ").append(skippedScopeCount)
                        .append(" 行因教师不属于当前管理专业而跳过。");
                if (!skippedScopeMessages.isEmpty()) {
                    msg.append("明细：").append(buildImportErrorMessage(skippedScopeMessages, 20));
                }
                return R.ok(msg.toString());
            }
            if (skippedEmptyRows > 0) {
                return R.error("Excel 未解析到有效数据行，请检查表头是否为：课题名称、题目类型、课题性质、题目范围，且数据从表头下一行开始填写");
            }
            return R.error("Excel 中没有有效的数据行");
        }
        for (TimuxinxiEntity entity : toInsert) {
            timuxinxiService.insert(entity);
        }
        StringBuilder msg = new StringBuilder("导入成功：").append(toInsert.size()).append(" 条题目");
        if (!isSuperAdmin && StringUtils.isNotBlank(scopedMajor)) {
            msg.append("（专业：").append(scopedMajor).append("）");
        }
        if (skippedScopeCount > 0) {
            msg.append("，已跳过非本专业教师 ").append(skippedScopeCount).append(" 行");
            if (!skippedScopeMessages.isEmpty()) {
                msg.append("（").append(buildImportErrorMessage(skippedScopeMessages, 10)).append("）");
            }
        }
        if (!errorMessages.isEmpty()) {
            msg.append("，").append(errorMessages.size()).append(" 行未导入。问题明细：")
                    .append(buildImportErrorMessage(errorMessages, 3));
        }
        if (skippedEmptyRows > 0) {
            msg.append("；跳过空行 ").append(skippedEmptyRows).append(" 行");
        }
        return R.ok(msg.toString());
    }

    private String buildImportErrorMessage(List<String> errorMessages, int maxLines) {
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
}
