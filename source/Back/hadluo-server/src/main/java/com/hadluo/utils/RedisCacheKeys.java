package com.hadluo.utils;

import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;

import cn.hutool.crypto.digest.DigestUtil;

public final class RedisCacheKeys {

	public static final String PREFIX = "hadluo:";

	public static final String CONFIG_INFO = PREFIX + "config:info:";

	public static final String CONFIG_LIST = PREFIX + "config:list:";

	public static final String MENU_LIST = PREFIX + "menu:list:";

	public static final String DASHBOARD_STATS = PREFIX + "dashboard:stats:";

	private RedisCacheKeys() {
	}

	public static String paramsSuffix(Map<String, Object> params) {
		if (params == null || params.isEmpty()) {
			return "default";
		}
		return DigestUtil.md5Hex(JSON.toJSONString(new TreeMap<>(params)));
	}
}
