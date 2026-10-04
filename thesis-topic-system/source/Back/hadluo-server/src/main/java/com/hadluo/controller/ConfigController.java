package com.hadluo.controller;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import com.hadluo.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hadluo.entity.ConfigEntity;
import com.hadluo.config.RedisProperties;
import com.hadluo.service.ConfigService;
import com.hadluo.service.RedisCacheService;
import com.hadluo.utils.PageUtils;
import com.hadluo.utils.R;
import com.hadluo.utils.MPUtil;
import com.hadluo.utils.RedisCacheKeys;

/**
 * 配置管理
 */
@RestController
@RequestMapping("config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private RedisProperties redisProperties;

    /**
     * 根据name查询配置
     */
    @IgnoreAuth
    @RequestMapping("/info")
    public R info(@RequestParam("name") String name, HttpServletRequest request) {
        String cacheKey = RedisCacheKeys.CONFIG_INFO + name;
        ConfigEntity config = redisCacheService.getOrLoad(cacheKey, ConfigEntity.class,
                () -> configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", name)),
                redisProperties.getCache().getConfigTtl());
        return R.ok().put("data", config);
    }

    /**
     * 保存配置
     */
    @RequestMapping("/save")
    @Transactional
    public R save(@RequestBody ConfigEntity config, HttpServletRequest request) {
        configService.insert(config);
        redisCacheService.evictConfigCache();
        return R.ok();
    }

    /**
     * 更新配置
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ConfigEntity config, HttpServletRequest request) {
        configService.updateById(config);
        redisCacheService.evictConfigCache();
        return R.ok();
    }

    /**
     * 删除配置
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        configService.deleteBatchIds(Arrays.asList(ids));
        redisCacheService.evictConfigCache();
        return R.ok();
    }

    /**
     * 后端列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ConfigEntity config, HttpServletRequest request) {
        EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();
        PageUtils page = configService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, config), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ConfigEntity config, HttpServletRequest request) {
        String cacheKey = RedisCacheKeys.CONFIG_LIST + RedisCacheKeys.paramsSuffix(params);
        PageUtils page = redisCacheService.getOrLoad(cacheKey, PageUtils.class, () -> {
            EntityWrapper<ConfigEntity> ew = new EntityWrapper<ConfigEntity>();
            return configService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, config), params), params));
        }, redisProperties.getCache().getConfigTtl());
        return R.ok().put("data", page);
    }
}
