package com.hadluo.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.hadluo.utils.PageUtils;
import com.hadluo.entity.TimuleixingEntity;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.hadluo.entity.view.TimuleixingView;
import com.hadluo.entity.vo.TimuleixingMergedItem;
import com.hadluo.entity.vo.TopicTypeScope;

import javax.servlet.http.HttpServletRequest;


/**
 * 题目类型
 *
 * @author 
 * @email 
 * @date 2024-02-27 12:32:44
 */
public interface TimuleixingService extends IService<TimuleixingEntity> {

    PageUtils queryPage(Map<String, Object> params);
    
   	List<TimuleixingView> selectListView(Wrapper<TimuleixingEntity> wrapper);
   	
   	TimuleixingView selectView(@Param("ew") Wrapper<TimuleixingEntity> wrapper);
   	
   	PageUtils queryPage(Map<String, Object> params,Wrapper<TimuleixingEntity> wrapper);

    /** 题目信息表中的题目类型分页列表（不读 timuleixing 字典表） */
    PageUtils queryTopicTypePage(Map<String, Object> params, String keyword, TopicTypeScope scope);

    /** 题目信息表中的全部题目类型名称（去重、排序） */
    List<String> listTypeNames(TopicTypeScope scope);

    /** 根据登录身份解析题目类型查询范围 */
    TopicTypeScope resolveScope(HttpServletRequest request);

    List<String> listTypeNames(HttpServletRequest request);

}

