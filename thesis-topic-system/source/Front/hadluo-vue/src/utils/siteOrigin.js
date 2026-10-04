/**
 * 与接口、静态资源同源的前端站点 origin（不含末尾 /）
 * - 生产可省略 VUE_APP_BASE_API_URL，浏览器内自动取 window.location.origin
 * - 构建脚本等非浏览器环境无 window 时，回退到环境变量
 */
export function getSiteOrigin() {
	// 浏览器内优先使用当前页面 origin，使图片等静态资源与接口一样走 devServer 代理
	if (typeof window !== "undefined" && window.location?.origin) {
		return window.location.origin.replace(/\/$/, "");
	}
	const fromEnv = (process.env.VUE_APP_BASE_API_URL || "").trim().replace(/\/$/, "");
	return fromEnv;
}
