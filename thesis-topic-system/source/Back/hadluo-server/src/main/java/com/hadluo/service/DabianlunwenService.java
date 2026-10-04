package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.utils.PageUtils;
import com.hadluo.entity.DabianlunwenEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.DabianlunwenView;


/**
 * 答辩论文
 *
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface DabianlunwenService extends IService<DabianlunwenEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<DabianlunwenView> selectListView(Wrapper<DabianlunwenEntity> wrapper);
   	
   	DabianlunwenView selectView(@Param("ew") Wrapper<DabianlunwenEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<DabianlunwenEntity> wrapper);
   	

}

