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
 * 题目信息
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@TableName("timuxinxi")
public class TimuxinxiEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public TimuxinxiEntity() {
		
	}
	
	public TimuxinxiEntity(T t) {
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
	 * 题目封面
	 */
					
	private String timufengmian;
	
	/**
	 * 课题性质
	 */
					
	private String ketixingzhi;
	
	/**
	 * 题目范围
	 */
					
	private String timufanwei;
	
	/**
	 * 选题时间
	 */
					
	private String xuantishijian;
	
	/**
	 * 教师工号
	 */
					
	private String jiaoshigonghao;
	
	/**
	 * 教师姓名
	 */
					
	private String jiaoshixingming;
	
	/**
	 * 发布时间
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 		
	private Date fabushijian;
	
	/**
	 * 收藏数量
	 */
					
	private Integer storeupnum;

	/**
	 * 是否已被选（前端列表用，非数据库字段）
	 */
	@TableField(exist = false)
	private Boolean yibeixuan;
	
	
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
	 * 设置：题目封面
	 */
	public void setTimufengmian(String timufengmian) {
		this.timufengmian = timufengmian;
	}
	/**
	 * 获取：题目封面
	 */
	public String getTimufengmian() {
		return timufengmian;
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
	 * 设置：题目范围
	 */
	public void setTimufanwei(String timufanwei) {
		this.timufanwei = timufanwei;
	}
	/**
	 * 获取：题目范围
	 */
	public String getTimufanwei() {
		return timufanwei;
	}
	/**
	 * 设置：选题时间
	 */
	public void setXuantishijian(String xuantishijian) {
		this.xuantishijian = xuantishijian;
	}
	/**
	 * 获取：选题时间
	 */
	public String getXuantishijian() {
		return xuantishijian;
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
	 * 设置：发布时间
	 */
	public void setFabushijian(Date fabushijian) {
		this.fabushijian = fabushijian;
	}
	/**
	 * 获取：发布时间
	 */
	public Date getFabushijian() {
		return fabushijian;
	}
	/**
	 * 设置：收藏数量
	 */
	public void setStoreupnum(Integer storeupnum) {
		this.storeupnum = storeupnum;
	}
	/**
	 * 获取：收藏数量
	 */
	public Integer getStoreupnum() {
		return storeupnum;
	}
	/**
	 * 设置：是否已被选
	 */
	public void setYibeixuan(Boolean yibeixuan) {
		this.yibeixuan = yibeixuan;
	}
	/**
	 * 获取：是否已被选
	 */
	public Boolean getYibeixuan() {
		return yibeixuan;
	}

}
