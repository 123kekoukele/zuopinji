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


import com.hadluo.dao.TimuxinxiDao;
import com.hadluo.entity.TimuxinxiEntity;
import com.hadluo.service.TimuxinxiService;
import com.hadluo.entity.view.TimuxinxiView;

@Service("timuxinxiService")
public class TimuxinxiServiceImpl extends ServiceImpl<TimuxinxiDao, TimuxinxiEntity> implements TimuxinxiService {

	@Override
	public TimuxinxiEntity selectByTimubianhaoForUpdate(String timubianhao) {
		return baseMapper.selectByTimubianhaoForUpdate(timubianhao);
	}
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<TimuxinxiEntity> page = this.selectPage(
                new Query<TimuxinxiEntity>(params).getPage(),
                new EntityWrapper<TimuxinxiEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<TimuxinxiEntity> wrapper) {
		  Page<TimuxinxiView> page =new Query<TimuxinxiView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<TimuxinxiView> selectListView(Wrapper<TimuxinxiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public TimuxinxiView selectView(Wrapper<TimuxinxiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
