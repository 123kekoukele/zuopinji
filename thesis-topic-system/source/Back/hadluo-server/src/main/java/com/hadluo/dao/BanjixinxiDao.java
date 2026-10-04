package com.hadluo.dao;

import com.hadluo.entity.BanjixinxiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.BanjixinxiView;

/**
 * 班级信息
 */
public interface BanjixinxiDao extends BaseMapper<BanjixinxiEntity> {
	
	List<BanjixinxiView> selectListView(@Param("ew") Wrapper<BanjixinxiEntity> wrapper);

	List<BanjixinxiView> selectListView(Pagination page,@Param("ew") Wrapper<BanjixinxiEntity> wrapper);
	
	BanjixinxiView selectView(@Param("ew") Wrapper<BanjixinxiEntity> wrapper);
	
}

