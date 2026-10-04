import http from '@/utils/http'
import { ElLoading, ElMessage } from 'element-plus'
import { normalizeStoredFilePath } from '@/utils/graduationYear'

let activeDownloadCount = 0
let activeLoadingInstance = null

function beginDownloadLoading() {
	activeDownloadCount += 1
	if (activeDownloadCount === 1) {
		activeLoadingInstance = ElLoading.service({
			lock: true,
			text: '正在下载中......',
			background: 'rgba(0, 0, 0, 0.35)',
		})
	}
}

function endDownloadLoading() {
	activeDownloadCount = Math.max(0, activeDownloadCount - 1)
	if (activeDownloadCount === 0 && activeLoadingInstance) {
		activeLoadingInstance.close()
		activeLoadingInstance = null
	}
}

/** 从存储路径解析下载展示文件名 */
export function normalizeFileName(filePath) {
	const normalized = normalizeStoredFilePath(filePath)
	if (!normalized) return ''
	const slash = normalized.lastIndexOf('/')
	const name = slash >= 0 ? normalized.slice(slash + 1) : normalized
	const underscore = name.indexOf('_')
	if (underscore > 0 && /^\d{10,}_/.test(name)) {
		return decodeURIComponentSafe(name.slice(underscore + 1))
	}
	return decodeURIComponentSafe(name)
}

function decodeURIComponentSafe(value) {
	try {
		return decodeURIComponent(value)
	} catch (_) {
		return value
	}
}

export function getMimeTypeByFileName(fileName) {
	const lower = (fileName || '').toLowerCase()
	if (lower.endsWith('.pdf')) return 'application/pdf'
	if (lower.endsWith('.doc')) return 'application/msword'
	if (lower.endsWith('.docx')) return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
	if (lower.endsWith('.ppt')) return 'application/vnd.ms-powerpoint'
	if (lower.endsWith('.pptx')) return 'application/vnd.openxmlformats-officedocument.presentationml.presentation'
	if (lower.endsWith('.jpg') || lower.endsWith('.jpeg')) return 'image/jpeg'
	if (lower.endsWith('.png')) return 'image/png'
	return 'application/octet-stream'
}

function buildFileAccessUrl(filePath) {
	const storedPath = normalizeStoredFilePath(filePath)
	if (!storedPath) return ''
	return `/file/${storedPath.split('/').map(encodeURIComponent).join('/')}`
}

function buildDownloadApiUrl(filePath) {
	const storedPath = normalizeStoredFilePath(filePath)
	if (!storedPath) return ''
	return `/file/download?fileName=${encodeURIComponent(storedPath)}`
}

function triggerBlobSave(data, fileName, contentType) {
	const mime = contentType && !contentType.includes('text/html') && !contentType.includes('application/json')
		? contentType.split(';')[0]
		: getMimeTypeByFileName(fileName)
	const blob = data instanceof Blob ? data : new Blob([data], { type: mime })
	if (blob.type?.includes('text/html') || blob.type?.includes('application/json')) {
		return false
	}
	const objectUrl = window.URL.createObjectURL(blob)
	const a = document.createElement('a')
	a.href = objectUrl
	a.download = fileName
	a.dispatchEvent(new MouseEvent('click', { bubbles: true, cancelable: true, view: window }))
	window.URL.revokeObjectURL(objectUrl)
	return true
}

/** 下载存储文件，保留原始文件名与扩展名 */
export async function downloadStoredFile(filePath) {
	const fileName = normalizeFileName(filePath)
	if (!fileName) {
		throw new Error('文件不存在')
	}
	if (activeDownloadCount > 0) {
		ElMessage({
			message: '正在下载中，请稍候……',
			type: 'info',
			duration: 2000,
		})
		return
	}
	beginDownloadLoading()
	try {
		const urls = [
			buildDownloadApiUrl(filePath),
			buildFileAccessUrl(filePath),
		]
		for (const url of urls) {
			const { data, status, headers } = await http.get(url, {
				responseType: 'blob',
				validateStatus: () => true,
			})
			if (status >= 400 || !data) continue
			if (triggerBlobSave(data, fileName, headers?.['content-type'])) {
				return
			}
		}
		throw new Error('文件不存在或已被删除')
	} finally {
		endDownloadLoading()
	}
}
