package com.hadluo.dao;

import com.hadluo.entity.XueshengEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.XueshengView;


/**
 * 学生
 * 
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface XueshengDao extends BaseMapper<XueshengEntity> {
	
	List<XueshengView> selectListView(@Param("ew") Wrapper<XueshengEntity> wrapper);

	List<XueshengView> selectListView(Pagination page,@Param("ew") Wrapper<XueshengEntity> wrapper);
	
	XueshengView selectView(@Param("ew") Wrapper<XueshengEntity> wrapper);
	

}
