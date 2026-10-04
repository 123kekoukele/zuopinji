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
							<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
							<circle cx="9" cy="7" r="4"/>
							<path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
							<path d="M16 3.13a4 4 0 0 1 0 7.75"/>
						</svg>
					</div>
					<h1 class="system-title">学生账号注册</h1>
					<p class="system-subtitle">{{ projectName }}</p>
				</div>

				<el-form :model="registerForm" class="register-form" @keydown.enter="handleRegister">
					<div class="form-item">
						<label class="form-label">学号</label>
						<input
							class="form-input"
							v-model="registerForm.xuehao"
							placeholder="请输入学号"
							type="text"
							autocomplete="username"
						/>
					</div>
					<div class="form-item">
						<label class="form-label">密码</label>
						<input
							class="form-input"
							v-model="registerForm.mima"
							placeholder="请输入密码"
							type="password"
							autocomplete="new-password"
						/>
					</div>
					<div class="form-item">
						<label class="form-label">确认密码</label>
						<input
							class="form-input"
							v-model="registerForm.mima2"
							type="password"
							placeholder="请输入确认密码"
							autocomplete="new-password"
						/>
					</div>
					<div class="form-item">
						<label class="form-label">学生姓名</label>
						<input
							class="form-input"
							v-model="registerForm.xueshengxingming"
							placeholder="请输入学生姓名"
							type="text"
						/>
					</div>
					<div class="form-item">
						<label class="form-label">性别</label>
						<el-select
							class="form-select"
							v-model="registerForm.xingbie"
							placeholder="请选择性别"
						>
							<el-option v-for="item in xueshengxingbieLists" :key="item" :label="item" :value="item" />
						</el-select>
					</div>
					<div class="form-item">
						<label class="form-label">手机号码</label>
						<input
							class="form-input"
							v-model="registerForm.shoujihaoma"
							placeholder="请输入手机号码"
							type="text"
						/>
					</div>
					<div class="form-item">
						<label class="form-label">年级</label>
						<el-select
							class="form-select"
							v-model="registerForm.nianji"
							placeholder="请选择年级"
						>
							<el-option v-for="item in nianjiOptions" :key="item.value" :label="item.label" :value="item.value" />
						</el-select>
					</div>
					<div class="form-item">
						<label class="form-label">毕业届别</label>
						<el-select
							class="form-select"
							v-model="registerForm.biyejie"
							placeholder="请选择毕业届别"
						>
							<el-option v-for="item in biyejieOptions" :key="item.value" :label="item.label" :value="item.value" />
						</el-select>
					</div>
					<div class="form-item">
						<label class="form-label">专业</label>
						<el-select
							class="form-select"
							v-model="registerForm.zhuanye"
							placeholder="请选择专业"
						>
							<el-option v-for="item in majorOptions" :key="item" :label="item" :value="item" />
						</el-select>
					</div>
					<div class="form-item">
						<div class="form-label-row">
							<label class="form-label">班级</label>
							<button type="button" class="btn-add-banji" @click="addBanjiOption">+ 增加班级号</button>
						</div>
						<el-select
							class="form-select"
							v-model="registerForm.banji"
							placeholder="请选择班级"
							filterable
							allow-create
							default-first-option
						>
							<el-option v-for="item in banjiOptions" :key="item" :label="item" :value="item" />
						</el-select>
					</div>

					<div class="btn-area">
						<button type="button" class="btn-register" @click="handleRegister">
							<span>注 册</span>
						</button>
						<span class="aux-link" @click="close">已有账号，直接登录</span>
					</div>
				</el-form>
			</div>
		</div>
	</div>
</template>
<script setup>
	import {
		ref,
		getCurrentInstance,
		watch,
	} from 'vue';
	import { majorOptions } from '@/utils/majorOptions';
	import { buildGraduationYearOptions, buildEnrollmentGradeOptions } from '@/utils/graduationYear';
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const projectName = context?.$project.projectName
	const tableName = ref('xuesheng')
	const biyejieOptions = ref(buildGraduationYearOptions(2024, 7))
	const nianjiOptions = ref(buildEnrollmentGradeOptions(2018, 10))

	const registerForm = ref({
        xingbie: '',
		banji: '',
	})
	const xueshengxingbieLists = ref([])
	const banjiOptions = ref(['1', '2', '3', '4'])

	const mergeBanjiOptions = (values) => {
		const merged = new Set(banjiOptions.value)
		;(values || []).forEach((item) => {
			const text = String(item || '').trim()
			if (text) merged.add(text)
		})
		banjiOptions.value = Array.from(merged).sort((a, b) => {
			const na = Number(a)
			const nb = Number(b)
			if (!Number.isNaN(na) && !Number.isNaN(nb)) return na - nb
			return a.localeCompare(b, 'zh-CN')
		})
	}

	const addBanjiOption = () => {
		const numbers = banjiOptions.value
			.map(item => Number(item))
			.filter(num => !Number.isNaN(num))
		const next = numbers.length ? Math.max(...numbers) + 1 : 1
		mergeBanjiOptions([String(next)])
		registerForm.value.banji = String(next)
	}

	const loadBanjiOptionsByMajor = (zhuanye) => {
		if (!zhuanye) return
		context?.$http({
			url: 'banjixinxi/registerOptions',
			method: 'get',
			params: { zhuanye }
		}).then(res => {
			const list = res?.data?.data || []
			if (list.length) {
				banjiOptions.value = list
			}
		}).catch(() => {})
	}

	watch(() => registerForm.value.zhuanye, (zhuanye) => {
		loadBanjiOptionsByMajor(zhuanye)
	})
	const init=()=>{
		xueshengxingbieLists.value = "男,女".split(',')
		context?.$http({
			url: 'config/info',
			method: 'get',
			params: { name: 'system_graduation_year' }
		}).then(res => {
			const year = res?.data?.data?.value
			if (year) {
				registerForm.value.biyejie = String(year).replace(/[^0-9]/g, '').slice(0, 4)
			}
		}).catch(() => {})
	}
	const handleRegister = () => {
		let url = tableName.value +"/register";
		if((!registerForm.value.xuehao)){
			context?.$toolUtil.message(`学号不能为空`,'error')
			return false
		}
		if((!registerForm.value.mima)){
			context?.$toolUtil.message(`密码不能为空`,'error')
			return false
		}
		if(registerForm.value.mima!=registerForm.value.mima2){
			context?.$toolUtil.message('两次密码输入不一致','error')
			return false
		}
		if((!registerForm.value.xueshengxingming)){
			context?.$toolUtil.message(`学生姓名不能为空`,'error')
			return false
		}
		if(registerForm.value.shoujihaoma&&(!context?.$toolUtil.isMobile(registerForm.value.shoujihaoma))){
			context?.$toolUtil.message(`手机号码应输入手机格式`,'error')
			return false
		}
		if((!registerForm.value.zhuanye)){
			context?.$toolUtil.message(`请选择专业`,'error')
			return false
		}
		if((!registerForm.value.nianji)){
			context?.$toolUtil.message(`请选择年级`,'error')
			return false
		}
		if((!registerForm.value.biyejie)){
			context?.$toolUtil.message(`请选择毕业届别`,'error')
			return false
		}
		if((!registerForm.value.banji)){
			context?.$toolUtil.message(`请选择班级`,'error')
			return false
		}

		context?.$http({
			url:url,
			method:'post',
			data:{
				xuehao: registerForm.value.xuehao,
				mima: registerForm.value.mima,
				xueshengxingming: registerForm.value.xueshengxingming,
				xingbie: registerForm.value.xingbie,
				shoujihaoma: registerForm.value.shoujihaoma,
				zhuanye: registerForm.value.zhuanye,
				banji: registerForm.value.banji,
				nianji: registerForm.value.nianji,
				biyejie: registerForm.value.biyejie,
			}
		}).then(res=>{
			context?.$toolUtil.message('注册成功','success', obj=>{
				context?.$router.push({
					path: "/login"
				});
			})
		}).catch(err => {
			const msg = err?.data?.msg || err?.response?.data?.msg || ''
			if (msg && msg.indexOf('注册用户已存在') !== -1) {
				context?.$toolUtil.message('该学号已注册，请直接登录', 'error')
			} else if (msg) {
				context?.$toolUtil.message(msg, 'error')
			} else {
				context?.$toolUtil.message('注册失败，请稍后重试', 'error')
			}
		})
	}
	const close = () => {
		context?.$router.push({
			path: "/login"
		});
	}
	init()
</script>
<style lang="scss" scoped>
$bg-dark: #061630;
$cyan: #22d3ee;
$cyan-dark: #0891b2;
$text-main: #e2e8f0;
$text-muted: #94a3b8;

.register-page {
	min-height: 100vh;
	width: 100%;
	display: flex;
	justify-content: center;
	align-items: center;
	background: linear-gradient(135deg, #061630 0%, #0a1a2e 50%, #061630 100%);
	position: relative;
	overflow: hidden;
	padding: 24px 16px;
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
	max-width: 480px;
}

.register-card {
	width: 100%;
	max-height: calc(100vh - 48px);
	overflow-y: auto;
	background: linear-gradient(145deg, rgba(10, 30, 58, 0.95) 0%, rgba(6, 22, 48, 0.98) 100%);
	border: 1px solid rgba(34, 211, 238, 0.2);
	border-radius: 16px;
	padding: 32px 28px 24px;
	box-shadow:
		0 0 40px rgba(34, 211, 238, 0.08),
		0 8px 32px rgba(0, 0, 0, 0.4),
		inset 0 1px 0 rgba(255, 255, 255, 0.03);
	backdrop-filter: blur(20px);
	animation: slideUp 0.6s ease-out;

	&::-webkit-scrollbar { width: 6px; }
	&::-webkit-scrollbar-thumb {
		background: rgba($cyan, 0.35);
		border-radius: 3px;
	}
}

@keyframes slideUp {
	from { opacity: 0; transform: translateY(30px); }
	to { opacity: 1; transform: translateY(0); }
}

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
	padding: 12px;
	background: linear-gradient(135deg, rgba(34, 211, 238, 0.15) 0%, rgba(8, 145, 178, 0.15) 100%);
	border: 1px solid rgba(34, 211, 238, 0.3);
	border-radius: 14px;
	color: $cyan;

	svg { width: 100%; height: 100%; }
}

.system-title {
	font-size: 22px;
	font-weight: 600;
	margin: 0 0 6px;
	background: linear-gradient(135deg, #fff 0%, $cyan 100%);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
}

.system-subtitle {
	font-size: 12px;
	color: #64748b;
	margin: 0;
	letter-spacing: 1px;
}

.register-form {
	display: flex;
	flex-direction: column;
}

.form-item {
	display: flex;
	flex-direction: column;
	margin-bottom: 14px;
}

.form-label {
	font-size: 13px;
	color: $text-muted;
	font-weight: 500;
	margin-bottom: 6px;
}

.form-label-row {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 6px;

	.form-label {
		margin-bottom: 0;
	}
}

.btn-add-banji {
	border: 1px solid rgba(34, 211, 238, 0.35);
	background: rgba(34, 211, 238, 0.08);
	color: $cyan;
	font-size: 12px;
	line-height: 1;
	padding: 6px 10px;
	border-radius: 6px;
	cursor: pointer;
	transition: all 0.2s;

	&:hover {
		background: rgba(34, 211, 238, 0.16);
		border-color: $cyan;
	}
}

.form-input {
	width: 100%;
	height: 42px;
	padding: 0 14px;
	background: rgba(5, 18, 36, 0.8);
	border: 1px solid rgba(34, 211, 238, 0.2);
	border-radius: 8px;
	color: $text-main;
	font-size: 14px;
	box-sizing: border-box;
	transition: all 0.3s;

	&::placeholder { color: #4a5568; }

	&:hover {
		border-color: rgba(34, 211, 238, 0.35);
		background: rgba(5, 18, 36, 0.9);
	}

	&:focus {
		outline: none;
		border-color: $cyan;
		box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.12);
	}
}

.form-select {
	width: 100%;

	:deep(.el-select__wrapper) {
		background-color: rgba(5, 18, 36, 0.8) !important;
		box-shadow: 0 0 0 1px rgba(34, 211, 238, 0.2) inset !important;
		min-height: 42px;
		padding: 0 14px;
		transition: all 0.3s;

		&:hover {
			box-shadow: 0 0 0 1px rgba(34, 211, 238, 0.35) inset !important;
		}
	}

	:deep(.el-select__wrapper.is-focused) {
		box-shadow: 0 0 0 1px $cyan inset, 0 0 0 3px rgba(34, 211, 238, 0.12) !important;
	}

	:deep(.el-select__selected-item),
	:deep(.el-select__placeholder) {
		font-size: 14px;
	}

	:deep(.el-select__selected-item) {
		color: $text-main !important;
	}

	:deep(.el-select__placeholder) {
		color: #4a5568 !important;
	}

	:deep(.el-select__caret) {
		color: $text-muted;
	}
}

.btn-area {
	margin-top: 8px;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 12px;
}

.btn-register {
	width: 100%;
	height: 44px;
	border: none;
	border-radius: 10px;
	background: linear-gradient(135deg, $cyan 0%, $cyan-dark 100%);
	color: #fff;
	font-size: 16px;
	font-weight: 500;
	cursor: pointer;
	transition: all 0.25s;
	box-shadow: 0 4px 20px rgba(34, 211, 238, 0.35);

	&:hover {
		transform: translateY(-1px);
		box-shadow: 0 6px 24px rgba(34, 211, 238, 0.45);
	}
}

.aux-link {
	cursor: pointer;
	font-size: 14px;
	color: $cyan;
	transition: opacity 0.2s;

	&:hover {
		opacity: 0.85;
		text-decoration: underline;
	}
}

@media (max-width: 480px) {
	.register-card {
		padding: 24px 18px 20px;
	}

	.system-title {
		font-size: 20px;
	}
}
</style>
