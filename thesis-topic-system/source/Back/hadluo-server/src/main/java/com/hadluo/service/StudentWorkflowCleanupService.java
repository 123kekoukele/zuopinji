package com.hadluo.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hadluo.entity.DabianlunwenEntity;
import com.hadluo.entity.KaitibaogaoEntity;
import com.hadluo.entity.LunwenchugaoEntity;
import com.hadluo.entity.PingfenshenheEntity;
import com.hadluo.entity.ShenhejianyiEntity;
import com.hadluo.entity.ZhongqijianchaEntity;

/**
 * 选题申请被驳回后，清空该学生已提交的毕业流程材料，便于重新选题后再次提交。
 */
@Service
public class StudentWorkflowCleanupService {

    @Autowired
    private KaitibaogaoService kaitibaogaoService;
    @Autowired
    private LunwenchugaoService lunwenchugaoService;
    @Autowired
    private ZhongqijianchaService zhongqijianchaService;
    @Autowired
    private DabianlunwenService dabianlunwenService;
    @Autowired
    private ShenhejianyiService shenhejianyiService;
    @Autowired
    private PingfenshenheService pingfenshenheService;

    @Transactional(rollbackFor = Exception.class)
    public void clearByXuehao(String xuehao) {
        if (StringUtils.isBlank(xuehao)) {
            return;
        }
        String trimmed = xuehao.trim();
        dabianlunwenService.delete(new EntityWrapper<DabianlunwenEntity>().eq("xuehao", trimmed));
        zhongqijianchaService.delete(new EntityWrapper<ZhongqijianchaEntity>().eq("xuehao", trimmed));
        lunwenchugaoService.delete(new EntityWrapper<LunwenchugaoEntity>().eq("xuehao", trimmed));
        kaitibaogaoService.delete(new EntityWrapper<KaitibaogaoEntity>().eq("xuehao", trimmed));
        shenhejianyiService.delete(new EntityWrapper<ShenhejianyiEntity>().eq("xuehao", trimmed));
        pingfenshenheService.delete(new EntityWrapper<PingfenshenheEntity>().eq("xuehao", trimmed));
    }
}
