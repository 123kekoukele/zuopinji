import axios from 'axios'
import router from '../router/index'
import toolUtil from '@/utils/toolUtil'
import { ElMessage } from 'element-plus'

/** 接口 msg 可能是对象/空，直接传给 ElMessage 会显示 [object Object] */
function apiMsg(data, fallback = '请求失败') {
	const m = data && data.msg
	if (m == null || m === '') return fallback
	if (typeof m === 'string') return m
	return fallback
}

/**
 * 生产：axios 使用相对路径 /hadluo-xt，与页面同源，由 Nginx 反代到 Spring Boot。
 * 开发：仍为 /hadluo-xt，走 vue.config.js 代理到本地后端。
 */
function resolveApiBaseURL() {
	return process.env.VUE_APP_BASE_API || '/hadluo-xt'
}

const http = axios.create({
    timeout: 1000 * 86400,
    withCredentials: true,
    baseURL: resolveApiBaseURL(),
    headers: {
        'Content-Type': 'application/json; charset=utf-8'
    }
})
// 请求拦截
http.interceptors.request.use(config => {
    config.headers['Token'] = toolUtil.storageGet('Token') // 请求头带上token
    return config
}, error => {
    return Promise.reject(error)
})
// 响应拦截
http.interceptors.response.use(response => {
    const respType = response.config?.responseType
    if (respType === 'blob' || respType === 'arraybuffer') {
        return response
    }
    if (response.data && response.data.code === 401) { // 401, token失效
		toolUtil.storageClear()
		ElMessage.error(apiMsg(response.data, '登录已过期，请重新登录'))
        router.push('/login')
        // 避免因未捕获的 Promise 拒绝导致开发环境 overlay 报错
        return new Promise(() => {})
    }
	else if(response.data && response.data.code === 0){
		return response
	}else{
		ElMessage.error(apiMsg(response.data))
		return Promise.reject(response)
	}
    
}, error => {
    // HTTP 层面的 401（例如 Session 过期，后端直接返回 401）
    if (error && error.response && error.response.status === 401) {
        toolUtil.storageClear()
        ElMessage.error('登录已过期，请重新登录')
        router.push('/login')
        // 不再向上抛出，避免 webpack overlay 报未捕获错误
        return new Promise(() => {})
    }
    // 忽略已取消的请求，避免未捕获的 Promise 拒绝导致 overlay 报错
    if ((axios.isCancel && axios.isCancel(error)) || error?.message === 'cancel') {
        return new Promise(() => {})
    }
    return Promise.reject(error)
})
export default http
