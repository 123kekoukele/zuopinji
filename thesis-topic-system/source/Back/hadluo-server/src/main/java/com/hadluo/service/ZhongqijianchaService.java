package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.entity.ZhongqijianchaEntity;
import com.hadluo.entity.view.ZhongqijianchaView;
import com.hadluo.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ZhongqijianchaService extends IService<ZhongqijianchaEntity> {

	PageUtils queryPage(Map<String, Object> params);

	List<ZhongqijianchaView> selectListView(Wrapper<ZhongqijianchaEntity> wrapper);

	ZhongqijianchaView selectView(@Param("ew") Wrapper<ZhongqijianchaEntity> wrapper);

	PageUtils queryPage(Map<String, Object> params, Wrapper<ZhongqijianchaEntity> wrapper);
}
