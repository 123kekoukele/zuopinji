package com.hadluo.controller;

import com.hadluo.service.DashboardService;
import com.hadluo.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@RequestMapping("/stats")
	public R stats(@RequestParam(required = false) String zhuanye, HttpServletRequest request) {
		return R.ok().put("data", dashboardService.buildStats(request, zhuanye));
	}
}
