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


import com.hadluo.dao.XuantishenqingDao;
import com.hadluo.entity.XuantishenqingEntity;
import com.hadluo.service.XuantishenqingService;
import com.hadluo.entity.view.XuantishenqingView;

@Service("xuantishenqingService")
public class XuantishenqingServiceImpl extends ServiceImpl<XuantishenqingDao, XuantishenqingEntity> implements XuantishenqingService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<XuantishenqingEntity> page = this.selectPage(
                new Query<XuantishenqingEntity>(params).getPage(),
                new EntityWrapper<XuantishenqingEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<XuantishenqingEntity> wrapper) {
		  Page<XuantishenqingView> page =new Query<XuantishenqingView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<XuantishenqingView> selectListView(Wrapper<XuantishenqingEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public XuantishenqingView selectView(Wrapper<XuantishenqingEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
