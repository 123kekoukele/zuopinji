<template>
	<div class="dc-screen" v-loading="loading">
		<div class="dc-bg-grid"></div>
		<header class="dc-header">
			<div class="dc-header-deco dc-header-deco--left"></div>
			<h1 class="dc-title">{{ pageTitle }}</h1>
			<div class="dc-header-deco dc-header-deco--right"></div>
		</header>

		<div class="dc-scope-bar">
			<span class="dc-scope-label">{{ t.scopeLabel }}</span>
			<el-select
				v-if="canSwitchScope"
				v-model="selectedScope"
				size="small"
				class="dc-scope-select"
				popper-class="dc-scope-popper"
			>
				<el-option :label="t.collegeAll" value="" />
				<el-option
					v-for="m in majorOptions"
					:key="m"
					:label="m"
					:value="m"
				/>
			</el-select>
			<span v-else class="dc-scope-fixed">{{ scopeDisplayText }}</span>
		</div>

		<div class="dc-body">
			<div class="dc-col dc-col--left">
				<section class="dc-panel">
					<div class="dc-panel-head">
						<span class="dc-panel-icon"></span>
						<span>{{ t.studentOverview }}</span>
					</div>
					<div class="dc-pedestals">
						<div
							v-for="item in overviewStats"
							:key="item.key"
							class="dc-pedestal"
						>
							<div class="dc-pedestal-icon" :class="'dc-pedestal-icon--' + item.key">
								<component :is="item.icon" />
							</div>
							<div class="dc-pedestal-num">{{ item.value }}</div>
							<div class="dc-pedestal-label">{{ item.label }}</div>
							<div class="dc-pedestal-base"></div>
						</div>
					</div>
				</section>

				<section class="dc-panel dc-panel--chart">
					<div class="dc-panel-head">
						<span class="dc-panel-icon"></span>
						<span>{{ t.topicDist }}</span>
					</div>
					<div id="dcTopicPie" class="dc-chart"></div>
				</section>

				<section class="dc-panel dc-panel--chart">
					<div class="dc-panel-head">
						<span class="dc-panel-icon"></span>
						<span>{{ t.auditDist }}</span>
					</div>
					<div id="dcAuditFunnel" class="dc-chart dc-chart--funnel"></div>
				</section>
			</div>

			<div class="dc-col dc-col--right">
				<section class="dc-panel">
					<div class="dc-panel-head">
						<span class="dc-panel-icon"></span>
						<span>{{ t.progressOverview }}</span>
					</div>
					<div class="dc-rings">
						<div
							v-for="ring in progressRings"
							:key="ring.key"
							class="dc-ring-item"
						>
							<div class="dc-ring-wrap">
								<svg viewBox="0 0 100 100" class="dc-ring-svg">
									<circle cx="50" cy="50" r="42" class="dc-ring-track" />
									<circle
										cx="50"
										cy="50"
										r="42"
										class="dc-ring-bar"
										:style="ringStyle(ring.percent, ring.color)"
									/>
								</svg>
								<div class="dc-ring-center">
									<span class="dc-ring-pct">{{ ring.percent }}%</span>
								</div>
							</div>
							<div class="dc-ring-label">{{ ring.label }}</div>
							<div class="dc-ring-count">{{ ring.count }} {{ t.personUnit }}</div>
						</div>
					</div>
				</section>

				<section class="dc-panel dc-panel--majors">
					<div class="dc-panel-head">
						<span class="dc-panel-icon"></span>
						<span>{{ t.majorProgress }}</span>
					</div>
					<div class="dc-major-list">
						<div
							v-for="m in majorProgressList"
							:key="m.name"
							class="dc-major-card"
						>
							<div class="dc-major-head">
								<span class="dc-major-name">{{ m.name }}</span>
								<span class="dc-major-pct">{{ m.percent }}%</span>
							</div>
							<div class="dc-major-bar">
								<div
									class="dc-major-bar-fill"
									:style="{ width: m.percent + '%' }"
								></div>
							</div>
							<div class="dc-major-meta">
								<span>{{ t.selected }} {{ m.selected }} / {{ m.total }}</span>
							</div>
						</div>
						<div v-if="!majorProgressList.length" class="dc-empty">{{ t.noMajorData }}</div>
					</div>
				</section>
			</div>
		</div>
	</div>
</template>

<script setup>
import {
	ref,
	computed,
	inject,
	getCurrentInstance,
	onMounted,
	onBeforeUnmount,
	nextTick,
	watch,
	markRaw
} from 'vue'
import { User, Clock, CircleCheck, UserFilled } from '@element-plus/icons-vue'
import { majorOptions } from '@/utils/majorOptions'

const STATUS_APPROVED = '通过'
const STATUS_PENDING = '未审核'
const STATUS_REJECTED = '驳回'

const pageTitle = '毕业论文（设计）数据中心'
const t = {
	scopeLabel: '数据范围',
	collegeAll: '地理与空间信息学院（全院）',
	studentOverview: '学生选题概况（人）',
	topicDist: '选题情况分布',
	auditDist: '审核状态分布',
	progressOverview: '组织提交进度概览',
	majorProgress: '各专业选题进度',
	personUnit: '人',
	selected: '已选',
	noMajorData: '暂无专业数据',
	uncategorized: '未分类',
	noData: '暂无数据',
	notSelected: '未选题',
	selecting: '选题中',
	selectedTopic: '已选题',
	totalStudents: '学生总人数',
	notStarted: '未开始',
	inProgress: '进行中',
	completed: '已完成',
	teacherData: '教师课题数据'
}

const props = defineProps({
	scopeMajor: { type: String, default: '' },
	isTeacher: { type: Boolean, default: false },
	teacherGonghao: { type: String, default: '' },
	canSwitchScope: { type: Boolean, default: true }
})

const context = getCurrentInstance()?.appContext.config.globalProperties
const echarts = inject('echarts')
const http = context?.$http

const loading = ref(false)
const selectedScope = ref('')
const dashboardData = ref(null)

let pieChart = null
let funnelChart = null

const effectiveMajor = computed(() => {
	if (props.scopeMajor) return props.scopeMajor.trim()
	return (selectedScope.value || '').trim()
})

const scopeDisplayText = computed(() => {
	if (props.isTeacher) {
		if (effectiveMajor.value) {
			return effectiveMajor.value
		}
		return props.teacherGonghao
			? `${t.teacherData}\uFF08\u5de5\u53f7 ${props.teacherGonghao}\uFF09`
			: t.teacherData
	}
	if (effectiveMajor.value) return effectiveMajor.value
	return t.collegeAll
})

const overviewStats = computed(() => {
	const overview = dashboardData.value?.studentOverview || {}
	return [
		{ key: 'none', label: t.notSelected, value: overview.notSelected || 0, icon: markRaw(User) },
		{ key: 'pending', label: t.selecting, value: overview.selecting || 0, icon: markRaw(Clock) },
		{ key: 'done', label: t.selectedTopic, value: overview.selected || 0, icon: markRaw(CircleCheck) },
		{ key: 'total', label: t.totalStudents, value: overview.total || 0, icon: markRaw(UserFilled) }
	]
})

const progressRings = computed(() => {
	const progress = dashboardData.value?.progressOverview || {}
	const total = overviewStats.value[3]?.value || 1
	const none = progress.notStarted || 0
	const inProgress = progress.inProgress || 0
	const done = progress.completed || 0
	const pct = (n) => (total ? Math.round((n / total) * 100) : 0)
	return [
		{ key: 'none', label: t.notStarted, count: none, percent: pct(none), color: '#38bdf8' },
		{ key: 'pending', label: t.inProgress, count: inProgress, percent: pct(inProgress), color: '#fbbf24' },
		{ key: 'done', label: t.completed, count: done, percent: pct(done), color: '#34d399' }
	]
})

const topicDistribution = computed(() => dashboardData.value?.topicDistribution || [])

const auditModuleStats = computed(() => dashboardData.value?.auditModules || [])

const majorProgressList = computed(() => dashboardData.value?.majorProgress || [])

const ringStyle = (percent, color) => {
	const circumference = 2 * Math.PI * 42
	const offset = circumference * (1 - Math.min(percent, 100) / 100)
	return {
		strokeDasharray: `${circumference} ${circumference}`,
		strokeDashoffset: offset,
		stroke: color
	}
}

const renderPie = () => {
	const dom = document.getElementById('dcTopicPie')
	if (!dom || !echarts) return
	if (!pieChart) pieChart = echarts.init(dom)
	const data = topicDistribution.value.length
		? topicDistribution.value
		: [{ name: t.noData, value: 1 }]
	pieChart.setOption({
		backgroundColor: 'transparent',
		color: ['#22d3ee', '#38bdf8', '#818cf8', '#34d399', '#fbbf24', '#f472b6'],
		tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
		legend: {
			bottom: 0,
			left: 'center',
			textStyle: { color: '#94a3b8', fontSize: 11 },
			type: 'scroll'
		},
		series: [{
			type: 'pie',
			radius: ['42%', '62%'],
			center: ['50%', '44%'],
			itemStyle: {
				borderRadius: 4,
				borderColor: '#0a1628',
				borderWidth: 2
			},
			label: { color: '#cbd5e1', fontSize: 11 },
			data
		}]
	}, true)
}

const renderAuditChart = () => {
	const dom = document.getElementById('dcAuditFunnel')
	if (!dom || !echarts) return
	if (!funnelChart) funnelChart = echarts.init(dom)
	const modules = auditModuleStats.value
	const moduleNames = modules.map(item => item.module)
	funnelChart.setOption({
		backgroundColor: 'transparent',
		tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
		legend: {
			top: 0,
			textStyle: { color: '#94a3b8', fontSize: 11 }
		},
		grid: { left: 48, right: 16, top: 36, bottom: 24 },
		xAxis: {
			type: 'category',
			data: moduleNames,
			axisLabel: { color: '#cbd5e1', fontSize: 11, interval: 0 },
			axisLine: { lineStyle: { color: 'rgba(148,163,184,0.35)' } }
		},
		yAxis: {
			type: 'value',
			minInterval: 1,
			axisLabel: { color: '#94a3b8', fontSize: 11 },
			splitLine: { lineStyle: { color: 'rgba(148,163,184,0.12)' } }
		},
		series: [
			{
				name: STATUS_APPROVED,
				type: 'bar',
				stack: 'audit',
				barMaxWidth: 36,
				itemStyle: { color: '#34d399' },
				data: modules.map(item => item.approved || 0)
			},
			{
				name: STATUS_PENDING,
				type: 'bar',
				stack: 'audit',
				barMaxWidth: 36,
				itemStyle: { color: '#fbbf24' },
				data: modules.map(item => item.pending || 0)
			},
			{
				name: STATUS_REJECTED,
				type: 'bar',
				stack: 'audit',
				barMaxWidth: 36,
				itemStyle: { color: '#f87171' },
				data: modules.map(item => item.rejected || 0)
			}
		]
	}, true)
}

const renderCharts = () => {
	nextTick(() => {
		renderPie()
		renderAuditChart()
	})
}

watch([selectedScope, effectiveMajor, dashboardData], () => renderCharts(), { deep: true })

const handleResize = () => {
	pieChart?.resize()
	funnelChart?.resize()
}

const loadData = async () => {
	if (!http) return
	loading.value = true
	try {
		const params = {}
		if (effectiveMajor.value) {
			params.zhuanye = effectiveMajor.value
		}
		const res = await http({
			url: 'dashboard/stats',
			method: 'get',
			params
		})
		dashboardData.value = res?.data?.data || null
		renderCharts()
	} catch {
		dashboardData.value = null
		renderCharts()
	} finally {
		loading.value = false
	}
}

watch(selectedScope, () => {
	loadData()
})

onMounted(() => {
	if (props.scopeMajor) selectedScope.value = props.scopeMajor
	window.addEventListener('resize', handleResize)
	loadData()
})

onBeforeUnmount(() => {
	window.removeEventListener('resize', handleResize)
	pieChart?.dispose()
	funnelChart?.dispose()
})
</script>

<style lang="scss" scoped>
.dc-screen {
	--dc-bg: #061630;
	--dc-panel: rgba(8, 28, 58, 0.82);
	--dc-border: rgba(34, 211, 238, 0.35);
	--dc-glow: rgba(34, 211, 238, 0.15);
	--dc-cyan: #22d3ee;
	--dc-text: #e2e8f0;
	--dc-muted: #94a3b8;
	min-height: calc(100vh - 120px);
	padding: 12px 16px 24px;
	background: radial-gradient(ellipse at 50% 0%, #0c2d5a 0%, var(--dc-bg) 55%);
	color: var(--dc-text);
	position: relative;
	overflow: hidden;
	box-sizing: border-box;
}

.dc-bg-grid {
	position: absolute;
	inset: 0;
	background-image:
		linear-gradient(rgba(34, 211, 238, 0.04) 1px, transparent 1px),
		linear-gradient(90deg, rgba(34, 211, 238, 0.04) 1px, transparent 1px);
	background-size: 48px 48px;
	pointer-events: none;
}

.dc-header {
	position: relative;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 8px 0 20px;
	gap: 16px;
}

.dc-title {
	margin: 0;
	font-size: 26px;
	font-weight: 600;
	letter-spacing: 4px;
	color: #fff;
	text-shadow: 0 0 20px rgba(34, 211, 238, 0.6);
}

.dc-header-deco {
	width: 120px;
	height: 2px;
	background: linear-gradient(90deg, transparent, var(--dc-cyan), transparent);
	position: relative;

	&::after {
		content: '';
		position: absolute;
		top: -4px;
		width: 8px;
		height: 8px;
		background: var(--dc-cyan);
		transform: rotate(45deg);
		box-shadow: 0 0 8px var(--dc-cyan);
	}

	&--left::after { right: 0; }
	&--right::after { left: 0; }
}

.dc-scope-bar {
	position: relative;
	display: flex;
	align-items: center;
	justify-content: flex-end;
	gap: 10px;
	margin-bottom: 12px;
	font-size: 13px;
}

.dc-scope-label { color: var(--dc-muted); }

.dc-scope-fixed {
	padding: 4px 14px;
	border: 1px solid var(--dc-border);
	border-radius: 4px;
	background: var(--dc-panel);
	color: var(--dc-cyan);
}

.dc-scope-select {
	width: 260px;

	:deep(.el-input__wrapper) {
		background: var(--dc-panel);
		box-shadow: 0 0 0 1px var(--dc-border) inset;
	}

	:deep(.el-input__inner) {
		color: var(--dc-text);
	}
}

.dc-body {
	position: relative;
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 14px;
}

.dc-col {
	display: flex;
	flex-direction: column;
	gap: 14px;
	min-width: 0;
}

.dc-panel {
	background: var(--dc-panel);
	border: 1px solid var(--dc-border);
	border-radius: 6px;
	box-shadow: 0 0 24px var(--dc-glow), inset 0 1px 0 rgba(255, 255, 255, 0.05);
	padding: 14px 16px;
	position: relative;

	&::before {
		content: '';
		position: absolute;
		top: 0;
		left: 16px;
		right: 16px;
		height: 1px;
		background: linear-gradient(90deg, transparent, var(--dc-cyan), transparent);
		opacity: 0.5;
	}
}

.dc-panel-head {
	display: flex;
	align-items: center;
	gap: 8px;
	font-size: 15px;
	font-weight: 500;
	color: var(--dc-cyan);
	margin-bottom: 14px;
}

.dc-panel-icon {
	width: 4px;
	height: 14px;
	background: var(--dc-cyan);
	border-radius: 2px;
	box-shadow: 0 0 6px var(--dc-cyan);
}

.dc-pedestals {
	display: grid;
	grid-template-columns: repeat(4, 1fr);
	gap: 10px;
}

.dc-pedestal {
	text-align: center;
	position: relative;
	padding-bottom: 8px;
}

.dc-pedestal-icon {
	width: 44px;
	height: 44px;
	margin: 0 auto 8px;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 22px;
	color: var(--dc-cyan);
	background: rgba(34, 211, 238, 0.12);
	border: 1px solid var(--dc-border);

	&--done { color: #34d399; background: rgba(52, 211, 153, 0.12); }
	&--pending { color: #fbbf24; background: rgba(251, 191, 36, 0.12); }
	&--total { color: #818cf8; background: rgba(129, 140, 248, 0.12); }
}

.dc-pedestal-num {
	font-size: 28px;
	font-weight: 700;
	color: #fff;
	line-height: 1.2;
}

.dc-pedestal-label {
	font-size: 12px;
	color: var(--dc-muted);
	margin-top: 4px;
}

.dc-pedestal-base {
	margin: 10px auto 0;
	width: 70%;
	height: 6px;
	background: linear-gradient(180deg, rgba(34, 211, 238, 0.4), rgba(34, 211, 238, 0.05));
	border-radius: 2px;
	transform: perspective(40px) rotateX(25deg);
}

.dc-chart {
	width: 100%;
	height: 220px;
}

.dc-chart--funnel {
	height: 240px;
}

.dc-rings {
	display: flex;
	justify-content: space-around;
	gap: 8px;
	padding: 8px 0;
}

.dc-ring-item {
	flex: 1;
	text-align: center;
}

.dc-ring-wrap {
	position: relative;
	width: 100px;
	height: 100px;
	margin: 0 auto;
}

.dc-ring-svg {
	width: 100%;
	height: 100%;
	transform: rotate(-90deg);
}

.dc-ring-track {
	fill: none;
	stroke: rgba(148, 163, 184, 0.2);
	stroke-width: 8;
}

.dc-ring-bar {
	fill: none;
	stroke-width: 8;
	stroke-linecap: round;
	transition: stroke-dashoffset 0.6s ease;
}

.dc-ring-center {
	position: absolute;
	inset: 0;
	display: flex;
	align-items: center;
	justify-content: center;
}

.dc-ring-pct {
	font-size: 18px;
	font-weight: 700;
	color: #fff;
}

.dc-ring-label {
	margin-top: 8px;
	font-size: 13px;
	color: var(--dc-text);
}

.dc-ring-count {
	font-size: 11px;
	color: var(--dc-muted);
	margin-top: 2px;
}

.dc-major-list {
	display: flex;
	flex-direction: column;
	gap: 10px;
	max-height: 320px;
	overflow-y: auto;

	&::-webkit-scrollbar { width: 4px; }
	&::-webkit-scrollbar-thumb {
		background: rgba(34, 211, 238, 0.3);
		border-radius: 2px;
	}
}

.dc-major-card {
	padding: 10px 12px;
	background: rgba(6, 22, 48, 0.6);
	border: 1px solid rgba(34, 211, 238, 0.2);
	border-radius: 4px;
}

.dc-major-head {
	display: flex;
	justify-content: space-between;
	margin-bottom: 8px;
	font-size: 13px;
}

.dc-major-name { color: var(--dc-text); }
.dc-major-pct { color: var(--dc-cyan); font-weight: 600; }

.dc-major-bar {
	height: 6px;
	background: rgba(148, 163, 184, 0.15);
	border-radius: 3px;
	overflow: hidden;
}

.dc-major-bar-fill {
	height: 100%;
	background: linear-gradient(90deg, #0284c7, #22d3ee);
	border-radius: 3px;
	transition: width 0.5s ease;
}

.dc-major-meta {
	display: flex;
	justify-content: space-between;
	margin-top: 6px;
	font-size: 11px;
	color: var(--dc-muted);
}

.dc-empty {
	text-align: center;
	padding: 24px;
	color: var(--dc-muted);
	font-size: 13px;
}

@media (max-width: 1100px) {
	.dc-body {
		grid-template-columns: 1fr;
	}

	.dc-pedestals {
		grid-template-columns: repeat(2, 1fr);
	}
}
</style>
