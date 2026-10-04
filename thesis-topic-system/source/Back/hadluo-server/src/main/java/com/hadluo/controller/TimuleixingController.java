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

import com.hadluo.entity.TimuleixingEntity;
import com.hadluo.entity.view.TimuleixingView;
import com.hadluo.entity.vo.TopicTypeScope;

import com.hadluo.service.TimuleixingService;
import com.hadluo.service.TokenService;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.CommonUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hadluo.utils.ExcelImportUtil;

/**
 * 题目类型
 * 后端接口
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@RestController
@RequestMapping("/timuleixing")
public class TimuleixingController {
    @Autowired
    private TimuleixingService timuleixingService;



    


    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params,TimuleixingEntity timuleixing,
		HttpServletRequest request){
		TopicTypeScope scope = timuleixingService.resolveScope(request);
		PageUtils page = timuleixingService.queryTopicTypePage(params, extractKeyword(timuleixing), scope);
        return R.ok().put("data", page);
    }
    
    /**
     * 前端列表
     */
	@IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params,TimuleixingEntity timuleixing, 
		HttpServletRequest request){
		TopicTypeScope scope = timuleixingService.resolveScope(request);
		PageUtils page = timuleixingService.queryTopicTypePage(params, extractKeyword(timuleixing), scope);
        return R.ok().put("data", page);
    }

	/**
     * 列表
     */
    @RequestMapping("/lists")
    public R list( TimuleixingEntity timuleixing){
       	EntityWrapper<TimuleixingEntity> ew = new EntityWrapper<TimuleixingEntity>();
      	ew.allEq(MPUtil.allEQMapPre( timuleixing, "timuleixing")); 
        return R.ok().put("data", timuleixingService.selectListView(ew));
    }

	 /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(TimuleixingEntity timuleixing){
        EntityWrapper< TimuleixingEntity> ew = new EntityWrapper< TimuleixingEntity>();
 		ew.allEq(MPUtil.allEQMapPre( timuleixing, "timuleixing")); 
		TimuleixingView timuleixingView =  timuleixingService.selectView(ew);
		return R.ok("查询题目类型成功").put("data", timuleixingView);
    }
	
    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        TimuleixingEntity timuleixing = timuleixingService.selectById(id);
		timuleixing = timuleixingService.selectView(new EntityWrapper<TimuleixingEntity>().eq("id", id));
        return R.ok().put("data", timuleixing);
    }

    /**
     * 前端详情
     */
	@IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id){
        TimuleixingEntity timuleixing = timuleixingService.selectById(id);
		timuleixing = timuleixingService.selectView(new EntityWrapper<TimuleixingEntity>().eq("id", id));
        return R.ok().put("data", timuleixing);
    }
    



    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody TimuleixingEntity timuleixing, HttpServletRequest request){
    	timuleixing.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(timuleixing);
        timuleixingService.insert(timuleixing);
        return R.ok();
    }
    
    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody TimuleixingEntity timuleixing, HttpServletRequest request){
    	timuleixing.setId(new Date().getTime()+new Double(Math.floor(Math.random()*1000)).longValue());
    	//ValidatorUtils.validateEntity(timuleixing);
        timuleixingService.insert(timuleixing);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody TimuleixingEntity timuleixing, HttpServletRequest request){
        //ValidatorUtils.validateEntity(timuleixing);
        timuleixingService.updateById(timuleixing);//全部更新
        return R.ok();
    }



    

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
        timuleixingService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * Excel 批量导入题目类型。
     * Excel 只需包含「题目类型」列；表头可在任意行、任意列，系统自动扫描识别。
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public R importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            return R.error("请选择要导入的文件");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx"))) {
            return R.error("仅支持 .xls 或 .xlsx 格式的 Excel 文件");
        }

        List<TimuleixingEntity> toInsert = new ArrayList<>();
        Set<String> namesInFile = new LinkedHashSet<>();
        Set<String> existingNames = new HashSet<>();
        for (TimuleixingEntity item : timuleixingService.selectList(new EntityWrapper<TimuleixingEntity>())) {
            if (item != null && StringUtils.isNotBlank(item.getTimuleixing())) {
                existingNames.add(item.getTimuleixing().trim());
            }
        }

        int skippedDuplicate = 0;
        try (InputStream is = file.getInputStream()) {
            Workbook wb = fileName.endsWith(".xlsx") ? new XSSFWorkbook(is) : new HSSFWorkbook(is);
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) {
                wb.close();
                return R.error("Excel 工作表为空");
            }

            int[] headerPos = ExcelImportUtil.locateColumn(sheet, "题目类型", 30);
            if (headerPos == null) {
                wb.close();
                return R.error("未在 Excel 前 30 行内找到「题目类型」表头，请检查文件格式");
            }
            int headerRowIndex = headerPos[0];
            int colTimuleixing = headerPos[1];

            for (int rowIndex = headerRowIndex + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                String typeName = ExcelImportUtil.getCellString(row.getCell(colTimuleixing)).trim();
                if (typeName.isEmpty()) {
                    continue;
                }
                if (namesInFile.contains(typeName)) {
                    skippedDuplicate++;
                    continue;
                }
                if (existingNames.contains(typeName)) {
                    skippedDuplicate++;
                    continue;
                }
                namesInFile.add(typeName);
                TimuleixingEntity entity = new TimuleixingEntity();
                entity.setId(new Date().getTime() + new Double(Math.floor(Math.random() * 1000)).longValue());
                entity.setTimuleixing(typeName);
                entity.setAddtime(new Date());
                toInsert.add(entity);
            }
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("导入失败：" + e.getMessage());
        }

        if (toInsert.isEmpty()) {
            if (skippedDuplicate > 0) {
                return R.error("Excel 中没有可导入的新题目类型（已跳过重复 " + skippedDuplicate + " 条）");
            }
            return R.error("Excel 中没有有效的题目类型数据");
        }
        timuleixingService.insertBatch(toInsert);
        String msg = "导入成功，共导入 " + toInsert.size() + " 条题目类型";
        if (skippedDuplicate > 0) {
            msg += "，已跳过重复 " + skippedDuplicate + " 条";
        }
        return R.ok(msg);
    }

    private static String extractKeyword(TimuleixingEntity timuleixing) {
        if (timuleixing == null || StringUtils.isBlank(timuleixing.getTimuleixing())) {
            return null;
        }
        String value = timuleixing.getTimuleixing().trim();
        if (value.length() >= 2 && value.startsWith("%") && value.endsWith("%")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
    
	








}
