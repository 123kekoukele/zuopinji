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

import com.hadluo.entity.DabianlunwenEntity;
import com.hadluo.entity.view.DabianlunwenView;

import com.hadluo.service.DabianlunwenService;
import com.hadluo.service.TokenService;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.TopicDeletedMarkerUtil;
import com.hadluo.utils.CommonUtil;
import com.hadluo.entity.XuantishenqingEntity;
import com.hadluo.service.XuantishenqingService;
import java.io.IOException;

/**
 * 答辩论文
 * 后端接口
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@RestController
@RequestMapping("/dabianlunwen")
public class DabianlunwenController {
    @Autowired
    private DabianlunwenService dabianlunwenService;
    @Autowired
    private XuantishenqingService xuantishenqingService;

    @Autowired
    private TopicDeletedMarkerUtil topicDeletedMarkerUtil;



    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,DabianlunwenEntity dabianlunwen,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("jiaoshi")) {
			dabianlunwen.setJiaoshigonghao((String)request.getSession().getAttribute("username"));
		}
		if(tableName.equals("xuesheng")) {
			dabianlunwen.setXuehao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<DabianlunwenEntity> ew = new EntityWrapper<DabianlunwenEntity>();

		PageUtils page = dabianlunwenService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, dabianlunwen), params), params));
		topicDeletedMarkerUtil.markPage(page, request);

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,DabianlunwenEntity dabianlunwen, 
		HttpServletRequest request){
        EntityWrapper<DabianlunwenEntity> ew = new EntityWrapper<DabianlunwenEntity>();

		PageUtils page = dabianlunwenService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, dabianlunwen), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( DabianlunwenEntity dabianlunwen){
       	EntityWrapper<DabianlunwenEntity> ew = new EntityWrapper<DabianlunwenEntity>();
      	ew.allEq(MPUtil.allEQMapPre( dabianlunwen, "dabianlunwen")); 
        return R.ok().put("data", dabianlunwenService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(DabianlunwenEntity dabianlunwen){
        EntityWrapper< DabianlunwenEntity> ew = new EntityWrapper< DabianlunwenEntity>();
 		ew.allEq(MPUtil.allEQMapPre( dabianlunwen, "dabianlunwen")); 
		DabianlunwenView dabianlunwenView =  dabianlunwenService.selectView(ew);
		return R.ok("查询答辩论文成功").put("data", dabianlunwenView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        DabianlunwenEntity dabianlunwen = dabianlunwenService.selectById(id);
		dabianlunwen = dabianlunwenService.selectView(new EntityWrapper<DabianlunwenEntity>().eq("id", id));
        return R.ok().put("data", dabianlunwen);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        DabianlunwenEntity dabianlunwen = dabianlunwenService.selectById(id);
		dabianlunwen = dabianlunwenService.selectView(new EntityWrapper<DabianlunwenEntity>().eq("id", id));
        return R.ok().put("data", dabianlunwen);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DabianlunwenEntity dabianlunwen, HttpServletRequest request){
    	dabianlunwen.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	com.hadluo.utils.WorkflowAuditUtil.applyOnInsert(dabianlunwen.getShenhezhuangtai(), dabianlunwen::setShenhezhuangtai, request);
        dabianlunwenService.insert(dabianlunwen);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody DabianlunwenEntity dabianlunwen, HttpServletRequest request){
    	// 学生提交答辩论文：自动绑定到其已审核通过的选题
    	Object tableNameObj = request.getSession().getAttribute("tableName");
    	if (tableNameObj != null && "xuesheng".equals(tableNameObj.toString())) {
    		String xuehao = (String) request.getSession().getAttribute("username");
    		if (xuehao == null || xuehao.trim().isEmpty()) {
    			return R.error("未获取到学生学号，请重新登录后再试");
    		}
    		EntityWrapper<XuantishenqingEntity> ew = new EntityWrapper<XuantishenqingEntity>();
    		ew.eq("xuehao", xuehao).in("shenhezhuangtai", com.hadluo.utils.XuantishenqingStatusUtil.getAppliedStatuses()).orderBy("shenqingshijian", false);
    		List<XuantishenqingEntity> list = xuantishenqingService.selectList(ew);
    		if (list == null || list.isEmpty()) {
    			return R.error("您还没有审核通过的选题，无法提交答辩论文");
    		}
    		XuantishenqingEntity selected = list.get(0);
    		dabianlunwen.setTimubianhao(selected.getTimubianhao());
    		dabianlunwen.setKetimingcheng(selected.getKetimingcheng());
    		dabianlunwen.setTimuleixing(selected.getTimuleixing());
    		dabianlunwen.setZhuanye(selected.getZhuanye());
    		dabianlunwen.setKetixingzhi(selected.getKetixingzhi());
    		dabianlunwen.setJiaoshigonghao(selected.getJiaoshigonghao());
    		dabianlunwen.setJiaoshixingming(selected.getJiaoshixingming());
    		dabianlunwen.setXuehao(selected.getXuehao());
    		dabianlunwen.setXueshengxingming(selected.getXueshengxingming());
    	}
    	com.hadluo.utils.WorkflowAuditUtil.applyOnInsert(dabianlunwen.getShenhezhuangtai(), dabianlunwen::setShenhezhuangtai, request);
    	dabianlunwen.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        dabianlunwenService.insert(dabianlunwen);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody DabianlunwenEntity dabianlunwen, HttpServletRequest request){
    	com.hadluo.utils.WorkflowAuditUtil.clearAuditReasonOnStudentResubmit(request, dabianlunwen::setShenheyuanyin);
    	com.hadluo.utils.WorkflowAuditUtil.applyOnUpdate(dabianlunwen.getShenhezhuangtai(), dabianlunwen::setShenhezhuangtai, request);
        dabianlunwenService.updateById(dabianlunwen);//全部更新
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        dabianlunwenService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	








}
