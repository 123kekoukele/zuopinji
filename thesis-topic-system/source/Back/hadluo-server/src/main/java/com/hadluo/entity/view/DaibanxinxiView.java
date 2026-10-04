package com.hadluo.entity.view;

import com.hadluo.entity.DaibanxinxiEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import java.io.Serializable;
import com.hadluo.utils.EncryptUtil;
 

/**
 * 待办信息
 * 后端返回视图实体辅助类   
 * （通常后端关联的表或者自定义的字段需要返回使用）
 * @author 
 * @email 
 * @date 2024-02-27 12:32:45
 */
@TableName("daibanxinxi")
public class DaibanxinxiView  extends DaibanxinxiEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public DaibanxinxiView(){
	}
 
 	public DaibanxinxiView(DaibanxinxiEntity daibanxinxiEntity){
 	try {
			BeanUtils.copyProperties(this, daibanxinxiEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
	}


}
