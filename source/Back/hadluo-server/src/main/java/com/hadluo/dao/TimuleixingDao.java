package com.hadluo.dao;

import com.hadluo.entity.TimuleixingEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.TimuleixingView;


/**
 * 题目类型
 * 
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface TimuleixingDao extends BaseMapper<TimuleixingEntity> {
	
	List<TimuleixingView> selectListView(@Param("ew") Wrapper<TimuleixingEntity> wrapper);

	List<TimuleixingView> selectListView(Pagination page,@Param("ew") Wrapper<TimuleixingEntity> wrapper);
	
	TimuleixingView selectView(@Param("ew") Wrapper<TimuleixingEntity> wrapper);

	/** 题目信息表中各题目类型及使用次数 */
	List<Map<String, Object>> selectTopicTypeStats(@Param("jiaoshigonghao") String jiaoshigonghao,
			@Param("zhuanye") String zhuanye);

}
