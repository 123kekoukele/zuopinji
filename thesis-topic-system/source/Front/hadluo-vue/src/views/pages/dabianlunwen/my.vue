<template>
	<div class="dabian_page">
		<div class="page-header">
			<div class="page-title">我的答辩论文</div>
		</div>
		<div class="content_grid" v-loading="loading">
			<div class="sidebar_card action_card">
				<div class="card_header">
					<el-icon class="card_icon"><Edit /></el-icon>
					<span class="card_title">快速操作</span>
				</div>
				<div class="card_body">
					<el-button type="primary" class="submit_btn" @click="goAdd">
						<el-icon><Edit /></el-icon>
						去提交
					</el-button>
					<div class="upload_area" :class="{ dragover: isDragover }" @dragover.prevent="isDragover = true" @dragleave="isDragover = false" @drop.prevent="handleDrop">
						<el-icon class="upload_icon"><Upload /></el-icon>
						<div class="upload_text">或拖拽文件到此处</div>
						<div class="upload_hint">支持 .doc, .docx, .pdf 格式</div>
						<input type="file" accept=".doc,.docx,.pdf" class="upload_input" @change="handleFileSelect" />
					</div>
					<div class="upload_limit">
						<el-icon><InfoFilled /></el-icon>
						单个文件最大 50MB
					</div>
				</div>
			</div>

			<div class="sidebar_card history_card">
				<div class="card_header">
					<el-icon class="card_icon"><Clock /></el-icon>
					<span class="card_title">历史记录</span>
				</div>
				<div class="card_body">
					<div class="history_empty" v-if="!historyRecords.length">
						<el-icon><Document /></el-icon>
						<span>暂无提交记录</span>
					</div>
					<div class="history_version" v-for="(record, index) in historyRecords" :key="index">
						<div class="version_info">
							<span class="version_num">v{{ record.version }}</span>
							<span class="version_name">{{ record.fileName }}</span>
							<span class="version_date">{{ record.date }}</span>
						</div>
						<div class="version_status" :class="record.status">
							<span v-if="record.status === 'pending'">待评分</span>
							<span v-else>已提交</span>
						</div>
						<el-button link type="primary" v-if="record.filePath" @click="openFile(record.filePath)">查看文件</el-button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref, computed, onMounted, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { downloadStoredFile } from '@/utils/fileDownload'
import {
	Upload, Edit, Clock, Document, InfoFilled
} from '@element-plus/icons-vue'

const router = useRouter()
const context = getCurrentInstance()?.appContext.config.globalProperties
const loading = ref(false)
const records = ref([])

const historyRecords = computed(() =>
	records.value.map((row, index) => ({
		version: records.value.length - index,
		fileName: row.lunwenfujian ? row.lunwenfujian.split('/').pop() : (row.ketimingcheng || '答辩论文'),
		date: row.tijiaoshijian || row.addtime || '',
		filePath: row.lunwenfujian || '',
		status: row.pingfenzhuangtai ? 'passed' : 'pending'
	}))
)

const isDragover = ref(false)
const handleDrop = (e) => {
	isDragover.value = false
	if (e.dataTransfer?.files?.length) goAdd()
}
const handleFileSelect = () => goAdd()

const openFile = (filePath) => {
	if (!filePath) {
		context?.$toolUtil.message('文件不存在', 'error')
		return
	}
	downloadStoredFile(filePath).catch(err => {
		context?.$toolUtil.message(err?.message || '下载失败', 'error')
	})
}

const loadRecords = () => {
	loading.value = true
	context?.$http({
		url: 'dabianlunwen/page',
		method: 'get',
		params: { page: 1, limit: 20, sort: 'id', order: 'desc' }
	}).then(res => {
		records.value = res.data.data?.list || []
	}).catch(() => {
		records.value = []
	}).finally(() => {
		loading.value = false
	})
}

const goAdd = () => router.push('/index/dabianlunwenAdd')

onMounted(() => {
	loadRecords()
})
</script>

<style lang="scss" scoped>
$bg-dark: #0a1929;
$bg-panel: #132f4c;
$border-color: #1e4976;
$cyan: #00bcd4;
$cyan-dark: #008ba3;
$text-main: #e0e0e0;
$text-muted: #90a4ae;
$danger: #f44336;

.dabian_page {
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

.content_grid {
	display: flex;
	gap: 20px;
	flex-wrap: wrap;
}

.sidebar_card {
	background: $bg-panel;
	border: 1px solid $border-color;
	border-radius: 12px;
	padding: 16px;
	flex: 1;
	min-width: 300px;
}

.card_header {
	display: flex;
	align-items: center;
	gap: 10px;
	margin-bottom: 14px;
}

.card_icon {
	font-size: 18px;
	color: $cyan;
}

.card_title {
	font-size: 15px;
	font-weight: 600;
	color: $cyan;
}

.card_body {
	color: $text-main;
}

.action_card {
	.submit_btn {
		width: 100%;
		height: 44px;
		font-size: 15px;
		margin-bottom: 14px;
		background: linear-gradient(135deg, $cyan, $cyan-dark);
		border: none;
		color: #fff;
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 8px;
		&:hover {
			box-shadow: 0 4px 16px rgba($cyan, 0.4);
		}
	}
}

.upload_area {
	position: relative;
	padding: 24px;
	border: 2px dashed $border-color;
	border-radius: 10px;
	text-align: center;
	cursor: pointer;
	transition: all 0.3s;
	background: rgba($cyan, 0.03);

	&:hover, &.dragover {
		border-color: $cyan;
		background: rgba($cyan, 0.08);
	}

	.upload_icon {
		font-size: 32px;
		color: $text-muted;
		margin-bottom: 8px;
	}
	.upload_text {
		font-size: 13px;
		color: $text-main;
		margin-bottom: 4px;
	}
	.upload_hint {
		font-size: 11px;
		color: $text-muted;
	}
	.upload_input {
		position: absolute;
		inset: 0;
		opacity: 0;
		cursor: pointer;
	}
}

.upload_limit {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 6px;
	margin-top: 10px;
	font-size: 11px;
	color: $text-muted;
}

.history_card {
	.history_empty {
		display: flex;
		flex-direction: column;
		align-items: center;
		gap: 8px;
		padding: 24px;
		color: $text-muted;
		font-size: 13px;
		.el-icon {
			font-size: 32px;
			opacity: 0.5;
		}
	}
}

.history_version {
	padding: 12px 0;
	border-bottom: 1px solid $border-color;

	&:last-child {
		border-bottom: none;
	}

	.version_info {
		display: flex;
		align-items: center;
		gap: 12px;
		margin-bottom: 6px;
		flex-wrap: wrap;
	}

	.version_num {
		padding: 2px 8px;
		background: rgba($cyan, 0.1);
		border-radius: 4px;
		font-size: 11px;
		color: $cyan;
		font-weight: 500;
	}

	.version_name {
		font-size: 13px;
		color: $text-main;
	}

	.version_date {
		font-size: 12px;
		color: $text-muted;
	}

	.version_status {
		font-size: 12px;
		margin-bottom: 4px;
		color: #4caf50;
		&.pending { color: #ff9800; }
	}
}
</style>
