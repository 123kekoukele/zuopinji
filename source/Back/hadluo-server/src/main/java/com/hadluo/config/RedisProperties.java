package com.hadluo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

	/** 是否启用 Redis 缓存；关闭后自动降级为直连数据库 */
	private boolean enabled = true;

	private String host = "127.0.0.1";

	private int port = 6379;

	private String password = "";

	private int database = 0;

	private long timeout = 3000;

	private Cache cache = new Cache();

	public static class Cache {
		private long configTtl = 3600;
		private long menuTtl = 1800;
		private long dashboardTtl = 300;

		public long getConfigTtl() {
			return configTtl;
		}

		public void setConfigTtl(long configTtl) {
			this.configTtl = configTtl;
		}

		public long getMenuTtl() {
			return menuTtl;
		}

		public void setMenuTtl(long menuTtl) {
			this.menuTtl = menuTtl;
		}

		public long getDashboardTtl() {
			return dashboardTtl;
		}

		public void setDashboardTtl(long dashboardTtl) {
			this.dashboardTtl = dashboardTtl;
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}
}
