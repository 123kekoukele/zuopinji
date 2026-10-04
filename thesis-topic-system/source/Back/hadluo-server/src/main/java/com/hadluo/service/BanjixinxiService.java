package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.utils.PageUtils;
import com.hadluo.entity.BanjixinxiEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.BanjixinxiView;

/**
 * 班级信息
 */
public interface BanjixinxiService extends IService<BanjixinxiEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<BanjixinxiView> selectListView(Wrapper<BanjixinxiEntity> wrapper);
   	
   	BanjixinxiView selectView(@Param("ew") Wrapper<BanjixinxiEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<BanjixinxiEntity> wrapper);
   	

}

