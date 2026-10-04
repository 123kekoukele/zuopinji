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


import com.hadluo.dao.StoreupDao;
import com.hadluo.entity.StoreupEntity;
import com.hadluo.service.StoreupService;
import com.hadluo.entity.view.StoreupView;

@Service("storeupService")
public class StoreupServiceImpl extends ServiceImpl<StoreupDao, StoreupEntity> implements StoreupService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<StoreupEntity> page = this.selectPage(
                new Query<StoreupEntity>(params).getPage(),
                new EntityWrapper<StoreupEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<StoreupEntity> wrapper) {
		  Page<StoreupView> page =new Query<StoreupView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<StoreupView> selectListView(Wrapper<StoreupEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public StoreupView selectView(Wrapper<StoreupEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
