package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.utils.PageUtils;
import com.hadluo.entity.MenuEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.MenuView;


/**
 * 菜单
 *
 * @author 
 * @email 
 * @date 2024-02-27 12:32:45
 */
public interface MenuService extends IService<MenuEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<MenuView> selectListView(Wrapper<MenuEntity> wrapper);
   	
   	MenuView selectView(@Param("ew") Wrapper<MenuEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<MenuEntity> wrapper);
   	

}

