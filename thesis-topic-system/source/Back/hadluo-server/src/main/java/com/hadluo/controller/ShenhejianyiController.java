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

import com.hadluo.entity.ShenhejianyiEntity;
import com.hadluo.entity.view.ShenhejianyiView;

import com.hadluo.service.ShenhejianyiService;
import com.hadluo.service.TokenService;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.CommonUtil;
import java.io.IOException;

/**
 * 审核建议
 * 后端接口
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@RestController
@RequestMapping("/shenhejianyi")
public class ShenhejianyiController {
    @Autowired
    private ShenhejianyiService shenhejianyiService;



    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,ShenhejianyiEntity shenhejianyi,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("jiaoshi")) {
			shenhejianyi.setJiaoshigonghao((String)request.getSession().getAttribute("username"));
		}
		if(tableName.equals("xuesheng")) {
			shenhejianyi.setXuehao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<ShenhejianyiEntity> ew = new EntityWrapper<ShenhejianyiEntity>();

		PageUtils page = shenhejianyiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shenhejianyi), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,ShenhejianyiEntity shenhejianyi, 
		HttpServletRequest request){
        EntityWrapper<ShenhejianyiEntity> ew = new EntityWrapper<ShenhejianyiEntity>();

		PageUtils page = shenhejianyiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shenhejianyi), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( ShenhejianyiEntity shenhejianyi){
       	EntityWrapper<ShenhejianyiEntity> ew = new EntityWrapper<ShenhejianyiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( shenhejianyi, "shenhejianyi")); 
        return R.ok().put("data", shenhejianyiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ShenhejianyiEntity shenhejianyi){
        EntityWrapper< ShenhejianyiEntity> ew = new EntityWrapper< ShenhejianyiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( shenhejianyi, "shenhejianyi")); 
		ShenhejianyiView shenhejianyiView =  shenhejianyiService.selectView(ew);
		return R.ok("查询审核建议成功").put("data", shenhejianyiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        ShenhejianyiEntity shenhejianyi = shenhejianyiService.selectById(id);
		shenhejianyi = shenhejianyiService.selectView(new EntityWrapper<ShenhejianyiEntity>().eq("id", id));
        return R.ok().put("data", shenhejianyi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        ShenhejianyiEntity shenhejianyi = shenhejianyiService.selectById(id);
		shenhejianyi = shenhejianyiService.selectView(new EntityWrapper<ShenhejianyiEntity>().eq("id", id));
        return R.ok().put("data", shenhejianyi);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ShenhejianyiEntity shenhejianyi, HttpServletRequest request){
    	shenhejianyi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(shenhejianyi);
        shenhejianyiService.insert(shenhejianyi);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ShenhejianyiEntity shenhejianyi, HttpServletRequest request){
    	shenhejianyi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(shenhejianyi);
        shenhejianyiService.insert(shenhejianyi);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ShenhejianyiEntity shenhejianyi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(shenhejianyi);
        shenhejianyiService.updateById(shenhejianyi);//全部更新
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        shenhejianyiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	








}
