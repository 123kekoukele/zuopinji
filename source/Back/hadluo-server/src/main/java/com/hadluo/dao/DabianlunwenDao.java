package com.hadluo.dao;

import com.hadluo.entity.DabianlunwenEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.DabianlunwenView;


/**
 * 答辩论文
 * 
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface DabianlunwenDao extends BaseMapper<DabianlunwenEntity> {
	
	List<DabianlunwenView> selectListView(@Param("ew") Wrapper<DabianlunwenEntity> wrapper);

	List<DabianlunwenView> selectListView(Pagination page,@Param("ew") Wrapper<DabianlunwenEntity> wrapper);
	
	DabianlunwenView selectView(@Param("ew") Wrapper<DabianlunwenEntity> wrapper);
	

}
