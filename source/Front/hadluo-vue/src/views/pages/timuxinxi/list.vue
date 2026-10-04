<template>
	<div class="list_page">
		<!-- 页面标题区 -->
		<header class="page_header">
			<div class="page_header_top">
				<el-breadcrumb separator="/" class="page_breadcrumb">
					<el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
					<el-breadcrumb-item>题目信息</el-breadcrumb-item>
				</el-breadcrumb>
				<div class="page_stats">
					<span class="stat_item">
						<span class="stat_num">{{ total }}</span>
						<span class="stat_text">个选题</span>
					</span>
					<span class="stat_divider">|</span>
					<span class="stat_item stat_available">
						<span class="stat_num">{{ availableCount }}</span>
						<span class="stat_text">个可选</span>
					</span>
				</div>
			</div>
			<h1 class="page_title">毕业设计选题</h1>
		</header>

		<!-- 筛选区域 -->
		<div class="filter_card">
			<div class="back_view" v-if="centerType">
				<el-button class="back_btn" @click="backClick" type="primary">返回</el-button>
			</div>
			
			<!-- 搜索行 -->
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
				
				<el-input
					v-model="searchQuery.zhuanye"
					placeholder="输入专业名称"
					clearable
					class="search_input"
					@keyup.enter="searchClick"
				>
					<template #prefix>
						<el-icon><Location /></el-icon>
					</template>
				</el-input>
				
				<el-select
					v-model="categoryIndex"
					placeholder="题目类型"
					clearable
					class="type_select"
					@change="categoryChange"
				>
					<el-option label="全部类型" :value="-1" />
					<el-option
						v-for="(item, index) in categoryList"
						:key="index"
						:label="item"
						:value="index"
					/>
				</el-select>
				
				<el-button type="primary" class="search_btn" @click="searchClick">
					<el-icon><Search /></el-icon>
					搜索
				</el-button>
				
				<el-button class="reset_btn" @click="resetSearch">
					<el-icon><RefreshRight /></el-icon>
					重置
				</el-button>
			</div>
		</div>

		<!-- 列表区域：卡片式布局 -->
		<div class="cards_container" v-loading="listLoading">
			<!-- 空状态 -->
			<div v-if="!listLoading && list.length === 0" class="empty_state">
				<el-empty description="暂无题目信息" />
			</div>
			
			<!-- 题目卡片 -->
			<div
				v-for="(item, index) in list"
				:key="item.id"
				class="topic_card"
				:class="{ 'topic_card_disabled': item.yibeixuan }"
			>
				<!-- 卡片头部：编号 + 状态 -->
				<div class="card_header">
					<div class="card_index">{{ (listQuery.page - 1) * listQuery.limit + index + 1 }}</div>
					<div class="card_code" @click="copyCode(item.timubianhao)" title="点击复制">
						<el-icon class="code_icon"><DocumentCopy /></el-icon>
						<span>{{ item.timubianhao }}</span>
					</div>
					<div class="card_status" :class="item.yibeixuan ? 'status_selected' : 'status_available'">
						<el-icon v-if="!item.yibeixuan"><CircleCheckFilled /></el-icon>
						<el-icon v-else><CircleCloseFilled /></el-icon>
						<span>{{ item.yibeixuan ? '已被选' : '可选' }}</span>
					</div>
				</div>
				
				<!-- 卡片主体：课题名称 -->
				<div class="card_body">
					<h3 class="card_title">{{ item.ketimingcheng }}</h3>
				</div>
				
				<!-- 卡片信息：类型 + 专业 + 教师 -->
				<div class="card_meta">
					<div class="meta_item">
						<el-icon><Collection /></el-icon>
						<span>{{ item.timuleixing || '-' }}</span>
					</div>
					<div class="meta_item">
						<el-icon><School /></el-icon>
						<span>{{ item.zhuanye || '-' }}</span>
					</div>
					<div class="meta_item">
						<el-icon><User /></el-icon>
						<span>{{ item.jiaoshixingming || '-' }}</span>
					</div>
				</div>
				
				<!-- 卡片底部：操作按钮 -->
				<div class="card_footer">
					<el-button class="detail_btn" @click="detailClick(item.id)">
						<el-icon><View /></el-icon>
						查看详情
					</el-button>
					<el-button
						v-if="!item.yibeixuan"
						type="primary"
						class="select_btn"
						@click="selectClick(item)"
					>
						<el-icon><Plus /></el-icon>
						选择此课题
					</el-button>
					<el-tag v-else class="disabled_tag" type="info" effect="plain">
						<el-icon><Lock /></el-icon>
						已被选择
					</el-tag>
				</div>
			</div>
		</div>

		<!-- 分页器 -->
		<div class="pagination_wrap" v-if="total > 0">
			<el-pagination
				v-model:current-page="listQuery.page"
				v-model:page-size="listQuery.limit"
				:page-sizes="[8, 12, 16, 24]"
				:total="total"
				layout="total, sizes, prev, pager, next, jumper"
				background
				prev-text="上一页"
				next-text="下一页"
				@size-change="handleSizeChange"
				@current-change="handleCurrentChange"
			/>
		</div>

		<!-- 选题确认弹窗 -->
		<el-dialog
			v-model="selectDialogVisible"
			width="520px"
			align-center
			class="select_dialog"
			modal-class="select_dialog_overlay"
			destroy-on-close
			:show-close="true"
		>
			<template #header>
				<div class="select_dialog_header">
					<div class="select_dialog_icon">
						<el-icon><DocumentChecked /></el-icon>
					</div>
					<div class="select_dialog_heading">
						<h3 class="select_dialog_title">确认选题</h3>
						<p class="select_dialog_subtitle">请核对课题信息后再提交申请</p>
					</div>
				</div>
			</template>

			<div class="select_dialog_content" v-if="selectedTopic">
				<div class="confirm_topic_card">
					<div class="confirm_topic_name">{{ selectedTopic.ketimingcheng }}</div>
					<div class="confirm_meta_grid">
						<div class="confirm_meta_item">
							<span class="confirm_meta_label">题目编号</span>
							<span class="confirm_meta_value">{{ selectedTopic.timubianhao }}</span>
						</div>
						<div class="confirm_meta_item">
							<span class="confirm_meta_label">题目类型</span>
							<span class="confirm_meta_value">{{ selectedTopic.timuleixing || '-' }}</span>
						</div>
						<div class="confirm_meta_item">
							<span class="confirm_meta_label">所属专业</span>
							<span class="confirm_meta_value">{{ selectedTopic.zhuanye || '-' }}</span>
						</div>
						<div class="confirm_meta_item">
							<span class="confirm_meta_label">指导教师</span>
							<span class="confirm_meta_value">{{ selectedTopic.jiaoshixingming || '-' }}</span>
						</div>
					</div>
				</div>

				<div class="confirm_tip_box">
					<el-icon class="confirm_tip_icon"><InfoFilled /></el-icon>
					<p class="confirm_tip_text">确认后将向教师发送选题申请，待教师审核通过后正式选定。</p>
				</div>
			</div>

			<template #footer>
				<div class="select_dialog_footer">
					<el-button class="dialog_btn dialog_btn--cancel" @click="selectDialogVisible = false">取消</el-button>
					<el-button
						class="dialog_btn dialog_btn--confirm"
						@click="confirmSelect"
						:loading="submitLoading"
					>
						<el-icon v-if="!submitLoading"><CircleCheckFilled /></el-icon>
						确认选择
					</el-button>
				</div>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
	import { ref, computed, getCurrentInstance } from 'vue';
	import { useRoute, useRouter } from 'vue-router';
	import { ElMessage, ElMessageBox } from 'element-plus';
	import {
		Search,
		Location,
		RefreshRight,
		DocumentCopy,
		CircleCheckFilled,
		CircleCloseFilled,
		Collection,
		School,
		User,
		View,
		Plus,
		Lock,
		DocumentChecked,
		InfoFilled
	} from '@element-plus/icons-vue';

	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const router = useRouter();
	const route = useRoute();

	const tableName = 'timuxinxi';

	// 列表数据
	const list = ref([]);
	const listQuery = ref({
		page: 1,
		limit: 8
	});
	const total = ref(0);
	const listLoading = ref(false);

	// 可选题目数量
	const availableCount = computed(() => {
		return list.value.filter(item => !item.yibeixuan).length;
	});

	// 分类
	const categoryList = ref([]);
	const categoryIndex = ref(-1);

	// 搜索
	const searchQuery = ref({
		timubianhao: '',
		zhuanye: ''
	});

	// 选题弹窗
	const selectDialogVisible = ref(false);
	const selectedTopic = ref(null);
	const submitLoading = ref(false);

	// 返回按钮
	const centerType = ref(false);
	const backClick = () => router.push(`/index/${context?.$toolUtil.storageGet('frontSessionTable')}Center`);

	// 初始化
	const init = () => {
		if (route.query.centerType) centerType.value = true;
		getCategoryList();
		getList();
	};

	// 获取分类列表
	const getCategoryList = () => {
		context?.$http({
			url: 'option/timuleixing/timuleixing',
			method: 'get'
		}).then(res => {
			categoryList.value = res.data.data || [];
		}).catch(() => {
			categoryList.value = [];
		});
	};

	// 获取列表数据
	const getList = async () => {
		listLoading.value = true;
		let params = JSON.parse(JSON.stringify(listQuery.value));
		
		if (categoryIndex.value != -1) {
			params.timuleixing = categoryList.value[categoryIndex.value];
		}
		if (searchQuery.value.timubianhao?.trim()) {
			params.timubianhao = '%' + searchQuery.value.timubianhao.trim() + '%';
		}
		if (searchQuery.value.zhuanye?.trim()) {
			params.zhuanye = '%' + searchQuery.value.zhuanye.trim() + '%';
		}

		const primaryUrl = `${tableName}/${centerType.value ? 'page' : 'list'}`;
		const fallbackUrl = `${tableName}/list`;

		try {
			const res = await context?.$http({
				url: primaryUrl,
				method: 'get',
				params: params
			});
			const resData = res?.data?.data || {};
			list.value = resData.list || [];
			total.value = Number(resData.total || 0);
		} catch (e) {
			try {
				const res2 = await context?.$http({
					url: fallbackUrl,
					method: 'get',
					params: params
				});
				const resData2 = res2?.data?.data || {};
				list.value = resData2.list || [];
				total.value = Number(resData2.total || 0);
			} catch (e2) {
				list.value = [];
			}
		} finally {
			listLoading.value = false;
		}
	};

	// 搜索
	const searchClick = () => {
		listQuery.value.page = 1;
		getList();
	};

	// 重置
	const resetSearch = () => {
		searchQuery.value = { timubianhao: '', zhuanye: '' };
		categoryIndex.value = -1;
		listQuery.value.page = 1;
		getList();
	};

	// 分类切换
	const categoryChange = () => {
		listQuery.value.page = 1;
		getList();
	};

	// 分页
	const handleSizeChange = (size) => {
		listQuery.value.limit = size;
		listQuery.value.page = 1;
		getList();
	};

	const handleCurrentChange = (page) => {
		listQuery.value.page = page;
		getList();
	};

	// 复制编号
	const copyCode = (code) => {
		navigator.clipboard.writeText(code).then(() => {
			ElMessage.success('题目编号已复制');
		}).catch(() => {
			ElMessage.error('复制失败');
		});
	};

	// 查看详情
	const detailClick = (id) => {
		router.push('timuxinxiDetail?id=' + id + (centerType.value ? '&&centerType=1' : ''));
	};

	// 选择课题
	const selectClick = (item) => {
		selectedTopic.value = item;
		selectDialogVisible.value = true;
	};

	// 确认选择
	const confirmSelect = async () => {
		if (!selectedTopic.value) return;
		
		submitLoading.value = true;
		try {
			const res = await context?.$http({
				url: 'xuantishenqing/add',
				method: 'post',
				data: {
					timubianhao: selectedTopic.value.timubianhao
				}
			});
			
			if (res?.data?.code === 0) {
				ElMessage.success('选题申请已提交，请等待教师审核');
				selectDialogVisible.value = false;
				getList(); // 刷新列表
			} else {
				ElMessage.error(res?.data?.msg || '提交失败');
			}
		} catch (e) {
			ElMessage.error('提交失败，请重试');
		} finally {
			submitLoading.value = false;
		}
	};

	init();
</script>

<style lang="scss" scoped>
/* 页面基础 */
.list_page {
	width: 100%;
	min-height: 100%;
	padding: 0 20px 40px;
	box-sizing: border-box;
}

/* 页面标题区 */
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
.stat_item {
	display: flex;
	align-items: baseline;
	gap: 4px;
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
.stat_divider {
	color: var(--tech-border-soft);
}
.stat_available .stat_num {
	color: #86efac;
}
.page_title {
	margin: 8px 0 0;
	font-size: 22px;
	font-weight: 600;
	color: var(--tech-cyan);
	line-height: 1.4;
}

/* 筛选卡片 */
.filter_card {
	background: var(--tech-panel);
	border-radius: 12px;
	border: 1px solid var(--tech-border-soft);
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
	padding: 20px;
	margin-bottom: 20px;
}
.back_view {
	margin-bottom: 16px;
	.back_btn {
		border: 1px solid var(--tech-cyan);
		border-radius: 8px;
		padding: 0 20px;
		color: #fff;
		background: linear-gradient(135deg, var(--tech-cyan) 0%, var(--tech-cyan-dark) 100%);
	}
}
.search_row {
	display: flex;
	align-items: center;
	gap: 12px;
	flex-wrap: wrap;
}
.search_input {
	width: 200px;
	:deep(.el-input__wrapper) {
		background: rgba(34, 211, 238, 0.05);
		border: 1px solid var(--tech-border-soft);
		box-shadow: none;
		border-radius: 8px;
		padding: 0 12px;
		&:hover, &:focus {
			border-color: var(--tech-cyan);
		}
	}
	:deep(.el-input__inner) {
		color: rgba(255, 255, 255, 0.85);
		&::placeholder {
			color: rgba(255, 255, 255, 0.4);
		}
	}
	:deep(.el-input__prefix) {
		color: var(--tech-cyan);
	}
}
.type_select {
	width: 150px;
	:deep(.el-input__wrapper) {
		background: rgba(34, 211, 238, 0.05);
		border: 1px solid var(--tech-border-soft);
		box-shadow: none;
		border-radius: 8px;
		&:hover, &:focus {
			border-color: var(--tech-cyan);
		}
	}
	:deep(.el-input__inner) {
		color: rgba(255, 255, 255, 0.85);
	}
	:deep(.el-input__suffix) {
		color: var(--tech-cyan);
	}
}
.search_btn {
	border: none;
	border-radius: 8px;
	padding: 0 20px;
	height: 36px;
	background: linear-gradient(135deg, var(--tech-cyan) 0%, var(--tech-cyan-dark) 100%);
	color: #fff;
	font-weight: 500;
	display: flex;
	align-items: center;
	gap: 6px;
	&:hover {
		opacity: 0.9;
		transform: translateY(-1px);
		box-shadow: 0 4px 12px rgba(34, 211, 238, 0.4);
	}
}
.reset_btn {
	border: 1px solid var(--tech-border-soft);
	border-radius: 8px;
	padding: 0 16px;
	height: 36px;
	background: transparent;
	color: rgba(255, 255, 255, 0.7);
	display: flex;
	align-items: center;
	gap: 6px;
	&:hover {
		border-color: var(--tech-cyan);
		color: var(--tech-cyan);
	}
}

/* 卡片容器 */
.cards_container {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
	gap: 20px;
	padding: 4px 0;
}

/* 空状态 */
.empty_state {
	grid-column: 1 / -1;
	padding: 60px 0;
	:deep(.el-empty__description) {
		color: rgba(255, 255, 255, 0.5);
	}
}

/* 题目卡片 */
.topic_card {
	background: var(--tech-panel);
	border-radius: 12px;
	border: 1px solid var(--tech-border-soft);
	box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
	overflow: hidden;
	transition: all 0.3s ease;
	position: relative;
	
	&::before {
		content: '';
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		height: 3px;
		background: linear-gradient(90deg, var(--tech-cyan), var(--tech-cyan-dark));
		opacity: 0;
		transition: opacity 0.3s;
	}
	
	&:hover {
		transform: translateY(-4px);
		box-shadow: 0 8px 30px rgba(34, 211, 238, 0.2);
		border-color: rgba(34, 211, 238, 0.4);
		&::before {
			opacity: 1;
		}
	}
	
	&.topic_card_disabled {
		opacity: 0.6;
		&::before {
			background: linear-gradient(90deg, #64748b, #475569);
			opacity: 1;
		}
		&:hover {
			transform: none;
			box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
			border-color: var(--tech-border-soft);
		}
	}
}

/* 卡片头部 */
.card_header {
	display: flex;
	align-items: center;
	padding: 14px 16px;
	background: rgba(34, 211, 238, 0.05);
	border-bottom: 1px solid var(--tech-border-soft);
	gap: 12px;
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
	font-size: 14px;
}
.card_code {
	flex: 1;
	display: flex;
	align-items: center;
	gap: 6px;
	color: rgba(255, 255, 255, 0.7);
	font-size: 13px;
	cursor: pointer;
	padding: 4px 8px;
	border-radius: 4px;
	transition: all 0.2s;
	&:hover {
		color: var(--tech-cyan);
		background: rgba(34, 211, 238, 0.1);
	}
	.code_icon {
		font-size: 14px;
	}
}
.card_status {
	display: flex;
	align-items: center;
	gap: 4px;
	padding: 4px 10px;
	border-radius: 12px;
	font-size: 12px;
	font-weight: 500;
	
	&.status_available {
		background: rgba(103, 194, 58, 0.15);
		color: #86efac;
		border: 1px solid rgba(103, 194, 58, 0.3);
	}
	
	&.status_selected {
		background: rgba(100, 116, 139, 0.15);
		color: #94a3b8;
		border: 1px solid rgba(100, 116, 139, 0.3);
	}
}

/* 卡片主体 */
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
	min-height: 48px;
}

/* 卡片信息 */
.card_meta {
	display: flex;
	flex-wrap: wrap;
	gap: 12px;
	padding: 0 16px 16px;
}
.meta_item {
	display: flex;
	align-items: center;
	gap: 6px;
	font-size: 13px;
	color: rgba(255, 255, 255, 0.6);
	padding: 4px 10px;
	background: rgba(34, 211, 238, 0.05);
	border-radius: 6px;
	border: 1px solid var(--tech-border-soft);
	
	.el-icon {
		color: var(--tech-cyan);
		font-size: 14px;
	}
}

/* 卡片底部 */
.card_footer {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 14px 16px;
	background: rgba(34, 211, 238, 0.03);
	border-top: 1px solid var(--tech-border-soft);
	gap: 12px;
}
.detail_btn {
	border: 1px solid var(--tech-border-soft);
	border-radius: 8px;
	padding: 0 16px;
	height: 34px;
	background: transparent;
	color: rgba(255, 255, 255, 0.7);
	font-size: 13px;
	display: flex;
	align-items: center;
	gap: 6px;
	&:hover {
		border-color: var(--tech-cyan);
		color: var(--tech-cyan);
		background: rgba(34, 211, 238, 0.08);
	}
}
.select_btn {
	border: none;
	border-radius: 8px;
	padding: 0 16px;
	height: 34px;
	background: linear-gradient(135deg, var(--tech-cyan) 0%, var(--tech-cyan-dark) 100%);
	color: #fff;
	font-size: 13px;
	font-weight: 500;
	display: flex;
	align-items: center;
	gap: 6px;
	&:hover {
		opacity: 0.9;
		box-shadow: 0 4px 12px rgba(34, 211, 238, 0.4);
	}
}
.disabled_tag {
	border-radius: 8px;
	padding: 0 12px;
	height: 34px;
	font-size: 13px;
	background: rgba(100, 116, 139, 0.1);
	border-color: rgba(100, 116, 139, 0.3);
	color: #94a3b8;
	display: flex;
	align-items: center;
	gap: 4px;
}

/* 分页器 */
.pagination_wrap {
	margin-top: 30px;
	display: flex;
	justify-content: center;
}
:deep(.el-pagination) {
	--el-pagination-bg-color: transparent;
	--el-pagination-text-color: rgba(255, 255, 255, 0.7);
	--el-pagination-button-bg-color: rgba(34, 211, 238, 0.08);
	--el-pagination-hover-color: var(--el-color-primary);
	
	.el-pagination__total {
		color: rgba(255, 255, 255, 0.6);
	}
	
	.btn-prev, .btn-next {
		border: 1px solid var(--tech-border-soft);
		border-radius: 8px;
		background: rgba(34, 211, 238, 0.08);
		color: var(--tech-cyan);
		&:hover {
			color: #fff;
			background: var(--tech-cyan);
			border-color: var(--tech-cyan);
		}
		&.is-disabled {
			background: rgba(255, 255, 255, 0.03);
			border-color: rgba(255, 255, 255, 0.1);
			color: rgba(255, 255, 255, 0.3);
		}
	}
	
	.el-pager {
		li {
			border: 1px solid var(--tech-border-soft);
			border-radius: 8px;
			background: rgba(34, 211, 238, 0.08);
			color: var(--tech-cyan);
			margin: 0 4px;
			min-width: 36px;
			height: 36px;
			line-height: 36px;
			font-weight: 500;
			&:hover {
				color: #fff;
				background: var(--tech-cyan);
				border-color: var(--tech-cyan);
			}
			&.is-active {
				background: linear-gradient(135deg, var(--tech-cyan) 0%, var(--tech-cyan-dark) 100%);
				border-color: var(--tech-cyan);
				color: #fff;
			}
		}
	}
	
	.el-pagination__sizes {
		.el-select {
			.el-input__wrapper {
				background: rgba(34, 211, 238, 0.08);
				border-color: var(--tech-border-soft);
				box-shadow: none;
			}
			.el-input__inner {
				color: var(--tech-cyan);
			}
		}
	}
	
	.el-pagination__jump {
		color: rgba(255, 255, 255, 0.6);
		.el-input__wrapper {
			background: rgba(34, 211, 238, 0.08);
			border-color: var(--tech-border-soft);
			box-shadow: none;
		}
		.el-input__inner {
			color: var(--tech-cyan);
		}
	}
}

/* 响应式适配 */
@media (max-width: 768px) {
	.cards_container {
		grid-template-columns: 1fr;
	}
	.search_row {
		.search_input, .type_select {
			width: 100%;
		}
	}
}
</style>

<style lang="scss">
/* 选题确认弹窗遮罩 */
.select_dialog_overlay {
	background-color: rgba(2, 8, 23, 0.72) !important;
	backdrop-filter: blur(4px);
}

/* 选题确认弹窗（teleport 到 body，需非 scoped） */
.select_dialog.el-dialog {
	background: linear-gradient(180deg, rgba(8, 28, 58, 0.98) 0%, rgba(6, 22, 48, 0.99) 100%);
	border: 1px solid rgba(34, 211, 238, 0.28);
	border-radius: 16px;
	box-shadow: 0 24px 64px rgba(0, 0, 0, 0.55), 0 0 0 1px rgba(34, 211, 238, 0.08) inset;
	overflow: hidden;
}

.select_dialog .el-dialog__header {
	margin: 0;
	padding: 22px 24px 18px;
	border-bottom: 1px solid rgba(34, 211, 238, 0.15);
	background: rgba(34, 211, 238, 0.04);
}

.select_dialog .el-dialog__headerbtn {
	top: 18px;
	right: 18px;
	width: 32px;
	height: 32px;
}

.select_dialog .el-dialog__close {
	color: rgba(226, 232, 240, 0.75);
	font-size: 18px;
}

.select_dialog .el-dialog__close:hover {
	color: #22d3ee;
}

.select_dialog .el-dialog__body {
	padding: 20px 24px 8px;
	color: rgba(255, 255, 255, 0.88);
}

.select_dialog .el-dialog__footer {
	padding: 16px 24px 22px;
	border-top: 1px solid rgba(34, 211, 238, 0.15);
	background: rgba(6, 22, 48, 0.65);
}

.select_dialog_header {
	display: flex;
	align-items: center;
	gap: 14px;
}

.select_dialog_icon {
	width: 44px;
	height: 44px;
	border-radius: 12px;
	display: flex;
	align-items: center;
	justify-content: center;
	background: rgba(34, 211, 238, 0.12);
	border: 1px solid rgba(34, 211, 238, 0.35);
	color: #22d3ee;
	font-size: 22px;
	box-shadow: 0 0 18px rgba(34, 211, 238, 0.15);
}

.select_dialog_title {
	margin: 0;
	font-size: 18px;
	font-weight: 600;
	color: #22d3ee;
	line-height: 1.3;
}

.select_dialog_subtitle {
	margin: 4px 0 0;
	font-size: 13px;
	color: rgba(203, 213, 225, 0.72);
}

.confirm_topic_card {
	padding: 16px;
	border-radius: 12px;
	background: rgba(34, 211, 238, 0.06);
	border: 1px solid rgba(34, 211, 238, 0.18);
}

.confirm_topic_name {
	font-size: 16px;
	font-weight: 600;
	color: rgba(255, 255, 255, 0.95);
	line-height: 1.55;
	margin-bottom: 14px;
	word-break: break-word;
}

.confirm_meta_grid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 10px 16px;
}

.confirm_meta_item {
	display: flex;
	flex-direction: column;
	gap: 4px;
	min-width: 0;
}

.confirm_meta_label {
	font-size: 12px;
	color: rgba(148, 163, 184, 0.95);
}

.confirm_meta_value {
	font-size: 14px;
	color: rgba(226, 232, 240, 0.92);
	word-break: break-all;
}

.confirm_tip_box {
	display: flex;
	align-items: flex-start;
	gap: 10px;
	margin-top: 16px;
	padding: 12px 14px;
	border-radius: 10px;
	background: rgba(34, 211, 238, 0.08);
	border: 1px solid rgba(34, 211, 238, 0.22);
}

.confirm_tip_icon {
	flex-shrink: 0;
	margin-top: 2px;
	font-size: 16px;
	color: #22d3ee;
}

.confirm_tip_text {
	margin: 0;
	font-size: 13px;
	line-height: 1.6;
	color: rgba(186, 230, 253, 0.92);
}

.select_dialog_footer {
	display: flex;
	justify-content: flex-end;
	gap: 12px;
}

.select_dialog .dialog_btn {
	min-width: 108px;
	height: 38px;
	border-radius: 10px;
	font-size: 14px;
}

.select_dialog .dialog_btn--cancel {
	border: 1px solid rgba(148, 163, 184, 0.35);
	background: transparent;
	color: rgba(226, 232, 240, 0.88);
}

.select_dialog .dialog_btn--cancel:hover {
	border-color: rgba(34, 211, 238, 0.45);
	color: #22d3ee;
	background: rgba(34, 211, 238, 0.08);
}

.select_dialog .dialog_btn--confirm {
	border: none;
	background: linear-gradient(135deg, #22d3ee 0%, #0891b2 100%);
	color: #fff;
	box-shadow: 0 8px 20px rgba(34, 211, 238, 0.25);
}

.select_dialog .dialog_btn--confirm:hover {
	opacity: 0.92;
	box-shadow: 0 10px 24px rgba(34, 211, 238, 0.35);
}

@media (max-width: 768px) {
	.select_dialog.el-dialog {
		width: calc(100vw - 32px) !important;
		margin: 16px auto;
	}

	.confirm_meta_grid {
		grid-template-columns: 1fr;
	}

	.select_dialog_footer {
		flex-direction: column-reverse;
	}

	.select_dialog .dialog_btn {
		width: 100%;
	}
}
</style>
