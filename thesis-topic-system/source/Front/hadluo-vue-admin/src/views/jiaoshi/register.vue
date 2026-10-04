<template>
	<div class="register-page">
		<div class="bg-decoration">
			<div class="grid-bg"></div>
			<div class="glow-orb orb-1"></div>
			<div class="glow-orb orb-2"></div>
		</div>

		<div class="register-container">
			<div class="register-card">
				<div class="card-header">
					<div class="logo-icon">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
							<path d="M12 2L2 7l10 5 10-5-10-5z"/>
							<path d="M2 17l10 5 10-5"/>
							<path d="M2 12l10 5 10-5"/>
						</svg>
					</div>
					<h1 class="system-title">教师账号注册</h1>
					<p class="system-subtitle">Teacher Registration · {{ projectName }}</p>
				</div>

				<el-form :model="registerForm" class="register-form" @keydown.enter.native="handleRegister">
					<div class="form-item">
						<label class="form-label">教师工号</label>
						<input
							class="form-input"
							v-model="registerForm.jiaoshigonghao"
							placeholder="请输入教师工号"
							type="text"
						/>
					</div>

					<div class="form-item">
						<label class="form-label">密码</label>
						<input
							class="form-input"
							v-model="registerForm.mima"
							placeholder="请输入密码"
							type="password"
						/>
					</div>

					<div class="form-item">
						<label class="form-label">确认密码</label>
						<input
							class="form-input"
							v-model="registerForm.mima2"
							placeholder="请再次输入密码"
							type="password"
						/>
					</div>

					<div class="form-item">
						<label class="form-label">教师姓名</label>
						<input
							class="form-input"
							v-model="registerForm.jiaoshixingming"
							placeholder="请输入教师姓名"
							type="text"
						/>
					</div>

					<div class="form-item">
						<label class="form-label">联系电话</label>
						<input
							class="form-input"
							v-model="registerForm.lianxidianhua"
							placeholder="请输入联系电话"
							type="text"
						/>
					</div>

					<div class="form-item">
						<label class="form-label">性别</label>
						<el-select v-model="registerForm.xingbie" placeholder="请选择性别" class="field-select">
							<el-option v-for="item in jiaoshixingbieLists" :key="item" :label="item" :value="item" />
						</el-select>
					</div>

					<div class="form-item">
						<label class="form-label">专业</label>
						<el-select v-model="registerForm.zhuanye" placeholder="请选择专业" class="field-select">
							<el-option v-for="item in zhuanyeLists" :key="item" :label="item" :value="item" />
						</el-select>
					</div>

					<div class="btn-area">
						<button type="button" class="btn-submit" @click="handleRegister">
							<span>注 册</span>
							<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
								<path d="M5 12h14"/>
								<path d="M12 5l7 7-7 7"/>
							</svg>
						</button>
						<span class="aux-link" @click="close">已有账号，立即登录</span>
					</div>
				</el-form>

				<div class="footer-info">
					<span>地理与空间信息学院</span>
					<span class="separator">|</span>
					<span>毕业设计选题管理系统</span>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'

const context = getCurrentInstance()?.appContext.config.globalProperties
const router = useRouter()
const projectName = context?.$project.projectName
const tableName = ref('jiaoshi')

const registerForm = ref({
	xingbie: '',
	zhuanye: '',
})
const jiaoshixingbieLists = ref([])
const zhuanyeLists = ref([])

const init = () => {
	jiaoshixingbieLists.value = '男,女'.split(',')
	zhuanyeLists.value = '地理信息科学,地理科学,风景园林,测绘工程,城乡规划'.split(',')
}

const handleRegister = () => {
	const url = `${tableName.value}/register`
	if (!registerForm.value.jiaoshigonghao) {
		context?.$toolUtil.message('教师工号不能为空', 'error')
		return
	}
	if (!registerForm.value.mima) {
		context?.$toolUtil.message('密码不能为空', 'error')
		return
	}
	if (registerForm.value.mima !== registerForm.value.mima2) {
		context?.$toolUtil.message('两次密码输入不一致', 'error')
		return
	}
	if (!registerForm.value.jiaoshixingming) {
		context?.$toolUtil.message('教师姓名不能为空', 'error')
		return
	}
	if (registerForm.value.lianxidianhua && !context?.$toolUtil.isMobile(registerForm.value.lianxidianhua)) {
		context?.$toolUtil.message('联系电话应输入手机格式', 'error')
		return
	}
	if (!registerForm.value.zhuanye) {
		context?.$toolUtil.message('请选择专业', 'error')
		return
	}

	context?.$http({
		url,
		method: 'post',
		data: registerForm.value,
	}).then(() => {
		context?.$toolUtil.message('注册成功', 'success', () => {
			router.push('/login')
		})
	})
}

const close = () => {
	router.push('/login')
}

init()
</script>

<style lang="scss" scoped>
* {
	box-sizing: border-box;
}

.register-page {
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

.register-container {
	position: relative;
	z-index: 10;
	width: 100%;
	max-width: 440px;
}

.register-card {
	width: 100%;
	background: linear-gradient(145deg, rgba(10, 30, 58, 0.95) 0%, rgba(6, 22, 48, 0.98) 100%);
	border: 1px solid rgba(34, 211, 238, 0.2);
	border-radius: 16px;
	padding: 28px 28px 24px;
	box-shadow:
		0 0 40px rgba(34, 211, 238, 0.08),
		0 8px 32px rgba(0, 0, 0, 0.4),
		inset 0 1px 0 rgba(255, 255, 255, 0.03);
	backdrop-filter: blur(20px);
	animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
	from { opacity: 0; transform: translateY(30px); }
	to { opacity: 1; transform: translateY(0); }
}

.card-header {
	text-align: center;
	margin-bottom: 20px;
	padding-bottom: 18px;
	border-bottom: 1px solid rgba(34, 211, 238, 0.1);
}

.logo-icon {
	width: 52px;
	height: 52px;
	margin: 0 auto 10px;
	padding: 10px;
	background: linear-gradient(135deg, rgba(34, 211, 238, 0.15) 0%, rgba(8, 145, 178, 0.15) 100%);
	border: 1px solid rgba(34, 211, 238, 0.3);
	border-radius: 12px;
	color: #22d3ee;

	svg { width: 100%; height: 100%; }
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
	letter-spacing: 1px;
}

.register-form {
	width: 100%;
}

.form-item {
	width: 100%;
	margin-bottom: 14px;
}

.form-label {
	display: block;
	font-size: 13px;
	color: #94a3b8;
	font-weight: 500;
	margin-bottom: 8px;
}

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

	&::placeholder {
		color: #64748b !important;
	}

	&:hover {
		border-color: rgba(34, 211, 238, 0.45) !important;
		background-color: rgba(15, 32, 60, 0.95) !important;
	}

	&:focus {
		border-color: #22d3ee !important;
		background-color: rgba(10, 25, 50, 0.95) !important;
		box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.15), 0 0 15px rgba(34, 211, 238, 0.1);
	}
}

.field-select {
	width: 100%;

	:deep(.el-input__wrapper) {
		background: rgba(15, 32, 60, 0.9) !important;
		border: 1px solid rgba(34, 211, 238, 0.25) !important;
		border-radius: 8px !important;
		box-shadow: none !important;
		height: 42px !important;
		padding: 0 14px !important;
		transition: all 0.3s;

		&:hover {
			border-color: rgba(34, 211, 238, 0.45) !important;
		}
	}

	:deep(.el-input__wrapper.is-focus) {
		border-color: #22d3ee !important;
		box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.15), 0 0 15px rgba(34, 211, 238, 0.1) !important;
	}

	:deep(.el-input__inner) {
		color: #e0f2fe !important;
		font-size: 14px !important;
		height: 42px !important;
		line-height: 42px !important;

		&::placeholder {
			color: #64748b;
		}
	}

	:deep(.el-select__caret) {
		color: #64748b !important;
	}
}

.btn-area {
	width: 100%;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 12px;
	margin-top: 18px;
}

.btn-submit {
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
	}
}

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

.footer-info {
	margin-top: 20px;
	padding-top: 14px;
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
</style>
