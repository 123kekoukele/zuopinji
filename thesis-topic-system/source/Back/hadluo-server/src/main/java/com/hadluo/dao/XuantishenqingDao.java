package com.hadluo.dao;

import com.hadluo.entity.XuantishenqingEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.XuantishenqingView;


/**
 * 选题申请
 * 
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface XuantishenqingDao extends BaseMapper<XuantishenqingEntity> {
	
	List<XuantishenqingView> selectListView(@Param("ew") Wrapper<XuantishenqingEntity> wrapper);

	List<XuantishenqingView> selectListView(Pagination page,@Param("ew") Wrapper<XuantishenqingEntity> wrapper);
	
	XuantishenqingView selectView(@Param("ew") Wrapper<XuantishenqingEntity> wrapper);
	

}
