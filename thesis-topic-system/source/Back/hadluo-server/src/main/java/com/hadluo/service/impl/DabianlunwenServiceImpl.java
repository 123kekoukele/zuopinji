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


import com.hadluo.dao.DabianlunwenDao;
import com.hadluo.entity.DabianlunwenEntity;
import com.hadluo.service.DabianlunwenService;
import com.hadluo.entity.view.DabianlunwenView;

@Service("dabianlunwenService")
public class DabianlunwenServiceImpl extends ServiceImpl<DabianlunwenDao, DabianlunwenEntity> implements DabianlunwenService {
	
	
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<DabianlunwenEntity> page = this.selectPage(
                new Query<DabianlunwenEntity>(params).getPage(),
                new EntityWrapper<DabianlunwenEntity>()
        );
        return new PageUtils(page);
    }
    
    @Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<DabianlunwenEntity> wrapper) {
		  Page<DabianlunwenView> page =new Query<DabianlunwenView>(params).getPage();
	        page.setRecords(baseMapper.selectListView(page,wrapper));
	    	PageUtils pageUtil = new PageUtils(page);
	    	return pageUtil;
 	}
    
	@Override
	public List<DabianlunwenView> selectListView(Wrapper<DabianlunwenEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public DabianlunwenView selectView(Wrapper<DabianlunwenEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}


}
