package com.hadluo.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

@TableName("zhongqijiancha")
public class ZhongqijianchaEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	public ZhongqijianchaEntity() {
	}

	public ZhongqijianchaEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@TableId
	private Long id;
	private String timubianhao;
	private String ketimingcheng;
	private String timuleixing;
	private String zhuanye;
	private String ketixingzhi;
	private String jiaoshigonghao;
	private String jiaoshixingming;
	private String xuehao;
	private String xueshengxingming;
	private String zhongqijianjie;
	private String zhongqifujian;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date zhongqishijian;

	private String shenhezhuangtai;

	/**
	 * 审核意见（通过/驳回理由）
	 */
	private String shenheyuanyin;

	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
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

	public String getTimubianhao() {
		return timubianhao;
	}

	public void setTimubianhao(String timubianhao) {
		this.timubianhao = timubianhao;
	}

	public String getKetimingcheng() {
		return ketimingcheng;
	}

	public void setKetimingcheng(String ketimingcheng) {
		this.ketimingcheng = ketimingcheng;
	}

	public String getTimuleixing() {
		return timuleixing;
	}

	public void setTimuleixing(String timuleixing) {
		this.timuleixing = timuleixing;
	}

	public String getZhuanye() {
		return zhuanye;
	}

	public void setZhuanye(String zhuanye) {
		this.zhuanye = zhuanye;
	}

	public String getKetixingzhi() {
		return ketixingzhi;
	}

	public void setKetixingzhi(String ketixingzhi) {
		this.ketixingzhi = ketixingzhi;
	}

	public String getJiaoshigonghao() {
		return jiaoshigonghao;
	}

	public void setJiaoshigonghao(String jiaoshigonghao) {
		this.jiaoshigonghao = jiaoshigonghao;
	}

	public String getJiaoshixingming() {
		return jiaoshixingming;
	}

	public void setJiaoshixingming(String jiaoshixingming) {
		this.jiaoshixingming = jiaoshixingming;
	}

	public String getXuehao() {
		return xuehao;
	}

	public void setXuehao(String xuehao) {
		this.xuehao = xuehao;
	}

	public String getXueshengxingming() {
		return xueshengxingming;
	}

	public void setXueshengxingming(String xueshengxingming) {
		this.xueshengxingming = xueshengxingming;
	}

	public String getZhongqijianjie() {
		return zhongqijianjie;
	}

	public void setZhongqijianjie(String zhongqijianjie) {
		this.zhongqijianjie = zhongqijianjie;
	}

	public String getZhongqifujian() {
		return zhongqifujian;
	}

	public void setZhongqifujian(String zhongqifujian) {
		this.zhongqifujian = zhongqifujian;
	}

	public Date getZhongqishijian() {
		return zhongqishijian;
	}

	public void setZhongqishijian(Date zhongqishijian) {
		this.zhongqishijian = zhongqishijian;
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
