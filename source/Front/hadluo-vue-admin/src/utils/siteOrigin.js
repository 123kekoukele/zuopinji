/**
 * 与接口、静态资源同源的前端站点 origin（不含末尾 /）
 */
export function getSiteOrigin() {
	const fromEnv = (process.env.VUE_APP_BASE_API_URL || "").trim().replace(/\/$/, "");
	if (fromEnv) {
		return fromEnv;
	}
	if (typeof window !== "undefined" && window.location?.origin) {
		return window.location.origin.replace(/\/$/, "");
	}
	return "";
}
