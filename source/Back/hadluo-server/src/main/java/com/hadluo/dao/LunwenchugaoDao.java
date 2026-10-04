package com.hadluo.dao;

import com.hadluo.entity.LunwenchugaoEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;

import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.LunwenchugaoView;


/**
 * 论文初稿
 * 
 * @author 
 * @email 
 * @date 2024-02-27 12:32:45
 */
public interface LunwenchugaoDao extends BaseMapper<LunwenchugaoEntity> {
	
	List<LunwenchugaoView> selectListView(@Param("ew") Wrapper<LunwenchugaoEntity> wrapper);

	List<LunwenchugaoView> selectListView(Pagination page,@Param("ew") Wrapper<LunwenchugaoEntity> wrapper);
	
	LunwenchugaoView selectView(@Param("ew") Wrapper<LunwenchugaoEntity> wrapper);
	

}
