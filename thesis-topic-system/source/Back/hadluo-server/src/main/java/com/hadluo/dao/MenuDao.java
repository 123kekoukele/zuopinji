package com.hadluo.dao;

import com.hadluo.entity.MenuEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.MenuView;


/**
 * 菜单
 * 
 * @author 
 * @email 
 * @date 2024-02-27 12:32:45
 */
public interface MenuDao extends BaseMapper<MenuEntity> {
	
	List<MenuView> selectListView(@Param("ew") Wrapper<MenuEntity> wrapper);

	List<MenuView> selectListView(Pagination page,@Param("ew") Wrapper<MenuEntity> wrapper);
	
	MenuView selectView(@Param("ew") Wrapper<MenuEntity> wrapper);
	

}
