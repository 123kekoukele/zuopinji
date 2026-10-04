package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.utils.PageUtils;
import com.hadluo.entity.KaitibaogaoEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.KaitibaogaoView;


/**
 * 开题报告
 *
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface KaitibaogaoService extends IService<KaitibaogaoEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<KaitibaogaoView> selectListView(Wrapper<KaitibaogaoEntity> wrapper);
   	
   	KaitibaogaoView selectView(@Param("ew") Wrapper<KaitibaogaoEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<KaitibaogaoEntity> wrapper);
   	

}

