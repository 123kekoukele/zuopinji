<template>
	<div class="zhongqi_page">
		<div class="page-header">
			<div class="page-title">我的中期检查</div>
		</div>
		<div class="placeholder_card">
			<el-icon class="placeholder_icon"><InfoFilled /></el-icon>
			<div class="placeholder_title">中期检查模块暂未开放</div>
			<div class="placeholder_desc">
				系统当前尚未配置中期检查提交与审核流程。请关注学院通知，或联系指导教师了解安排。
			</div>
			<div class="placeholder_actions">
				<el-button type="primary" @click="goKaitibaogao">查看我的开题报告</el-button>
				<el-button @click="goLunwenchugao">查看我的论文初稿</el-button>
			</div>
		</div>
	</div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { InfoFilled } from '@element-plus/icons-vue'

const router = useRouter()
const goKaitibaogao = () => router.push('/index/kaitibaogaoAdd')
const goLunwenchugao = () => router.push('/index/lunwenchugaoAdd')
</script>

<style lang="scss" scoped>
$bg-dark: #0a1929;
$bg-panel: #132f4c;
$border-color: #1e4976;
$cyan: #00bcd4;
$text-main: #e0e0e0;
$text-muted: #90a4ae;

.zhongqi_page {
	padding: 16px 20px;
	min-height: calc(100vh - 120px);
	background: $bg-dark;
}

.page-header {
	padding: 14px 18px;
	background: $bg-panel;
	border-radius: 10px;
	border: 1px solid $border-color;
	margin-bottom: 16px;
}

.page-title {
	font-size: 16px;
	font-weight: 600;
	color: $cyan;
}

.placeholder_card {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	text-align: center;
	padding: 48px 24px;
	background: $bg-panel;
	border: 1px solid $border-color;
	border-radius: 12px;
	min-height: 320px;
}

.placeholder_icon {
	font-size: 48px;
	color: $cyan;
	margin-bottom: 16px;
}

.placeholder_title {
	font-size: 18px;
	font-weight: 600;
	color: $text-main;
	margin-bottom: 12px;
}

.placeholder_desc {
	max-width: 480px;
	font-size: 14px;
	line-height: 1.6;
	color: $text-muted;
	margin-bottom: 24px;
}

.placeholder_actions {
	display: flex;
	gap: 12px;
	flex-wrap: wrap;
	justify-content: center;
}
</style>
