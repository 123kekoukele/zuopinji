package com.hadluo.dao;

import com.hadluo.entity.PingfenshenheEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.PingfenshenheView;


/**
 * 评分审核
 * 
 * @author 
 * @email 
 * @date 2024-02-27 12:32:45
 */
public interface PingfenshenheDao extends BaseMapper<PingfenshenheEntity> {
	
	List<PingfenshenheView> selectListView(@Param("ew") Wrapper<PingfenshenheEntity> wrapper);

	List<PingfenshenheView> selectListView(Pagination page,@Param("ew") Wrapper<PingfenshenheEntity> wrapper);
	
	PingfenshenheView selectView(@Param("ew") Wrapper<PingfenshenheEntity> wrapper);
	

    List<Map<String, Object>> selectValue(@Param("params") Map<String, Object> params,@Param("ew") Wrapper<PingfenshenheEntity> wrapper);

    List<Map<String, Object>> selectTimeStatValue(@Param("params") Map<String, Object> params,@Param("ew") Wrapper<PingfenshenheEntity> wrapper);

    List<Map<String, Object>> selectGroup(@Param("params") Map<String, Object> params,@Param("ew") Wrapper<PingfenshenheEntity> wrapper);



}
