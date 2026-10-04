package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.utils.PageUtils;
import com.hadluo.entity.ShenhejianyiEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.ShenhejianyiView;


/**
 * 审核建议
 *
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface ShenhejianyiService extends IService<ShenhejianyiEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<ShenhejianyiView> selectListView(Wrapper<ShenhejianyiEntity> wrapper);
   	
   	ShenhejianyiView selectView(@Param("ew") Wrapper<ShenhejianyiEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<ShenhejianyiEntity> wrapper);
   	

}

