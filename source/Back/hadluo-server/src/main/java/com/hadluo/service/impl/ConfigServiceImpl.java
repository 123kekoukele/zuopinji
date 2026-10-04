package com.hadluo.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hadluo.dao.ConfigDao;
import com.hadluo.entity.ConfigEntity;
import com.hadluo.service.ConfigService;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.Query;

/**
 * 系统配置
 */
@Service("configService")
public class ConfigServiceImpl extends ServiceImpl<ConfigDao, ConfigEntity> implements ConfigService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<ConfigEntity> page = this.selectPage(new Query<ConfigEntity>(params).getPage(),
				new EntityWrapper<ConfigEntity>());
		return new PageUtils(page);
	}

	@Override
	public List<ConfigEntity> selectListView(Wrapper<ConfigEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ConfigEntity> wrapper) {
		Page<ConfigEntity> page = new Query<ConfigEntity>(params).getPage();
		page.setRecords(baseMapper.selectListView(page, wrapper));
		PageUtils pageUtil = new PageUtils(page);
		return pageUtil;
	}
}
