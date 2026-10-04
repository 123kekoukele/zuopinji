import config from './config'

/** 将数据库存储的 file/xxx 路径转为可访问的完整 URL */
export function resolveFileUrl(filePath) {
	if (!filePath) return ''
	if (filePath.startsWith('http://') || filePath.startsWith('https://')) {
		return filePath
	}
	const base = config.get().url || ''
	const path = filePath.startsWith('/') ? filePath.slice(1) : filePath
	return base + path
}
