package com.hadluo.entity.view;

import com.hadluo.entity.TimuleixingEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import java.io.Serializable;
import com.hadluo.utils.EncryptUtil;
 

/**
 * 题目类型
 * 后端返回视图实体辅助类   
 * （通常后端关联的表或者自定义的字段需要返回使用）
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
@TableName("timuleixing")
public class TimuleixingView  extends TimuleixingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public TimuleixingView(){
	}
 
 	public TimuleixingView(TimuleixingEntity timuleixingEntity){
 	try {
			BeanUtils.copyProperties(this, timuleixingEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}


}
