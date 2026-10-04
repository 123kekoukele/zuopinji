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


import com.hadluo.dao.LunwenchugaoDao;
import com.hadluo.entity.LunwenchugaoEntity;
import com.hadluo.service.LunwenchugaoService;
import com.hadluo.entity.view.LunwenchugaoView;

@Service("lunwenchugaoService")
public class LunwenchugaoServiceImpl extends ServiceImpl<LunwenchugaoDao, LunwenchugaoEntity> implements LunwenchugaoService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<LunwenchugaoEntity> page = this.selectPage(
                new Query<LunwenchugaoEntity>(params).getPage(),
                new EntityWrapper<LunwenchugaoEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<LunwenchugaoEntity> wrapper) {
		  Page<LunwenchugaoView> page =new Query<LunwenchugaoView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<LunwenchugaoView> selectListView(Wrapper<LunwenchugaoEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public LunwenchugaoView selectView(Wrapper<LunwenchugaoEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
