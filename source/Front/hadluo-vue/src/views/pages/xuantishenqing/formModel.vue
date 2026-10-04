<template>
	<div class="detail_page" v-loading="pageLoading">
		<header class="page_header">
			<div class="page_header_top">
				<el-breadcrumb separator="/" class="page_breadcrumb">
					<el-breadcrumb-item :to="{ path: '/index/xuantishenqingList' }">毕设流程</el-breadcrumb-item>
					<el-breadcrumb-item :to="{ path: '/index/xuantishenqingList' }">选题申请</el-breadcrumb-item>
					<el-breadcrumb-item>申请详情</el-breadcrumb-item>
				</el-breadcrumb>
				<el-button class="back_btn" @click="backClick">
					<el-icon><ArrowLeft /></el-icon>
					返回
				</el-button>
			</div>
			<h1 class="page_title">选题申请详情</h1>
		</header>

		<template v-if="!pageLoading && detail.id">
			<section class="hero_card">
				<div class="hero_cover">
					<template v-if="coverList.length">
						<el-image
							:src="currentCover"
							fit="cover"
							class="cover_main"
							:preview-src-list="coverList"
							:initial-index="currentCoverIndex"
							preview-teleported
						/>
						<div class="cover_thumbs" v-if="coverList.length > 1">
							<div
								v-for="(img, idx) in coverList"
								:key="img + idx"
								class="thumb_item"
								:class="{ thumb_active: idx === currentCoverIndex }"
								@click="switchCover(idx)"
							>
								<el-image :src="img" fit="cover" class="thumb_img" />
							</div>
						</div>
					</template>
					<div v-else class="cover_placeholder">
						<el-icon><Picture /></el-icon>
						<span>暂无题目封面</span>
					</div>
				</div>

				<div class="hero_main">
					<div class="status_row">
						<span class="status_tag" :class="'status_' + statusTag.type">
							<el-icon v-if="statusTag.type === 'approved'"><CircleCheckFilled /></el-icon>
							<el-icon v-else-if="statusTag.type === 'rejected'"><CloseBold /></el-icon>
							<el-icon v-else><Clock /></el-icon>
							{{ statusTag.text }}
						</span>
						<span class="apply_time" v-if="detail.shenqingshijian">
							<el-icon><Calendar /></el-icon>
							{{ formatDateTime(detail.shenqingshijian) }}
						</span>
					</div>

					<h2 class="topic_title">{{ detail.ketimingcheng || '-' }}</h2>

					<div class="topic_meta">
						<div class="meta_chip">
							<el-icon><Collection /></el-icon>
							<span>{{ detail.timuleixing || '-' }}</span>
						</div>
						<div class="meta_chip">
							<el-icon><School /></el-icon>
							<span>{{ detail.zhuanye || '-' }}</span>
						</div>
						<div class="meta_chip">
							<el-icon><User /></el-icon>
							<span>{{ detail.jiaoshixingming || '-' }}</span>
						</div>
					</div>

					<div class="topic_code" @click="copyCode(detail.timubianhao)" title="点击复制题目编号">
						<el-icon><DocumentCopy /></el-icon>
						<span>{{ detail.timubianhao || '-' }}</span>
					</div>
				</div>
			</section>

			<section class="panel_card">
				<div class="panel_title">
					<el-icon><Document /></el-icon>
					<span>题目信息</span>
				</div>
				<div class="info_grid">
					<div v-for="field in topicFields" :key="field.label" class="info_cell">
						<div class="info_label">{{ field.label }}</div>
						<div class="info_value">{{ field.value }}</div>
					</div>
				</div>
			</section>

			<section class="panel_card">
				<div class="panel_title">
					<el-icon><Avatar /></el-icon>
					<span>指导教师</span>
				</div>
				<div class="info_grid">
					<div v-for="field in teacherFields" :key="field.label" class="info_cell">
						<div class="info_label">{{ field.label }}</div>
						<div class="info_value">{{ field.value }}</div>
					</div>
				</div>
			</section>

			<section class="panel_card">
				<div class="panel_title">
					<el-icon><EditPen /></el-icon>
					<span>申请信息</span>
				</div>
				<div class="info_grid">
					<div v-for="field in studentFields" :key="field.label" class="info_cell">
						<div class="info_label">{{ field.label }}</div>
						<div class="info_value">{{ field.value }}</div>
					</div>
				</div>
				<div class="reason_block" v-if="detail.shenqingyuanyin">
					<div class="reason_label">申请原因</div>
					<div class="text_block">{{ displayReason }}</div>
				</div>
			</section>

			<section class="panel_card" v-if="detail.ketixingzhi">
				<div class="panel_title">
					<el-icon><Reading /></el-icon>
					<span>课题性质</span>
				</div>
				<div class="text_block">{{ detail.ketixingzhi }}</div>
			</section>

			<div class="action_row" v-if="hasActions">
				<el-button
					class="ghost_btn"
					v-if="btnFrontAuth(tableName, '审核建议')"
					@click="shenhejianyionAcross('审核建议', 'shenhejianyi', '', 'shenhezhuangtai', '通过', '通过,未审核'.split(',')[0])"
				>
					<el-icon><ChatLineSquare /></el-icon>
					审核建议
				</el-button>
				<el-button
					v-if="centerType && (detail.ispay == '未支付' || !detail.ispay) && btnFrontAuth('xuantishenqing', '支付')"
					class="ghost_btn"
					@click="payClick"
				>支付</el-button>
				<el-button
					class="ghost_btn"
					v-if="centerType && btnAuth('xuantishenqing', '修改')"
					@click="editClick"
				>
					<el-icon><Edit /></el-icon>
					修改
				</el-button>
				<el-button
					class="danger_btn"
					v-if="centerType && btnAuth('xuantishenqing', '删除')"
					@click="delClick"
				>
					<el-icon><Delete /></el-icon>
					删除
				</el-button>
			</div>
		</template>

		<div v-else-if="!pageLoading" class="empty_panel">
			<el-empty description="未找到申请记录或记录已被删除">
				<el-button type="primary" @click="router.push('/index/xuantishenqingList')">返回列表</el-button>
			</el-empty>
		</div>
	</div>
</template>

<script setup>
import { downloadStoredFile } from '@/utils/fileDownload'
import { ref, computed, getCurrentInstance, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
	ArrowLeft, Document, Avatar, EditPen, Reading, Calendar, Collection,
	School, User, DocumentCopy, CircleCheckFilled, Clock, CloseBold,
	ChatLineSquare, Edit, Delete, Picture
} from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import {
	isAppliedStatus,
	isRejectedStatus,
	normalizeApplicationStatus
} from '@/utils/xuantishenqingStatus'

const context = getCurrentInstance()?.appContext.config.globalProperties
const route = useRoute()
const router = useRouter()
const tableName = 'xuantishenqing'
const formName = '选题申请'

const pageLoading = ref(false)
const centerType = ref(false)
const detail = ref({})
const topicCoverRaw = ref('')
const currentCoverIndex = ref(0)

const coverList = computed(() => {
	const raw = topicCoverRaw.value
	if (!raw) return []
	return String(raw).split(',').map(item => item.trim()).filter(Boolean).map(item => {
		if (item.startsWith('http')) return item
		return `${context?.$config.url}${item}`
	})
})
const currentCover = computed(() => {
	if (!coverList.value.length) return ''
	const maxIndex = coverList.value.length - 1
	const safeIndex = Math.min(Math.max(currentCoverIndex.value, 0), maxIndex)
	return coverList.value[safeIndex]
})
const switchCover = (index) => {
	currentCoverIndex.value = index
}

const btnAuth = (e, a) => centerType.value
	? context?.$toolUtil.isBackAuth(e, a)
	: context?.$toolUtil.isAuth(e, a)

const btnFrontAuth = (e, a) => centerType.value
	? context?.$toolUtil.isBackAuth(e, a)
	: context?.$toolUtil.isFrontAuth(e, a)

const formatDateTime = (val) => {
	if (!val) return '-'
	const d = new Date(val)
	if (Number.isNaN(d.getTime())) return String(val)
	return d.toLocaleString('zh-CN', {
		year: 'numeric',
		month: '2-digit',
		day: '2-digit',
		hour: '2-digit',
		minute: '2-digit',
		second: '2-digit',
		hour12: false
	})
}

const displayReason = computed(() => {
	const raw = detail.value?.shenqingyuanyin
	if (!raw) return ''
	return String(raw).replace(/\n\[驳回原因\]:[^\n]*/g, '').trim()
})

const statusTag = computed(() => {
	const status = detail.value?.shenhezhuangtai
	if (isAppliedStatus(status)) return { text: '通过', type: 'approved' }
	if (isRejectedStatus(status)) return { text: '驳回', type: 'rejected' }
	return { text: normalizeApplicationStatus(status), type: 'pending' }
})

const topicFields = computed(() => [
	{ label: '题目编号', value: detail.value.timubianhao || '-' },
	{ label: '题目类型', value: detail.value.timuleixing || '-' },
	{ label: '专业', value: detail.value.zhuanye || '-' },
	{ label: '选题时间', value: detail.value.xuantishijian || '-' }
])

const teacherFields = computed(() => [
	{ label: '教师工号', value: detail.value.jiaoshigonghao || '-' },
	{ label: '教师姓名', value: detail.value.jiaoshixingming || '-' }
])

const studentFields = computed(() => [
	{ label: '学号', value: detail.value.xuehao || '-' },
	{ label: '学生姓名', value: detail.value.xueshengxingming || '-' },
	{ label: '申请时间', value: formatDateTime(detail.value.shenqingshijian) },
	{ label: '审核状态', value: normalizeApplicationStatus(detail.value.shenhezhuangtai) }
])

const hasActions = computed(() => {
	return btnFrontAuth(tableName, '审核建议')
		|| (centerType.value && btnAuth('xuantishenqing', '修改'))
		|| (centerType.value && btnAuth('xuantishenqing', '删除'))
		|| (centerType.value && (detail.value.ispay == '未支付' || !detail.value.ispay) && btnFrontAuth('xuantishenqing', '支付'))
})

const backClick = () => history.back()

const copyCode = (code) => {
	if (!code) return
	navigator.clipboard?.writeText(code).then(() => {
		ElMessage.success('题目编号已复制')
	}).catch(() => {
		ElMessage.info(code)
	})
}

const loadTopicCover = async (record) => {
	topicCoverRaw.value = ''
	currentCoverIndex.value = 0
	if (!record) return
	if (record.crossrefid) {
		try {
			const res = await context?.$http({
				url: `timuxinxi/detail/${record.crossrefid}`,
				method: 'get'
			})
			const topic = res?.data?.data
			if (topic?.timufengmian) {
				topicCoverRaw.value = topic.timufengmian
				return
			}
		} catch {
			// fallback by topic number
		}
	}
	if (!record.timubianhao) return
	try {
		const res = await context?.$http({
			url: 'timuxinxi/list',
			method: 'get',
			params: { page: 1, limit: 1, timubianhao: record.timubianhao }
		})
		const topic = res?.data?.data?.list?.[0]
		if (topic?.timufengmian) {
			topicCoverRaw.value = topic.timufengmian
		}
	} catch {
		topicCoverRaw.value = ''
	}
}

const getDetail = () => {
	pageLoading.value = true
	context?.$http({
		url: `${tableName}/detail/${route.query.id}`,
		method: 'get'
	}).then(async res => {
		detail.value = res.data.data || {}
		await loadTopicCover(detail.value)
	}).finally(() => {
		pageLoading.value = false
	})
}

const downClick = (file) => {
	if (!file) {
		context?.$toolUtil.message('文件不存在', 'error')
		return
	}
	downloadStoredFile(file).catch(err => {
		context?.$toolUtil.message(err?.message || '下载失败', 'error')
	})
}

const payClick = () => {}

const editClick = () => {
	router.push(`/index/${tableName}Add?id=${detail.value.id}&type=edit`)
}

const delClick = () => {
	ElMessageBox.confirm(`是否删除此${formName}？`, '提示', {
		confirmButtonText: '是',
		cancelButtonText: '否',
		type: 'warning'
	}).then(() => {
		context?.$http({
			url: `${tableName}/delete`,
			method: 'post',
			data: [detail.value.id]
		}).then(() => {
			context?.$toolUtil.message('删除成功', 'success', () => history.back())
		})
	})
}

const shenhejianyionAcross = (btnType, table, crossOptAudit, statusColumnName, tips, statusColumnValue) => {
	if (!context?.$toolUtil.storageGet('frontToken')) {
		context?.$toolUtil.message('请登录后再操作！', 'error')
		return false
	}
	if (!btnAuth(tableName, btnType)) {
		context?.$toolUtil.message('暂无权限操作！', 'error')
		return false
	}
	context?.$toolUtil.storageSet('crossObj', JSON.stringify(detail.value))
	context?.$toolUtil.storageSet('crossTable', tableName)
	context?.$toolUtil.storageSet('crossStatusColumnName', statusColumnName)
	context?.$toolUtil.storageSet('crossTips', tips)
	context?.$toolUtil.storageSet('crossStatusColumnValue', statusColumnValue)
	if (statusColumnName !== '' && !statusColumnName.startsWith('[')) {
		const obj = detail.value
		for (const o in obj) {
			if (o === statusColumnName && obj[o] === statusColumnValue) {
				context?.$toolUtil.message(tips, 'error')
				return
			}
		}
	}
	nextTick(() => {
		router.push(`/index/${table}Add?type=cross&id=${detail.value.id}`)
	})
}

onMounted(() => {
	if (route.query.centerType) centerType.value = true
	getDetail()
})
</script>

<style lang="scss" scoped>
.detail_page {
	width: 100%;
	min-height: 100%;
	padding: 0 20px 48px;
	box-sizing: border-box;
}

.page_header {
	padding: 20px 0 16px;
}

.page_header_top {
	display: flex;
	align-items: center;
	justify-content: space-between;
	flex-wrap: wrap;
	gap: 12px;
}

.page_breadcrumb {
	font-size: 13px;
	:deep(.el-breadcrumb__inner) {
		color: #67e8f9;
		font-weight: 400;
	}
	:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
		color: var(--tech-cyan);
		font-weight: 500;
	}
	:deep(.el-breadcrumb__separator) {
		color: rgba(255, 255, 255, 0.35);
	}
}

.back_btn {
	border: 1px solid rgba(34, 211, 238, 0.45);
	border-radius: 8px;
	padding: 8px 16px;
	color: var(--tech-cyan);
	background: rgba(34, 211, 238, 0.08);
	&:hover {
		color: #fff;
		background: linear-gradient(135deg, var(--tech-cyan) 0%, var(--tech-cyan-dark) 100%);
		border-color: var(--tech-cyan);
	}
}

.page_title {
	margin: 10px 0 0;
	font-size: 22px;
	font-weight: 600;
	color: var(--tech-cyan);
}

.hero_card {
	display: grid;
	grid-template-columns: 320px 1fr;
	gap: 24px;
	background: var(--tech-panel);
	border: 1px solid var(--tech-border-soft);
	border-radius: 16px;
	padding: 24px;
	margin-bottom: 16px;
	box-shadow: 0 4px 24px rgba(0, 0, 0, 0.25);
}

.hero_cover {
	min-width: 0;
}

.cover_main {
	width: 100%;
	height: 240px;
	border-radius: 12px;
	border: 1px solid rgba(255, 255, 255, 0.08);
	background: #0a1929;
	display: block;
}

.cover_placeholder {
	height: 240px;
	border-radius: 12px;
	border: 1px dashed rgba(34, 211, 238, 0.25);
	background: rgba(10, 25, 41, 0.8);
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	gap: 10px;
	color: rgba(255, 255, 255, 0.35);
	font-size: 14px;
	.el-icon { font-size: 42px; }
}

.cover_thumbs {
	margin-top: 12px;
	display: flex;
	gap: 8px;
	flex-wrap: wrap;
}

.thumb_item {
	width: 72px;
	height: 54px;
	border-radius: 8px;
	overflow: hidden;
	border: 2px solid transparent;
	cursor: pointer;
	transition: border-color 0.2s;
	&:hover { border-color: rgba(34, 211, 238, 0.45); }
	&.thumb_active {
		border-color: var(--tech-cyan);
		box-shadow: 0 0 0 2px rgba(34, 211, 238, 0.15);
	}
}

.thumb_img {
	width: 100%;
	height: 100%;
}

.hero_main {
	min-width: 0;
}

.status_row {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 12px;
	margin-bottom: 14px;
}

.status_tag {
	display: inline-flex;
	align-items: center;
	gap: 5px;
	padding: 4px 12px;
	border-radius: 20px;
	font-size: 12px;
	font-weight: 600;
	&.status_pending {
		background: rgba(245, 158, 11, 0.15);
		color: #fcd34d;
		border: 1px solid rgba(245, 158, 11, 0.35);
	}
	&.status_approved {
		background: rgba(52, 211, 153, 0.15);
		color: #86efac;
		border: 1px solid rgba(52, 211, 153, 0.35);
	}
	&.status_rejected {
		background: rgba(239, 68, 68, 0.12);
		color: #fca5a5;
		border: 1px solid rgba(239, 68, 68, 0.35);
	}
}

.apply_time {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	font-size: 13px;
	color: rgba(255, 255, 255, 0.5);
}

.topic_title {
	margin: 0 0 16px;
	font-size: 22px;
	font-weight: 600;
	line-height: 1.45;
	color: #e2e8f0;
}

.topic_meta {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
	margin-bottom: 14px;
}

.meta_chip {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	padding: 6px 12px;
	border-radius: 8px;
	font-size: 13px;
	color: rgba(255, 255, 255, 0.78);
	background: rgba(8, 28, 58, 0.55);
	border: 1px solid rgba(34, 211, 238, 0.12);
}

.topic_code {
	display: inline-flex;
	align-items: center;
	gap: 8px;
	padding: 8px 14px;
	border-radius: 10px;
	font-size: 13px;
	color: var(--tech-cyan);
	background: rgba(34, 211, 238, 0.08);
	border: 1px dashed rgba(34, 211, 238, 0.28);
	cursor: pointer;
	transition: background 0.2s, border-color 0.2s;
	&:hover {
		background: rgba(34, 211, 238, 0.14);
		border-color: rgba(34, 211, 238, 0.45);
	}
}

.panel_card {
	background: var(--tech-panel);
	border: 1px solid var(--tech-border-soft);
	border-radius: 16px;
	padding: 20px 24px;
	margin-bottom: 16px;
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
}

.panel_title {
	display: flex;
	align-items: center;
	gap: 8px;
	margin-bottom: 18px;
	padding-bottom: 12px;
	border-bottom: 1px solid rgba(34, 211, 238, 0.12);
	font-size: 15px;
	font-weight: 600;
	color: var(--tech-cyan);
	.el-icon { font-size: 18px; }
}

.info_grid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 16px 24px;
}

.info_cell {
	min-width: 0;
}

.info_label {
	margin-bottom: 6px;
	font-size: 12px;
	color: rgba(255, 255, 255, 0.45);
}

.info_value {
	font-size: 14px;
	line-height: 1.5;
	color: #e2e8f0;
	word-break: break-word;
}

.reason_block {
	margin-top: 18px;
	padding-top: 16px;
	border-top: 1px solid rgba(34, 211, 238, 0.1);
}

.reason_label {
	margin-bottom: 10px;
	font-size: 12px;
	color: rgba(255, 255, 255, 0.45);
}

.text_block {
	padding: 14px 16px;
	border-radius: 10px;
	font-size: 14px;
	line-height: 1.7;
	color: rgba(255, 255, 255, 0.82);
	background: rgba(8, 28, 58, 0.55);
	border: 1px solid rgba(34, 211, 238, 0.1);
	white-space: pre-wrap;
}

.action_row {
	display: flex;
	flex-wrap: wrap;
	gap: 12px;
	margin-top: 8px;
}

.ghost_btn {
	border: 1px solid rgba(34, 211, 238, 0.35);
	border-radius: 10px;
	color: var(--tech-cyan);
	background: rgba(34, 211, 238, 0.06);
	&:hover {
		color: #fff;
		background: rgba(34, 211, 238, 0.18);
		border-color: var(--tech-cyan);
	}
}

.danger_btn {
	border: 1px solid rgba(239, 68, 68, 0.4);
	border-radius: 10px;
	color: #fca5a5;
	background: rgba(239, 68, 68, 0.08);
	&:hover {
		color: #fff;
		background: rgba(239, 68, 68, 0.55);
		border-color: #ef4444;
	}
}

@media (max-width: 768px) {
	.hero_card {
		grid-template-columns: 1fr;
	}
	.info_grid {
		grid-template-columns: 1fr;
	}
}

.empty_panel {
	padding: 60px 20px;
	:deep(.el-empty__description) {
		color: rgba(255, 255, 255, 0.5);
	}
}
</style>
