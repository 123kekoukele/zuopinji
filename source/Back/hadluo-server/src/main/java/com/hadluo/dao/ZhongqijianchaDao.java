package com.hadluo.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.hadluo.entity.ZhongqijianchaEntity;
import com.hadluo.entity.view.ZhongqijianchaView;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ZhongqijianchaDao extends BaseMapper<ZhongqijianchaEntity> {

	List<ZhongqijianchaView> selectListView(@Param("ew") Wrapper<ZhongqijianchaEntity> wrapper);

	List<ZhongqijianchaView> selectListView(Pagination page, @Param("ew") Wrapper<ZhongqijianchaEntity> wrapper);

	ZhongqijianchaView selectView(@Param("ew") Wrapper<ZhongqijianchaEntity> wrapper);
}
