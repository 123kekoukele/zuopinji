package com.hadluo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;

import com.hadluo.annotation.IgnoreAuth;
import com.hadluo.entity.BanjixinxiEntity;
import com.hadluo.entity.XueshengEntity;
import com.hadluo.entity.UsersEntity;
import com.hadluo.entity.view.BanjixinxiView;

import com.hadluo.service.BanjixinxiService;
import com.hadluo.service.JiaoshiService;
import com.hadluo.service.XueshengService;
import com.hadluo.service.UsersService;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.ScopeMajorUtil;
import com.hadluo.utils.MajorConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * 班级信息
 * 后端接口
 */
@RestController
@RequestMapping("/banjixinxi")
public class BanjixinxiController {
    @Autowired
    private BanjixinxiService banjixinxiService;

    @Autowired
    private XueshengService xueshengService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private JiaoshiService jiaoshiService;

    /**
     * 按班级名称及所属专业查询该班学生（学生表的「班级」「专业」需与班级档案一致）
     * 专业管理员额外受 session 专业限制，与 {@link com.hadluo.controller.XueshengController#page} 规则一致。
     */
    @RequestMapping("/students")
    public R students(@RequestParam("banjimingcheng") String banjimingcheng,
                      @RequestParam(value = "zhuanye", required = false) String zhuanye,
                      HttpServletRequest request) {
        if (banjimingcheng == null || banjimingcheng.trim().isEmpty()) {
            return R.error("班级名称不能为空");
        }
        EntityWrapper<XueshengEntity> ew = new EntityWrapper<XueshengEntity>();
        ew.eq("banji", banjimingcheng.trim());
        if (zhuanye != null && !zhuanye.trim().isEmpty()) {
            ew.eq("zhuanye", zhuanye.trim());
        }
        String scopedMajor = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);
        if (scopedMajor != null) {
            ew.eq("zhuanye", scopedMajor);
        } else if (ScopeMajorUtil.isTeacherWithoutMajor(request, jiaoshiService)) {
            return R.ok().put("data", new ArrayList<>());
        }
        List<XueshengEntity> list = xueshengService.selectList(ew);
        return R.ok().put("data", list);
    }

    /**
     * 学生注册页班级下拉（无需登录），默认含 1–4，并按专业合并管理端班级名称
     */
    @IgnoreAuth
    @RequestMapping("/registerOptions")
    public R registerOptions(@RequestParam(value = "zhuanye", required = false) String zhuanye) {
        LinkedHashSet<String> options = new LinkedHashSet<>(Arrays.asList("1", "2", "3", "4"));
        if (StringUtils.isNotBlank(zhuanye)) {
            String major = MajorConstants.normalizeMajor(zhuanye.trim());
            EntityWrapper<BanjixinxiEntity> ew = new EntityWrapper<>();
            ew.eq("zhuanye", major);
            for (BanjixinxiEntity item : banjixinxiService.selectList(ew)) {
                if (item != null && StringUtils.isNotBlank(item.getBanjimingcheng())) {
                    options.add(item.getBanjimingcheng().trim());
                }
            }
        }
        List<String> sorted = new ArrayList<>(options);
        sorted.sort(Comparator.comparingInt(BanjixinxiController::banjiSortKey).thenComparing(String::compareTo));
        return R.ok().put("data", sorted);
    }

    private static int banjiSortKey(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,BanjixinxiEntity banjixinxi,
		HttpServletRequest request){
        EntityWrapper<BanjixinxiEntity> ew = new EntityWrapper<BanjixinxiEntity>();

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
		ScopeMajorUtil.applyMajorToQuery(scopedMajor, banjixinxi, params);

		PageUtils page = banjixinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, banjixinxi), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(BanjixinxiEntity banjixinxi, HttpServletRequest request){
       	EntityWrapper<BanjixinxiEntity> ew = new EntityWrapper<BanjixinxiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( banjixinxi, "banjixinxi"));
		String scopedMajor = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);
		if (scopedMajor != null) {
			ew.eq("zhuanye", scopedMajor);
		} else if (ScopeMajorUtil.isTeacherWithoutMajor(request, jiaoshiService)) {
			return R.ok().put("data", new ArrayList<>());
		}
        return R.ok().put("data", banjixinxiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(BanjixinxiEntity banjixinxi){
        EntityWrapper< BanjixinxiEntity> ew = new EntityWrapper< BanjixinxiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( banjixinxi, "banjixinxi")); 
		BanjixinxiView banjixinxiView =  banjixinxiService.selectView(ew);
		return R.ok("查询班级信息成功").put("data", banjixinxiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        BanjixinxiEntity banjixinxi = banjixinxiService.selectById(id);
		BanjixinxiView view = banjixinxiService.selectView(new EntityWrapper<BanjixinxiEntity>().eq("id", id));
		BanjixinxiEntity record = view != null ? view : banjixinxi;
		String scopedMajor = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);
		R denied = ScopeMajorUtil.denyIfOutOfScope(scopedMajor, record);
		if (denied != null) {
			return denied;
		}
        return R.ok().put("data", record);
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody BanjixinxiEntity banjixinxi, HttpServletRequest request){
    	R mutateDenied = ScopeMajorUtil.denyIfTeacherMutate(request);
    	if (mutateDenied != null) {
    		return mutateDenied;
    	}
    	R validated = applyAndValidateBanji(banjixinxi, request, false);
    	if (validated != null) {
    		return validated;
    	}
    	banjixinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	banjixinxi.setAddtime(new Date());
        banjixinxiService.insert(banjixinxi);
        return R.ok();
    }
    
    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody BanjixinxiEntity banjixinxi, HttpServletRequest request){
    	R mutateDenied = ScopeMajorUtil.denyIfTeacherMutate(request);
    	if (mutateDenied != null) {
    		return mutateDenied;
    	}
    	if (banjixinxi.getId() == null) {
    		return R.error("缺少班级记录ID");
    	}
    	R scopeDenied = denyIfBanjiOutOfScope(banjixinxi.getId(), request);
    	if (scopeDenied != null) {
    		return scopeDenied;
    	}
    	R validated = applyAndValidateBanji(banjixinxi, request, true);
    	if (validated != null) {
    		return validated;
    	}
        banjixinxiService.updateById(banjixinxi);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids, HttpServletRequest request){
    	R mutateDenied = ScopeMajorUtil.denyIfTeacherMutate(request);
    	if (mutateDenied != null) {
    		return mutateDenied;
    	}
    	if (ids == null || ids.length == 0) {
    		return R.error("请选择要删除的班级");
    	}
    	for (Long id : ids) {
    		R scopeDenied = denyIfBanjiOutOfScope(id, request);
    		if (scopeDenied != null) {
    			return scopeDenied;
    		}
    	}
        banjixinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    private R denyIfBanjiOutOfScope(Long id, HttpServletRequest request) {
    	BanjixinxiEntity existing = banjixinxiService.selectById(id);
    	if (existing == null) {
    		return R.error("班级记录不存在");
    	}
    	String scopedMajor = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);
    	return ScopeMajorUtil.denyIfOutOfScope(scopedMajor, existing);
    }

    private R applyAndValidateBanji(BanjixinxiEntity banjixinxi, HttpServletRequest request, boolean isUpdate) {
    	if (banjixinxi == null) {
    		return R.error("班级信息不能为空");
    	}
    	String className = banjixinxi.getBanjimingcheng();
    	if (StringUtils.isBlank(className)) {
    		return R.error("班级名称不能为空");
    	}
    	banjixinxi.setBanjimingcheng(className.trim());

    	String scopedMajor = ScopeMajorUtil.resolveScopedMajor(request, usersService, jiaoshiService);
    	String major = MajorConstants.normalizeMajor(banjixinxi.getZhuanye());
    	if (StringUtils.isNotBlank(scopedMajor)) {
    		major = scopedMajor;
    	} else if (!MajorConstants.isMajorValid(major)) {
    		return R.error("请选择有效的所属专业");
    	}
    	banjixinxi.setZhuanye(major);

    	EntityWrapper<BanjixinxiEntity> duplicateEw = new EntityWrapper<>();
    	duplicateEw.eq("banjimingcheng", banjixinxi.getBanjimingcheng());
    	duplicateEw.eq("zhuanye", major);
    	if (isUpdate && banjixinxi.getId() != null) {
    		duplicateEw.ne("id", banjixinxi.getId());
    	}
    	if (banjixinxiService.selectCount(duplicateEw) > 0) {
    		return R.error("该专业下班级名称已存在");
    	}
    	return null;
    }
    
}

