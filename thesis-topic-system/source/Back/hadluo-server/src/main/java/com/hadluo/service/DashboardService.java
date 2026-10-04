package com.hadluo.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface DashboardService {

	Map<String, Object> buildStats(HttpServletRequest request, String zhuanye);
}
