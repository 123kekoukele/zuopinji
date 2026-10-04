package com.hadluo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 注册全局 CORS 过滤器（见 {@link GlobalCorsFilter}）。
 */
@Configuration
public class CorsConfig {

	@Bean
	public FilterRegistrationBean<GlobalCorsFilter> globalCorsFilterRegistration() {
		FilterRegistrationBean<GlobalCorsFilter> bean = new FilterRegistrationBean<>(new GlobalCorsFilter());
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}
}
