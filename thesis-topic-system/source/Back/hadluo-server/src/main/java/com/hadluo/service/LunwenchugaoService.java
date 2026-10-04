package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.utils.PageUtils;
import com.hadluo.entity.LunwenchugaoEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.LunwenchugaoView;


/**
 * 论文初稿
 *
 * @author 
 * @email 
 * @date 2024-02-27 12:32:45
 */
public interface LunwenchugaoService extends IService<LunwenchugaoEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<LunwenchugaoView> selectListView(Wrapper<LunwenchugaoEntity> wrapper);
   	
   	LunwenchugaoView selectView(@Param("ew") Wrapper<LunwenchugaoEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<LunwenchugaoEntity> wrapper);
   	

}

