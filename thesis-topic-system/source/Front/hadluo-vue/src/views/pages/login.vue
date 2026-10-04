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
					<h1 class="system-title">地信专业毕业论文选题系统</h1>
					<p class="system-subtitle">Student Thesis Selection System</p>
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
							autocomplete="username"
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
							autocomplete="current-password"
						/>
					</div>

					<!-- 验证码 -->
					<div class="form-item captcha-item" v-if="loginType==1">
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

					<!-- 角色选择 -->
					<div class="form-item" v-if="userList.length>1">
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

					<!-- 记住密码 -->
					<div class="remember-view" v-if="loginType==1">
						<el-checkbox v-model="rememberPassword" label="记住密码" />
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
						<div class="register-btns">
							<button type="button" class="btn-register" @click="handleRegister('xuesheng')">
								注册学生
							</button>
							<button type="button" class="btn-register" @click="handleRegister('jiaoshi')">
								注册教师
							</button>
						</div>
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
					<el-input v-model="changePwdForm.username" placeholder="请输入账号（学号/工号）" />
				</el-form-item>
				<el-form-item v-if="userList.length > 1" label="用户类型">
					<el-select v-model="changePwdForm.role" placeholder="请选择用户类型">
						<el-option
							v-for="(item, index) in userList"
							:key="index"
							:label="item.roleName"
							:value="item.roleName"
						/>
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
	import menu from '@/utils/menu'

	const userList = ref([])
	const menus = ref([])
	const loginForm = ref({ role: '', username: '', password: '' })
	const captchaText = ref('')
	const captchaInput = ref('')
	const changePwdVisible = ref(false)
	const changePwdForm = ref({
		role: '', username: '', oldPassword: '', newPassword: '', confirmPassword: ''
	})
	const tableName = ref('')
	const loginType = ref(1)
	const rememberPassword = ref(true)
	const context = getCurrentInstance()?.appContext.config.globalProperties;

	const handleRegister = (name) => {
		context?.$router.push(`/${name}Register`);
	};

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
		if (userList.value.length > 1) {
			if (!loginForm.value.role) {
				context?.$toolUtil.message('请选择角色', 'error');
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
		} else {
			context?.$toolUtil.message('未获取到登录角色配置，请刷新页面或联系管理员', 'error');
			return;
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
					return menus.value[i].tableName
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
			if (rememberPassword.value) {
				const loginForm1 = JSON.parse(JSON.stringify(loginForm.value));
				delete loginForm1.code;
				context?.$toolUtil.storageSet("frontLoginForm", JSON.stringify(loginForm1));
			} else {
				context?.$toolUtil.storageRemove("frontLoginForm");
			}
			context?.$toolUtil.storageSet("frontToken", res.data.token);
			context?.$toolUtil.storageSet("frontRole", loginForm.value.role);
			context?.$toolUtil.storageSet("frontSessionTable", tableName.value);
			const path = context?.$toolUtil.storageGet('toPath');
			if (path) {
				context?.$router.push(path);
				context?.$toolUtil.storageRemove('toPath');
				return;
			}
			context?.$router.push('/index/home');
			context?.$toolUtil.message('登录成功', 'success');
		}).catch(err => {
			// 登录接口返回的业务错误（如账号密码错误），直接显示后端消息
			// 不显示"请先登录"这种误导性的错误提示
			if (err?.isLoginError) {
				context?.$toolUtil.message(err.message || '登录失败，请检查账号密码', 'error');
			} else if (err?.response?.data?.msg) {
				context?.$toolUtil.message(err.response.data.msg, 'error');
			} else if (err?.message && err.message !== 'cancel') {
				context?.$toolUtil.message(err.message || '账号或密码错误，请重试', 'error');
			}
		});
	}

	const fetchMenusFromServer = () => {
		return context?.$http({
			url: 'menu/list',
			method: 'get',
			params: { page: 1, limit: 1, sort: 'id' }
		}).then(res => {
			const list = res?.data?.data?.list
			if (list && list.length > 0 && list[0].menujson != null) {
				context?.$toolUtil.storageSet('menus', list[0].menujson)
			}
		}).catch(() => {})
	}

	const getMenu = () => {
		const arr = menu.list()
		if (!arr) {
			menus.value = []
		} else if (Array.isArray(arr)) {
			menus.value = arr
		} else {
			menus.value = [arr]
		}
		userList.value = []
		for (let i = 0; i < menus.value.length; i++) {
			if (menus.value[i].hasFrontLogin == '是') {
				userList.value.push(menus.value[i])
			}
		}
	}

	const init = async () => {
		if (!context?.$toolUtil.storageGet('menus')) {
			await fetchMenusFromServer()
		}
		getMenu()
		let form = context?.$toolUtil.storageGet('frontLoginForm')
		if (form) {
			try {
				loginForm.value = JSON.parse(form)
			} catch (e) {
				loginForm.value = { role: '', username: '', password: '' }
			}
		} else if (userList.value.length > 0) {
			loginForm.value.role = userList.value[0].roleName
		}
		refreshCaptcha()
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
// 页面整体布局 - 居中对齐
// =============================================
.login-page {
	min-height: 100vh;
	width: 100%;
	display: flex;
	justify-content: center;
	align-items: center;
	background: linear-gradient(135deg, #061630 0%, #0a1a2e 50%, #061630 100%);
	position: relative;
	overflow: hidden;
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
// 登录容器 - 居中定位
// =============================================
.login-container {
	position: relative;
	z-index: 10;
	padding: 20px;
	width: 100%;
	max-width: 420px;
}

// =============================================
// 登录卡片 - 主容器
// =============================================
.login-card {
	width: 100%;
	background: linear-gradient(145deg, rgba(10, 30, 58, 0.95) 0%, rgba(6, 22, 48, 0.98) 100%);
	border: 1px solid rgba(34, 211, 238, 0.2);
	border-radius: 16px;
	padding: 36px 32px 28px;
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
	margin-bottom: 28px;
	padding-bottom: 24px;
	border-bottom: 1px solid rgba(34, 211, 238, 0.1);
}

.logo-icon {
	width: 60px;
	height: 60px;
	margin: 0 auto 14px;
	padding: 12px;
	background: linear-gradient(135deg, rgba(34, 211, 238, 0.15) 0%, rgba(8, 145, 178, 0.15) 100%);
	border: 1px solid rgba(34, 211, 238, 0.3);
	border-radius: 14px;
	color: #22d3ee;
	transition: all 0.3s;

	svg { width: 100%; height: 100%; }

	&:hover {
		border-color: rgba(34, 211, 238, 0.5);
		box-shadow: 0 0 20px rgba(34, 211, 238, 0.2);
	}
}

.system-title {
	font-size: 22px;
	font-weight: 600;
	color: #fff;
	margin: 0 0 8px;
	background: linear-gradient(135deg, #fff 0%, #22d3ee 100%);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
}

.system-subtitle {
	font-size: 12px;
	color: #64748b;
	margin: 0;
	letter-spacing: 2px;
	text-transform: uppercase;
}

// =============================================
// 表单容器
// =============================================
.login-form {
	display: flex;
	flex-direction: column;
}

// =============================================
// 表单项 - 统一间距
// =============================================
.form-item {
	display: flex;
	flex-direction: column;
	margin-bottom: 18px;

	&:last-child {
		margin-bottom: 0;
	}
}

.form-label {
	font-size: 13px;
	color: #94a3b8;
	font-weight: 500;
	margin-bottom: 8px;
	letter-spacing: 0.5px;
}

// =============================================
// 输入框 - 统一样式
// =============================================
.form-input {
	width: 100%;
	height: 44px;
	padding: 0 16px;
	background: rgba(5, 18, 36, 0.8);
	border: 1px solid rgba(34, 211, 238, 0.2);
	border-radius: 8px;
	color: #e2e8f0;
	font-size: 14px;
	box-sizing: border-box;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

	&::placeholder {
		color: #4a5568;
	}

	&:hover {
		border-color: rgba(34, 211, 238, 0.35);
		background: rgba(5, 18, 36, 0.9);
	}

	&:focus {
		outline: none;
		border-color: #22d3ee;
		background: rgba(5, 18, 36, 0.95);
		box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.12);
	}
}

// =============================================
// 验证码区域
// =============================================
.captcha-item {
	.form-label {
		margin-bottom: 8px;
	}
}

.captcha-wrapper {
	display: flex;
	gap: 12px;
	align-items: stretch;
}

.captcha-input {
	flex: 1;
}

.captcha-code {
	min-width: 100px;
	height: 44px;
	padding: 0 12px;
	background: linear-gradient(135deg, rgba(34, 211, 238, 0.12) 0%, rgba(8, 145, 178, 0.12) 100%);
	border: 1px solid rgba(34, 211, 238, 0.3);
	border-radius: 8px;
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
	transition: all 0.3s;

	&:hover {
		border-color: rgba(34, 211, 238, 0.5);
		background: linear-gradient(135deg, rgba(34, 211, 238, 0.18) 0%, rgba(8, 145, 178, 0.18) 100%);
	}

	span {
		font-size: 18px;
		font-weight: 700;
		font-family: 'Courier New', Consolas, monospace;
		letter-spacing: 6px;
		color: #22d3ee;
		text-shadow: 0 0 10px rgba(34, 211, 238, 0.5);
	}
}

// =============================================
// 用户类型下拉框
// =============================================
.role-select {
	width: 100%;

	:deep(.el-input__wrapper) {
		background: rgba(5, 18, 36, 0.8);
		border: 1px solid rgba(34, 211, 238, 0.2);
		border-radius: 8px;
		box-shadow: none !important;
		height: 44px;
		padding: 0 16px;
		transition: all 0.3s;

		&:hover {
			border-color: rgba(34, 211, 238, 0.35);
		}
	}

	:deep(.el-input__wrapper.is-focus) {
		border-color: #22d3ee;
		box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.12) !important;
	}

	:deep(.el-input__inner) {
		color: #e2e8f0;
		font-size: 14px;
		line-height: 44px;

		&::placeholder {
			color: #4a5568;
		}
	}

	:deep(.el-select__caret) {
		color: #64748b;
	}
}

// =============================================
// 记住密码
// =============================================
.remember-view {
	margin-bottom: 18px;

	:deep(.el-checkbox__label) {
		color: #64748b;
		font-size: 13px;
	}

	:deep(.is-checked .el-checkbox__label) {
		color: #22d3ee;
	}

	:deep(.is-checked .el-checkbox__inner) {
		background-color: #22d3ee;
		border-color: #22d3ee;
	}

	:deep(.el-checkbox__inner) {
		background-color: rgba(5, 18, 36, 0.8);
		border-color: rgba(34, 211, 238, 0.3);
	}
}

// =============================================
// 按钮区域 - 统一排列
// =============================================
.btn-area {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 12px;
	margin-top: 8px;
}

// 登录按钮
.btn-login {
	width: 100%;
	height: 46px;
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
	gap: 10px;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
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

// 注册按钮组
.register-btns {
	display: flex;
	gap: 12px;
	width: 100%;
}

.btn-register {
	flex: 1;
	height: 42px;
	background: transparent;
	border: 1px solid rgba(34, 211, 238, 0.35);
	border-radius: 8px;
	color: #22d3ee;
	font-size: 14px;
	font-weight: 500;
	cursor: pointer;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

	&:hover {
		background: rgba(34, 211, 238, 0.08);
		border-color: #22d3ee;
		transform: translateY(-1px);
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
	padding: 8px 16px;

	&:hover {
		color: #22d3ee;
	}
}

// =============================================
// 底部信息
// =============================================
.footer-info {
	margin-top: 28px;
	padding-top: 20px;
	border-top: 1px solid rgba(34, 211, 238, 0.08);
	text-align: center;
	font-size: 12px;
	color: #4a5568;
	display: flex;
	justify-content: center;
	align-items: center;
	gap: 10px;
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
