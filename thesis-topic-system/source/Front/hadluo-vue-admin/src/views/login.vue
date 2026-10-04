<template>
	<div class="login-page">
		<!-- 背景装饰 -->
		<div class="bg-decoration">
			<div class="grid-bg"></div>
			<div class="glow-orb orb-1"></div>
			<div class="glow-orb orb-2"></div>
		</div>

		<!-- 登录卡片 -->
		<div class="login-container">
			<div class="login-card">
				<!-- 顶部装饰 -->
				<div class="card-header">
					<div class="logo-icon">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
							<path d="M12 2L2 7l10 5 10-5-10-5z"/>
							<path d="M2 17l10 5 10-5"/>
							<path d="M2 12l10 5 10-5"/>
						</svg>
					</div>
					<h1 class="system-title">地空院毕业论文选题系统</h1>
					<p class="system-subtitle">Graduate Thesis Selection System</p>
				</div>

				<!-- 表单区域 -->
				<el-form :model="loginForm" class="login-form" @keydown.enter.native="handleLogin">
					<!-- 用户名 -->
					<div class="form-item" v-if="loginType==1">
						<label class="form-label">账号</label>
						<input 
							class="form-input" 
							v-model="loginForm.username" 
							placeholder="请输入账号"
						/>
					</div>

					<!-- 密码 -->
					<div class="form-item" v-if="loginType==1">
						<label class="form-label">密码</label>
						<input 
							class="form-input" 
							v-model="loginForm.password" 
							type="password" 
							placeholder="请输入密码"
						/>
					</div>

					<!-- 验证码 -->
					<div class="form-item" v-if="loginType==1">
						<label class="form-label">验证码</label>
						<div class="captcha-wrapper">
							<input 
								class="form-input captcha-input" 
								v-model="captchaInput" 
								placeholder="请输入验证码"
							/>
							<div class="captcha-code" @click="refreshCaptcha">
								<span v-for="(char, i) in captchaText.split('')" :key="i">
									{{ char }}
								</span>
							</div>
						</div>
					</div>

					<!-- 加载提示 -->
					<div class="loading-tip" v-if="menuLoading">
						<span class="loading-dot"></span>
						<span>正在加载登录选项...</span>
					</div>

					<!-- 角色选择 -->
					<div class="form-item" v-if="!menuLoading && userList.length > 1">
						<label class="form-label">用户类型</label>
						<el-select v-model="loginForm.role" placeholder="请选择用户类型" class="role-select">
							<el-option
								v-for="(item, index) in userList"
								:key="index"
								:label="item.roleName"
								:value="item.roleName"
							/>
						</el-select>
					</div>

					<!-- 按钮区域 -->
					<div class="btn-area">
						<button type="button" class="btn-login" v-if="loginType==1" @click="handleLogin">
							<span>登 录</span>
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
								<path d="M5 12h14"/>
								<path d="M12 5l7 7-7 7"/>
							</svg>
						</button>
						<button type="button" class="btn-register" @click="handleRegister('jiaoshi')">
							注册教师
						</button>
						<span class="aux-link" @click="openChangePwd">修改密码</span>
					</div>
				</el-form>

				<!-- 底部信息 -->
				<div class="footer-info">
					<span>地理与空间信息学院</span>
					<span class="separator">|</span>
					<span>毕业设计选题管理系统</span>
				</div>
			</div>
		</div>

		<!-- 修改密码弹窗 -->
		<el-dialog v-model="changePwdVisible" title="修改密码" width="420px" destroy-on-close class="tech-dialog">
			<el-form :model="changePwdForm" label-width="90px">
				<el-form-item label="账号">
					<el-input v-model="changePwdForm.username" placeholder="请输入账号（管理员账号/教师工号）" />
				</el-form-item>
				<el-form-item v-if="userList.length > 1" label="用户类型">
					<el-select v-model="changePwdForm.role" placeholder="请选择用户类型">
						<el-option v-for="(item, index) in userList" :key="index" :label="item.roleName" :value="item.roleName" />
					</el-select>
				</el-form-item>
				<el-form-item label="原密码">
					<el-input v-model="changePwdForm.oldPassword" type="password" placeholder="请输入原密码" />
				</el-form-item>
				<el-form-item label="新密码">
					<el-input v-model="changePwdForm.newPassword" type="password" placeholder="请输入新密码" />
				</el-form-item>
				<el-form-item label="确认密码">
					<el-input v-model="changePwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
				</el-form-item>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button @click="changePwdVisible = false">取 消</el-button>
					<el-button type="primary" @click="handleChangePwd">确 定</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>
<script setup>
	import { ref, getCurrentInstance, onMounted } from "vue";
	import { useRouter } from "vue-router";
	const router = useRouter()
	const userList = ref([])
	const menus = ref([])
	const menuLoading = ref(true)
	const loginForm = ref({ role: '', username: '', password: '' })
	const captchaText = ref('')
	const captchaInput = ref('')
	const changePwdVisible = ref(false)
	const changePwdForm = ref({
		role: '', username: '', oldPassword: '', newPassword: '', confirmPassword: ''
	})
	const tableName = ref('')
	const loginType = ref(1)
	const context = getCurrentInstance()?.appContext.config.globalProperties;

	const handleRegister = (registerTable) => {
		router.push(`/${registerTable}Register`)
	}

	const handleLogin = () => {
		if (!loginForm.value.username) {
			context?.$toolUtil.message('请输入用户名', 'error')
			return;
		}
		if (!loginForm.value.password) {
			context?.$toolUtil.message('请输入密码', 'error')
			return;
		}
		if (!captchaInput.value) {
			context?.$toolUtil.message('请输入验证码', 'error')
			return;
		}
		if (captchaInput.value.toLowerCase() !== captchaText.value.toLowerCase()) {
			context?.$toolUtil.message('验证码不正确', 'error')
			refreshCaptcha()
			return;
		}
		if (menuLoading.value) {
			context?.$toolUtil.message('登录选项加载中，请稍候', 'warning')
			return
		}
		if (userList.value.length === 0) {
			context?.$toolUtil.message('未获取到可登录角色，请刷新页面或检查后台菜单配置', 'error')
			return
		}
		if (userList.value.length > 1) {
			if (!loginForm.value.role) {
				context?.$toolUtil.message('请选择角色', 'error')
				return;
			}
			for (let i = 0; i < menus.value.length; i++) {
				if (menus.value[i].roleName == loginForm.value.role) {
					tableName.value = menus.value[i].tableName;
				}
			}
		} else if (userList.value.length === 1) {
			tableName.value = userList.value[0].tableName;
			loginForm.value.role = userList.value[0].roleName;
		}
		login()
	}

	const resolveTableNameForChange = () => {
		if (userList.value.length > 1) {
			if (!changePwdForm.value.role) {
				context?.$toolUtil.message('请选择用户类型', 'error')
				return null
			}
			for (let i = 0; i < menus.value.length; i++) {
				if (menus.value[i].roleName == changePwdForm.value.role) {
					return menus.value[i].tableName;
				}
			}
			return null
		} else if (userList.value.length === 1) {
			changePwdForm.value.role = userList.value[0].roleName
			return userList.value[0].tableName
		}
		return null
	}

	const openChangePwd = () => {
		changePwdForm.value = {
			role: loginForm.value.role || '',
			username: loginForm.value.username || '',
			oldPassword: '', newPassword: '', confirmPassword: ''
		}
		changePwdVisible.value = true
	}

	const handleChangePwd = () => {
		if (!changePwdForm.value.username) {
			context?.$toolUtil.message('请输入账号', 'error')
			return
		}
		if (!changePwdForm.value.oldPassword) {
			context?.$toolUtil.message('请输入原密码', 'error')
			return
		}
		if (!changePwdForm.value.newPassword) {
			context?.$toolUtil.message('请输入新密码', 'error')
			return
		}
		if (changePwdForm.value.newPassword !== changePwdForm.value.confirmPassword) {
			context?.$toolUtil.message('两次输入的新密码不一致', 'error')
			return
		}
		const tn = resolveTableNameForChange()
		if (!tn) return
		context?.$http({
			url: `${tn}/changePassword`,
			method: 'post',
			params: {
				username: changePwdForm.value.username,
				oldPassword: changePwdForm.value.oldPassword,
				newPassword: changePwdForm.value.newPassword
			}
		}).then(res => {
			context?.$toolUtil.message(res.data.msg || '密码修改成功', 'success')
			changePwdVisible.value = false
		}).catch(err => {
			const msg = err?.response?.data?.msg || '密码修改失败'
			context?.$toolUtil.message(msg, 'error')
		})
	}

	const login = () => {
		context?.$http({
			url: `${tableName.value}/login?username=${loginForm.value.username}&password=${loginForm.value.password}`,
			method: 'post'
		}).then(res => {
			context?.$toolUtil.storageSet("Token", res.data.token);
			context?.$toolUtil.storageSet("role", loginForm.value.role);
			context?.$toolUtil.storageSet("sessionTable", tableName.value);
			context?.$toolUtil.storageSet("adminName", loginForm.value.username);
			context?.$router.push('/')
		}, err => {})
	}

	const parseMenuJson = (raw) => {
		if (raw == null) return []
		if (Array.isArray(raw)) return raw
		if (typeof raw === 'string') {
			try {
				const v = JSON.parse(raw)
				return Array.isArray(v) ? v : []
			} catch (e) { return [] }
		}
		return []
	}

	const isBackLogin = (row) => {
		const v = row && row.hasBackLogin
		return v === '是' || v === true || v === 1 || v === '1'
	}

	const getMenu = () => {
		menuLoading.value = true
		userList.value = []
		const params = { page: 1, limit: 1, sort: 'id' }
		context?.$http({
			url: 'menu/list',
			method: 'get',
			params
		}).then(res => {
			const row = res?.data?.data?.list?.[0]
			if (!row) {
				context?.$toolUtil.message('未读取到菜单配置，请联系管理员检查 menu 表数据', 'error')
				return
			}
			menus.value = parseMenuJson(row.menujson)
			for (let i = 0; i < menus.value.length; i++) {
				if (isBackLogin(menus.value[i])) {
					userList.value.push(menus.value[i])
				}
			}
			if (userList.value.length > 0) {
				loginForm.value.role = userList.value[0].roleName
			}
			context?.$toolUtil.storageSet('menus', JSON.stringify(menus.value))
		}).catch(() => {}).finally(() => {
			menuLoading.value = false
		})
	}

	const init = () => {
		localStorage.removeItem('Token')
		getMenu();
		refreshCaptcha();
	}

	const refreshCaptcha = () => {
		const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
		let s = ''
		for (let i = 0; i < 4; i++) {
			s += chars.charAt(Math.floor(Math.random() * chars.length))
		}
		captchaText.value = s
		captchaInput.value = ''
	}

	onMounted(() => { init() })
</script>

<style lang="scss" scoped>
// =============================================
// 页面整体布局 - 强制居中
// =============================================
* {
	box-sizing: border-box;
}

.login-page {
	min-height: 100vh;
	width: 100%;
	display: flex;
	justify-content: center;
	align-items: center;
	background: linear-gradient(135deg, #061630 0%, #0a1a2e 50%, #061630 100%);
	position: relative;
	overflow: hidden;
	padding: 20px;
}

// =============================================
// 背景装饰
// =============================================
.bg-decoration {
	position: absolute;
	inset: 0;
	pointer-events: none;
	z-index: 1;

	.grid-bg {
		position: absolute;
		inset: 0;
		background-image:
			linear-gradient(rgba(34, 211, 238, 0.03) 1px, transparent 1px),
			linear-gradient(90deg, rgba(34, 211, 238, 0.03) 1px, transparent 1px);
		background-size: 50px 50px;
	}

	.glow-orb {
		position: absolute;
		border-radius: 50%;
		filter: blur(100px);
	}

	.orb-1 {
		width: 500px;
		height: 500px;
		background: radial-gradient(circle, rgba(34, 211, 238, 0.15) 0%, transparent 70%);
		top: -150px;
		left: -150px;
		animation: float 10s ease-in-out infinite;
	}

	.orb-2 {
		width: 400px;
		height: 400px;
		background: radial-gradient(circle, rgba(8, 145, 178, 0.15) 0%, transparent 70%);
		bottom: -100px;
		right: -100px;
		animation: float 10s ease-in-out infinite reverse;
		animation-delay: -3s;
	}
}

@keyframes float {
	0%, 100% { transform: translate(0, 0); }
	50% { transform: translate(30px, -30px); }
}

// =============================================
// 登录容器 - 固定宽度居中
// =============================================
.login-container {
	position: relative;
	z-index: 10;
	width: 100%;
	max-width: 400px;
}

// =============================================
// 登录卡片 - 主容器
// =============================================
.login-card {
	width: 100%;
	background: linear-gradient(145deg, rgba(10, 30, 58, 0.95) 0%, rgba(6, 22, 48, 0.98) 100%);
	border: 1px solid rgba(34, 211, 238, 0.2);
	border-radius: 16px;
	padding: 32px 28px;
	box-shadow:
		0 0 40px rgba(34, 211, 238, 0.08),
		0 8px 32px rgba(0, 0, 0, 0.4),
		0 2px 8px rgba(0, 0, 0, 0.2),
		inset 0 1px 0 rgba(255, 255, 255, 0.03);
	backdrop-filter: blur(20px);
	animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
	from { opacity: 0; transform: translateY(30px); }
	to { opacity: 1; transform: translateY(0); }
}

// =============================================
// 卡片头部
// =============================================
.card-header {
	text-align: center;
	margin-bottom: 24px;
	padding-bottom: 20px;
	border-bottom: 1px solid rgba(34, 211, 238, 0.1);
}

.logo-icon {
	width: 56px;
	height: 56px;
	margin: 0 auto 12px;
	padding: 10px;
	background: linear-gradient(135deg, rgba(34, 211, 238, 0.15) 0%, rgba(8, 145, 178, 0.15) 100%);
	border: 1px solid rgba(34, 211, 238, 0.3);
	border-radius: 12px;
	color: #22d3ee;
	transition: all 0.3s;

	svg { width: 100%; height: 100%; }

	&:hover {
		border-color: rgba(34, 211, 238, 0.5);
		box-shadow: 0 0 20px rgba(34, 211, 238, 0.2);
	}
}

.system-title {
	font-size: 20px;
	font-weight: 600;
	color: #fff;
	margin: 0 0 6px;
	background: linear-gradient(135deg, #fff 0%, #22d3ee 100%);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
}

.system-subtitle {
	font-size: 11px;
	color: #64748b;
	margin: 0;
	letter-spacing: 1.5px;
	text-transform: uppercase;
}

// =============================================
// 表单容器
// =============================================
.login-form {
	width: 100%;
}

// =============================================
// 表单项 - 统一间距
// =============================================
.form-item {
	width: 100%;
	margin-bottom: 16px;
}

.form-label {
	display: block;
	font-size: 13px;
	color: #94a3b8;
	font-weight: 500;
	margin-bottom: 8px;
}

// =============================================
// 输入框 - 统一样式（科技蓝风格）
// =============================================
.form-input {
	width: 100%;
	height: 42px;
	padding: 0 14px;
	background-color: rgba(15, 32, 60, 0.9) !important;
	border: 1px solid rgba(34, 211, 238, 0.25) !important;
	border-radius: 8px;
	color: #e0f2fe !important;
	font-size: 14px;
	transition: all 0.3s;
	outline: none;
	-webkit-appearance: none;
	-moz-appearance: none;
	appearance: none;

	&::placeholder {
		color: #64748b !important;
	}

	&:-webkit-autofill {
		-webkit-box-shadow: 0 0 0 1000px rgba(15, 32, 60, 0.95) inset !important;
		-webkit-text-fill-color: #e0f2fe !important;
		caret-color: #e0f2fe !important;
		transition: background-color 5000s ease-in-out 0s !important;
	}

	&:-webkit-autofill:hover {
		-webkit-box-shadow: 0 0 0 1000px rgba(15, 32, 60, 0.95) inset !important;
		-webkit-text-fill-color: #e0f2fe !important;
		caret-color: #e0f2fe !important;
		transition: background-color 5000s ease-in-out 0s !important;
	}

	&:-webkit-autofill:focus {
		-webkit-box-shadow: 0 0 0 1000px rgba(15, 32, 60, 0.95) inset !important;
		-webkit-text-fill-color: #e0f2fe !important;
		caret-color: #e0f2fe !important;
		transition: background-color 5000s ease-in-out 0s !important;
	}

	&:-webkit-autofill:active {
		-webkit-box-shadow: 0 0 0 1000px rgba(15, 32, 60, 0.95) inset !important;
		-webkit-text-fill-color: #e0f2fe !important;
		caret-color: #e0f2fe !important;
		transition: background-color 5000s ease-in-out 0s !important;
	}

	&:hover {
		border-color: rgba(34, 211, 238, 0.45) !important;
		background-color: rgba(15, 32, 60, 0.95) !important;
	}

	&:focus {
		border-color: #22d3ee !important;
		background-color: rgba(10, 25, 50, 0.95) !important;
		box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.15), 0 0 15px rgba(34, 211, 238, 0.1);
		outline: none;
	}
}

// =============================================
// 验证码区域 - 修正对齐
// =============================================
.captcha-wrapper {
	width: 100%;
	display: flex;
	gap: 10px;
}

.captcha-input {
	flex: 1;
	height: 42px;
	padding: 0 14px;
	background: rgba(15, 32, 60, 0.9);
	border: 1px solid rgba(34, 211, 238, 0.25);
	border-radius: 8px;
	color: #e0f2fe;
	font-size: 14px;
	transition: all 0.3s;
	outline: none;

	&::placeholder {
		color: #64748b;
	}

	&:-webkit-autofill,
	&:-webkit-autofill:hover,
	&:-webkit-autofill:focus,
	&:-webkit-autofill:active {
		-webkit-box-shadow: 0 0 0 1000px rgba(15, 32, 60, 0.95) inset !important;
		-webkit-text-fill-color: #e0f2fe !important;
		caret-color: #e0f2fe !important;
	}

	&:hover {
		border-color: rgba(34, 211, 238, 0.45);
		background: rgba(15, 32, 60, 0.95);
	}

	&:focus {
		border-color: #22d3ee;
		background: rgba(10, 25, 50, 0.95);
		box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.15), 0 0 15px rgba(34, 211, 238, 0.1);
	}
}

.captcha-code {
	flex-shrink: 0;
	width: 100px;
	height: 42px;
	display: flex;
	align-items: center;
	justify-content: center;
	background: linear-gradient(135deg, rgba(34, 211, 238, 0.15) 0%, rgba(8, 145, 178, 0.15) 100%);
	border: 1px solid rgba(34, 211, 238, 0.35);
	border-radius: 8px;
	cursor: pointer;
	transition: all 0.3s;
	outline: none;

	&:hover {
		border-color: rgba(34, 211, 238, 0.6);
		background: linear-gradient(135deg, rgba(34, 211, 238, 0.2) 0%, rgba(8, 145, 178, 0.2) 100%);
	}

	&:focus,
	&:focus-visible {
		outline: none !important;
		border-color: rgba(34, 211, 238, 0.6);
	}

	span {
		font-size: 18px;
		font-weight: 700;
		font-family: 'Courier New', Consolas, monospace;
		letter-spacing: 5px;
		color: #22d3ee;
		text-shadow: 0 0 10px rgba(34, 211, 238, 0.6), 0 0 20px rgba(34, 211, 238, 0.3);
	}
}

// =============================================
// 加载提示
// =============================================
.loading-tip {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 10px;
	padding: 14px;
	margin-bottom: 16px;
	color: #64748b;
	font-size: 13px;
}

.loading-dot {
	width: 8px;
	height: 8px;
	background: #22d3ee;
	border-radius: 50%;
	animation: loadingPulse 1.2s ease-in-out infinite;
}

@keyframes loadingPulse {
	0%, 100% { opacity: 0.4; transform: scale(0.8); }
	50% { opacity: 1; transform: scale(1.2); }
}

// =============================================
// 用户类型下拉框 - 统一高度
// =============================================
.role-select {
	width: 100%;

	:deep(.el-input__wrapper) {
		background: rgba(15, 32, 60, 0.9) !important;
		border: 1px solid rgba(34, 211, 238, 0.25) !important;
		border-radius: 8px !important;
		box-shadow: none !important;
		height: 42px !important;
		padding: 0 14px !important;
		transition: all 0.3s;
		outline: none !important;

		&:hover {
			border-color: rgba(34, 211, 238, 0.45) !important;
		}
	}

	:deep(.el-input__wrapper.is-focus) {
		border-color: #22d3ee !important;
		box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.15), 0 0 15px rgba(34, 211, 238, 0.1) !important;
		outline: none !important;
	}

	:deep(.el-input__inner) {
		color: #e0f2fe !important;
		font-size: 14px !important;
		line-height: 42px !important;
		height: 42px !important;

		&::placeholder {
			color: #64748b;
		}
	}

	:deep(.el-select__caret) {
		color: #64748b !important;
	}
}

// =============================================
// 按钮区域 - 垂直排列居中
// =============================================
.btn-area {
	width: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 12px;
	margin-top: 20px;
}

// 登录按钮
.btn-login {
	width: 100%;
	height: 44px;
	background: linear-gradient(135deg, #22d3ee 0%, #0891b2 100%);
	border: none;
	border-radius: 8px;
	color: #fff;
	font-size: 15px;
	font-weight: 600;
	cursor: pointer;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 8px;
	transition: all 0.3s;
	box-shadow: 0 4px 15px rgba(34, 211, 238, 0.25);

	svg {
		width: 18px;
		height: 18px;
		transition: transform 0.3s;
	}

	&:hover {
		transform: translateY(-2px);
		box-shadow: 0 6px 25px rgba(34, 211, 238, 0.35);

		svg {
			transform: translateX(3px);
		}
	}

	&:active {
		transform: translateY(0);
		box-shadow: 0 2px 10px rgba(34, 211, 238, 0.2);
	}
}

// 注册按钮
.btn-register {
	width: 100%;
	height: 40px;
	background: transparent;
	border: 1px solid rgba(34, 211, 238, 0.35);
	border-radius: 8px;
	color: #22d3ee;
	font-size: 14px;
	font-weight: 500;
	cursor: pointer;
	transition: all 0.3s;

	&:hover {
		background: rgba(34, 211, 238, 0.08);
		border-color: #22d3ee;
	}

	&:active {
		transform: translateY(0);
	}
}

// 修改密码链接
.aux-link {
	font-size: 13px;
	color: #64748b;
	cursor: pointer;
	transition: color 0.3s;
	padding: 6px 12px;

	&:hover {
		color: #22d3ee;
	}
}

// =============================================
// 底部信息
// =============================================
.footer-info {
	margin-top: 24px;
	padding-top: 16px;
	border-top: 1px solid rgba(34, 211, 238, 0.08);
	text-align: center;
	font-size: 12px;
	color: #4a5568;
	display: flex;
	justify-content: center;
	align-items: center;
	gap: 8px;
	letter-spacing: 0.5px;

	.separator {
		color: #2d3748;
	}
}

// =============================================
// 修改密码弹窗
// =============================================
.tech-dialog {
	:deep(.el-dialog) {
		background: linear-gradient(145deg, rgba(10, 30, 58, 0.98) 0%, rgba(6, 22, 48, 1) 100%);
		border: 1px solid rgba(34, 211, 238, 0.2);
		border-radius: 12px;
		box-shadow: 0 0 30px rgba(34, 211, 238, 0.1), 0 20px 40px rgba(0, 0, 0, 0.4);
	}

	:deep(.el-dialog__header) {
		border-bottom: 1px solid rgba(34, 211, 238, 0.12);
		padding: 18px 20px;
	}

	:deep(.el-dialog__title) {
		color: #22d3ee;
		font-weight: 600;
	}

	:deep(.el-dialog__body) {
		padding: 24px 20px;
	}

	:deep(.el-form-item__label) {
		color: #94a3b8;
		font-size: 13px;
	}

	:deep(.el-input__wrapper) {
		background: rgba(5, 18, 36, 0.8);
		border: 1px solid rgba(34, 211, 238, 0.2);
		border-radius: 6px;
		box-shadow: none !important;
		transition: all 0.3s;

		&:hover {
			border-color: rgba(34, 211, 238, 0.35);
		}
	}

	:deep(.el-input__wrapper.is-focus) {
		border-color: #22d3ee;
		box-shadow: 0 0 0 2px rgba(34, 211, 238, 0.12) !important;
	}

	:deep(.el-input__inner) {
		color: #e2e8f0;
		font-size: 14px;

		&::placeholder {
			color: #4a5568;
		}
	}

	:deep(.el-button--primary) {
		background: linear-gradient(135deg, #22d3ee 0%, #0891b2 100%);
		border-color: #22d3ee;
	}
}
</style>
