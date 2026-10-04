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
import java.util.Set;
import java.util.HashSet;
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

import com.hadluo.entity.JiaoshiEntity;
import com.hadluo.entity.ConfigEntity;
import com.hadluo.entity.TokenEntity;
import com.hadluo.entity.UsersEntity;
import com.hadluo.entity.XueshengEntity;
import com.hadluo.entity.view.JiaoshiView;

import com.hadluo.service.JiaoshiService;
import com.hadluo.service.ConfigService;
import com.hadluo.service.TokenService;
import com.hadluo.service.UsersService;
import com.hadluo.service.XueshengService;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.MajorConstants;
import com.hadluo.utils.PasswordUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.CommonUtil;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 教师
 * 后端接口
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@RestController
@RequestMapping("/jiaoshi")
public class JiaoshiController {
	private static final String SYSTEM_OPEN_START = "system_open_start";
	private static final String SYSTEM_OPEN_END = "system_open_end";

    @Autowired
    private JiaoshiService jiaoshiService;

	@Autowired
	private UsersService usersService;

	@Autowired
	private XueshengService xueshengService;

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

	private R checkSystemOpenTimeForAllMembers() {
		try {
			String startText = getConfigValue(SYSTEM_OPEN_START);
			String endText = getConfigValue(SYSTEM_OPEN_END);
			boolean hasStart = startText != null && !startText.trim().isEmpty();
			boolean hasEnd = endText != null && !endText.trim().isEmpty();
			if (!hasStart && !hasEnd) {
				return null;
			}
			if (!hasStart || !hasEnd) {
				return R.error("系统开放时间未完整设置，暂无法登录");
			}
			Date start = parseDateTime(startText);
			Date end = parseDateTime(endText);
			if (start == null || end == null) {
				return R.error("系统开放时间配置格式错误，请联系管理员");
			}
			Date now = new Date();
			if (now.before(start)) {
				return R.error("还未到系统登录时间，开放时间：" + startText);
			}
			if (now.after(end)) {
				return R.error("系统登录已关闭，结束时间：" + endText);
			}
			return null;
		} catch (Exception e) {
			return R.error("系统开放时间校验失败，请稍后重试");
		}
	}


    
	@Autowired
	private TokenService tokenService;
	
	/**
	 * 登录
	 */
	@IgnoreAuth
	@RequestMapping(value = "/login")
	public R login(String username, String password, String captcha, HttpServletRequest request) {
		R openCheck = checkSystemOpenTimeForAllMembers();
		if (openCheck != null) {
			return openCheck;
		}
		JiaoshiEntity u = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("jiaoshigonghao", username));
		if (u == null || !PasswordUtils.matches(password, u.getMima())) {
			return R.error("账号或密码不正确");
		}
		if (!PasswordUtils.isEncoded(u.getMima())) {
			u.setMima(PasswordUtils.encode(password));
			jiaoshiService.updateById(u);
		}
		String token = tokenService.generateToken(u.getId(), username, "jiaoshi", "管理员");
		return R.ok().put("token", token);
	}


	
	/**
     * 未登录场景下的修改密码（教师/管理员）
     * 需要提供：教师工号(username) + 原密码 + 新密码
     */
	@IgnoreAuth
	@RequestMapping("/changePassword")
	public R changePassword(String username, String oldPassword, String newPassword, HttpServletRequest request) {
		if (username == null || username.trim().isEmpty()
				|| oldPassword == null || oldPassword.isEmpty()
				|| newPassword == null || newPassword.isEmpty()) {
			return R.error("工号、原密码和新密码均不能为空");
		}
		JiaoshiEntity u = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("jiaoshigonghao", username));
		if (u == null) {
			return R.error("账号不存在");
		}
		if (!PasswordUtils.matches(oldPassword, u.getMima())) {
			return R.error("原密码不正确");
		}
		u.setMima(PasswordUtils.encode(newPassword));
		jiaoshiService.updateById(u);
		return R.ok("密码修改成功，请使用新密码登录");
	}

	/**
     * 注册
     */
	@IgnoreAuth
    @RequestMapping("/register")
    public R register(@RequestBody JiaoshiEntity jiaoshi){
    	//ValidatorUtils.validateEntity(jiaoshi);
    	JiaoshiEntity u = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("jiaoshigonghao", jiaoshi.getJiaoshigonghao()));
		if (u != null) {
			return R.error("注册用户已存在");
		}
		if (jiaoshi.getMima() != null && !jiaoshi.getMima().isEmpty()) {
			jiaoshi.setMima(PasswordUtils.encode(jiaoshi.getMima()));
		}
		R majorCheck = validateMajorField(jiaoshi.getZhuanye());
		if (majorCheck != null) {
			return majorCheck;
		}
		jiaoshi.setZhuanye(MajorConstants.normalizeMajor(jiaoshi.getZhuanye()));
		Long uId = new Date().getTime();
		jiaoshi.setId(uId);
        jiaoshiService.insert(jiaoshi);
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
        JiaoshiEntity u = jiaoshiService.selectById(id);
        return R.ok().put("data", u);
    }
    
    /**
     * 密码重置
     */
    @IgnoreAuth
	@RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request){
    	JiaoshiEntity u = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("jiaoshigonghao", username));
    	if (u == null) {
    		return R.error("账号不存在");
    	}
        u.setMima(PasswordUtils.encode("123456"));
        jiaoshiService.updateById(u);
        return R.ok("密码已重置为：123456");
    }


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,JiaoshiEntity jiaoshi,
		HttpServletRequest request){
        EntityWrapper<JiaoshiEntity> ew = new EntityWrapper<JiaoshiEntity>();
		String tableName = String.valueOf(request.getSession().getAttribute("tableName"));
		if ("users".equals(tableName)) {
			Long userId = (Long) request.getSession().getAttribute("userId");
			if (userId != null) {
				UsersEntity admin = usersService.selectById(userId);
				if (admin != null && admin.getZhuanye() != null && !admin.getZhuanye().trim().isEmpty()) {
					String major = admin.getZhuanye().trim();
					jiaoshi.setZhuanye(major);
					if (params != null) {
						params.put("zhuanye", major);
					}
				}
			}
		}

		PageUtils page = jiaoshiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jiaoshi), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,JiaoshiEntity jiaoshi, 
		HttpServletRequest request){
        EntityWrapper<JiaoshiEntity> ew = new EntityWrapper<JiaoshiEntity>();
		String token = request.getHeader("Token");
		TokenEntity tokenEntity = null;
		if (StringUtils.isNotBlank(token)) {
			tokenEntity = tokenService.getTokenEntity(token);
		}
		if (tokenEntity != null && "xuesheng".equals(tokenEntity.getTablename())) {
			XueshengEntity stu = xueshengService.selectOne(new EntityWrapper<XueshengEntity>().eq("xuehao", tokenEntity.getUsername()));
			if (stu != null && stu.getZhuanye() != null && !stu.getZhuanye().trim().isEmpty()) {
				String major = stu.getZhuanye().trim();
				jiaoshi.setZhuanye(major);
				if (params != null) {
					params.put("zhuanye", major);
				}
			}
		}

		PageUtils page = jiaoshiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, jiaoshi), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( JiaoshiEntity jiaoshi){
       	EntityWrapper<JiaoshiEntity> ew = new EntityWrapper<JiaoshiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( jiaoshi, "jiaoshi")); 
        return R.ok().put("data", jiaoshiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(JiaoshiEntity jiaoshi){
        EntityWrapper< JiaoshiEntity> ew = new EntityWrapper< JiaoshiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( jiaoshi, "jiaoshi")); 
		JiaoshiView jiaoshiView =  jiaoshiService.selectView(ew);
		return R.ok("查询教师成功").put("data", jiaoshiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        JiaoshiEntity jiaoshi = jiaoshiService.selectById(id);
		jiaoshi = jiaoshiService.selectView(new EntityWrapper<JiaoshiEntity>().eq("id", id));
        return R.ok().put("data", jiaoshi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        JiaoshiEntity jiaoshi = jiaoshiService.selectById(id);
		jiaoshi = jiaoshiService.selectView(new EntityWrapper<JiaoshiEntity>().eq("id", id));
        return R.ok().put("data", jiaoshi);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody JiaoshiEntity jiaoshi, HttpServletRequest request){
    	jiaoshi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(jiaoshi);
    	JiaoshiEntity u = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("jiaoshigonghao", jiaoshi.getJiaoshigonghao()));
		if (u != null) {
			return R.error("用户已存在");
		}
		if (jiaoshi.getMima() != null && !jiaoshi.getMima().isEmpty()) {
			jiaoshi.setMima(PasswordUtils.encode(jiaoshi.getMima()));
		}
		R majorCheck = validateMajorField(jiaoshi.getZhuanye());
		if (majorCheck != null) {
			return majorCheck;
		}
		jiaoshi.setZhuanye(MajorConstants.normalizeMajor(jiaoshi.getZhuanye()));
		jiaoshi.setId(new Date().getTime());
        jiaoshiService.insert(jiaoshi);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody JiaoshiEntity jiaoshi, HttpServletRequest request){
    	jiaoshi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(jiaoshi);
    	JiaoshiEntity u = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("jiaoshigonghao", jiaoshi.getJiaoshigonghao()));
		if (u != null) {
			return R.error("用户已存在");
		}
		if (jiaoshi.getMima() != null && !jiaoshi.getMima().isEmpty()) {
			jiaoshi.setMima(PasswordUtils.encode(jiaoshi.getMima()));
		}
		R majorCheck = validateMajorField(jiaoshi.getZhuanye());
		if (majorCheck != null) {
			return majorCheck;
		}
		jiaoshi.setZhuanye(MajorConstants.normalizeMajor(jiaoshi.getZhuanye()));
		jiaoshi.setId(new Date().getTime());
        jiaoshiService.insert(jiaoshi);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody JiaoshiEntity jiaoshi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(jiaoshi);
        if (jiaoshi.getMima() != null && !jiaoshi.getMima().isEmpty() && !PasswordUtils.isEncoded(jiaoshi.getMima())) {
            jiaoshi.setMima(PasswordUtils.encode(jiaoshi.getMima()));
        }
		R majorCheck = validateMajorField(jiaoshi.getZhuanye());
		if (majorCheck != null) {
			return majorCheck;
		}
		jiaoshi.setZhuanye(MajorConstants.normalizeMajor(jiaoshi.getZhuanye()));
        jiaoshiService.updateById(jiaoshi);//全部更新
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        jiaoshiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

	/**
	 * Excel 批量导入教师信息
	 * 表头字段：
	 * - 教师工号
	 * - 密码（若为空，则默认取联系电话后六位）
	 * - 教师姓名
	 * - 联系电话
	 * - 性别
	 * - 专业
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public R importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		if (file == null || file.isEmpty()) {
			return R.error("请选择要导入的 Excel 文件");
		}
		String fileName = file.getOriginalFilename();
		if (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
			return R.error("仅支持 .xls 或 .xlsx 格式文件");
		}

		String tableName = String.valueOf(request.getSession().getAttribute("tableName"));
		if (!"users".equals(tableName)) {
			return R.error("仅管理员可执行教师批量导入");
		}
		Long userId = (Long) request.getSession().getAttribute("userId");
		UsersEntity admin = userId != null ? usersService.selectById(userId) : null;
		String adminMajor = admin != null ? MajorConstants.normalizeMajor(admin.getZhuanye()) : "";
		boolean isSuperAdmin = adminMajor.isEmpty();
		if (!isSuperAdmin && !MajorConstants.isMajorValid(adminMajor)) {
			return R.error("当前专业管理员专业配置不合法，请先修正后再导入");
		}

		List<JiaoshiEntity> toInsert = new ArrayList<>();
		int skippedCount = 0;
		List<String> errorMessages = new ArrayList<>();
		Set<String> teacherNoInFile = new HashSet<>();
		try (InputStream is = file.getInputStream()) {
			Workbook wb = fileName.endsWith(".xlsx") ? new XSSFWorkbook(is) : new HSSFWorkbook(is);
			Sheet sheet = wb.getSheetAt(0);
			if (sheet == null) {
				wb.close();
				return R.error("Excel 工作表为空");
			}
			Row headerRow = sheet.getRow(0);
			if (headerRow == null) {
				wb.close();
				return R.error("Excel 表头为空");
			}

			int lastCellNum = headerRow.getLastCellNum();
			int colTeacherNo = -1;
			int colPassword = -1;
			int colTeacherName = -1;
			int colMobile = -1;
			int colGender = -1;
			int colMajor = -1;
			for (int i = 0; i < lastCellNum; i++) {
				String title = getCellString(headerRow.getCell(i)).trim();
				if ("教师工号".equals(title)) {
					colTeacherNo = i;
				} else if ("密码".equals(title)) {
					colPassword = i;
				} else if ("教师姓名".equals(title)) {
					colTeacherName = i;
				} else if ("联系电话".equals(title)) {
					colMobile = i;
				} else if ("性别".equals(title)) {
					colGender = i;
				} else if ("专业".equals(title)) {
					colMajor = i;
				}
			}
			if (colTeacherNo < 0 || colPassword < 0 || colTeacherName < 0 || colMobile < 0 || colGender < 0 || colMajor < 0) {
				wb.close();
				return R.error("Excel 表头必须包含：教师工号、密码、教师姓名、联系电话、性别、专业");
			}

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null) {
					continue;
				}
				String teacherNo = getCellString(row.getCell(colTeacherNo)).trim();
				String rawPassword = getCellString(row.getCell(colPassword)).trim();
				String teacherName = getCellString(row.getCell(colTeacherName)).trim();
				String mobile = getCellString(row.getCell(colMobile)).trim();
				String gender = getCellString(row.getCell(colGender)).trim();
				String teacherMajor = MajorConstants.normalizeMajor(getCellString(row.getCell(colMajor)));
				if (teacherNo.isEmpty() && teacherName.isEmpty() && mobile.isEmpty() && rawPassword.isEmpty() && gender.isEmpty() && teacherMajor.isEmpty()) {
					continue;
				}
				if (teacherNo.isEmpty() || teacherName.isEmpty() || mobile.isEmpty() || teacherMajor.isEmpty()) {
					errorMessages.add("第 " + (i + 1) + " 行：教师工号、教师姓名、联系电话、专业不能为空");
					continue;
				}
				if (!"男".equals(gender) && !"女".equals(gender)) {
					errorMessages.add("第 " + (i + 1) + " 行：性别仅支持“男”或“女”");
					continue;
				}
				if (!MajorConstants.isMajorValid(teacherMajor)) {
					errorMessages.add("第 " + (i + 1) + " 行：专业不在允许范围内（地理信息科学、地理科学、风景园林、测绘工程、城乡规划）");
					continue;
				}
				if (!isSuperAdmin && !adminMajor.equals(teacherMajor)) {
					errorMessages.add("第 " + (i + 1) + " 行：专业管理员仅可导入本专业教师（当前管理员专业：" + adminMajor + "）");
					continue;
				}
				if (teacherNoInFile.contains(teacherNo)) {
					errorMessages.add("第 " + (i + 1) + " 行：教师工号在 Excel 中重复（" + teacherNo + "）");
					continue;
				}
				teacherNoInFile.add(teacherNo);

				if (rawPassword.isEmpty()) {
					String mobileDigits = mobile.replaceAll("[^0-9]", "");
					if (mobileDigits.length() < 6) {
						errorMessages.add("第 " + (i + 1) + " 行：密码为空时联系电话需至少 6 位数字");
						continue;
					}
					rawPassword = mobileDigits.substring(mobileDigits.length() - 6);
				}

				JiaoshiEntity exists = jiaoshiService.selectOne(new EntityWrapper<JiaoshiEntity>().eq("jiaoshigonghao", teacherNo));
				if (exists != null) {
					skippedCount++;
					continue;
				}

				JiaoshiEntity entity = new JiaoshiEntity();
				entity.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
				entity.setJiaoshigonghao(teacherNo);
				entity.setJiaoshixingming(teacherName);
				entity.setLianxidianhua(mobile);
				entity.setXingbie(gender);
				entity.setZhuanye(teacherMajor);
				entity.setMima(PasswordUtils.encode(rawPassword));
				toInsert.add(entity);
			}
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
			return R.error("导入失败：" + e.getMessage());
		}

		if (!errorMessages.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("导入失败，共 ").append(errorMessages.size()).append(" 行数据存在问题：");
			for (int i = 0; i < errorMessages.size(); i++) {
				if (i >= 8) {
					sb.append("\n...其余 ").append(errorMessages.size() - 8).append(" 条请修正后重试");
					break;
				}
				sb.append("\n").append(errorMessages.get(i));
			}
			return R.error(sb.toString());
		}

		if (toInsert.isEmpty()) {
			return R.error("没有可导入的新教师数据（可能全部已存在）");
		}
		jiaoshiService.insertBatch(toInsert);
		return R.ok("导入成功：" + toInsert.size() + " 条，已跳过重复工号 " + skippedCount + " 条");
	}

	private static String getCellString(Cell cell) {
		if (cell == null) return "";
		CellType type = cell.getCellType();
		if (type == CellType.STRING) {
			return cell.getStringCellValue();
		}
		if (type == CellType.NUMERIC) {
			double n = cell.getNumericCellValue();
			return (long) n == n ? String.valueOf((long) n) : String.valueOf(n);
		}
		if (type == CellType.BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		}
		if (type == CellType.FORMULA) {
			try {
				return String.valueOf(cell.getNumericCellValue());
			} catch (Exception e) {
				return cell.getCellFormula();
			}
		}
		return "";
	}
    
	








}
