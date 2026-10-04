package com.hadluo.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hadluo.entity.BanjixinxiEntity;
import java.io.Serializable;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;

/**
 * 班级信息
 * 后端返回视图实体辅助类
 */
@TableName("banjixinxi")
public class BanjixinxiView extends BanjixinxiEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public BanjixinxiView() {
	}

	public BanjixinxiView(BanjixinxiEntity banjixinxiEntity) {
		try {
			BeanUtils.copyProperties(this, banjixinxiEntity);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}

