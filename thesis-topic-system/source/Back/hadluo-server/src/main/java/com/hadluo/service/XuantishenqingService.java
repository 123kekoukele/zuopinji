package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.utils.PageUtils;
import com.hadluo.entity.XuantishenqingEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.XuantishenqingView;


/**
 * 选题申请
 *
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface XuantishenqingService extends IService<XuantishenqingEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<XuantishenqingView> selectListView(Wrapper<XuantishenqingEntity> wrapper);
   	
   	XuantishenqingView selectView(@Param("ew") Wrapper<XuantishenqingEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<XuantishenqingEntity> wrapper);
   	

}

