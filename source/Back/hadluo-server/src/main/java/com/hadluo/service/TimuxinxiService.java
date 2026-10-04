package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.utils.PageUtils;
import com.hadluo.entity.TimuxinxiEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.TimuxinxiView;


/**
 * 题目信息
 *
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface TimuxinxiService extends IService<TimuxinxiEntity> {

	/**
	 * 按题目编号加行锁（FOR UPDATE），用于选题申请时保证同一题目并发安全
	 */
	TimuxinxiEntity selectByTimubianhaoForUpdate(String timubianhao);

    PageUtils queryPage(Map<String, Object> params);
    
   	List<TimuxinxiView> selectListView(Wrapper<TimuxinxiEntity> wrapper);
   	
   	TimuxinxiView selectView(@Param("ew") Wrapper<TimuxinxiEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<TimuxinxiEntity> wrapper);
   	

}

