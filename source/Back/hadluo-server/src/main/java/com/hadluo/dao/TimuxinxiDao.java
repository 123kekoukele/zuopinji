package com.hadluo.dao;

import com.hadluo.entity.TimuxinxiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.TimuxinxiView;


/**
 * 题目信息
 * 
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface TimuxinxiDao extends BaseMapper<TimuxinxiEntity> {

	/**
	 * 按题目编号加行锁（FOR UPDATE），用于选题申请时保证同一题目不会被并发抢占
	 */
	TimuxinxiEntity selectByTimubianhaoForUpdate(@Param("timubianhao") String timubianhao);
	
	List<TimuxinxiView> selectListView(@Param("ew") Wrapper<TimuxinxiEntity> wrapper);

	List<TimuxinxiView> selectListView(Pagination page,@Param("ew") Wrapper<TimuxinxiEntity> wrapper);
	
	TimuxinxiView selectView(@Param("ew") Wrapper<TimuxinxiEntity> wrapper);
	

}
