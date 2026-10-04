package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.utils.PageUtils;
import com.hadluo.entity.DaibanxinxiEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.DaibanxinxiView;


/**
 * 待办信息
 *
 * @author 
 * @email 
 * @date 2024-02-27 12:32:45
 */
public interface DaibanxinxiService extends IService<DaibanxinxiEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<DaibanxinxiView> selectListView(Wrapper<DaibanxinxiEntity> wrapper);
   	
   	DaibanxinxiView selectView(@Param("ew") Wrapper<DaibanxinxiEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<DaibanxinxiEntity> wrapper);
   	

}

