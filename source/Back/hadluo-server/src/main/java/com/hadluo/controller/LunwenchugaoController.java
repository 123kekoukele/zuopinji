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
import java.util.stream.Collectors;
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

import com.hadluo.entity.LunwenchugaoEntity;
import com.hadluo.entity.view.LunwenchugaoView;
import com.hadluo.entity.DabianlunwenEntity;
import com.hadluo.entity.ZhongqijianchaEntity;

import com.hadluo.service.LunwenchugaoService;
import com.hadluo.service.DabianlunwenService;
import com.hadluo.service.ZhongqijianchaService;
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
 * 论文初稿
 * 后端接口
 * @author 
 * @email 
 * @date 2024-02-27 12:32:45
 */
@RestController
@RequestMapping("/lunwenchugao")
public class LunwenchugaoController {
    @Autowired
    private LunwenchugaoService lunwenchugaoService;
    @Autowired
    private XuantishenqingService xuantishenqingService;

    @Autowired
    private ZhongqijianchaService zhongqijianchaService;

    @Autowired
    private DabianlunwenService dabianlunwenService;

    @Autowired
    private TopicDeletedMarkerUtil topicDeletedMarkerUtil;



    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,LunwenchugaoEntity lunwenchugao,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("jiaoshi")) {
			lunwenchugao.setJiaoshigonghao((String)request.getSession().getAttribute("username"));
		}
		if(tableName.equals("xuesheng")) {
			lunwenchugao.setXuehao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<LunwenchugaoEntity> ew = new EntityWrapper<LunwenchugaoEntity>();

		PageUtils page = lunwenchugaoService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, lunwenchugao), params), params));
		topicDeletedMarkerUtil.markPage(page, request);

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,LunwenchugaoEntity lunwenchugao, 
		HttpServletRequest request){
        EntityWrapper<LunwenchugaoEntity> ew = new EntityWrapper<LunwenchugaoEntity>();

		PageUtils page = lunwenchugaoService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, lunwenchugao), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( LunwenchugaoEntity lunwenchugao){
       	EntityWrapper<LunwenchugaoEntity> ew = new EntityWrapper<LunwenchugaoEntity>();
      	ew.allEq(MPUtil.allEQMapPre( lunwenchugao, "lunwenchugao")); 
        return R.ok().put("data", lunwenchugaoService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(LunwenchugaoEntity lunwenchugao){
        EntityWrapper< LunwenchugaoEntity> ew = new EntityWrapper< LunwenchugaoEntity>();
 		ew.allEq(MPUtil.allEQMapPre( lunwenchugao, "lunwenchugao")); 
		LunwenchugaoView lunwenchugaoView =  lunwenchugaoService.selectView(ew);
		return R.ok("查询论文初稿成功").put("data", lunwenchugaoView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        LunwenchugaoEntity lunwenchugao = lunwenchugaoService.selectById(id);
		lunwenchugao = lunwenchugaoService.selectView(new EntityWrapper<LunwenchugaoEntity>().eq("id", id));
        return R.ok().put("data", lunwenchugao);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        LunwenchugaoEntity lunwenchugao = lunwenchugaoService.selectById(id);
		lunwenchugao = lunwenchugaoService.selectView(new EntityWrapper<LunwenchugaoEntity>().eq("id", id));
        return R.ok().put("data", lunwenchugao);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody LunwenchugaoEntity lunwenchugao, HttpServletRequest request){
    	lunwenchugao.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	com.hadluo.utils.WorkflowAuditUtil.applyOnInsert(lunwenchugao.getShenhezhuangtai(), lunwenchugao::setShenhezhuangtai, request);
        lunwenchugaoService.insert(lunwenchugao);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody LunwenchugaoEntity lunwenchugao, HttpServletRequest request){
    	// 学生提交论文初稿：自动绑定到其已审核通过的选题
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
    			return R.error("您还没有审核通过的选题，无法提交论文初稿");
    		}
    		XuantishenqingEntity selected = list.get(0);
    		lunwenchugao.setTimubianhao(selected.getTimubianhao());
    		lunwenchugao.setKetimingcheng(selected.getKetimingcheng());
    		lunwenchugao.setTimuleixing(selected.getTimuleixing());
    		lunwenchugao.setZhuanye(selected.getZhuanye());
    		lunwenchugao.setKetixingzhi(selected.getKetixingzhi());
    		lunwenchugao.setJiaoshigonghao(selected.getJiaoshigonghao());
    		lunwenchugao.setJiaoshixingming(selected.getJiaoshixingming());
    		lunwenchugao.setXuehao(selected.getXuehao());
    		lunwenchugao.setXueshengxingming(selected.getXueshengxingming());
    		EntityWrapper<LunwenchugaoEntity> dupEw = new EntityWrapper<LunwenchugaoEntity>();
    		dupEw.eq("xuehao", xuehao);
    		if (lunwenchugaoService.selectCount(dupEw) > 0) {
    			return R.error("您已提交论文初稿，如需重新提交请先撤退提交后再提交");
    		}
    	}
    	com.hadluo.utils.WorkflowAuditUtil.applyOnInsert(lunwenchugao.getShenhezhuangtai(), lunwenchugao::setShenhezhuangtai, request);
    	lunwenchugao.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
        lunwenchugaoService.insert(lunwenchugao);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody LunwenchugaoEntity lunwenchugao, HttpServletRequest request){
    	com.hadluo.utils.WorkflowAuditUtil.clearAuditReasonOnStudentResubmit(request, lunwenchugao::setShenheyuanyin);
    	com.hadluo.utils.WorkflowAuditUtil.applyOnUpdate(lunwenchugao.getShenhezhuangtai(), lunwenchugao::setShenhezhuangtai, request);
        lunwenchugaoService.updateById(lunwenchugao);//全部更新
        return R.ok();
    }



    /**
     * 学生撤退提交论文初稿（删除后可重新提交）
     */
    @RequestMapping("/revoke")
    @Transactional
    public R revoke(HttpServletRequest request) {
    	Object tableNameObj = request.getSession().getAttribute("tableName");
    	if (tableNameObj == null || !"xuesheng".equals(tableNameObj.toString())) {
    		return R.error("仅学生可撤退提交论文初稿");
    	}
    	String xuehao = (String) request.getSession().getAttribute("username");
    	if (StringUtils.isBlank(xuehao)) {
    		return R.error("未获取到学生学号，请重新登录后再试");
    	}
    	EntityWrapper<LunwenchugaoEntity> ew = new EntityWrapper<LunwenchugaoEntity>();
    	ew.eq("xuehao", xuehao);
    	List<LunwenchugaoEntity> list = lunwenchugaoService.selectList(ew);
    	if (list == null || list.isEmpty()) {
    		return R.error("当前没有可撤退的论文初稿");
    	}
    	EntityWrapper<ZhongqijianchaEntity> midEw = new EntityWrapper<ZhongqijianchaEntity>();
    	midEw.eq("xuehao", xuehao);
    	if (zhongqijianchaService.selectCount(midEw) > 0) {
    		return R.error("您已提交中期检查，无法撤退论文初稿");
    	}
    	EntityWrapper<DabianlunwenEntity> defenseEw = new EntityWrapper<DabianlunwenEntity>();
    	defenseEw.eq("xuehao", xuehao);
    	if (dabianlunwenService.selectCount(defenseEw) > 0) {
    		return R.error("您已提交答辩论文，无法撤退论文初稿");
    	}
    	lunwenchugaoService.deleteBatchIds(list.stream().map(LunwenchugaoEntity::getId).collect(Collectors.toList()));
    	return R.ok("论文初稿已撤退，可重新提交");
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        lunwenchugaoService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	








}
