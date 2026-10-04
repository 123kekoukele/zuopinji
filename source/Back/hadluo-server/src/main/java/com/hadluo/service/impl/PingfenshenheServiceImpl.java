package com.hadluo.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.Query;


import com.hadluo.dao.PingfenshenheDao;
import com.hadluo.entity.PingfenshenheEntity;
import com.hadluo.service.PingfenshenheService;
import com.hadluo.entity.view.PingfenshenheView;

@Service("pingfenshenheService")
public class PingfenshenheServiceImpl extends ServiceImpl<PingfenshenheDao, PingfenshenheEntity> implements PingfenshenheService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<PingfenshenheEntity> page = this.selectPage(
                new Query<PingfenshenheEntity>(params).getPage(),
                new EntityWrapper<PingfenshenheEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<PingfenshenheEntity> wrapper) {
		  Page<PingfenshenheView> page =new Query<PingfenshenheView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<PingfenshenheView> selectListView(Wrapper<PingfenshenheEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public PingfenshenheView selectView(Wrapper<PingfenshenheEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}

    @Override
    public List<Map<String, Object>> selectValue(Map<String, Object> params, Wrapper<PingfenshenheEntity> wrapper) {
        return baseMapper.selectValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params, Wrapper<PingfenshenheEntity> wrapper) {
        return baseMapper.selectTimeStatValue(params, wrapper);
    }

    @Override
    public List<Map<String, Object>> selectGroup(Map<String, Object> params, Wrapper<PingfenshenheEntity> wrapper) {
        return baseMapper.selectGroup(params, wrapper);
    }




}
