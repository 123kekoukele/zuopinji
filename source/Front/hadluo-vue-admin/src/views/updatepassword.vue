<template>
	<div class="update-password-page">
		<div class="password-card">
			<div class="card-header">
				<div class="header-icon">
					<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
						<rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
						<path d="M7 11V7a5 5 0 0 1 10 0v4"/>
					</svg>
				</div>
				<div class="header-text">
					<h2 class="card-title">修改密码</h2>
					<p class="card-subtitle">请妥善保管您的新密码</p>
				</div>
			</div>

			<el-form class="password_form" ref="passwordFormRef" :model="form" label-width="100px" :rules="rules">
				<el-form-item label="原密码" prop="mima1">
					<el-input
						class="list_inp"
						v-model="form.mima1"
						type="password"
						show-password
						placeholder="请输入原密码"
						clearable
					>
						<template #prefix>
							<svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
								<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
								<circle cx="12" cy="7" r="4"/>
							</svg>
						</template>
					</el-input>
				</el-form-item>

				<el-form-item label="新密码" prop="mima">
					<el-input
						class="list_inp"
						v-model="form.mima"
						type="password"
						show-password
						placeholder="请输入新密码"
						clearable
					>
						<template #prefix>
							<svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
								<rect x="3" y="11" width="18" height="11" rx="2" ry="2"/>
								<path d="M7 11V7a5 5 0 0 1 10 0v4"/>
							</svg>
						</template>
					</el-input>
				</el-form-item>

				<el-form-item label="确认密码" prop="mima2">
					<el-input
						class="list_inp"
						v-model="form.mima2"
						type="password"
						show-password
						placeholder="请再次输入新密码"
						clearable
					>
						<template #prefix>
							<svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
								<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
								<polyline points="22 4 12 14.01 9 11.01"/>
							</svg>
						</template>
					</el-input>
				</el-form-item>

				<div class="form-tips">
					<div class="tip-item">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
							<circle cx="12" cy="12" r="10"/>
							<line x1="12" y1="16" x2="12" y2="12"/>
							<line x1="12" y1="8" x2="12.01" y2="8"/>
						</svg>
						<span>密码长度至少6位</span>
					</div>
					<div class="tip-item">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
							<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
						</svg>
						<span>建议使用字母、数字组合</span>
					</div>
				</div>

				<div class="form-actions">
					<el-button class="submit-btn" type="primary" @click="onSubmit">
						<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
							<path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
							<polyline points="17 21 17 13 7 13 7 21"/>
							<polyline points="7 3 7 8 15 8"/>
						</svg>
						保存修改
					</el-button>
				</div>
			</el-form>
		</div>
	</div>
</template>

<script setup>
	import {
		reactive,
		ref,
		getCurrentInstance
	} from 'vue'
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const form = ref({})
	const user = ref({})
	const sessionTable = ref('')
	const passwordFormRef = ref(null)
	const rules = ref({
		mima1: [{
			required: true,
			message: '请输入原密码',
			trigger: 'blur'
		}, ],
		mima: [{
			required: true,
			message: '请输入新密码',
			trigger: 'blur'
		}, ],
		mima2: [{
			required: true,
			message: '请再次输入新密码',
			trigger: 'blur'
		}, ],
	})
	const onSubmit = async () => {
		passwordFormRef.value.validate(async (valid) => {
			if (valid) {
				if(sessionTable.value == 'users'){
					if (form.value.mima1 != user.value.password) {
						context?.$toolUtil.message('原密码不正确','error')
						return false
					}
					user.value.password = form.value.mima
				}else{
					if(sessionTable.value == 'jiaoshi'){
						if(form.value.mima1 != user.value.mima){
							context?.$toolUtil.message('原密码不正确', 'error')
							return false
						}
					}
				}
				if (form.value.mima2 != form.value.mima) {
					context?.$toolUtil.message('两次密码输入不一致','error')
					return false
				}
				if(sessionTable.value == 'jiaoshi'){
					user.value.mima = form.value.mima
				}
				context?.$http({
					url: `${sessionTable.value}/update`,
					method: 'post',
					data: user.value
				}).then(res => {
					context?.$toolUtil.message('修改成功，下次登录将使用新密码登录','success')
				})
			}
		})

	}
	const getInfo = () => {
		sessionTable.value = context?.$toolUtil.storageGet('sessionTable')
		context?.$http({
			url: `${sessionTable.value}/session`,
			method: 'get'
		}).then(res => {
			user.value = res.data.data
		})
	}
	getInfo()
</script>

<style lang="scss" scoped>
// 科技蓝主题 - 修改密码页面样式
.update-password-page {
	padding: 16px;
	background: transparent;
	min-height: calc(100vh - 100px);
	display: flex;
	align-items: flex-start;
	justify-content: center;
}

.password-card {
	background: var(--tech-panel);
	border: 1px solid var(--tech-border-soft);
	border-radius: 12px;
	box-shadow: 0 0 24px var(--tech-glow);
	padding: 0;
	width: 100%;
	max-width: 500px;
	overflow: hidden;
}

.card-header {
	display: flex;
	align-items: center;
	padding: 24px 28px;
	background: linear-gradient(135deg, rgba(34, 211, 238, 0.08) 0%, rgba(8, 145, 178, 0.05) 100%);
	border-bottom: 1px solid var(--tech-border-soft);
}

.header-icon {
	width: 48px;
	height: 48px;
	border-radius: 12px;
	background: linear-gradient(135deg, var(--primary-blue-dark), var(--primary-blue));
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 16px;
	box-shadow: 0 0 16px rgba(34, 211, 238, 0.2);

	svg {
		width: 24px;
		height: 24px;
		color: #fff;
	}
}

.header-text {
	.card-title {
		margin: 0;
		font-size: 18px;
		font-weight: 600;
		color: var(--tech-cyan);
	}

	.card-subtitle {
		margin: 4px 0 0;
		font-size: 13px;
		color: var(--text-secondary);
	}
}

.password_form {
	padding: 28px;

	:deep(.el-form-item) {
		margin-bottom: 24px;

		.el-form-item__label {
			color: var(--text-regular);
			font-weight: 500;
			padding-right: 12px;
		}

		.el-form-item__content {
			justify-content: flex-start;
		}

		.list_inp {
			width: 100%;
			max-width: 320px;

			.el-input__wrapper {
				border: 1px solid var(--tech-border-soft);
				border-radius: 8px;
				background: rgba(8, 28, 58, 0.55);
				box-shadow: 0 0 0 1px var(--tech-border-soft) inset;
				padding: 4px 12px;
				transition: all 0.3s;

				&:hover {
					border-color: var(--primary-blue-light);
					box-shadow: 0 0 0 1px rgba(34, 211, 238, 0.2) inset;
				}

				&.is-focus {
					border-color: var(--primary-blue-light);
					box-shadow: 0 0 0 2px rgba(34, 211, 238, 0.18);
				}
			}

			.el-input__inner {
				color: var(--text-primary);
				height: 36px;
				line-height: 36px;

				&::placeholder {
					color: var(--text-placeholder);
				}
			}

			.el-input__prefix {
				color: var(--tech-cyan);
				margin-right: 8px;
			}

			.input-icon {
				width: 18px;
				height: 18px;
			}
		}
	}
}

.form-tips {
	background: rgba(34, 211, 238, 0.05);
	border: 1px solid rgba(34, 211, 238, 0.1);
	border-radius: 8px;
	padding: 16px 20px;
	margin-bottom: 28px;

	.tip-item {
		display: flex;
		align-items: center;
		gap: 10px;
		color: var(--text-secondary);
		font-size: 13px;
		line-height: 1.6;

		& + .tip-item {
			margin-top: 8px;
		}

		svg {
			width: 16px;
			height: 16px;
			color: var(--tech-cyan);
			flex-shrink: 0;
		}
	}
}

.form-actions {
	padding-top: 8px;
	border-top: 1px solid var(--tech-border-soft);
	display: flex;
	justify-content: center;

	.submit-btn {
		border: none;
		border-radius: 10px;
		padding: 0 36px;
		color: #fff;
		background: linear-gradient(135deg, var(--primary-blue-dark), var(--primary-blue));
		font-size: 15px;
		height: 44px;
		transition: all 0.3s;
		box-shadow: 0 0 16px rgba(34, 211, 238, 0.2);
		display: flex;
		align-items: center;
		gap: 8px;

		svg {
			width: 18px;
			height: 18px;
		}
	}

	.submit-btn:hover {
		background: linear-gradient(135deg, var(--primary-blue), var(--primary-blue-light));
		box-shadow: 0 0 24px rgba(34, 211, 238, 0.35);
		transform: translateY(-2px);
	}
}
</style>
