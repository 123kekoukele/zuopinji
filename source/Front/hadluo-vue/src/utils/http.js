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
    config.headers['Token'] = toolUtil.storageGet('frontToken') // 请求头带上token
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
    if (response.data && response.data.code === 401) {
    	// 区分登录接口的 401（账号密码错误）和其他接口的 401（token失效）
    	const url = response.config?.url || ''
    	const isLoginRequest = url.includes('/login') || url.includes('/xuesheng/login') || url.includes('/jiaoshi/login')
    	if (isLoginRequest) {
    		const msg = response.data.msg === '请先登录'
    			? '登录服务异常，请稍后重试或联系管理员'
    			: (response.data.msg || '账号或密码错误')
    		return Promise.reject({
    			isLoginError: true,
    			data: response.data,
    			message: msg
    		})
    	}
		// 非登录接口的 401 才视为 token 失效
		toolUtil.storageClear()
		ElMessage.error(apiMsg(response.data, '登录已过期，请重新登录'))
        router.push('/login')
        return new Promise(() => {})
    }
	else if(response.data && response.data.code === 0){
		return response
	}else{
		ElMessage.error(apiMsg(response.data))
		return Promise.reject(response)
	}
}, error => {
    if (error && error.response && error.response.status === 401) {
    	// 检查是否是登录接口
    	const url = error.config?.url || ''
    	const isLoginRequest = url.includes('/login') || url.includes('/xuesheng/login') || url.includes('/jiaoshi/login')
    	if (isLoginRequest) {
    		return Promise.reject({
    			isLoginError: true,
    			data: error.response?.data,
    			message: error.response?.data?.msg || '账号或密码错误'
    		})
    	}
        toolUtil.storageClear()
        ElMessage.error('登录已过期，请重新登录')
        router.push('/login')
        return new Promise(() => {})
    }
    // 忽略已取消的请求
    if ((axios.isCancel && axios.isCancel(error)) || error?.message === 'cancel') {
        return new Promise(() => {})
    }
    return Promise.reject(error)
})
export default http
