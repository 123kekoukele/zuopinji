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


import com.hadluo.dao.DaibanxinxiDao;
import com.hadluo.entity.DaibanxinxiEntity;
import com.hadluo.service.DaibanxinxiService;
import com.hadluo.entity.view.DaibanxinxiView;

@Service("daibanxinxiService")
public class DaibanxinxiServiceImpl extends ServiceImpl<DaibanxinxiDao, DaibanxinxiEntity> implements DaibanxinxiService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<DaibanxinxiEntity> page = this.selectPage(
                new Query<DaibanxinxiEntity>(params).getPage(),
                new EntityWrapper<DaibanxinxiEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<DaibanxinxiEntity> wrapper) {
		  Page<DaibanxinxiView> page =new Query<DaibanxinxiView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<DaibanxinxiView> selectListView(Wrapper<DaibanxinxiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public DaibanxinxiView selectView(Wrapper<DaibanxinxiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
