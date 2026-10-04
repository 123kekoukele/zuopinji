<template>
	<div class="detail_page" v-loading="pageLoading">
		<!-- 顶栏 -->
		<header class="page_header">
			<div class="page_header_top">
				<el-breadcrumb separator="/" class="page_breadcrumb">
					<el-breadcrumb-item :to="{ path: '/index/home' }">首页</el-breadcrumb-item>
					<el-breadcrumb-item :to="{ path: '/index/timuxinxiList' }">题目信息</el-breadcrumb-item>
					<el-breadcrumb-item>题目详情</el-breadcrumb-item>
				</el-breadcrumb>
				<el-button class="back_btn" @click="backClick">
					<el-icon><ArrowLeft /></el-icon>
					返回
				</el-button>
			</div>
			<h1 class="page_title">题目详情</h1>
		</header>

		<template v-if="!pageLoading && detail.id">
			<!-- 主卡片：封面 + 概要 -->
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
						<span>暂无封面</span>
					</div>
				</div>

				<div class="hero_info">
					<div class="status_row">
						<span class="status_tag" :class="'status_' + statusTag.type">
							<el-icon v-if="statusTag.type === 'available'"><CircleCheckFilled /></el-icon>
							<el-icon v-else-if="statusTag.type === 'selected'"><Lock /></el-icon>
							<el-icon v-else><Clock /></el-icon>
							{{ statusTag.text }}
						</span>
						<span class="publish_time" v-if="detail.fabushijian">
							<el-icon><Calendar /></el-icon>
							{{ formatDateTime(detail.fabushijian) }}
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

					<el-alert
						v-if="applyDisabledReason"
						:title="applyDisabledReason"
						type="warning"
						:closable="false"
						show-icon
						class="apply_alert"
					/>

					<div class="action_row">
						<el-button
							v-if="isStudent && btnFrontAuth(tableName, '申请')"
							type="primary"
							class="apply_btn"
							:disabled="applyDisabled"
							@click="handleApplyClick"
						>
							<el-icon><Plus /></el-icon>
							{{ applyDisabled ? '不可申请' : '申请选题' }}
						</el-button>
						<el-button
							v-if="centerType && (detail.ispay == '未支付' || !detail.ispay) && btnFrontAuth('timuxinxi', '支付')"
							class="ghost_btn"
							@click="payClick"
						>支付</el-button>
						<el-button
							v-if="centerType && btnAuth('timuxinxi', '修改')"
							class="ghost_btn"
							@click="editClick"
						>修改</el-button>
						<el-button
							v-if="centerType && btnAuth('timuxinxi', '删除')"
							class="danger_btn"
							@click="delClick"
						>删除</el-button>
					</div>
				</div>
			</section>

			<!-- 基本信息 -->
			<section class="panel_card">
				<div class="panel_title">
					<el-icon><InfoFilled /></el-icon>
					<span>基本信息</span>
				</div>
				<div class="info_grid">
					<div v-for="field in infoFields" :key="field.label" class="info_cell">
						<div class="info_label">{{ field.label }}</div>
						<div class="info_value">{{ field.value }}</div>
					</div>
				</div>
			</section>

			<!-- 课题性质 -->
			<section class="panel_card" v-if="detail.ketixingzhi">
				<div class="panel_title">
					<el-icon><Document /></el-icon>
					<span>课题性质</span>
				</div>
				<div class="text_block">{{ detail.ketixingzhi }}</div>
			</section>

			<!-- 题目范围 -->
			<section class="panel_card" v-if="detail.timufanwei">
				<div class="panel_title">
					<el-icon><Reading /></el-icon>
					<span>题目范围</span>
				</div>
					<div class="rich_content" v-html="sanitizedTimufanwei"></div>
			</section>
		</template>
	</div>
</template>

<script setup>
import axios from 'axios';
import { ref, computed, getCurrentInstance, onMounted, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
	ArrowLeft, Picture, CircleCheckFilled, Lock, Clock, Calendar,
	Collection, School, User, DocumentCopy, Plus, InfoFilled,
	Document, Reading
} from '@element-plus/icons-vue';
import { useRoute, useRouter } from 'vue-router';
import { isActiveApplicationRecord } from '@/utils/xuantishenqingStatus';

const context = getCurrentInstance()?.appContext.config.globalProperties;
const route = useRoute();
const router = useRouter();

const tableName = 'timuxinxi';
const formName = '题目信息';

const pageLoading = ref(true);
const centerType = ref(false);
const isStudent = computed(() => context?.$toolUtil.storageGet('frontSessionTable') === 'xuesheng');

const detail = ref({});
const applyDisabled = ref(false);
const applyDisabledReason = ref('');

const currentCoverIndex = ref(0);
const coverList = computed(() => {
	const raw = detail.value?.timufengmian;
	if (!raw) return [];
	return String(raw).split(',').map(item => item.trim()).filter(Boolean).map(item => {
		if (item.startsWith('http')) return item;
		return `${context?.$config.url}${item}`;
	});
});
const currentCover = computed(() => {
	if (!coverList.value.length) return '';
	const maxIndex = coverList.value.length - 1;
	const safeIndex = Math.min(Math.max(currentCoverIndex.value, 0), maxIndex);
	return coverList.value[safeIndex];
});
const switchCover = (index) => {
	currentCoverIndex.value = index;
};

const formatDateTime = (val) => {
	if (!val) return '-';
	const d = new Date(val);
	if (isNaN(d.getTime())) return String(val);
	return d.toLocaleString('zh-CN', {
		year: 'numeric', month: '2-digit', day: '2-digit',
		hour: '2-digit', minute: '2-digit'
	});
};

const infoFields = computed(() => [
	{ label: '题目编号', value: detail.value.timubianhao || '-' },
	{ label: '题目类型', value: detail.value.timuleixing || '-' },
	{ label: '专业', value: detail.value.zhuanye || '-' },
	{ label: '选题时间', value: detail.value.xuantishijian || '-' },
	{ label: '教师工号', value: detail.value.jiaoshigonghao || '-' },
	{ label: '教师姓名', value: detail.value.jiaoshixingming || '-' },
	{ label: '发布时间', value: formatDateTime(detail.value.fabushijian) }
]);

const sanitizedTimufanwei = computed(() => {
	const html = detail.value?.timufanwei;
	if (!html) return '';
	return String(html)
		.replace(/background-color\s*:\s*[^;"]+;?/gi, '')
		.replace(/background\s*:\s*[^;"]+;?/gi, '')
		.replace(/color\s*:\s*[^;"]+;?/gi, '');
});

const statusTag = computed(() => {
	if (detail.value.yibeixuan) {
		return { text: '已被选', type: 'selected' };
	}
	const reason = applyDisabledReason.value || '';
	if (reason.includes('已被') && reason.includes('申请或锁定')) {
		return { text: '已被选', type: 'selected' };
	}
	if (reason.includes('已有选题申请') || reason.includes('已对该题目提交')) {
		return { text: '申请中', type: 'pending' };
	}
	return { text: '可选', type: 'available' };
});

const btnAuth = (e, a) => centerType.value
	? context?.$toolUtil.isBackAuth(e, a)
	: context?.$toolUtil.isAuth(e, a);

const btnFrontAuth = (e, a) => centerType.value
	? context?.$toolUtil.isBackAuth(e, a)
	: context?.$toolUtil.isFrontAuth(e, a);

const backClick = () => history.back();

const copyCode = (code) => {
	if (!code) return;
	navigator.clipboard?.writeText(code).then(() => {
		ElMessage.success('题目编号已复制');
	}).catch(() => {
		ElMessage.info(code);
	});
};

const getDetail = () => {
	pageLoading.value = true;
	context?.$http({
		url: `${tableName}/detail/${route.query.id}`,
		method: 'get'
	}).then(res => {
		const data = res?.data?.data;
		if (!data) {
			context?.$toolUtil.message('当前题目信息不存在或已被删除', 'error', () => history.back());
			return;
		}
		detail.value = data;
		currentCoverIndex.value = 0;
		checkApplyStatus();
	}).finally(() => {
		pageLoading.value = false;
	});
};

const checkApplyStatus = () => {
	applyDisabled.value = false;
	applyDisabledReason.value = '';
	if (!isStudent.value) return;

	context?.$http({
		url: `${context?.$toolUtil.storageGet('frontSessionTable')}/session`,
		method: 'get'
	}).then(res => {
		const xuehao = res.data?.data?.xuehao || '';
		context?.$http({
			url: 'xuantishenqing/list',
			method: 'get',
			params: { page: 1, limit: 100, sort: 'shenqingshijian', order: 'desc' }
		}).then(r => {
			const list = r.data?.data?.list || [];
			const valid = list.filter(isActiveApplicationRecord);
			if (valid.length > 0) {
				applyDisabled.value = true;
				const row = valid[0];
				const titleText = row.ketimingcheng || row.timubianhao || '某个课题';
				applyDisabledReason.value = `每个学生只能选择一个课题，您已有选题申请「${titleText}」`;
				return;
			}
			if (!detail.value.timubianhao) return;
			context?.$http({
				url: 'xuantishenqing/list',
				method: 'get',
				params: { page: 1, limit: 100, timubianhao: detail.value.timubianhao }
			}).then(r2 => {
				const rows = (r2.data?.data?.list || []).filter(isActiveApplicationRecord);
				if (!rows.length) return;
				const row2 = rows[0];
				applyDisabled.value = true;
				if (!xuehao || row2.xuehao !== xuehao) {
					const stuName = row2.xueshengxingming || row2.xuehao || '其他同学';
					applyDisabledReason.value = `该题目已被${stuName}申请或锁定`;
				} else {
					applyDisabledReason.value = '您已对该题目提交申请，请勿重复申请';
				}
			});
		});
	});
};

const editClick = () => {
	router.push(`/index/${tableName}Add?id=${detail.value.id}&&type=edit`);
};

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
			context?.$toolUtil.message('删除成功', 'success', () => history.back());
		});
	});
};

const handleApplyClick = () => {
	if (!isStudent.value) {
		context?.$toolUtil.message('仅学生可以申请选题', 'warning');
		return;
	}
	if (applyDisabled.value && applyDisabledReason.value) {
		context?.$toolUtil.message(applyDisabledReason.value, 'warning');
		return;
	}
	xuantishenqingonAcross('申请', 'xuantishenqing', '', '[1]', '您对该题目已有进行中的申请');
};

const xuantishenqingonAcross = (btnType, table, crossOptAudit, statusColumnName, tips, statusColumnValue) => {
	if (btnType === '申请' && context?.$toolUtil.storageGet('frontSessionTable') !== 'xuesheng') {
		context?.$toolUtil.message('仅学生可以申请选题', 'error');
		return false;
	}
	if (!context?.$toolUtil.storageGet('frontToken')) {
		context?.$toolUtil.message('请登录后再操作！', 'error');
		return false;
	}
	if (!btnAuth(tableName, btnType)) {
		context?.$toolUtil.message('暂无权限操作！', 'error');
		return false;
	}
	context?.$toolUtil.storageSet('crossObj', JSON.stringify(detail.value));
	context?.$toolUtil.storageSet('crossTable', tableName);
	context?.$toolUtil.storageSet('crossStatusColumnName', statusColumnName);
	context?.$toolUtil.storageSet('crossTips', tips);
	context?.$toolUtil.storageSet('crossStatusColumnValue', statusColumnValue);
	if (statusColumnName !== '' && !statusColumnName.startsWith('[')) {
		const obj = detail.value;
		for (const o in obj) {
			if (o === statusColumnName && obj[o] === statusColumnValue) {
				context?.$toolUtil.message(tips, 'error');
				return;
			}
		}
	}
	nextTick(() => {
		router.push(`/index/${table}Add?type=cross&id=${detail.value.id}`);
	});
};

onMounted(() => {
	if (route.query.centerType) centerType.value = true;
	getDetail();
});
</script>

<style lang="scss" scoped>
.detail_page {
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
	grid-template-columns: 380px 1fr;
	gap: 24px;
	background: var(--tech-panel);
	border: 1px solid var(--tech-border-soft);
	border-radius: 16px;
	padding: 24px;
	margin-bottom: 20px;
	box-shadow: 0 4px 24px rgba(0, 0, 0, 0.25);
}

.hero_cover {
	min-width: 0;
}

.cover_main {
	width: 100%;
	height: 280px;
	border-radius: 12px;
	border: 1px solid rgba(255, 255, 255, 0.08);
	background: #0a1929;
	display: block;
}

.cover_placeholder {
	height: 280px;
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

.hero_info {
	min-width: 0;
	display: flex;
	flex-direction: column;
	gap: 14px;
}

.status_row {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 10px;
}

.status_tag {
	display: inline-flex;
	align-items: center;
	gap: 5px;
	padding: 4px 12px;
	border-radius: 20px;
	font-size: 12px;
	font-weight: 600;
	&.status_available {
		background: rgba(52, 211, 153, 0.15);
		color: #86efac;
		border: 1px solid rgba(52, 211, 153, 0.35);
	}
	&.status_selected {
		background: rgba(100, 116, 139, 0.2);
		color: #cbd5e1;
		border: 1px solid rgba(100, 116, 139, 0.35);
	}
	&.status_pending {
		background: rgba(245, 158, 11, 0.15);
		color: #fcd34d;
		border: 1px solid rgba(245, 158, 11, 0.35);
	}
}

.publish_time {
	display: inline-flex;
	align-items: center;
	gap: 5px;
	font-size: 12px;
	color: rgba(255, 255, 255, 0.5);
}

.topic_title {
	margin: 0;
	font-size: 22px;
	font-weight: 600;
	color: #e2e8f0;
	line-height: 1.45;
}

.topic_meta {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
}

.meta_chip {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	padding: 6px 12px;
	border-radius: 8px;
	font-size: 13px;
	color: rgba(255, 255, 255, 0.85);
	background: rgba(34, 211, 238, 0.08);
	border: 1px solid var(--tech-border-soft);
	.el-icon { color: var(--tech-cyan); font-size: 14px; }
}

.topic_code {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	width: fit-content;
	padding: 6px 12px;
	border-radius: 8px;
	font-size: 13px;
	color: var(--tech-cyan-light);
	background: rgba(8, 28, 58, 0.6);
	border: 1px solid var(--tech-border-soft);
	cursor: pointer;
	transition: border-color 0.2s;
	&:hover { border-color: var(--tech-cyan); }
}

.apply_alert {
	background: rgba(245, 158, 11, 0.08) !important;
	border: 1px solid rgba(245, 158, 11, 0.25) !important;
	:deep(.el-alert__title) {
		color: #fcd34d;
		font-size: 13px;
	}
}

.action_row {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
	margin-top: auto;
	padding-top: 8px;
}

.apply_btn {
	border: none;
	border-radius: 10px;
	padding: 10px 24px;
	background: linear-gradient(135deg, var(--tech-cyan) 0%, var(--tech-cyan-dark) 100%);
	&:disabled {
		background: rgba(100, 116, 139, 0.35);
		color: rgba(255, 255, 255, 0.5);
	}
}

.ghost_btn {
	border: 1px solid var(--tech-border-soft);
	border-radius: 10px;
	color: var(--tech-cyan);
	background: rgba(34, 211, 238, 0.06);
	&:hover {
		border-color: var(--tech-cyan);
		background: rgba(34, 211, 238, 0.12);
	}
}

.danger_btn {
	border: 1px solid rgba(239, 68, 68, 0.4);
	border-radius: 10px;
	color: #fca5a5;
	background: rgba(239, 68, 68, 0.1);
	&:hover {
		color: #fff;
		background: #ef4444;
		border-color: #ef4444;
	}
}

.panel_card {
	background: var(--tech-panel);
	border: 1px solid var(--tech-border-soft);
	border-radius: 16px;
	padding: 20px 24px;
	margin-bottom: 16px;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
}

.panel_title {
	display: flex;
	align-items: center;
	gap: 8px;
	margin-bottom: 16px;
	padding-bottom: 12px;
	border-bottom: 1px solid var(--tech-border-soft);
	font-size: 15px;
	font-weight: 600;
	color: var(--tech-cyan);
	.el-icon { font-size: 18px; }
}

.info_grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
	gap: 12px;
}

.info_cell {
	padding: 12px 14px;
	border-radius: 10px;
	background: rgba(10, 25, 41, 0.55);
	border: 1px solid rgba(255, 255, 255, 0.06);
}

.info_label {
	font-size: 12px;
	color: rgba(255, 255, 255, 0.45);
	margin-bottom: 6px;
}

.info_value {
	font-size: 14px;
	color: #e2e8f0;
	font-weight: 500;
	word-break: break-all;
}

.text_block {
	font-size: 14px;
	line-height: 1.8;
	color: #cbd5e1;
	white-space: pre-wrap;
}

.rich_content {
	padding: 16px 18px;
	border-radius: 12px;
	background: rgba(10, 25, 41, 0.65);
	border: 1px solid rgba(255, 255, 255, 0.06);
	color: #cbd5e1;
	line-height: 1.85;
	font-size: 14px;
	word-break: break-word;
}

@media (max-width: 960px) {
	.hero_card {
		grid-template-columns: 1fr;
	}
	.cover_main,
	.cover_placeholder {
		height: 220px;
	}
}

@media (max-width: 768px) {
	.detail_page {
		padding: 0 12px 32px;
	}
	.info_grid {
		grid-template-columns: 1fr;
	}
}
</style>

<style lang="scss">
.detail_page .rich_content {
	color: #cbd5e1;
	line-height: 1.85;
	font-size: 14px;

	&,
	p,
	li,
	span,
	div,
	td,
	th,
	ol,
	ul,
	section,
	article {
		color: #cbd5e1 !important;
		background: transparent !important;
		background-color: transparent !important;
		border-color: rgba(34, 211, 238, 0.15) !important;
	}

	strong,
	b {
		color: #22d3ee !important;
		font-weight: 600;
		background: transparent !important;
	}

	a {
		color: #22d3ee !important;
	}

	ol,
	ul {
		padding-left: 1.4em;
	}

	table {
		width: 100%;
		border-collapse: collapse;
	}

	td,
	th {
		padding: 8px 10px;
		border: 1px solid rgba(34, 211, 238, 0.15) !important;
	}
}
</style>
