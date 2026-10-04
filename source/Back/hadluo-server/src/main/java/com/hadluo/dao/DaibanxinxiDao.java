package com.hadluo.dao;

import com.hadluo.entity.DaibanxinxiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.DaibanxinxiView;


/**
 * 待办信息
 * 
 * @author 
 * @email 
 * @date 2024-02-27 12:32:45
 */
public interface DaibanxinxiDao extends BaseMapper<DaibanxinxiEntity> {
	
	List<DaibanxinxiView> selectListView(@Param("ew") Wrapper<DaibanxinxiEntity> wrapper);

	List<DaibanxinxiView> selectListView(Pagination page,@Param("ew") Wrapper<DaibanxinxiEntity> wrapper);
	
	DaibanxinxiView selectView(@Param("ew") Wrapper<DaibanxinxiEntity> wrapper);
	

}
