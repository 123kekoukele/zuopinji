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

import com.hadluo.entity.DaibanxinxiEntity;
import com.hadluo.entity.view.DaibanxinxiView;

import com.hadluo.service.DaibanxinxiService;
import com.hadluo.service.TokenService;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.CommonUtil;
import java.io.IOException;

/**
 * 待办信息
 * 后端接口
 * @author 
 * @email 
 * @date 2024-02-27 12:32:45
 */
@RestController
@RequestMapping("/daibanxinxi")
public class DaibanxinxiController {
    @Autowired
    private DaibanxinxiService daibanxinxiService;



    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,DaibanxinxiEntity daibanxinxi,
		HttpServletRequest request){
		String tableName = request.getSession().getAttribute("tableName").toString();
		if(tableName.equals("xuesheng")) {
			daibanxinxi.setXuehao((String)request.getSession().getAttribute("username"));
		}
        EntityWrapper<DaibanxinxiEntity> ew = new EntityWrapper<DaibanxinxiEntity>();

		PageUtils page = daibanxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, daibanxinxi), params), params));

        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,DaibanxinxiEntity daibanxinxi, 
		HttpServletRequest request){
        EntityWrapper<DaibanxinxiEntity> ew = new EntityWrapper<DaibanxinxiEntity>();

		PageUtils page = daibanxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, daibanxinxi), params), params));
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( DaibanxinxiEntity daibanxinxi){
       	EntityWrapper<DaibanxinxiEntity> ew = new EntityWrapper<DaibanxinxiEntity>();
      	ew.allEq(MPUtil.allEQMapPre( daibanxinxi, "daibanxinxi")); 
        return R.ok().put("data", daibanxinxiService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(DaibanxinxiEntity daibanxinxi){
        EntityWrapper< DaibanxinxiEntity> ew = new EntityWrapper< DaibanxinxiEntity>();
 		ew.allEq(MPUtil.allEQMapPre( daibanxinxi, "daibanxinxi")); 
		DaibanxinxiView daibanxinxiView =  daibanxinxiService.selectView(ew);
		return R.ok("查询待办信息成功").put("data", daibanxinxiView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        DaibanxinxiEntity daibanxinxi = daibanxinxiService.selectById(id);
		daibanxinxi = daibanxinxiService.selectView(new EntityWrapper<DaibanxinxiEntity>().eq("id", id));
        return R.ok().put("data", daibanxinxi);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        DaibanxinxiEntity daibanxinxi = daibanxinxiService.selectById(id);
		daibanxinxi = daibanxinxiService.selectView(new EntityWrapper<DaibanxinxiEntity>().eq("id", id));
        return R.ok().put("data", daibanxinxi);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DaibanxinxiEntity daibanxinxi, HttpServletRequest request){
    	daibanxinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(daibanxinxi);
        daibanxinxiService.insert(daibanxinxi);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody DaibanxinxiEntity daibanxinxi, HttpServletRequest request){
    	daibanxinxi.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(daibanxinxi);
        daibanxinxiService.insert(daibanxinxi);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody DaibanxinxiEntity daibanxinxi, HttpServletRequest request){
        //ValidatorUtils.validateEntity(daibanxinxi);
        daibanxinxiService.updateById(daibanxinxi);//全部更新
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        daibanxinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }
    
	








}
