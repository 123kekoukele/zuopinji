<template>
	<div>
		<div class="forget_view">
			<el-form :model="forgetForm" class="forget_box">
				<div class="forget_title">基于SpringBoot的毕业设计选题系统{{pageType==1?'找回密码':pageType==2?'输入密保':'重置密码'}}</div>
				<div class="tab_view">
					<div class="header_number" :class="pageType>1?'header_number2':pageType==1?'header_number1':''"><div class="number" v-if="pageType<2">1</div><div class="number" v-else><el-icon><Check /></el-icon></div></div>
					<div class="header_number" :class="pageType>2?'header_number2':pageType==2?'header_number1':''"><div class="number" v-if="pageType<3">2</div><div class="number" v-else><el-icon><Check /></el-icon></div></div>
					<div class="header_number" :class="pageType==3?'header_number1':''"><div class="number">3</div></div>
				</div>
			</el-form>
		</div>
	</div>
</template>
<script setup>
	import {
		ref,
		getCurrentInstance,
		nextTick,
	} from 'vue';
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const pageType = ref(1)
	const forgetForm = ref({})
	const userForm = ref({})
	//返回登录
	const close = () => {
		context?.$router.push({
			path: "/login"
		});
	}
</script>

<style lang="scss" scoped>
	.forget_view {
		min-height: 100vh;
		display: flex;
		justify-content: center;
		align-items: center;
		background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 50%, #0369a1 100%);
		background-size: cover;
		background-position: center;
		padding: 24px;
		.forget_box {
			width: 100%;
			max-width: 480px;
			padding: 36px 40px;
			background: rgba(255, 255, 255, 0.96);
			backdrop-filter: blur(12px);
			border-radius: 16px;
			box-shadow: 0 20px 50px rgba(2, 132, 199, 0.2);
			border: 1px solid rgba(255, 255, 255, 0.6);
		}
		.forget_title {
			padding: 0 0 28px;
			margin: 0;
			color: #0c4a6e;
			font-size: 20px;
			font-weight: 600;
			text-align: center;
		}
		.tab_view {
			padding: 8px 0;
			margin: 0 0 24px;
			display: flex;
			width: 100%;
			align-items: center;
			justify-content: center;
			gap: 16px;
			.header_number .number {
				border: 2px solid #bae6fd;
				border-radius: 50%;
				color: #94a3b8;
				width: 40px;
				height: 40px;
				display: flex;
				align-items: center;
				justify-content: center;
				font-size: 16px;
			}
			.header_number1 .number {
				color: #0284c7;
				background: #f0f9ff;
				border-color: #0284c7;
			}
			.header_number2 .number {
				color: #0d9488;
				border-color: #0d9488;
				background: #ccfbf1;
			}
		}
		.list_item {
			margin: 10px 0 20px;
			.item_label {
				color: #0c4a6e;
			}
			:deep(.list_inp) {
				border: 1px solid #bae6fd;
				border-radius: 10px;
				&:focus {
					border-color: #0284c7;
					box-shadow: 0 0 0 2px rgba(2, 132, 199, 0.2);
				}
			}
			:deep(.list_sel) {
				border: 1px solid #bae6fd;
				border-radius: 10px;
				.el-input__wrapper.is-focus {
					border-color: #0284c7;
				}
			}
		}
		.list_btn {
			margin: 24px 0 0;
			:deep(.el-button--success),
			:deep(.el-button--primary),
			:deep(.el-button--warning) {
				border: none;
				border-radius: 12px;
				color: #fff;
				background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 100%);
				width: 100%;
				font-size: 15px;
				height: 42px;
			}
			:deep(.el-button--success:hover),
			:deep(.el-button--primary:hover),
			:deep(.el-button--warning:hover) {
				opacity: 0.92;
			}
			.r-login {
				cursor: pointer;
				margin: 16px 0 0;
				color: #0284c7;
				font-size: 14px;
				text-align: center;
			}
			.r-login:hover {
				text-decoration: underline;
			}
		}
	}
</style>
