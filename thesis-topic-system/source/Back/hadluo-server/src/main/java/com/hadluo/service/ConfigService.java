package com.hadluo.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.entity.ConfigEntity;
import com.hadluo.utils.PageUtils;

/**
 * 系统配置
 */
public interface ConfigService extends IService<ConfigEntity> {
	PageUtils queryPage(Map<String, Object> params);

	List<ConfigEntity> selectListView(Wrapper<ConfigEntity> wrapper);

	PageUtils queryPage(Map<String, Object> params, Wrapper<ConfigEntity> wrapper);
}
