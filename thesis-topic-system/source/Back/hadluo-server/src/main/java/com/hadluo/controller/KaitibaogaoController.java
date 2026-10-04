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

import com.hadluo.entity.KaitibaogaoEntity;
import com.hadluo.entity.view.KaitibaogaoView;

import com.hadluo.service.KaitibaogaoService;
import com.hadluo.service.TokenService;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.TopicDeletedMarkerUtil;
import com.hadluo.utils.CommonUtil;
import com.hadluo.entity.XuantishenqingEntity;
import com.hadluo.entity.LunwenchugaoEntity;
import com.hadluo.service.XuantishenqingService;
import com.hadluo.service.LunwenchugaoService;
import java.util.stream.Collectors;

/**
 * 开题报告
 * 后端接口
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@RestController
@RequestMapping("/kaitibaogao")
public class KaitibaogaoController {
    @Autowired
    private KaitibaogaoService kaitibaogaoService;
    @Autowired
    private XuantishenqingService xuantishenqingService;
    @Autowired
    private LunwenchugaoService lunwenchugaoService;

    @Autowired
    private TopicDeletedMarkerUtil topicDeletedMarkerUtil;



    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,KaitibaogaoEntity kaitibaogao,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("jiaoshi")) {
			kaitibaogao.setJiaoshigonghao((String)request.getSession().getAttribute("username"));
		}
		if(tableName.equals("xuesheng")) {
			kaitibaogao.setXuehao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<KaitibaogaoEntity> ew = new EntityWrapper<KaitibaogaoEntity>();

		PageUtils page = kaitibaogaoService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, kaitibaogao), params), params));
		topicDeletedMarkerUtil.markPage(page, request);

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,KaitibaogaoEntity kaitibaogao, 
		HttpServletRequest request){
        EntityWrapper<KaitibaogaoEntity> ew = new EntityWrapper<KaitibaogaoEntity>();

		PageUtils page = kaitibaogaoService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, kaitibaogao), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( KaitibaogaoEntity kaitibaogao){
       	EntityWrapper<KaitibaogaoEntity> ew = new EntityWrapper<KaitibaogaoEntity>();
      	ew.allEq(MPUtil.allEQMapPre( kaitibaogao, "kaitibaogao")); 
        return R.ok().put("data", kaitibaogaoService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(KaitibaogaoEntity kaitibaogao){
        EntityWrapper< KaitibaogaoEntity> ew = new EntityWrapper< KaitibaogaoEntity>();
 		ew.allEq(MPUtil.allEQMapPre( kaitibaogao, "kaitibaogao")); 
		KaitibaogaoView kaitibaogaoView =  kaitibaogaoService.selectView(ew);
		return R.ok("查询开题报告成功").put("data", kaitibaogaoView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        KaitibaogaoEntity kaitibaogao = kaitibaogaoService.selectById(id);
		kaitibaogao = kaitibaogaoService.selectView(new EntityWrapper<KaitibaogaoEntity>().eq("id", id));
        return R.ok().put("data", kaitibaogao);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        KaitibaogaoEntity kaitibaogao = kaitibaogaoService.selectById(id);
		kaitibaogao = kaitibaogaoService.selectView(new EntityWrapper<KaitibaogaoEntity>().eq("id", id));
        return R.ok().put("data", kaitibaogao);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody KaitibaogaoEntity kaitibaogao, HttpServletRequest request){
    	kaitibaogao.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	com.hadluo.utils.WorkflowAuditUtil.applyOnInsert(kaitibaogao.getShenhezhuangtai(), kaitibaogao::setShenhezhuangtai, request);
        kaitibaogaoService.insert(kaitibaogao);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody KaitibaogaoEntity kaitibaogao, HttpServletRequest request){
    	// 学生提交开题报告：自动绑定到其已审核通过的选题，忽略前端传来的题目/老师信息
    	Object tableNameObj = request.getSession().getAttribute("tableName");
    	if (tableNameObj != null && "xuesheng".equals(tableNameObj.toString())) {
    		String xuehao = (String) request.getSession().getAttribute("username");
    		if (xuehao == null || xuehao.trim().isEmpty()) {
    			return R.error("未获取到学生学号，请重新登录后再试");
    		}
    		// 查找该学生最新一条“已审核”的选题申请
    		EntityWrapper<XuantishenqingEntity> ew = new EntityWrapper<XuantishenqingEntity>();
    		ew.eq("xuehao", xuehao).in("shenhezhuangtai", com.hadluo.utils.XuantishenqingStatusUtil.getAppliedStatuses()).orderBy("shenqingshijian", false);
    		List<XuantishenqingEntity> list = xuantishenqingService.selectList(ew);
    		if (list == null || list.isEmpty()) {
    			return R.error("您还没有审核通过的选题，无法提交开题报告");
    		}
    		XuantishenqingEntity selected = list.get(0);
    		// 强制覆盖开题报告记录中的题目信息与师生信息
    		kaitibaogao.setTimubianhao(selected.getTimubianhao());
    		kaitibaogao.setKetimingcheng(selected.getKetimingcheng());
    		kaitibaogao.setTimuleixing(selected.getTimuleixing());
    		kaitibaogao.setZhuanye(selected.getZhuanye());
    		kaitibaogao.setKetixingzhi(selected.getKetixingzhi());
    		kaitibaogao.setJiaoshigonghao(selected.getJiaoshigonghao());
    		kaitibaogao.setJiaoshixingming(selected.getJiaoshixingming());
    		kaitibaogao.setXuehao(selected.getXuehao());
    		kaitibaogao.setXueshengxingming(selected.getXueshengxingming());
    		EntityWrapper<KaitibaogaoEntity> dupEw = new EntityWrapper<KaitibaogaoEntity>();
    		dupEw.eq("xuehao", xuehao);
    		if (kaitibaogaoService.selectCount(dupEw) > 0) {
    			return R.error("您已提交开题报告，如需重新提交请先撤销后再提交");
    		}
    	}
    	com.hadluo.utils.WorkflowAuditUtil.applyOnInsert(kaitibaogao.getShenhezhuangtai(), kaitibaogao::setShenhezhuangtai, request);
    	kaitibaogao.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        kaitibaogaoService.insert(kaitibaogao);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody KaitibaogaoEntity kaitibaogao, HttpServletRequest request){
    	com.hadluo.utils.WorkflowAuditUtil.clearAuditReasonOnStudentResubmit(request, kaitibaogao::setShenheyuanyin);
    	com.hadluo.utils.WorkflowAuditUtil.applyOnUpdate(kaitibaogao.getShenhezhuangtai(), kaitibaogao::setShenhezhuangtai, request);
        kaitibaogaoService.updateById(kaitibaogao);//全部更新
        return R.ok();
    }



    

    /**
     * 学生撤销开题报告（删除后可重新提交）
     */
    @RequestMapping("/revoke")
    @Transactional
    public R revoke(HttpServletRequest request) {
    	Object tableNameObj = request.getSession().getAttribute("tableName");
    	if (tableNameObj == null || !"xuesheng".equals(tableNameObj.toString())) {
    		return R.error("仅学生可撤销开题报告");
    	}
    	String xuehao = (String) request.getSession().getAttribute("username");
    	if (StringUtils.isBlank(xuehao)) {
    		return R.error("未获取到学生学号，请重新登录后再试");
    	}
    	EntityWrapper<KaitibaogaoEntity> ew = new EntityWrapper<KaitibaogaoEntity>();
    	ew.eq("xuehao", xuehao);
    	List<KaitibaogaoEntity> list = kaitibaogaoService.selectList(ew);
    	if (list == null || list.isEmpty()) {
    		return R.error("当前没有可撤销的开题报告");
    	}
    	EntityWrapper<LunwenchugaoEntity> draftEw = new EntityWrapper<LunwenchugaoEntity>();
    	draftEw.eq("xuehao", xuehao);
    	if (lunwenchugaoService.selectCount(draftEw) > 0) {
    		return R.error("您已提交论文初稿，无法撤销开题报告");
    	}
    	kaitibaogaoService.deleteBatchIds(list.stream().map(KaitibaogaoEntity::getId).collect(Collectors.toList()));
    	return R.ok("开题报告已撤销，可重新提交");
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        kaitibaogaoService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	








}
