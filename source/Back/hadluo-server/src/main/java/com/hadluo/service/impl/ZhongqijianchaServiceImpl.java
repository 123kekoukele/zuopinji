package com.hadluo.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hadluo.dao.ZhongqijianchaDao;
import com.hadluo.entity.ZhongqijianchaEntity;
import com.hadluo.entity.view.ZhongqijianchaView;
import com.hadluo.service.ZhongqijianchaService;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("zhongqijianchaService")
public class ZhongqijianchaServiceImpl extends ServiceImpl<ZhongqijianchaDao, ZhongqijianchaEntity> implements ZhongqijianchaService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<ZhongqijianchaEntity> page = this.selectPage(
				new Query<ZhongqijianchaEntity>(params).getPage(),
				new EntityWrapper<ZhongqijianchaEntity>()
		);
		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ZhongqijianchaEntity> wrapper) {
		Page<ZhongqijianchaView> page = new Query<ZhongqijianchaView>(params).getPage();
		page.setRecords(baseMapper.selectListView(page, wrapper));
		return new PageUtils(page);
	}

	@Override
	public List<ZhongqijianchaView> selectListView(Wrapper<ZhongqijianchaEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ZhongqijianchaView selectView(Wrapper<ZhongqijianchaEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}
}
