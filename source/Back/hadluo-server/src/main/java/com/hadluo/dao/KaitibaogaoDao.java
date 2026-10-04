package com.hadluo.dao;

import com.hadluo.entity.KaitibaogaoEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.KaitibaogaoView;


/**
 * 开题报告
 * 
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface KaitibaogaoDao extends BaseMapper<KaitibaogaoEntity> {
	
	List<KaitibaogaoView> selectListView(@Param("ew") Wrapper<KaitibaogaoEntity> wrapper);

	List<KaitibaogaoView> selectListView(Pagination page,@Param("ew") Wrapper<KaitibaogaoEntity> wrapper);
	
	KaitibaogaoView selectView(@Param("ew") Wrapper<KaitibaogaoEntity> wrapper);
	

}
