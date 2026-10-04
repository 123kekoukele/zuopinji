package com.hadluo.dao;

import com.hadluo.entity.JiaoshiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.JiaoshiView;


/**
 * 教师
 * 
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface JiaoshiDao extends BaseMapper<JiaoshiEntity> {
	
	List<JiaoshiView> selectListView(@Param("ew") Wrapper<JiaoshiEntity> wrapper);

	List<JiaoshiView> selectListView(Pagination page,@Param("ew") Wrapper<JiaoshiEntity> wrapper);
	
	JiaoshiView selectView(@Param("ew") Wrapper<JiaoshiEntity> wrapper);
	

}
