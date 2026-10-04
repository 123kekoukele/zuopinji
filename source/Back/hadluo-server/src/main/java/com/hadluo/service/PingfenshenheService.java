package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.utils.PageUtils;
import com.hadluo.entity.PingfenshenheEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.PingfenshenheView;


/**
 * 评分审核
 *
 * @author 
 * @email 
 * @date 2024-02-27 12:32:45
 */
public interface PingfenshenheService extends IService<PingfenshenheEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<PingfenshenheView> selectListView(Wrapper<PingfenshenheEntity> wrapper);
   	
   	PingfenshenheView selectView(@Param("ew") Wrapper<PingfenshenheEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<PingfenshenheEntity> wrapper);
   	

    List<Map<String, Object>> selectValue(Map<String, Object> params,Wrapper<PingfenshenheEntity> wrapper);

    List<Map<String, Object>> selectTimeStatValue(Map<String, Object> params,Wrapper<PingfenshenheEntity> wrapper);

    List<Map<String, Object>> selectGroup(Map<String, Object> params,Wrapper<PingfenshenheEntity> wrapper);



}

