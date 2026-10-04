package com.hadluo.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 手写跨域：预检 OPTIONS 直接返回 200，并对 Allow-Headers 做「回显」，
 * 避免 Spring CorsFilter 在部分环境下与 WebMvcConfigurationSupport 组合时预检仍失败。
 */
public class GlobalCorsFilter extends OncePerRequestFilter {

	private static final Set<String> ALLOWED_ORIGINS = new HashSet<>(Arrays.asList(
			"http://127.0.0.1",
						"http://localhost:8081",
			"http://localhost:8082",
			"http://127.0.0.1:8081",
			"http://127.0.0.1:8082"));

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 允许所有来源（生产环境建议根据实际情况限制）
		String origin = request.getHeader("Origin");
		if (origin != null) {
			response.setHeader("Access-Control-Allow-Origin", origin);
			response.setHeader("Access-Control-Allow-Credentials", "true");
		}
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
		// 预检里浏览器会带 Access-Control-Request-Headers，回显最稳妥
		String reqHeaders = request.getHeader("Access-Control-Request-Headers");
		if (reqHeaders != null && !reqHeaders.trim().isEmpty()) {
			response.setHeader("Access-Control-Allow-Headers", reqHeaders);
		} else {
			response.setHeader("Access-Control-Allow-Headers",
					"Authorization, Content-Type, Token, token, X-Requested-With, Accept, Origin, Cache-Control, Pragma");
		}
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Expose-Headers", "Token, Authorization, Content-Disposition");
		response.setHeader("Vary", "Origin");

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		filterChain.doFilter(request, response);
	}
}
