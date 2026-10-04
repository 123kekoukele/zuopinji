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


import com.hadluo.dao.ShenhejianyiDao;
import com.hadluo.entity.ShenhejianyiEntity;
import com.hadluo.service.ShenhejianyiService;
import com.hadluo.entity.view.ShenhejianyiView;

@Service("shenhejianyiService")
public class ShenhejianyiServiceImpl extends ServiceImpl<ShenhejianyiDao, ShenhejianyiEntity> implements ShenhejianyiService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ShenhejianyiEntity> page = this.selectPage(
                new Query<ShenhejianyiEntity>(params).getPage(),
                new EntityWrapper<ShenhejianyiEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<ShenhejianyiEntity> wrapper) {
		  Page<ShenhejianyiView> page =new Query<ShenhejianyiView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<ShenhejianyiView> selectListView(Wrapper<ShenhejianyiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public ShenhejianyiView selectView(Wrapper<ShenhejianyiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
