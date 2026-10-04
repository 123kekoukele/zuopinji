
package com.hadluo.controller;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hadluo.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hadluo.entity.TokenEntity;
import com.hadluo.entity.UsersEntity;
import com.hadluo.entity.ConfigEntity;
import com.hadluo.service.ConfigService;
import com.hadluo.service.TokenService;
import com.hadluo.service.UsersService;
import com.hadluo.utils.CommonUtil;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.MajorConstants;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.PasswordUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.ValidatorUtils;

/**
 * 登录相关
 */
@RequestMapping("users")
@RestController
public class UsersController{
	private static final String SYSTEM_OPEN_START = "system_open_start";
	private static final String SYSTEM_OPEN_END = "system_open_end";
	
	@Autowired
	private UsersService userService;

	@Autowired
	private ConfigService configService;
	
	@Autowired
	private TokenService tokenService;

	private R validateMajorField(String major) {
		String normalizedMajor = MajorConstants.normalizeMajor(major);
		if (!MajorConstants.isMajorValid(normalizedMajor)) {
			return R.error("专业必须为以下之一：" + String.join("、", MajorConstants.MAJOR_OPTIONS));
		}
		return null;
	}

	private boolean isSuperAdmin(HttpServletRequest request) {
		Long userId = (Long) request.getSession().getAttribute("userId");
		if (userId == null) {
			return false;
		}
		UsersEntity me = userService.selectById(userId);
		return me != null && MajorConstants.normalizeMajor(me.getZhuanye()).isEmpty();
	}

	private R denyUnlessSuperAdmin(HttpServletRequest request) {
		if (!isSuperAdmin(request)) {
			return R.error("仅超级管理员可管理专业管理员");
		}
		return null;
	}

	private boolean isMajorAdmin(UsersEntity user) {
		return user != null && !MajorConstants.normalizeMajor(user.getZhuanye()).isEmpty();
	}

	private void applyMajorAdminListFilter(EntityWrapper<UsersEntity> ew) {
		ew.isNotNull("zhuanye");
		ew.ne("zhuanye", "");
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

	/**
	 * 登录
	 */
	@IgnoreAuth
	@RequestMapping(value = "/login")
	public R login(String username, String password, String captcha, HttpServletRequest request) {
		UsersEntity user = userService.selectOne(new EntityWrapper<UsersEntity>().eq("username", username));
		if (user == null || !PasswordUtils.matches(password, user.getPassword())) {
			return R.error("账号或密码不正确");
		}
		boolean isSuperAdmin = MajorConstants.normalizeMajor(user.getZhuanye()).isEmpty();
		if (!isSuperAdmin) {
			R openCheck = checkSystemOpenTimeForAllMembers();
			if (openCheck != null) {
				return openCheck;
			}
		}
		if (!PasswordUtils.isEncoded(user.getPassword())) {
			user.setPassword(PasswordUtils.encode(password));
			userService.updateById(user);
		}
		String token = tokenService.generateToken(user.getId(), username, "users", user.getRole());
		return R.ok().put("token", token);
	}
	
	/**
	 * 未登录场景下的修改密码（管理员）
	 * 需要提供：用户名 + 原密码 + 新密码
	 */
	@IgnoreAuth
	@PostMapping(value = "/changePassword")
	public R changePassword(String username, String oldPassword, String newPassword, HttpServletRequest request) {
		if (username == null || username.trim().isEmpty()
				|| oldPassword == null || oldPassword.isEmpty()
				|| newPassword == null || newPassword.isEmpty()) {
			return R.error("用户名、原密码和新密码均不能为空");
		}
		UsersEntity user = userService.selectOne(new EntityWrapper<UsersEntity>().eq("username", username));
		if (user == null) {
			return R.error("账号不存在");
		}
		if (!PasswordUtils.matches(oldPassword, user.getPassword())) {
			return R.error("原密码不正确");
		}
		user.setPassword(PasswordUtils.encode(newPassword));
		userService.updateById(user);
		return R.ok("密码修改成功，请使用新密码登录");
	}
	
	/**
	 * 注册
	 */
	@IgnoreAuth
	@PostMapping(value = "/register")
	public R register(@RequestBody UsersEntity user){
//    	ValidatorUtils.validateEntity(user);
    	if (userService.selectOne(new EntityWrapper<UsersEntity>().eq("username", user.getUsername())) != null) {
    		return R.error("用户已存在");
    	}
		R majorCheck = validateMajorField(user.getZhuanye());
		if (majorCheck != null) {
			return majorCheck;
		}
		user.setZhuanye(MajorConstants.normalizeMajor(user.getZhuanye()));
    	if (user.getPassword() != null && !user.getPassword().isEmpty()) {
    		user.setPassword(PasswordUtils.encode(user.getPassword()));
    	}
        userService.insert(user);
        return R.ok();
    }

	/**
	 * 退出
	 */
	@GetMapping(value = "logout")
	public R logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return R.ok("退出成功");
	}
	
	/**
     * 密码重置
     */
    @IgnoreAuth
	@RequestMapping(value = "/resetPass")
    public R resetPass(String username, HttpServletRequest request){
    	UsersEntity user = userService.selectOne(new EntityWrapper<UsersEntity>().eq("username", username));
    	if (user == null) {
    		return R.error("账号不存在");
    	}
    	user.setPassword(PasswordUtils.encode("123456"));
        userService.updateById(user);
        return R.ok("密码已重置为：123456");
    }
	
	/**
     * 列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, UsersEntity user, HttpServletRequest request){
		R denied = denyUnlessSuperAdmin(request);
		if (denied != null) {
			return denied;
		}
        EntityWrapper<UsersEntity> ew = new EntityWrapper<UsersEntity>();
		applyMajorAdminListFilter(ew);
    	PageUtils page = userService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.allLike(ew, user), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/list")
    public R list(UsersEntity user, HttpServletRequest request){
		R denied = denyUnlessSuperAdmin(request);
		if (denied != null) {
			return denied;
		}
       	EntityWrapper<UsersEntity> ew = new EntityWrapper<UsersEntity>();
		applyMajorAdminListFilter(ew);
      	ew.allEq(MPUtil.allEQMapPre( user, "user")); 
        return R.ok().put("data", userService.selectListView(ew));
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") String id, HttpServletRequest request){
		R denied = denyUnlessSuperAdmin(request);
		if (denied != null) {
			return denied;
		}
        UsersEntity user = userService.selectById(id);
		if (!isMajorAdmin(user)) {
			return R.error("仅可查看专业管理员");
		}
        return R.ok().put("data", user);
    }
    
    /**
     * 获取用户的session用户信息
     */
    @RequestMapping("/session")
    public R getCurrUser(HttpServletRequest request){
    	Long id = (Long)request.getSession().getAttribute("userId");
        UsersEntity user = userService.selectById(id);
        return R.ok().put("data", user);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody UsersEntity user, HttpServletRequest request){
		R denied = denyUnlessSuperAdmin(request);
		if (denied != null) {
			return denied;
		}
//    	ValidatorUtils.validateEntity(user);
    	if (userService.selectOne(new EntityWrapper<UsersEntity>().eq("username", user.getUsername())) != null) {
    		return R.error("用户已存在");
    	}
		R majorCheck = validateMajorField(user.getZhuanye());
		if (majorCheck != null) {
			return majorCheck;
		}
		user.setRole("管理员");
		user.setZhuanye(MajorConstants.normalizeMajor(user.getZhuanye()));
    	if (user.getPassword() != null && !user.getPassword().isEmpty()) {
    		user.setPassword(PasswordUtils.encode(user.getPassword()));
    	}
        userService.insert(user);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UsersEntity user, HttpServletRequest request){
		R denied = denyUnlessSuperAdmin(request);
		if (denied != null) {
			return denied;
		}
//        ValidatorUtils.validateEntity(user);
		UsersEntity existing = userService.selectById(user.getId());
		if (!isMajorAdmin(existing)) {
			return R.error("仅可修改专业管理员");
		}
    	UsersEntity u = userService.selectOne(new EntityWrapper<UsersEntity>().eq("username", user.getUsername()));
    	if (u != null && u.getId() != user.getId() && u.getUsername().equals(user.getUsername())) {
    		return R.error("用户名已存在。");
    	}
		if (MajorConstants.normalizeMajor(user.getZhuanye()).isEmpty()) {
			return R.error("专业管理员必须绑定专业");
		}
		R majorCheck = validateMajorField(user.getZhuanye());
		if (majorCheck != null) {
			return majorCheck;
		}
		user.setRole("管理员");
		user.setZhuanye(MajorConstants.normalizeMajor(user.getZhuanye()));
    	if (user.getPassword() != null && !user.getPassword().isEmpty() && !PasswordUtils.isEncoded(user.getPassword())) {
    		user.setPassword(PasswordUtils.encode(user.getPassword()));
    	}
        userService.updateById(user);//全部更新
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
		R denied = denyUnlessSuperAdmin(request);
		if (denied != null) {
			return denied;
		}
		for (Long id : ids) {
			UsersEntity user = userService.selectById(id);
			if (!isMajorAdmin(user)) {
				return R.error("不能删除超级管理员或非专业管理员账号");
			}
		}
        userService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
}
