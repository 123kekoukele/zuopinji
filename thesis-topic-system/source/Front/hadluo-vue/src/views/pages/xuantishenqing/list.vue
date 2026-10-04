<template>
	<div class="list_page">
		<header class="page_header">
			<div class="page_header_top">
				<el-breadcrumb separator="/" class="page_breadcrumb">
					<el-breadcrumb-item :to="{ path: '/index/xuantishenqingList' }">毕设流程</el-breadcrumb-item>
					<el-breadcrumb-item>选题申请</el-breadcrumb-item>
				</el-breadcrumb>
				<div class="page_stats">
					<span class="stat_item">
						<span class="stat_num">{{ total }}</span>
						<span class="stat_text">条申请</span>
					</span>
				</div>
			</div>
			<h1 class="page_title">我的选题申请</h1>
		</header>

		<div class="filter_card">
			<div class="back_view" v-if="centerType">
				<el-button class="back_btn" @click="backClick">返回</el-button>
			</div>
			<div class="search_row">
				<el-input
					v-model="searchQuery.timubianhao"
					placeholder="输入题目编号"
					clearable
					class="search_input"
					@keyup.enter="searchClick"
				>
					<template #prefix>
						<el-icon><Search /></el-icon>
					</template>
				</el-input>
				<el-button type="primary" class="search_btn" @click="searchClick">
					<el-icon><Search /></el-icon>
					搜索
				</el-button>
				<el-button class="reset_btn" @click="resetSearch">
					<el-icon><RefreshRight /></el-icon>
					重置
				</el-button>
				<el-button class="add_btn" type="primary" v-if="btnAuth('xuantishenqing','新增')" @click="addClick">
					<el-icon><Plus /></el-icon>
					新增申请
				</el-button>
			</div>
		</div>

		<div class="cards_container" v-loading="listLoading">
			<div v-if="!listLoading && list.length === 0" class="empty_state">
				<el-empty description="暂无选题申请">
					<el-button type="primary" @click="router.push('/index/timuxinxiList')">去选题</el-button>
				</el-empty>
			</div>

			<div
				v-for="(item, index) in list"
				:key="item.id"
				class="apply_card"
				@click="tableDetailClick(item)"
			>
				<div class="card_header">
					<div class="card_index">{{ (listQuery.page - 1) * listQuery.limit + index + 1 }}</div>
					<div class="card_code">
						<el-icon><DocumentCopy /></el-icon>
						<span>{{ item.timubianhao }}</span>
					</div>
					<div class="card_status" :class="getStatusClass(item.shenhezhuangtai)">
						<el-icon v-if="isAppliedStatus(item.shenhezhuangtai)"><CircleCheckFilled /></el-icon>
						<el-icon v-else-if="isRejectedStatus(item.shenhezhuangtai)"><CircleCloseFilled /></el-icon>
						<el-icon v-else><Clock /></el-icon>
						<span>{{ normalizeApplicationStatus(item.shenhezhuangtai) }}</span>
					</div>
				</div>

				<div class="card_body">
					<h3 class="card_title">{{ item.ketimingcheng || '未命名课题' }}</h3>
				</div>

				<div class="card_meta">
					<div class="meta_item">
						<el-icon><Collection /></el-icon>
						<span>{{ item.timuleixing || '-' }}</span>
					</div>
					<div class="meta_item">
						<el-icon><User /></el-icon>
						<span>{{ item.jiaoshixingming || '-' }}</span>
					</div>
					<div class="meta_item">
						<el-icon><Calendar /></el-icon>
						<span>{{ formatDate(item.shenqingshijian || item.addtime) }}</span>
					</div>
				</div>

				<div class="card_footer">
					<el-button link type="primary" @click.stop="tableDetailClick(item)">
						查看详情
						<el-icon><ArrowRight /></el-icon>
					</el-button>
				</div>
			</div>
		</div>

		<div class="pagination_wrap" v-if="total > 0">
			<el-pagination
				background
				:layout="layouts.join(',')"
				:total="total"
				:page-size="listQuery.limit"
				:current-page="listQuery.page"
				prev-text="上一页"
				next-text="下一页"
				@size-change="sizeChange"
				@current-change="currentChange"
				@prev-click="prevClick"
				@next-click="nextClick"
			/>
		</div>
	</div>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
	Search, RefreshRight, Plus, DocumentCopy, CircleCheckFilled,
	CircleCloseFilled, Clock, Collection, User, Calendar, ArrowRight
} from '@element-plus/icons-vue'
import {
	isAppliedStatus,
	isRejectedStatus,
	normalizeApplicationStatus
} from '@/utils/xuantishenqingStatus'

const context = getCurrentInstance()?.appContext.config.globalProperties
const router = useRouter()
const route = useRoute()

const tableName = 'xuantishenqing'
const list = ref([])
const listQuery = ref({ page: 1, limit: 12 })
const total = ref(0)
const listLoading = ref(false)
const centerType = ref(false)
const searchQuery = ref({ timubianhao: '' })
const layouts = ref(['prev', 'pager', 'next'])

const btnAuth = (e, a) => centerType.value
	? context?.$toolUtil.isBackAuth(e, a)
	: context?.$toolUtil.isAuth(e, a)

const formatDate = (val) => {
	if (!val) return '-'
	const d = new Date(val)
	if (Number.isNaN(d.getTime())) return String(val)
	return d.toLocaleString('zh-CN', {
		year: 'numeric', month: '2-digit', day: '2-digit',
		hour: '2-digit', minute: '2-digit', hour12: false
	})
}

const getStatusClass = (status) => {
	if (isAppliedStatus(status)) return 'status_approved'
	if (isRejectedStatus(status)) return 'status_rejected'
	return 'status_pending'
}

const backClick = () => router.push(`/index/${context?.$toolUtil.storageGet('frontSessionTable')}Center`)
const addClick = () => router.push('/index/xuantishenqingAdd')
const resetSearch = () => {
	searchQuery.value = { timubianhao: '' }
	listQuery.value.page = 1
	getList()
}
const searchClick = () => {
	listQuery.value.page = 1
	getList()
}
const sizeChange = (size) => {
	listQuery.value.limit = size
	getList()
}
const currentChange = (page) => {
	listQuery.value.page = page
	getList()
}
const prevClick = () => {
	listQuery.value.page = listQuery.value.page - 1
	getList()
}
const nextClick = () => {
	listQuery.value.page = listQuery.value.page + 1
	getList()
}

const getList = () => {
	listLoading.value = true
	const params = JSON.parse(JSON.stringify(listQuery.value))
	if (searchQuery.value.timubianhao?.trim()) {
		params.timubianhao = '%' + searchQuery.value.timubianhao.trim() + '%'
	}
	const sessionTable = context?.$toolUtil.storageGet('frontSessionTable')
	const usePageApi = centerType.value || sessionTable === 'xuesheng' || sessionTable === 'jiaoshi'
	context?.$http({
		url: `${tableName}/${usePageApi ? 'page' : 'list'}`,
		method: 'get',
		params
	}).then(res => {
		const data = res.data?.data || {}
		const rows = data.list || []
		list.value = rows
		total.value = Number(data.total || 0)

		const isStudent = sessionTable === 'xuesheng'
		if (isStudent && rows.length) {
			const approved = rows.filter(r => isAppliedStatus(r.shenhezhuangtai))
			if (approved.length) {
				const latest = approved[0]
				const lastIdStr = context?.$toolUtil.storageGet('xuantishenqingApprovedId')
				const lastId = lastIdStr ? Number(lastIdStr) : null
				if (!lastId || lastId !== latest.id) {
					context?.$toolUtil.storageSet('xuantishenqingApprovedId', latest.id)
					const title = latest.ketimingcheng || latest.timubianhao || '您申请的课题'
					context?.$toolUtil.message(`选题「${title}」已审核通过，可在“我的开题报告 / 论文初稿 / 答辩论文”中上传对应文件。`, 'success')
				}
			}
		}
	}).finally(() => {
		listLoading.value = false
	})
}

const tableDetailClick = (row) => {
	router.push('/index/xuantishenqingDetail?id=' + row.id + (centerType.value ? '&centerType=1' : ''))
}

const init = () => {
	if (route.query.centerType) centerType.value = true
	getList()
}

init()
</script>

<style lang="scss" scoped>
.list_page {
	width: 100%;
	min-height: 100%;
	padding: 0 20px 40px;
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
		color: var(--tech-cyan-light);
		font-weight: 400;
	}
	:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
		color: var(--tech-cyan);
		font-weight: 500;
	}
}
.page_stats {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 6px 16px;
	background: rgba(34, 211, 238, 0.08);
	border-radius: 20px;
	border: 1px solid var(--tech-border-soft);
}
.stat_num {
	font-size: 18px;
	font-weight: 600;
	color: var(--tech-cyan);
}
.stat_text {
	font-size: 13px;
	color: rgba(255, 255, 255, 0.6);
}
.page_title {
	margin: 8px 0 0;
	font-size: 22px;
	font-weight: 600;
	color: var(--tech-cyan);
}

.filter_card {
	background: var(--tech-panel);
	border-radius: 12px;
	border: 1px solid var(--tech-border-soft);
	padding: 20px;
	margin-bottom: 20px;
}
.back_view {
	margin-bottom: 12px;
}
.search_row {
	display: flex;
	align-items: center;
	gap: 12px;
	flex-wrap: wrap;
}
.search_input {
	width: 220px;
	:deep(.el-input__wrapper) {
		background: rgba(34, 211, 238, 0.05);
		border: 1px solid var(--tech-border-soft);
		box-shadow: none;
		border-radius: 8px;
	}
	:deep(.el-input__inner) {
		color: rgba(255, 255, 255, 0.85);
	}
	:deep(.el-input__prefix) {
		color: var(--tech-cyan);
	}
}
.search_btn, .add_btn {
	border: none;
	border-radius: 8px;
	height: 36px;
	background: linear-gradient(135deg, var(--tech-cyan) 0%, var(--tech-cyan-dark) 100%);
	color: #fff;
	display: inline-flex;
	align-items: center;
	gap: 6px;
}
.reset_btn {
	border: 1px solid var(--tech-border-soft);
	border-radius: 8px;
	height: 36px;
	background: transparent;
	color: rgba(255, 255, 255, 0.7);
	display: inline-flex;
	align-items: center;
	gap: 6px;
	&:hover {
		border-color: var(--tech-cyan);
		color: var(--tech-cyan);
	}
}

.cards_container {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
	gap: 20px;
}
.empty_state {
	grid-column: 1 / -1;
	padding: 48px 0;
	:deep(.el-empty__description) {
		color: rgba(255, 255, 255, 0.5);
	}
}

.apply_card {
	background: var(--tech-panel);
	border-radius: 12px;
	border: 1px solid var(--tech-border-soft);
	overflow: hidden;
	cursor: pointer;
	transition: all 0.25s ease;
	&:hover {
		transform: translateY(-3px);
		border-color: rgba(34, 211, 238, 0.45);
		box-shadow: 0 8px 24px rgba(34, 211, 238, 0.15);
	}
}

.card_header {
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 14px 16px;
	background: rgba(34, 211, 238, 0.05);
	border-bottom: 1px solid var(--tech-border-soft);
}
.card_index {
	width: 28px;
	height: 28px;
	display: flex;
	align-items: center;
	justify-content: center;
	background: rgba(34, 211, 238, 0.15);
	border-radius: 6px;
	color: var(--tech-cyan);
	font-weight: 600;
	font-size: 13px;
}
.card_code {
	flex: 1;
	display: flex;
	align-items: center;
	gap: 6px;
	color: rgba(255, 255, 255, 0.65);
	font-size: 12px;
}
.card_status {
	display: flex;
	align-items: center;
	gap: 4px;
	padding: 4px 10px;
	border-radius: 12px;
	font-size: 12px;
	font-weight: 500;
	&.status_approved {
		background: rgba(103, 194, 58, 0.15);
		color: #86efac;
		border: 1px solid rgba(103, 194, 58, 0.3);
	}
	&.status_rejected {
		background: rgba(244, 67, 54, 0.15);
		color: #fca5a5;
		border: 1px solid rgba(244, 67, 54, 0.3);
	}
	&.status_pending {
		background: rgba(255, 152, 0, 0.15);
		color: #fdba74;
		border: 1px solid rgba(255, 152, 0, 0.3);
	}
}

.card_body {
	padding: 16px;
}
.card_title {
	margin: 0;
	font-size: 16px;
	font-weight: 600;
	color: rgba(255, 255, 255, 0.95);
	line-height: 1.5;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.card_meta {
	display: flex;
	flex-wrap: wrap;
	gap: 10px 16px;
	padding: 0 16px 14px;
}
.meta_item {
	display: flex;
	align-items: center;
	gap: 6px;
	font-size: 12px;
	color: rgba(255, 255, 255, 0.55);
	.el-icon {
		color: var(--tech-cyan);
		font-size: 14px;
	}
}

.card_footer {
	padding: 10px 16px 14px;
	border-top: 1px solid var(--tech-border-soft);
	display: flex;
	justify-content: flex-end;
}

.pagination_wrap {
	margin-top: 24px;
	display: flex;
	justify-content: center;
	:deep(.el-pagination) {
		.btn-prev, .btn-next {
			background: rgba(34, 211, 238, 0.08);
			border: 1px solid var(--tech-border-soft);
			color: var(--tech-cyan);
		}
		.el-pager li {
			background: rgba(34, 211, 238, 0.08);
			border: 1px solid var(--tech-border-soft);
			color: var(--tech-cyan);
			&.is-active {
				background: linear-gradient(135deg, var(--tech-cyan), var(--tech-cyan-dark));
				color: #fff;
			}
		}
	}
}

@media (max-width: 768px) {
	.cards_container {
		grid-template-columns: 1fr;
	}
	.search_input {
		width: 100%;
	}
}
</style>
