package com.hadluo.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hadluo.entity.ZhongqijianchaEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

@TableName("zhongqijiancha")
public class ZhongqijianchaView extends ZhongqijianchaEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public ZhongqijianchaView() {
	}

	public ZhongqijianchaView(ZhongqijianchaEntity entity) {
		try {
			BeanUtils.copyProperties(this, entity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
