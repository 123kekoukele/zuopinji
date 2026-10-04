<template>
	<div class="home_page">
		<!-- 未登录提示 -->
		<section v-if="!isLoggedIn" class="home_login_prompt">
			<div class="home_login_prompt_box">
				<div class="login_icon">
					<el-icon><User /></el-icon>
				</div>
				<div class="home_login_prompt_title">请先登录</div>
				<div class="home_login_prompt_desc">登录后才能查看首页的热门题目与系统公告等内容。</div>
				<el-button type="primary" class="login_btn" @click="goLogin">去登录</el-button>
			</div>
		</section>

		<!-- 登录后的首页 -->
		<section v-if="isLoggedIn" class="home_content" v-loading="loading">
			<div class="home_main">
				<!-- 左侧主内容 -->
				<div class="home_left">
				<!-- 热门题目推荐 -->
					<div class="topics_card">
						<div class="card_header">
							<div class="header_left">
								<el-icon class="header_icon fire"><TrendCharts /></el-icon>
								<span class="header_title">热门题目推荐</span>
							</div>
							<div class="header_actions">
								<el-button size="small" class="refresh_btn" @click="refreshTopics">
									<el-icon><Refresh /></el-icon>
									换一批
								</el-button>
								<el-button size="small" class="more_btn" @click="moreClick('timuxinxi')">更多</el-button>
							</div>
						</div>
						<div class="topics_scroll" v-if="timuxinxiHomeList.length > 0">
							<div class="topics_container" ref="topicsContainer">
								<div
									v-for="item in timuxinxiHomeList"
									:key="item.id"
									class="topic_card"
									:class="{ disabled: item.yibeixuan }"
									@click="detailClick('timuxinxi', item.id)"
								>
									<div class="topic_cover">
										<img v-if="topicCover(item)" :src="topicCover(item)" alt="" class="cover_img" />
										<div v-else class="cover_placeholder">
											<el-icon><Picture /></el-icon>
										</div>
										<div class="topic_status" :class="item.yibeixuan ? 'status_selected' : 'status_available'">
											<el-icon v-if="item.yibeixuan"><Lock /></el-icon>
											<el-icon v-else><Check /></el-icon>
											{{ item.yibeixuan ? '已被选' : '可选' }}
										</div>
										<div v-if="item.yitijiao" class="topic_tag">已投</div>
									</div>
									<div class="topic_info">
										<div class="topic_title">{{ item.ketimingcheng }}</div>
										<div class="topic_tags">
											<span class="tag type_tag">
												<el-icon><Collection /></el-icon>
												{{ item.timuleixing || '-' }}
											</span>
										</div>
										<div class="topic_teacher">
											<el-icon><User /></el-icon>
											{{ item.jiaoshixingming || '-' }}
										</div>
										<el-button size="small" type="primary" class="view_btn">查看详情</el-button>
									</div>
								</div>
							</div>
						</div>
						<div v-else class="empty_state">
							<el-icon class="empty_icon"><Document /></el-icon>
							<p>暂无题目信息</p>
							<el-button type="primary" size="small" @click="moreClick('timuxinxi')">去题目列表</el-button>
						</div>
					</div>

					<!-- 数据看板 -->
					<div class="charts_card">
						<div class="card_header">
							<div class="header_left">
								<el-icon class="header_icon chart"><DataAnalysis /></el-icon>
								<span class="header_title">数据看板</span>
							</div>
						</div>
						<div class="charts_grid">
							<!-- 题目类型分布 -->
							<div class="chart_item chart_item--pie">
								<div class="chart_title">题目类型分布</div>
								<div class="chart_body" ref="pieChartRef"></div>
							</div>
							<!-- 选题趋势 -->
							<div class="chart_item chart_item--line">
								<div class="chart_title">近7天选题趋势</div>
								<div class="chart_body" ref="lineChartRef"></div>
							</div>
							<!-- 最新发布 -->
							<div class="chart_item chart_item--timeline">
								<div class="chart_title">最新发布</div>
								<div class="timeline_body">
									<div
										v-for="item in latestTopics"
										:key="item.id"
										class="timeline_item clickable"
										@click="detailClick('timuxinxi', item.id)"
									>
										<div class="timeline_dot"></div>
										<div class="timeline_content">
											<div class="timeline_title">{{ item.title }}</div>
											<div class="timeline_time">{{ item.time }}</div>
										</div>
									</div>
									<div v-if="latestTopics.length === 0" class="timeline_empty">暂无最新题目</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</section>
	</div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';
import {
	User, Document, Picture, CircleCheck, CircleCheckFilled, CircleCloseFilled,
	Clock, Collection, Top, Check, Lock, Right, ArrowRight,
	DocumentCopy, DocumentChecked, Edit, View, Timer, TrendCharts,
	Refresh, DataAnalysis, CollectionTag
} from '@element-plus/icons-vue';

const context = getCurrentInstance()?.appContext?.config?.globalProperties;
const router = useRouter();

// 图表实例
let pieChart = null;
let lineChart = null;

// 登录状态
const isLoggedIn = computed(() => !!context?.$toolUtil?.storageGet?.('frontToken'));
const userName = ref(localStorage.getItem('frontName') || '学生');
const loading = ref(false);

// 热门题目
const timuxinxiHomeList = ref([]);
const topicsContainer = ref(null);
function topicCover(item) {
	const raw = item?.timufengmian;
	if (!raw) return '';
	const first = (raw + '').split(',')[0] || '';
	return first.substr(0, 4) === 'http' ? first : (context?.$config?.url || '') + first;
}
const getTimuxinxiList = () => {
	return context?.$http({
		url: 'timuxinxi/list',
		method: 'get',
		params: { page: 1, limit: 8 }
	}).then(res => {
		timuxinxiHomeList.value = res.data.data.list || [];
	});
};

// 最新发布
const latestTopics = ref([]);

function formatRelativeTime(dateStr) {
	if (!dateStr) return '-';
	const date = new Date(dateStr);
	if (isNaN(date.getTime())) return String(dateStr);
	const diff = Date.now() - date.getTime();
	const minutes = Math.floor(diff / 60000);
	if (minutes < 1) return '刚刚';
	if (minutes < 60) return `${minutes}分钟前`;
	const hours = Math.floor(minutes / 60);
	if (hours < 24) return `${hours}小时前`;
	const days = Math.floor(hours / 24);
	if (days < 30) return `${days}天前`;
	return date.toLocaleString('zh-CN', {
		year: 'numeric',
		month: '2-digit',
		day: '2-digit',
		hour: '2-digit',
		minute: '2-digit'
	});
}

const getLatestTopics = () => {
	return context?.$http({
		url: 'timuxinxi/list',
		method: 'get',
		params: {
			page: 1,
			limit: 5,
			sort: 'fabushijian',
			order: 'desc'
		}
	}).then(res => {
		const list = res.data.data.list || [];
		latestTopics.value = list.map(item => ({
			id: item.id,
			title: item.ketimingcheng || '-',
			time: formatRelativeTime(item.fabushijian)
		}));
	});
};
const refreshTopics = () => {
	loading.value = true;
	Promise.all([getTimuxinxiList(), getLatestTopics(), getChartStats()]).finally(() => {
		loading.value = false;
	});
};

// 图表数据
const chartStats = ref({
	typeDistribution: [],
	trendDates: [],
	trendCounts: []
});
const CHART_COLORS = ['#22d3ee', '#34d399', '#818cf8', '#f59e0b', '#64748b', '#f472b6', '#38bdf8'];

const getChartStats = () => {
	return context?.$http({
		url: 'timuxinxi/homeChartStats',
		method: 'get'
	}).then(res => {
		const data = res.data.data || {};
		chartStats.value = {
			typeDistribution: data.typeDistribution || [],
			trendDates: data.trendDates || [],
			trendCounts: data.trendCounts || []
		};
		initCharts();
	}).catch(() => {
		chartStats.value = {
			typeDistribution: [],
			trendDates: [],
			trendCounts: []
		};
		initCharts();
	});
};

// 图表
const pieChartRef = ref(null);
const lineChartRef = ref(null);

const initCharts = () => {
	nextTick(() => {
		const pieData = chartStats.value.typeDistribution.length
			? chartStats.value.typeDistribution.map((item, index) => ({
				name: item.name,
				value: item.value,
				itemStyle: { color: CHART_COLORS[index % CHART_COLORS.length] }
			}))
			: [{ name: '暂无数据', value: 0, itemStyle: { color: '#64748b' } }];
		const trendDates = chartStats.value.trendDates.length
			? chartStats.value.trendDates
			: ['-', '-', '-', '-', '-', '-', '-'];
		const trendCounts = chartStats.value.trendCounts.length
			? chartStats.value.trendCounts
			: [0, 0, 0, 0, 0, 0, 0];

		// 饼图
		if (pieChartRef.value) {
			if (pieChart) pieChart.dispose();
			pieChart = echarts.init(pieChartRef.value);
			pieChart.setOption({
				backgroundColor: 'transparent',
				tooltip: {
					trigger: 'item',
					backgroundColor: '#1e3a5f',
					borderColor: '#22d3ee',
					textStyle: { color: '#fff' },
					formatter: '{b}<br/>数量：{c}（{d}%）'
				},
				legend: {
					type: 'scroll',
					orient: 'vertical',
					right: 0,
					top: 'middle',
					height: '78%',
					textStyle: { color: '#90a4ae', fontSize: 10, lineHeight: 16 },
					itemWidth: 10,
					itemHeight: 10,
					itemGap: 10,
					pageTextStyle: { color: '#90a4ae', fontSize: 10 },
					pageIconColor: '#22d3ee',
					pageIconInactiveColor: '#64748b'
				},
				series: [{
					type: 'pie',
					radius: ['38%', '58%'],
					center: ['30%', '52%'],
					avoidLabelOverlap: false,
					itemStyle: { borderRadius: 6, borderColor: '#0a1929', borderWidth: 2 },
					label: { show: false },
					emphasis: { label: { show: true, fontSize: 12, fontWeight: 'bold' } },
					data: pieData
				}]
			});
		}

		// 折线图
		if (lineChartRef.value) {
			if (lineChart) lineChart.dispose();
			lineChart = echarts.init(lineChartRef.value);
			lineChart.setOption({
				backgroundColor: 'transparent',
				tooltip: { trigger: 'axis', backgroundColor: '#1e3a5f', borderColor: '#22d3ee', textStyle: { color: '#fff' } },
				grid: { left: '10%', right: '6%', bottom: '16%', top: '14%', containLabel: true },
				xAxis: {
					type: 'category', boundaryGap: false,
					data: trendDates,
					axisLine: { lineStyle: { color: '#334155' } },
					axisLabel: { color: '#90a4ae', fontSize: 10, margin: 10 }
				},
				yAxis: {
					type: 'value',
					minInterval: 1,
					axisLine: { show: false },
					axisLabel: { color: '#90a4ae', fontSize: 10 },
					splitLine: { lineStyle: { color: 'rgba(34, 211, 238, 0.1)' } }
				},
				series: [{
					type: 'line',
					smooth: true,
					symbol: 'circle',
					symbolSize: 6,
					lineStyle: { color: '#22d3ee', width: 2 },
					areaStyle: {
						color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
							{ offset: 0, color: 'rgba(34, 211, 238, 0.4)' },
							{ offset: 1, color: 'rgba(34, 211, 238, 0.05)' }
						])
					},
					itemStyle: { color: '#22d3ee' },
					data: trendCounts
				}]
			});
		}
	});
};

// 方法
const goLogin = () => {
	localStorage.setItem('toPath', '/index/home');
	router.push('/login');
};
const detailClick = (table, id) => {
	router.push(`/index/${table}Detail?id=${id}`);
};
const moreClick = (table) => {
	router.push(`/index/${table}List`);
};

// 初始化
const initContent = () => {
	if (!isLoggedIn.value) return;
	loading.value = true;
	Promise.all([
		getTimuxinxiList(),
		getLatestTopics(),
		getChartStats()
	]).finally(() => {
		loading.value = false;
	});
};

// 组件挂载
onMounted(() => {
	initContent();
	// 响应窗口变化
	window.addEventListener('resize', handleResize);
});

// 响应式
const handleResize = () => {
	pieChart?.resize();
	lineChart?.resize();
};

// 组件卸载
onUnmounted(() => {
	window.removeEventListener('resize', handleResize);
	pieChart?.dispose();
	lineChart?.dispose();
});
</script>

<style lang="scss" scoped>
// 主题变量
$bg-dark: #0a1929;
$bg-panel: #132f4c;
$bg-card: #1e3a5f;
$border-cyan: rgba(34, 211, 238, 0.35);
$border-card: #1e4976;
$glow-cyan: rgba(34, 211, 238, 0.15);
$cyan: #22d3ee;
$cyan-dark: #0891b2;
$green: #34d399;
$yellow: #f59e0b;
$red: #ef4444;
$purple: #818cf8;
$text-main: #e2e8f0;
$text-muted: #90a4ae;

.home_page {
	width: 100%;
	min-height: 100%;
	display: flex;
	flex-direction: column;
}

// 未登录
.home_login_prompt {
	width: 100%;
	flex: 1;
	min-height: calc(100vh - 64px);
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 24px;
}
.home_login_prompt_box {
	width: min(480px, 100%);
	background: $bg-panel;
	border-radius: 16px;
	padding: 48px 40px;
	text-align: center;
	border: 1px solid $border-cyan;
	box-shadow: 0 8px 32px $glow-cyan;
}
.login_icon {
	width: 80px;
	height: 80px;
	margin: 0 auto 24px;
	border-radius: 50%;
	background: rgba(34, 211, 238, 0.12);
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 36px;
	color: $cyan;
	border: 2px solid $border-cyan;
}
.home_login_prompt_title {
	font-size: 24px;
	font-weight: 700;
	color: #fff;
	margin-bottom: 12px;
}
.home_login_prompt_desc {
	font-size: 14px;
	color: $text-muted;
	margin-bottom: 28px;
	line-height: 1.6;
}
.login_btn {
	background: linear-gradient(135deg, $cyan 0%, $cyan-dark 100%);
	border: none;
	padding: 14px 40px;
	font-size: 15px;
	font-weight: 500;
	border-radius: 10px;
	box-shadow: 0 4px 20px rgba(34, 211, 238, 0.4);
}

// 主内容
.home_content {
	flex: 1;
	display: flex;
	flex-direction: column;
	background: $bg-dark;
}

// 主布局
.home_main {
	display: flex;
	flex-direction: column;
	padding: 0;
	min-width: 0;
}
.home_left {
	display: flex;
	flex-direction: column;
	gap: 16px;
	padding: 16px;
	padding-bottom: 24px;
	min-width: 0;
}

// 通用卡片头部
.card_header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 16px;
}
.header_left {
	display: flex;
	align-items: center;
	gap: 10px;
}
.header_icon {
	font-size: 20px;
	color: $cyan;
	&.fire { color: $yellow; }
	&.chart { color: $purple; }
}
.header_title {
	font-size: 16px;
	font-weight: 600;
	color: $text-main;
}
.header_actions {
	display: flex;
	gap: 8px;
}
.refresh_btn, .more_btn {
	background: rgba($cyan, 0.1);
	border: 1px solid rgba($cyan, 0.3);
	color: $cyan;
	font-size: 12px;
	padding: 6px 12px;
	&:hover {
		background: rgba($cyan, 0.2);
	}
}

// 热门题目卡片
.topics_card {
	flex-shrink: 0;
	background: $bg-panel;
	border: 1px solid $border-card;
	border-radius: 16px;
	padding: 16px 20px;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}
.topics_scroll {
	overflow-x: auto;
	&::-webkit-scrollbar { height: 6px; }
	&::-webkit-scrollbar-track { background: rgba($cyan, 0.05); border-radius: 3px; }
	&::-webkit-scrollbar-thumb { background: rgba($cyan, 0.3); border-radius: 3px; }
}
.topics_container {
	display: flex;
	gap: 16px;
	padding-bottom: 8px;
}
.topic_card {
	width: 240px;
	flex-shrink: 0;
	background: $bg-dark;
	border: 1px solid rgba(255, 255, 255, 0.08);
	border-radius: 12px;
	overflow: hidden;
	cursor: pointer;
	transition: all 0.25s;
	&:hover {
		border-color: $cyan;
		transform: translateY(-6px);
		box-shadow: 0 12px 32px rgba($cyan, 0.2);
	}
	&.disabled {
		opacity: 0.7;
		&:hover { transform: none; }
	}
}
.topic_cover {
	position: relative;
	height: 130px;
	overflow: hidden;
}
.cover_img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}
.cover_placeholder {
	width: 100%;
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
	background: linear-gradient(135deg, rgba($cyan, 0.1), rgba($cyan-dark, 0.15));
	color: rgba($cyan, 0.5);
	font-size: 40px;
}
.topic_status {
	position: absolute;
	top: 10px;
	right: 10px;
	display: flex;
	align-items: center;
	gap: 4px;
	padding: 4px 10px;
	border-radius: 12px;
	font-size: 11px;
	font-weight: 600;
	&.status_available { background: rgba($green, 0.9); color: #fff; }
	&.status_selected { background: rgba($text-muted, 0.9); color: #fff; }
}
.topic_tag {
	position: absolute;
	top: 10px;
	left: 10px;
	padding: 4px 10px;
	border-radius: 12px;
	font-size: 11px;
	font-weight: 600;
	background: rgba($purple, 0.9);
	color: #fff;
}
.topic_info {
	padding: 14px;
}
.topic_title {
	font-size: 14px;
	font-weight: 600;
	color: $text-main;
	line-height: 1.4;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	margin-bottom: 10px;
	min-height: 40px;
}
.topic_tags {
	display: flex;
	flex-wrap: wrap;
	gap: 6px;
	margin-bottom: 8px;
}
.tag {
	display: inline-flex;
	align-items: center;
	gap: 4px;
	padding: 3px 8px;
	border-radius: 6px;
	font-size: 11px;
	background: rgba($cyan, 0.15);
	color: $cyan;
}
.topic_teacher {
	display: flex;
	align-items: center;
	gap: 6px;
	font-size: 12px;
	color: $text-muted;
	margin-bottom: 12px;
}
.view_btn {
	width: 100%;
	background: linear-gradient(135deg, $cyan, $cyan-dark);
	border: none;
	font-size: 12px;
	&:hover { opacity: 0.9; }
}

// 图表卡片
.charts_card {
	flex-shrink: 0;
	display: flex;
	flex-direction: column;
	background: $bg-panel;
	border: 1px solid $border-card;
	border-radius: 16px;
	padding: 16px 20px;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);

	.card_header {
		flex-shrink: 0;
		margin-bottom: 12px;
	}
}
.charts_grid {
	display: grid;
	grid-template-columns: minmax(0, 1.15fr) minmax(0, 0.85fr);
	grid-template-rows: auto auto;
	gap: 16px;
}
.chart_item {
	display: flex;
	flex-direction: column;
	background: $bg-dark;
	border: 1px solid rgba(255, 255, 255, 0.06);
	border-radius: 12px;
	padding: 14px 18px;

	&.chart_item--timeline {
		grid-column: 1 / -1;
	}
}
.chart_title {
	flex-shrink: 0;
	font-size: 13px;
	font-weight: 500;
	color: $text-muted;
	margin-bottom: 10px;
}
.chart_body {
	flex: 1;
	min-height: 120px;
}
.chart_item--pie .chart_body {
	min-height: 260px;
}
.chart_item--line .chart_body {
	min-height: 260px;
}
.timeline_body {
	padding-right: 4px;
}
.timeline_item {
	display: flex;
	gap: 12px;
	padding: 10px 0;
	border-bottom: 1px dashed rgba(255, 255, 255, 0.06);
	&:last-child { border-bottom: none; }
	&.clickable {
		cursor: pointer;
		&:hover .timeline_title { color: $cyan; }
	}
}
.timeline_empty {
	padding: 24px 0;
	text-align: center;
	font-size: 12px;
	color: $text-muted;
}
.timeline_dot {
	width: 8px;
	height: 8px;
	border-radius: 50%;
	background: $cyan;
	margin-top: 6px;
	flex-shrink: 0;
}
.timeline_content { flex: 1; }
.timeline_title {
	font-size: 12px;
	color: $text-main;
	line-height: 1.4;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}
.timeline_time {
	font-size: 11px;
	color: $text-muted;
	margin-top: 4px;
}

// 空状态
.empty_state {
	padding: 40px 20px;
	text-align: center;
	.empty_icon {
		font-size: 40px;
		color: rgba($cyan, 0.4);
		margin-bottom: 12px;
	}
	p { margin: 0 0 16px; color: $text-muted; font-size: 14px; }
}

// 响应式
@media (max-width: 1200px) {
	.charts_grid {
		grid-template-columns: 1fr;
		grid-template-rows: auto;
	}

	.chart_item--pie .chart_body,
	.chart_item--line .chart_body {
		min-height: 240px;
	}
}
@media (max-width: 768px) {
	.home_left {
		padding: 12px;
		gap: 12px;
	}
}
</style>
