<template>
	<div>
		<div class="register_view">
			<el-form :model="registerForm" class="register_form">
				<div class="title_view">{{ projectName }} - 教师注册</div>
				<div class="list_item">
					<div class="list_label">教师工号：</div>
					<input
						class="list_inp"
						v-model="registerForm.jiaoshigonghao"
						placeholder="请输入教师工号"
						type="text"
					/>
				</div>
				<div class="list_item">
					<div class="list_label">密码：</div>
					<input
						class="list_inp"
						v-model="registerForm.mima"
						placeholder="请输入密码"
						type="password"
					/>
				</div>
				<div class="list_item">
					<div class="list_label">确认密码：</div>
					<input
						class="list_inp"
						v-model="registerForm.mima2"
						type="password"
						placeholder="请输入确认密码"
					/>
				</div>
				<div class="list_item">
					<div class="list_label">教师姓名：</div>
					<input
						class="list_inp"
						v-model="registerForm.jiaoshixingming"
						placeholder="请输入教师姓名"
						type="text"
					/>
				</div>
				<div class="list_item">
					<div class="list_label">性别：</div>
					<el-select
						class="list_sel"
						v-model="registerForm.xingbie"
						placeholder="请选择性别"
					>
						<el-option v-for="item in xingbieLists" :key="item" :label="item" :value="item" />
					</el-select>
				</div>
				<div class="list_item">
					<div class="list_label">联系电话：</div>
					<input
						class="list_inp"
						v-model="registerForm.lianxidianhua"
						placeholder="请输入联系电话"
						type="text"
					/>
				</div>
				<div class="list_item">
					<div class="list_label">专业：</div>
					<el-select
						class="list_sel"
						v-model="registerForm.zhuanye"
						placeholder="请选择专业"
					>
						<el-option v-for="item in majorOptions" :key="item" :label="item" :value="item" />
					</el-select>
				</div>
				<div class="list_btn">
					<el-button class="register" type="success" @click="handleRegister">注册</el-button>
					<div class="r-login" @click="close">已有账号，直接登录</div>
				</div>
			</el-form>
		</div>
	</div>
</template>
<script setup>
	import { ref, getCurrentInstance } from 'vue';
	import { majorOptions } from '@/utils/majorOptions';
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const projectName = context?.$project?.projectName || '毕业设计选题系统';
	const tableName = ref('jiaoshi');

	const registerForm = ref({
		jiaoshigonghao: '',
		mima: '',
		mima2: '',
		jiaoshixingming: '',
		xingbie: '',
		lianxidianhua: '',
		zhuanye: ''
	});
	const xingbieLists = ref(['男', '女']);

	const handleRegister = () => {
		const form = registerForm.value;
		if (!form.jiaoshigonghao) {
			context?.$toolUtil.message('教师工号不能为空', 'error');
			return;
		}
		if (form.jiaoshigonghao.length < 3 || form.jiaoshigonghao.length > 20) {
			context?.$toolUtil.message('教师工号长度建议 3～20 位', 'error');
			return;
		}
		if (!form.mima) {
			context?.$toolUtil.message('密码不能为空', 'error');
			return;
		}
		if (form.mima.length < 6) {
			context?.$toolUtil.message('密码长度不能小于 6', 'error');
			return;
		}
		if (form.mima !== form.mima2) {
			context?.$toolUtil.message('两次密码输入不一致', 'error');
			return;
		}
		if (!form.jiaoshixingming) {
			context?.$toolUtil.message('教师姓名不能为空', 'error');
			return;
		}
		if (form.lianxidianhua && context?.$toolUtil.isMobile && !context.$toolUtil.isMobile(form.lianxidianhua)) {
			context?.$toolUtil.message('请输入正确的手机号', 'error');
			return;
		}
		if (!form.zhuanye) {
			context?.$toolUtil.message('请选择专业', 'error');
			return;
		}

		context?.$http({
			url: tableName.value + '/register',
			method: 'post',
			data: form
		}).then(() => {
			context?.$toolUtil.message('注册成功', 'success', () => {
				context?.$router.push({ path: '/login' });
			});
		}).catch(err => {
			const msg = err?.data?.msg || err?.response?.data?.msg || ''
			if (msg && msg.indexOf('注册用户已存在') !== -1) {
				context?.$toolUtil.message('该教师账号已注册，请直接登录', 'error')
			} else if (msg) {
				context?.$toolUtil.message(msg, 'error')
			} else {
				context?.$toolUtil.message('注册失败，请稍后重试', 'error')
			}
		});
	};

	const close = () => {
		context?.$router.push({ path: '/login' });
	};
</script>
<style lang="scss" scoped>
	.register_view {
		min-height: 100vh;
		display: flex;
		justify-content: center;
		align-items: center;
		background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 50%, #0369a1 100%);
		padding: 24px;
		.register_form {
			width: 100%;
			max-width: 520px;
			padding: 36px 40px;
			background: rgba(255, 255, 255, 0.96);
			backdrop-filter: blur(12px);
			border-radius: 16px;
			box-shadow: 0 20px 50px rgba(2, 132, 199, 0.2);
			border: 1px solid rgba(255, 255, 255, 0.6);
		}
		.title_view {
			padding: 0 0 24px;
			margin: 0;
			color: #0c4a6e;
			font-size: 22px;
			font-weight: 600;
			text-align: center;
		}
		.list_item {
			margin: 0 0 16px;
			display: flex;
			width: 100%;
			align-items: center;
			.list_label {
				color: #0c4a6e;
				width: 100px;
				font-size: 14px;
				text-align: right;
				margin-right: 12px;
				flex-shrink: 0;
			}
			:deep(.list_inp) {
				flex: 1;
				border: 1px solid #bae6fd;
				border-radius: 10px;
				padding: 0 14px;
				line-height: 38px;
				height: 38px;
				font-size: 14px;
			}
		}
		:deep(.list_sel) {
			flex: 1;
		}
		.list_file_list {
			flex: 1;
		}
		.list_btn {
			margin: 24px 0 0;
			display: flex;
			flex-direction: column;
			gap: 12px;
			.register {
				width: 100%;
				height: 42px;
				border-radius: 12px;
				background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 100%);
			}
			.r-login {
				cursor: pointer;
				text-align: center;
				color: #0284c7;
				font-size: 14px;
			}
		}
	}
</style>
