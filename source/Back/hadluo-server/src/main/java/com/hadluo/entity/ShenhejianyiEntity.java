package com.hadluo.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * 审核建议
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@TableName("shenhejianyi")
public class ShenhejianyiEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public ShenhejianyiEntity() {
		
	}
	
	public ShenhejianyiEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 题目编号
	 */
					
	private String timubianhao;
	
	/**
	 * 课题名称
	 */
					
	private String ketimingcheng;
	
	/**
	 * 题目类型
	 */
					
	private String timuleixing;
	
	/**
	 * 专业
	 */
					
	private String zhuanye;
	
	/**
	 * 课题性质
	 */
					
	private String ketixingzhi;
	
	/**
	 * 教师工号
	 */
					
	private String jiaoshigonghao;
	
	/**
	 * 教师姓名
	 */
					
	private String jiaoshixingming;
	
	/**
	 * 学号
	 */
					
	private String xuehao;
	
	/**
	 * 学生姓名
	 */
					
	private String xueshengxingming;
	
	/**
	 * 审批结果
	 */
					
	private String shenpijieguo;
	
	/**
	 * 建议内容
	 */
					
	private String jianyineirong;
	
	/**
	 * 指导意见
	 */
					
	private String zhidaoyijian;
	
	/**
	 * 审核时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date shenheshijian;
	
	
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 设置：题目编号
	 */
	public void setTimubianhao(String timubianhao) {
		this.timubianhao = timubianhao;
	}
	/**
	 * 获取：题目编号
	 */
	public String getTimubianhao() {
		return timubianhao;
	}
	/**
	 * 设置：课题名称
	 */
	public void setKetimingcheng(String ketimingcheng) {
		this.ketimingcheng = ketimingcheng;
	}
	/**
	 * 获取：课题名称
	 */
	public String getKetimingcheng() {
		return ketimingcheng;
	}
	/**
	 * 设置：题目类型
	 */
	public void setTimuleixing(String timuleixing) {
		this.timuleixing = timuleixing;
	}
	/**
	 * 获取：题目类型
	 */
	public String getTimuleixing() {
		return timuleixing;
	}
	/**
	 * 设置：专业
	 */
	public void setZhuanye(String zhuanye) {
		this.zhuanye = zhuanye;
	}
	/**
	 * 获取：专业
	 */
	public String getZhuanye() {
		return zhuanye;
	}
	/**
	 * 设置：课题性质
	 */
	public void setKetixingzhi(String ketixingzhi) {
		this.ketixingzhi = ketixingzhi;
	}
	/**
	 * 获取：课题性质
	 */
	public String getKetixingzhi() {
		return ketixingzhi;
	}
	/**
	 * 设置：教师工号
	 */
	public void setJiaoshigonghao(String jiaoshigonghao) {
		this.jiaoshigonghao = jiaoshigonghao;
	}
	/**
	 * 获取：教师工号
	 */
	public String getJiaoshigonghao() {
		return jiaoshigonghao;
	}
	/**
	 * 设置：教师姓名
	 */
	public void setJiaoshixingming(String jiaoshixingming) {
		this.jiaoshixingming = jiaoshixingming;
	}
	/**
	 * 获取：教师姓名
	 */
	public String getJiaoshixingming() {
		return jiaoshixingming;
	}
	/**
	 * 设置：学号
	 */
	public void setXuehao(String xuehao) {
		this.xuehao = xuehao;
	}
	/**
	 * 获取：学号
	 */
	public String getXuehao() {
		return xuehao;
	}
	/**
	 * 设置：学生姓名
	 */
	public void setXueshengxingming(String xueshengxingming) {
		this.xueshengxingming = xueshengxingming;
	}
	/**
	 * 获取：学生姓名
	 */
	public String getXueshengxingming() {
		return xueshengxingming;
	}
	/**
	 * 设置：审批结果
	 */
	public void setShenpijieguo(String shenpijieguo) {
		this.shenpijieguo = shenpijieguo;
	}
	/**
	 * 获取：审批结果
	 */
	public String getShenpijieguo() {
		return shenpijieguo;
	}
	/**
	 * 设置：建议内容
	 */
	public void setJianyineirong(String jianyineirong) {
		this.jianyineirong = jianyineirong;
	}
	/**
	 * 获取：建议内容
	 */
	public String getJianyineirong() {
		return jianyineirong;
	}
	/**
	 * 设置：指导意见
	 */
	public void setZhidaoyijian(String zhidaoyijian) {
		this.zhidaoyijian = zhidaoyijian;
	}
	/**
	 * 获取：指导意见
	 */
	public String getZhidaoyijian() {
		return zhidaoyijian;
	}
	/**
	 * 设置：审核时间
	 */
	public void setShenheshijian(Date shenheshijian) {
		this.shenheshijian = shenheshijian;
	}
	/**
	 * 获取：审核时间
	 */
	public Date getShenheshijian() {
		return shenheshijian;
	}

}
