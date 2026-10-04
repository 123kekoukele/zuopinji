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
 * 答辩论文
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@TableName("dabianlunwen")
public class DabianlunwenEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public DabianlunwenEntity() {
		
	}
	
	public DabianlunwenEntity(T t) {
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
	 * 论文简介
	 */
					
	private String lunwenjianjie;
	
	/**
	 * 论文附件
	 */
					
	private String lunwenfujian;
	
	/**
	 * 提交时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date tijiaoshijian;
	
	/**
	 * 评分状态
	 */
					
	private String pingfenzhuangtai;

	/**
	 * 审核状态
	 */
	private String shenhezhuangtai;

	/**
	 * 审核意见（通过/驳回理由）
	 */
	private String shenheyuanyin;


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
	 * 设置：论文简介
	 */
	public void setLunwenjianjie(String lunwenjianjie) {
		this.lunwenjianjie = lunwenjianjie;
	}
	/**
	 * 获取：论文简介
	 */
	public String getLunwenjianjie() {
		return lunwenjianjie;
	}
	/**
	 * 设置：论文附件
	 */
	public void setLunwenfujian(String lunwenfujian) {
		this.lunwenfujian = lunwenfujian;
	}
	/**
	 * 获取：论文附件
	 */
	public String getLunwenfujian() {
		return lunwenfujian;
	}
	/**
	 * 设置：提交时间
	 */
	public void setTijiaoshijian(Date tijiaoshijian) {
		this.tijiaoshijian = tijiaoshijian;
	}
	/**
	 * 获取：提交时间
	 */
	public Date getTijiaoshijian() {
		return tijiaoshijian;
	}
	/**
	 * 设置：评分状态
	 */
	public void setPingfenzhuangtai(String pingfenzhuangtai) {
		this.pingfenzhuangtai = pingfenzhuangtai;
	}
	/**
	 * 获取：评分状态
	 */
	public String getPingfenzhuangtai() {
		return pingfenzhuangtai;
	}

	public String getShenhezhuangtai() {
		return shenhezhuangtai;
	}

	public void setShenhezhuangtai(String shenhezhuangtai) {
		this.shenhezhuangtai = shenhezhuangtai;
	}

	public void setShenheyuanyin(String shenheyuanyin) {
		this.shenheyuanyin = shenheyuanyin;
	}

	public String getShenheyuanyin() {
		return shenheyuanyin;
	}

	/**
	 * 关联题目是否已删除或不在当前账号题目信息范围内（非数据库字段）
	 */
	@TableField(exist = false)
	private Boolean timuyishanchu;

	public Boolean getTimuyishanchu() {
		return timuyishanchu;
	}

	public void setTimuyishanchu(Boolean timuyishanchu) {
		this.timuyishanchu = timuyishanchu;
	}

}
