<template>
	<div class="kaiti_page">
		<!-- 主内容区 -->
		<div class="content_wrapper" v-loading="loading">
			<!-- ========== 未提交状态 ========== -->
			<template v-if="pageStatus === 'unsubmitted'">
				<div class="unsubmitted_content">
					<!-- 快速操作 -->
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
								单个文件最大 20MB
							</div>
						</div>
					</div>

					<!-- 历史记录 -->
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
								<div class="version_status passed">已提交</div>
							</div>
						</div>
					</div>
				</div>
			</template>

			<!-- ========== 已提交状态 ========== -->
			<template v-else-if="pageStatus === 'submitted'">
				<div class="submitted_content">
					<!-- 提交记录 -->
					<div class="record_card">
						<div class="card_header">
							<el-icon class="card_icon"><Document /></el-icon>
							<span class="card_title">提交记录</span>
						</div>
						<div class="card_body">
							<div class="record_file">
								<div class="file_icon">
									<el-icon><Document /></el-icon>
								</div>
								<div class="file_info">
									<div class="file_name">{{ submittedRecord.fileName }}</div>
									<div class="file_meta">
										<span>提交时间：{{ submittedRecord.submitTime }}</span>
										<span>文件大小：{{ submittedRecord.fileSize }}</span>
									</div>
								</div>
								<div class="file_status" :class="submittedRecord.status">
									<el-icon v-if="submittedRecord.status === 'passed'"><CircleCheckFilled /></el-icon>
									<el-icon v-else-if="submittedRecord.status === 'pending'"><Clock /></el-icon>
									<el-icon v-else><Loading /></el-icon>
									{{ submittedRecord.statusText }}
								</div>
							</div>
							<div class="record_actions">
								<el-button type="primary" @click="previewFile">
									<el-icon><View /></el-icon>
									预览
								</el-button>
								<el-button @click="downloadFile">
									<el-icon><Download /></el-icon>
									下载
								</el-button>
								<el-button type="warning" @click="goReupload">
									<el-icon><Upload /></el-icon>
									重新上传
								</el-button>
								<el-button type="danger" @click="revokeSubmit">
									<el-icon><Warning /></el-icon>
									撤销提交
								</el-button>
							</div>
						</div>
					</div>

					<!-- 审核进度 -->
					<div class="audit_card">
						<div class="card_header">
							<el-icon class="card_icon"><Operation /></el-icon>
							<span class="card_title">审核进度</span>
						</div>
						<div class="card_body">
							<div class="audit_steps">
								<div
									v-for="(audit, index) in auditSteps"
									:key="index"
									class="audit_step"
									:class="audit.status"
								>
									<div class="audit_indicator">
										<el-icon v-if="audit.status === 'completed'"><Check /></el-icon>
										<el-icon v-else-if="audit.status === 'pending'"><Clock /></el-icon>
										<el-icon v-else-if="audit.status === 'processing'" class="spin"><Loading /></el-icon>
										<span v-else>{{ index + 1 }}</span>
									</div>
									<div class="audit_content">
										<div class="audit_name">{{ audit.name }}</div>
										<div class="audit_desc">{{ audit.desc }}</div>
										<div class="audit_time" v-if="audit.time">{{ audit.time }}</div>
									</div>
								</div>
							</div>
							<div class="audit_actions">
								<el-button type="primary" link @click="showAuditDetail = true" v-if="latestRecord">
									查看审核详情
									<el-icon><ArrowRight /></el-icon>
								</el-button>
							</div>
						</div>
					</div>

					<div v-if="auditFeedbackVisible" class="audit_card audit_feedback_card">
						<div class="card_header">
							<el-icon class="card_icon"><Operation /></el-icon>
							<span class="card_title">{{ auditDetail.reasonLabel }}</span>
						</div>
						<div class="card_body">
							<div class="audit_feedback_text">{{ auditDetail.reason }}</div>
							<div class="audit_feedback_meta">审核教师：{{ auditDetail.teacher }}</div>
						</div>
					</div>
				</div>
			</template>

			<!-- ========== 审核中状态 ========== -->
			<template v-else-if="pageStatus === 'reviewing'">
				<div class="submitted_content">
					<div class="record_card">
						<div class="card_header">
							<el-icon class="card_icon"><Document /></el-icon>
							<span class="card_title">提交记录</span>
						</div>
						<div class="card_body">
							<div class="record_file">
								<div class="file_icon">
									<el-icon><Document /></el-icon>
								</div>
								<div class="file_info">
									<div class="file_name">{{ submittedRecord.fileName }}</div>
									<div class="file_meta">
										<span>提交时间：{{ submittedRecord.submitTime }}</span>
										<span>文件大小：{{ submittedRecord.fileSize }}</span>
									</div>
								</div>
								<div class="file_status reviewing">
									<el-icon class="spin"><Loading /></el-icon>
									{{ submittedRecord.statusText }}
								</div>
							</div>
							<div class="record_actions">
								<el-button type="primary" @click="previewFile">
									<el-icon><View /></el-icon>
									预览
								</el-button>
								<el-button @click="downloadFile">
									<el-icon><Download /></el-icon>
									下载
								</el-button>
								<el-button type="warning" @click="goReupload">
									<el-icon><Upload /></el-icon>
									重新上传
								</el-button>
								<el-button type="danger" @click="revokeSubmit">
									<el-icon><Warning /></el-icon>
									撤销提交
								</el-button>
							</div>
						</div>
					</div>

					<div class="audit_card">
						<div class="card_header">
							<el-icon class="card_icon"><Operation /></el-icon>
							<span class="card_title">审核进度</span>
						</div>
						<div class="card_body">
							<div class="audit_steps">
								<div
									v-for="(audit, index) in auditSteps"
									:key="index"
									class="audit_step"
									:class="audit.status"
								>
									<div class="audit_indicator">
										<el-icon v-if="audit.status === 'completed'"><Check /></el-icon>
										<el-icon v-else-if="audit.status === 'processing'" class="spin"><Loading /></el-icon>
										<span v-else>{{ index + 1 }}</span>
									</div>
									<div class="audit_content">
										<div class="audit_name">{{ audit.name }}</div>
										<div class="audit_desc">{{ audit.desc }}</div>
										<div class="audit_time" v-if="audit.time">{{ audit.time }}</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</template>
		</div>

		<!-- 模板示例弹窗 -->
		<el-dialog v-model="showTemplate" title="开题报告模板示例" width="700px" class="template_dialog">
			<div class="template_content">
				<h4>毕业设计（论文）开题报告</h4>
				<div class="template_section">
					<h5>一、选题的背景与意义</h5>
					<p>说明选题的来源、研究背景以及该研究在理论和实践中的意义...</p>
				</div>
				<div class="template_section">
					<h5>二、国内外研究现状</h5>
					<p>综述该领域在国内外的研究进展、主要成果和存在的问题...</p>
				</div>
				<div class="template_section">
					<h5>三、研究内容与方法</h5>
					<p>明确研究的具体内容、采用的研究方法和技术路线...</p>
				</div>
				<div class="template_section">
					<h5>四、进度安排</h5>
					<p>制定详细的研究进度计划，包括各阶段的时间节点...</p>
				</div>
				<div class="template_section">
					<h5>五、参考文献</h5>
					<p>列出在开题报告中引用的主要文献...</p>
				</div>
			</div>
			<template #footer>
				<el-button @click="showTemplate = false">关闭</el-button>
				<el-button type="primary" @click="downloadTemplate">下载模板</el-button>
			</template>
		</el-dialog>

		<!-- 审核详情弹窗 -->
		<el-dialog v-model="showAuditDetail" title="审核详情" width="600px" class="audit_dialog">
			<div class="audit_detail_content" v-if="auditDetail">
				<div class="detail_item">
					<div class="detail_label">审核状态</div>
					<div class="detail_value" :class="auditDetail.statusClass">{{ auditDetail.status }}</div>
				</div>
				<div class="detail_item">
					<div class="detail_label">提交时间</div>
					<div class="detail_value">{{ auditDetail.time }}</div>
				</div>
				<div class="detail_item">
					<div class="detail_label">{{ auditDetail.reasonLabel }}</div>
					<div class="detail_value">{{ auditDetail.reason }}</div>
				</div>
				<div class="detail_item">
					<div class="detail_label">审核教师</div>
					<div class="detail_value">{{ auditDetail.teacher }}</div>
				</div>
			</div>
			<template #footer>
				<el-button @click="showAuditDetail = false">关闭</el-button>
			</template>
		</el-dialog>

	</div>
</template>

<script setup>
import { ref, computed, onMounted, getCurrentInstance } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { resolveFileUrl } from '@/utils/fileUrl'
import { downloadStoredFile, normalizeFileName } from '@/utils/fileDownload'
import {
	isAppliedStatus,
	isRejectedStatus,
	isPendingStatus,
	normalizeApplicationStatus,
	extractWorkflowAuditReason,
	getWorkflowAuditReasonLabel,
	hasWorkflowAuditFeedback
} from '@/utils/xuantishenqingStatus'
import {
	Document, Upload, Download, View, Edit, Clock, Check,
	CircleCheckFilled, Warning, InfoFilled,
	Loading, Operation, ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()
const context = getCurrentInstance()?.appContext.config.globalProperties
const loading = ref(false)
const records = ref([])
const pageStatus = ref('unsubmitted')

const latestRecord = computed(() => records.value[0] || null)

const mapFileStatus = (row) => {
	if (!row) return { css: 'pending', text: '未提交' }
	const status = row.shenhezhuangtai
	if (isAppliedStatus(status)) return { css: 'passed', text: '已通过' }
	if (isRejectedStatus(status)) return { css: 'rejected', text: '已驳回' }
	return { css: 'reviewing', text: '审核中' }
}

const submittedRecord = computed(() => {
	const row = latestRecord.value
	if (!row) {
		return { fileName: '', submitTime: '', fileSize: '', status: 'pending', statusText: '未提交', filePath: '' }
	}
	const filePath = row.kaitibaogao || ''
	const fileName = filePath ? normalizeFileName(filePath) : '开题报告'
	const audit = mapFileStatus(row)
	return {
		id: row.id,
		fileName,
		submitTime: row.tijiaoshijian || row.addtime || '',
		fileSize: '-',
		status: audit.css,
		statusText: audit.text,
		filePath
	}
})

const auditDetail = computed(() => {
	const row = latestRecord.value
	if (!row) return null
	const status = normalizeApplicationStatus(row.shenhezhuangtai)
	const reasonText = extractWorkflowAuditReason(row)
	const reasonLabel = getWorkflowAuditReasonLabel(row.shenhezhuangtai)
	let reason = reasonText
	if (!reason) {
		if (isPendingStatus(row.shenhezhuangtai)) {
			reason = '暂无，请等待教师审核'
		} else {
			reason = '教师未填写审核意见'
		}
	}
	return {
		status,
		statusClass: isAppliedStatus(row.shenhezhuangtai)
			? 'success'
			: (isRejectedStatus(row.shenhezhuangtai) ? 'danger' : ''),
		time: row.tijiaoshijian || row.addtime || '-',
		reason,
		reasonLabel,
		teacher: row.jiaoshixingming || '-'
	}
})

const auditFeedbackVisible = computed(() => {
	const row = latestRecord.value
	return !!(row && hasWorkflowAuditFeedback(row))
})

const auditSteps = computed(() => {
	const row = latestRecord.value
	if (!row) return []
	const submitTime = row.tijiaoshijian || row.addtime || ''
	const steps = [
		{
			name: '已提交',
			desc: row.ketimingcheng || '开题报告已上传',
			status: 'completed',
			time: submitTime
		}
	]
	if (isPendingStatus(row.shenhezhuangtai)) {
		steps.push({
			name: '导师审核中',
			desc: '预计 1-3 个工作日',
			status: 'processing',
			time: ''
		})
		steps.push({
			name: '审核结果',
			desc: '等待教师给出审核结论',
			status: 'pending',
			time: ''
		})
	} else if (isAppliedStatus(row.shenhezhuangtai)) {
		steps.push({
			name: '审核通过',
			desc: extractWorkflowAuditReason(row) || '教师已通过您的开题报告',
			status: 'completed',
			time: submitTime
		})
	} else if (isRejectedStatus(row.shenhezhuangtai)) {
		steps.push({
			name: '审核驳回',
			desc: extractWorkflowAuditReason(row) || '请根据教师意见修改后重新提交',
			status: 'completed',
			time: submitTime
		})
	}
	return steps
})

const historyRecords = computed(() =>
	records.value.map((row, index) => ({
		version: records.value.length - index,
		fileName: row.kaitibaogao ? row.kaitibaogao.split('/').pop() : '开题报告',
		date: row.tijiaoshijian || row.addtime || '',
		status: 'passed'
	}))
)

// 文件上传
const isDragover = ref(false)
const handleDrop = (e) => {
	isDragover.value = false
	const files = e.dataTransfer?.files
	if (files?.length) {
		uploadFile(files[0])
	}
}
const handleFileSelect = (e) => {
	const files = e.target?.files
	if (files?.length) {
		uploadFile(files[0])
	}
}
const uploadFile = () => {
	goAdd()
}

const openFile = (filePath) => {
	const url = resolveFileUrl(filePath)
	if (!url) {
		context?.$toolUtil.message('文件不存在', 'error')
		return
	}
	window.open(url, '_blank')
}

const syncPageStatus = () => {
	const row = records.value[0]
	if (!row) {
		pageStatus.value = 'unsubmitted'
		return
	}
	pageStatus.value = isPendingStatus(row.shenhezhuangtai) ? 'reviewing' : 'submitted'
}

const loadRecords = () => {
	loading.value = true
	context?.$http({
		url: 'kaitibaogao/page',
		method: 'get',
		params: { page: 1, limit: 20, sort: 'id', order: 'desc' }
	}).then(res => {
		const pageData = res.data.data || {}
		records.value = pageData.list || []
		syncPageStatus()
	}).catch(() => {
		records.value = []
		pageStatus.value = 'unsubmitted'
	}).finally(() => {
		loading.value = false
	})
}

const goAdd = () => router.push('/index/kaitibaogaoAdd')
const goReupload = () => {
	const id = latestRecord.value?.id || submittedRecord.value?.id
	if (id) {
		router.push(`/index/kaitibaogaoAdd?type=edit&id=${id}`)
	} else {
		router.push('/index/kaitibaogaoAdd?type=edit')
	}
}
const revokeSubmit = () => {
	ElMessageBox.confirm(
		'撤销后将删除当前开题报告记录，您可以重新上传并提交。若已提交论文初稿则无法撤销。',
		'确认撤销开题报告？',
		{ type: 'warning', confirmButtonText: '确认撤销', cancelButtonText: '取消' }
	).then(() => {
		return context?.$http({
			url: 'kaitibaogao/revoke',
			method: 'post',
		})
	}).then(() => {
		context?.$toolUtil.message('开题报告已撤销，请重新提交', 'success')
		loadRecords()
	}).catch(err => {
		if (err === 'cancel') return
		const msg = err?.data?.msg || err?.response?.data?.msg || err?.message
		if (msg) context?.$toolUtil.message(msg, 'error')
	})
}
const previewFile = () => openFile(submittedRecord.value.filePath)
const downloadFile = () => {
	const filePath = submittedRecord.value?.filePath
	if (!filePath) {
		context?.$toolUtil.message('文件不存在', 'error')
		return
	}
	downloadStoredFile(filePath).catch(err => {
		context?.$toolUtil.message(err?.message || '下载失败', 'error')
	})
}
const downloadTemplate = () => {
	// 实际项目中下载模板
	console.log('下载模板')
}

// 弹窗状态
const showAuditDetail = ref(false)

onMounted(() => {
	loadRecords()
})
</script>

<style lang="scss" scoped>
// 主题变量
$bg-dark: #061630;
$bg-panel: #132f4c;
$border-color: #1e4976;
$cyan: #00bcd4;
$cyan-dark: #008ba3;
$text-main: #e0e0e0;
$text-muted: #90a4ae;
$success: #4caf50;
$warning: #ff9800;
$danger: #f44336;

.kaiti_page {
	padding: 16px 20px;
	min-height: calc(100vh - 120px);
	background: $bg-dark;
}

// 主内容区
.content_wrapper {
	min-height: 400px;
}

.unsubmitted_content {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

// 通用卡片
.info_card, .sidebar_card {
	background: $bg-panel;
	border: 1px solid $border-color;
	border-radius: 12px;
	padding: 16px;
	transition: all 0.3s;
	
	&:hover {
		transform: translateY(-2px);
		box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
	}
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
	&.warning {
		color: $warning;
	}
}

.card_title {
	font-size: 15px;
	font-weight: 600;
	color: $cyan;
}

.card_body {
	color: $text-main;
}

// 快速操作卡片
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

// 历史记录
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

// 已提交内容
.submitted_content {
	max-width: 800px;
	margin: 0 auto;
	display: flex;
	flex-direction: column;
	gap: 20px;
}

.record_card, .audit_card {
	background: $bg-panel;
	border: 1px solid $border-color;
	border-radius: 12px;
	padding: 20px;
	
	&:hover {
		transform: translateY(-2px);
		box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
	}
}

.record_file {
	display: flex;
	align-items: center;
	gap: 14px;
	padding: 16px;
	background: rgba(0, 0, 0, 0.2);
	border-radius: 10px;
	margin-bottom: 16px;
}

.file_icon {
	width: 48px;
	height: 48px;
	border-radius: 10px;
	background: rgba($cyan, 0.15);
	display: flex;
	align-items: center;
	justify-content: center;
	color: $cyan;
	font-size: 24px;
}

.file_info {
	flex: 1;
}

.file_name {
	font-size: 14px;
	font-weight: 500;
	color: $text-main;
	margin-bottom: 6px;
}

.file_meta {
	display: flex;
	gap: 16px;
	font-size: 12px;
	color: $text-muted;
}

.file_status {
	display: flex;
	align-items: center;
	gap: 6px;
	padding: 6px 12px;
	border-radius: 20px;
	font-size: 12px;
	font-weight: 500;
	
	&.passed {
		background: rgba($success, 0.15);
		color: $success;
	}
	&.rejected {
		background: rgba($danger, 0.15);
		color: $danger;
	}
	&.pending, &.reviewing {
		background: rgba($warning, 0.15);
		color: $warning;
	}
	.spin {
		animation: spin 1s linear infinite;
	}
}

@keyframes spin {
	from { transform: rotate(0deg); }
	to { transform: rotate(360deg); }
}

.record_actions {
	display: flex;
	gap: 10px;
	
	.el-button {
		background: rgba($cyan, 0.1);
		border-color: $border-color;
		color: $text-main;
		display: flex;
		align-items: center;
		gap: 6px;
		&:hover {
			background: rgba($cyan, 0.2);
			border-color: $cyan;
			color: $cyan;
		}
	}
}

// 审核进度
.audit_steps {
	display: flex;
	flex-direction: column;
	gap: 0;
}

.audit_step {
	display: flex;
	gap: 14px;
	padding: 16px 0;
	border-bottom: 1px solid $border-color;
	position: relative;
	
	&:last-child {
		border-bottom: none;
	}
	
	&.completed {
		.audit_indicator {
			background: $success;
			border-color: $success;
			color: #fff;
		}
	}
	
	&.processing {
		.audit_indicator {
			background: $cyan;
			border-color: $cyan;
			color: #fff;
		}
	}
	
	&.pending {
		.audit_indicator {
			background: rgba($text-muted, 0.2);
			border-color: $border-color;
			color: $text-muted;
		}
		.audit_content {
			opacity: 0.6;
		}
	}
}

.audit_indicator {
	width: 36px;
	height: 36px;
	border-radius: 50%;
	border: 2px solid;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 14px;
	font-weight: 600;
	flex-shrink: 0;
}

.audit_content {
	flex: 1;
}

.audit_name {
	font-size: 14px;
	font-weight: 500;
	color: $text-main;
	margin-bottom: 4px;
}

.audit_desc {
	font-size: 12px;
	color: $text-muted;
}

.audit_time {
	font-size: 11px;
	color: $text-muted;
	margin-top: 4px;
}

.audit_actions {
	padding-top: 12px;
	border-top: 1px solid $border-color;
	
	.el-button {
		color: $cyan;
	}
}

// 弹窗样式
:deep(.el-dialog) {
	background: $bg-panel;
	border: 1px solid $border-color;
	border-radius: 12px;
	
	.el-dialog__header {
		border-bottom: 1px solid $border-color;
		padding: 16px 20px;
		.el-dialog__title {
			color: $cyan;
			font-weight: 600;
		}
	}
	
	.el-dialog__body {
		padding: 20px;
	}
	
	.el-dialog__footer {
		border-top: 1px solid $border-color;
		padding: 14px 20px;
	}
}

.template_content {
	h4 {
		text-align: center;
		color: $text-main;
		margin: 0 0 20px;
	}
	h5 {
		color: $cyan;
		margin: 16px 0 8px;
		font-size: 14px;
	}
	p {
		color: $text-muted;
		font-size: 13px;
		line-height: 1.6;
		margin: 0;
	}
}

.audit_feedback_card {
	.audit_feedback_text {
		font-size: 14px;
		line-height: 1.7;
		color: $text-main;
		white-space: pre-wrap;
		word-break: break-word;
	}
	.audit_feedback_meta {
		margin-top: 10px;
		font-size: 12px;
		color: $text-muted;
	}
}

.audit_detail_content {
	.detail_item {
		display: flex;
		padding: 12px 0;
		border-bottom: 1px solid $border-color;
		&:last-child {
			border-bottom: none;
		}
	}
	.detail_label {
		width: 100px;
		color: $text-muted;
		font-size: 13px;
		flex-shrink: 0;
	}
	.detail_value {
		flex: 1;
		color: $text-main;
		font-size: 13px;
		white-space: pre-wrap;
		word-break: break-word;
		&.success {
			color: $success;
		}
		&.danger {
			color: $danger;
		}
	}
}

// 演示切换
.demo_switch {
	position: fixed;
	bottom: 20px;
	right: 20px;
	background: $bg-panel;
	border: 1px solid $border-color;
	border-radius: 10px;
	padding: 12px 16px;
	display: flex;
	align-items: center;
	gap: 12px;
	z-index: 1000;
	
	> span {
		font-size: 12px;
		color: $text-muted;
	}
	
	:deep(.el-radio-button__inner) {
		background: rgba($cyan, 0.1);
		border-color: $border-color;
		color: $text-main;
	}
	
	:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
		background: $cyan;
		border-color: $cyan;
		color: #fff;
	}
}

// 响应式
@media (max-width: 900px) {
	.content_grid {
		grid-template-columns: 1fr;
	}
	.flow_steps {
		flex-wrap: wrap;
		&::before {
			display: none;
		}
	}
	.flow_step_item {
		max-width: none;
		flex: 0 0 50%;
	}
	}
</style>
