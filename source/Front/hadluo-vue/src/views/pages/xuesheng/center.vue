<template>
	<div class="center_page">
		<!-- 顶部横幅 -->
		<div class="center_banner">
			<div class="banner_content">
				<h1 class="banner_title">个人中心</h1>
				<p class="banner_subtitle">管理您的毕设进度与个人信息</p>
			</div>
		</div>

		<!-- 主内容区 -->
		<div class="center_main">
			<!-- 左侧个人摘要 -->
			<aside class="left_sidebar">
				<!-- 头像卡片 -->
				<div class="profile_card">
					<div class="avatar_wrapper">
						<div class="avatar">
							<img v-if="userForm.touxiang" :src="getAvatar(userForm.touxiang)" alt="头像" />
							<el-icon v-else><UserFilled /></el-icon>
						</div>
						<div class="online_status"></div>
					</div>
					<div class="profile_name">{{ userForm.xueshengxingming || '学生' }}</div>
					<div class="profile_meta">
						<span class="meta_item">{{ userForm.xuehao || '-' }}</span>
						<span class="meta_divider">|</span>
						<span class="meta_item">{{ formatNianji(userForm.nianji) }}</span>
						<span class="meta_divider">|</span>
						<span class="meta_item">{{ formatBiyejie(userForm.biyejie) }}</span>
						<span class="meta_divider">|</span>
						<span class="meta_item">{{ userForm.zhuanye || '-' }}</span>
					</div>
					<div class="profile_class" v-if="userForm.banji">
						{{ userForm.banji }}班 · {{ COLLEGE_NAME }}
					</div>
					<div class="profile_class" v-else>
						{{ COLLEGE_NAME }}
					</div>
				</div>

			</aside>

			<!-- 中间主内容区 -->
			<main class="main_content">
				<!-- Tab切换 -->
				<div class="tab_header">
					<div
						v-for="tab in tabs"
						:key="tab.key"
						class="tab_item"
						:class="{ active: activeTab === tab.key }"
						@click="activeTab = tab.key"
					>
						<el-icon><component :is="tab.icon" /></el-icon>
						<span>{{ tab.name }}</span>
					</div>
				</div>

				<!-- Tab内容 -->
				<div class="tab_content">
					<!-- Tab1: 我的选题 -->
					<div v-if="activeTab === 'topic'" class="tab_panel">
						<div class="panel_card">
							<div class="card_header">
								<el-icon class="header_icon"><Document /></el-icon>
								<span>我的选题信息</span>
							</div>
							<div class="card_body" v-if="selectedTopic">
								<div class="topic_info">
									<div class="info_row">
										<span class="info_label">题目编号</span>
										<span class="info_value">{{ selectedTopic.timubianhao || '-' }}</span>
									</div>
									<div class="info_row">
										<span class="info_label">论文题目</span>
										<span class="info_value topic_title">{{ selectedTopic.ketimingcheng || '-' }}</span>
									</div>
									<div class="info_row">
										<span class="info_label">指导教师</span>
										<span class="info_value">{{ selectedTopic.jiaoshixingming || '-' }}</span>
									</div>
									<div class="info_row">
										<span class="info_label">题目类型</span>
										<span class="info_value">{{ selectedTopic.timuleixing || '-' }}</span>
									</div>
									<div class="info_row">
										<span class="info_label">申请时间</span>
										<span class="info_value">{{ formatDate(selectedTopic.shenqingshijian || selectedTopic.addtime) }}</span>
									</div>
									<div class="info_row" v-if="selectedTopicApplyReason">
										<span class="info_label">申请原因</span>
										<span class="info_value">{{ selectedTopicApplyReason }}</span>
									</div>
									<div class="info_row">
										<span class="info_label">审核状态</span>
										<span class="info_value status_value">
											<span class="status_tag" :class="getStatusClass(selectedTopic.shenhezhuangtai)">
												<el-icon v-if="isAppliedStatus(selectedTopic.shenhezhuangtai)"><Check /></el-icon>
												<el-icon v-else-if="isPendingStatus(selectedTopic.shenhezhuangtai)"><Clock /></el-icon>
												<el-icon v-else-if="isRejectedStatus(selectedTopic.shenhezhuangtai)"><Close /></el-icon>
												{{ normalizeApplicationStatus(selectedTopic.shenhezhuangtai) }}
											</span>
											<span
												v-if="isSelectedTopicRejected && selectedTopicRejectReason"
												class="reject_reason_inline"
											>
												{{ selectedTopicRejectReason }}
											</span>
										</span>
									</div>
								</div>
								<div class="topic_actions">
									<el-button type="primary" @click="goTopicDetail(selectedTopic)">
										<el-icon><View /></el-icon>
										查看申请详情
									</el-button>
									<el-button v-if="isSelectedTopicRejected" @click="goSelectTopic">
										<el-icon><RefreshLeft /></el-icon>
										重新选题
									</el-button>
								</div>
							</div>
							<div class="card_body empty" v-else>
								<el-icon class="empty_icon"><Document /></el-icon>
								<p>当前还没有选题申请</p>
								<el-button type="primary" @click="goSelectTopic">去选择题目</el-button>
							</div>
						</div>

						<div class="panel_card" v-if="selectedTopic">
							<div class="card_header">
								<el-icon class="header_icon"><Clock /></el-icon>
								<span>审核历史</span>
							</div>
							<div class="card_body">
								<div class="history_timeline" v-if="auditHistory.length > 0">
									<div v-for="(item, index) in auditHistory" :key="index" class="timeline_item">
										<div class="timeline_dot" :class="item.status"></div>
										<div class="timeline_content">
											<div class="timeline_time">{{ item.time }}</div>
											<div class="timeline_action">{{ item.action }}</div>
											<div class="timeline_status" :class="item.status">{{ item.statusText }}</div>
										</div>
									</div>
								</div>
								<div v-else class="empty_tips">暂无审核记录</div>
							</div>
						</div>
					</div>

					<!-- Tab2: 个人信息 -->
					<div v-if="activeTab === 'info'" class="tab_panel">
						<div class="panel_card">
							<div class="card_header">
								<el-icon class="header_icon"><User /></el-icon>
								<span>基本信息</span>
								<span class="header_hint">（可编辑）</span>
							</div>
							<div class="card_body">
								<el-form :model="userForm" label-width="100px" class="info_form" ref="infoFormRef">
									<el-form-item label="学号">
										<el-input v-model="userForm.xuehao" readonly class="form_input" />
									</el-form-item>
									<el-form-item label="学生姓名">
										<el-input v-model="userForm.xueshengxingming" class="form_input" />
									</el-form-item>
									<el-form-item label="性别">
										<el-select v-model="userForm.xingbie" class="form_select">
											<el-option label="男" value="男" />
											<el-option label="女" value="女" />
										</el-select>
									</el-form-item>
									<el-form-item label="手机号码">
										<el-input v-model="userForm.shoujihaoma" class="form_input" placeholder="请输入手机号码" />
									</el-form-item>
									<el-form-item label="年级">
										<el-select v-model="userForm.nianji" class="form_select">
											<el-option v-for="item in nianjiOptions" :key="item.value" :label="item.label" :value="item.value" />
										</el-select>
									</el-form-item>
									<el-form-item label="毕业届别">
										<el-select v-model="userForm.biyejie" class="form_select">
											<el-option v-for="item in biyejieOptions" :key="item.value" :label="item.label" :value="item.value" />
										</el-select>
									</el-form-item>
									<el-form-item label="学院">
										<el-input :model-value="COLLEGE_NAME" class="form_input" readonly />
									</el-form-item>
									<el-form-item label="专业">
										<el-select v-model="userForm.zhuanye" class="form_select" disabled>
											<el-option v-for="item in zhuanyeLists" :key="item" :label="item" :value="item" />
										</el-select>
									</el-form-item>
									<el-form-item label="班级">
										<el-input v-model="userForm.banji" class="form_input" placeholder="请输入班级" />
									</el-form-item>
								</el-form>
								<div class="form_actions">
									<el-button type="primary" @click="updateSession" :loading="infoLoading">
										<el-icon><Check /></el-icon>
										保存修改
									</el-button>
								</div>
							</div>
						</div>
					</div>

					<!-- Tab3: 账号安全 -->
					<div v-if="activeTab === 'security'" class="tab_panel">
						<div class="panel_card">
							<div class="card_header">
								<el-icon class="header_icon"><Lock /></el-icon>
								<span>修改密码</span>
							</div>
							<div class="card_body">
								<el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" class="info_form">
									<el-form-item label="原密码" prop="mima">
										<el-input v-model="passwordForm.mima" type="password" show-password class="form_input" placeholder="请输入原密码" />
									</el-form-item>
									<el-form-item label="新密码" prop="newmima">
										<el-input v-model="passwordForm.newmima" type="password" show-password class="form_input" placeholder="请输入新密码（6位以上）" />
										<div class="password_strength" v-if="passwordForm.newmima">
											<span>密码强度：</span>
											<div class="strength_bar">
												<div class="strength_level" :class="passwordStrength"></div>
											</div>
										</div>
									</el-form-item>
									<el-form-item label="确认密码" prop="newmima2">
										<el-input v-model="passwordForm.newmima2" type="password" show-password class="form_input" placeholder="请再次输入新密码" />
									</el-form-item>
								</el-form>
								<div class="form_actions">
									<el-button type="primary" @click="updatePassword" :loading="pwdLoading">
										<el-icon><Lock /></el-icon>
										确认修改
									</el-button>
								</div>
							</div>
						</div>

						<div class="panel_card security_tips">
							<div class="card_header">
								<el-icon class="header_icon warning"><Warning /></el-icon>
								<span>安全提示</span>
							</div>
							<div class="card_body">
								<ul class="tips_list">
									<li>密码长度至少6位，建议包含字母和数字</li>
									<li>不要使用过于简单的密码，如123456</li>
									<li>定期更换密码，保障账户安全</li>
									<li>不要在公共电脑上保存密码</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</main>
		</div>
	</div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
	User, UserFilled, Document, Lock, Check, Clock, View,
	Warning, RefreshLeft, Close
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { isAppliedStatus, isPendingStatus, isRejectedStatus, normalizeApplicationStatus, extractRejectReason, pickDisplayApplicationRecord, extractApplicationReason } from '@/utils/xuantishenqingStatus'
import { buildGraduationYearOptions, buildEnrollmentGradeOptions, formatBiyejieLabel, formatNianjiLabel, COLLEGE_NAME } from '@/utils/graduationYear'
import axiosHttp from '@/utils/http'

const router = useRouter()
const context = window.__GLOBAL_PROPERTIES__ || {}

const tableName = 'xuesheng'

// Tab控制
const activeTab = ref('topic')
const tabs = [
	{ key: 'topic', name: '我的选题', icon: 'Document' },
	{ key: 'info', name: '个人信息', icon: 'User' },
	{ key: 'security', name: '账号安全', icon: 'Lock' },
]

// 个人信息
const userForm = ref({
	xuehao: '',
	xueshengxingming: '',
	xingbie: '',
	shoujihaoma: '',
	zhuanye: '',
	banji: '',
	xueyuan: '',
	biyejie: '',
	nianji: '',
	touxiang: ''
})

const biyejieOptions = buildGraduationYearOptions(2024, 7)
const nianjiOptions = buildEnrollmentGradeOptions(2018, 10)
const formatBiyejie = formatBiyejieLabel
const formatNianji = formatNianjiLabel

const buildProfilePayload = () => {
	const studentId = userForm.value.id || userid.value || localStorage.getItem('userid')
	return {
		id: studentId ? Number(studentId) : null,
		xuehao: userForm.value.xuehao,
		xueshengxingming: userForm.value.xueshengxingming,
		xingbie: userForm.value.xingbie,
		shoujihaoma: userForm.value.shoujihaoma,
		zhuanye: userForm.value.zhuanye,
		banji: userForm.value.banji,
		nianji: userForm.value.nianji,
		biyejie: userForm.value.biyejie,
	}
}

const userid = ref('')
const zhuanyeLists = ['地理信息科学', '地理科学', '风景园林', '测绘工程', '城乡规划']

// 选题信息
const selectedTopic = ref(null)
const auditHistory = ref([])

const isSelectedTopicRejected = computed(() => isRejectedStatus(selectedTopic.value?.shenhezhuangtai))
const selectedTopicRejectReason = computed(() => extractRejectReason(selectedTopic.value))
const selectedTopicApplyReason = computed(() => extractApplicationReason(selectedTopic.value))

// 密码表单
const infoFormRef = ref(null)
const passwordFormRef = ref(null)
const infoLoading = ref(false)
const pwdLoading = ref(false)
const passwordForm = ref({
	mima: '',
	newmima: '',
	newmima2: ''
})

const validatePasswordSame = (rule, value, callback) => {
	if (value !== passwordForm.value.newmima) {
		callback(new Error('两次密码输入不一致'))
	} else {
		callback()
	}
}

const passwordRules = {
	mima: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
	newmima: [
		{ required: true, message: '请输入新密码', trigger: 'blur' },
		{ min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
	],
	newmima2: [
		{ required: true, message: '请再次输入新密码', trigger: 'blur' },
		{ validator: validatePasswordSame, trigger: 'blur' }
	]
}

// 密码强度
const passwordStrength = computed(() => {
	const p = passwordForm.value.newmima
	if (!p) return ''
	if (p.length < 6) return 'weak'
	if (/^\d+$/.test(p) || /^[a-zA-Z]+$/.test(p)) return 'weak'
	if (p.length >= 8 && /[a-zA-Z]/.test(p) && /\d/.test(p)) return 'strong'
	return 'medium'
})

// 工具方法
const getAvatar = (url) => {
	if (!url) return ''
	return url.startsWith('http') ? url : '/hadluo-xt' + url
}

const getStatusClass = (status) => {
	if (status === '通过' || status === '已审核') return 'success'
	if (status === '未审核' || status === '待审核' || status === '未申请') return 'warning'
	if (status === '驳回' || status === '否' || status === '已驳回') return 'danger'
	return ''
}

const formatDate = (date) => {
	if (!date) return '-'
	const d = new Date(date)
	if (isNaN(d.getTime())) return '-'
	return d.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const http = (options) => {
	return new Promise((resolve, reject) => {
		const token = localStorage.getItem('frontToken') || ''
		fetch(options.url, {
			method: options.method || 'GET',
			headers: {
				'Content-Type': 'application/json',
				'Token': token,
				...options.headers
			},
			body: options.data ? JSON.stringify(options.data) : undefined
		})
		.then(res => res.json())
		.then(data => {
			if (data.code === 401) {
				localStorage.clear()
				router.push('/login')
				reject(data)
			} else if (data.code === 0) {
				resolve({ data: { data: data.data } })
			} else {
				ElMessage.error(data.msg || '请求失败')
				reject(data)
			}
		})
		.catch(err => {
			ElMessage.error('网络请求失败')
			reject(err)
		})
	})
}

// 加载用户信息
const loadUserInfo = () => {
	axiosHttp.get('/xuesheng/session').then(res => {
		const data = res.data.data
		userForm.value = { ...data }
		userid.value = data.id
		localStorage.setItem('userid', data.id)
		loadSelectedTopic()
	}).catch(() => {})
}

// 加载已选中的选题
const loadSelectedTopic = () => {
	if (!userForm.value.xuehao) return
	http({
		url: '/hadluo-xt/xuantishenqing/list',
		method: 'GET',
		params: {
			page: 1,
			limit: 100,
			sort: 'shenqingshijian',
			order: 'desc'
		}
	}).then(res => {
		const pageData = res.data.data || {}
		const all = pageData.list || []
		// 优先展示有效申请；若无有效申请则展示最近一条驳回记录
		selectedTopic.value = pickDisplayApplicationRecord(all)
		buildAuditHistory(all)
	}).catch(() => {})
}

// 构建审核历史
const parseAuditLogEntries = (log) => {
	if (!log) return []
	try {
		const parsed = JSON.parse(log)
		return Array.isArray(parsed) ? parsed : []
	} catch (e) {
		return []
	}
}

const buildAuditHistory = (allApplications) => {
	if (!userForm.value.xuehao) return
	const myApps = allApplications.slice().sort((a, b) => new Date(a.addtime) - new Date(b.addtime))

	const history = []
	myApps.forEach(app => {
		const logs = parseAuditLogEntries(app.shenhejilu)
		if (logs.length) {
			logs.forEach(entry => {
				let status = 'pending'
				let statusText = '审核中'
				if (entry.action === 'pass') {
					status = 'completed'
					statusText = '已通过'
				} else if (entry.action === 'reject') {
					status = 'rejected'
					statusText = entry.reason ? `已驳回：${entry.reason}` : '已驳回'
				} else if (entry.action === 'submit') {
					status = 'pending'
					statusText = '已提交'
				}
				history.push({
					time: entry.time || formatDate(app.addtime),
					action: (entry.text || '提交选题申请') + (app.ketimingcheng ? `：${app.ketimingcheng}` : ''),
					status,
					statusText
				})
			})
			return
		}

		if (app.addtime) {
			const rejectReason = extractRejectReason(app)
			history.push({
				time: formatDate(app.addtime),
				action: '提交选题申请：' + (app.ketimingcheng || ''),
				status: isAppliedStatus(app.shenhezhuangtai)
					? 'completed'
					: (isRejectedStatus(app.shenhezhuangtai) ? 'rejected' : 'pending'),
				statusText: isAppliedStatus(app.shenhezhuangtai)
					? '已通过'
					: (isRejectedStatus(app.shenhezhuangtai)
						? (rejectReason ? `已驳回：${rejectReason}` : '已驳回')
						: '审核中')
			})
		}
	})
	auditHistory.value = history
}

// 保存个人信息
const updateSession = () => {
	if (!userForm.value.nianji) {
		ElMessage.error('请选择年级')
		return
	}
	if (!userForm.value.biyejie) {
		ElMessage.error('请选择毕业届别')
		return
	}
	const payload = buildProfilePayload()
	if (!payload.id) {
		ElMessage.error('无法获取用户信息，请重新登录')
		return
	}
	infoLoading.value = true
	axiosHttp.post('/xuesheng/update', payload).then(() => {
		ElMessage.success('个人信息更新成功')
		loadUserInfo()
	}).finally(() => {
		infoLoading.value = false
	})
}

// 修改密码
const updatePassword = () => {
	passwordFormRef.value.validate((valid) => {
		if (!valid) return
		pwdLoading.value = true
		http({
			url: '/hadluo-xt/xuesheng/changePassword',
			method: 'POST',
			data: {
				username: userForm.value.xuehao,
				oldPassword: passwordForm.value.mima,
				newPassword: passwordForm.value.newmima
			}
		}).then(res => {
			ElMessage.success('密码修改成功')
			passwordForm.value = { mima: '', newmima: '', newmima2: '' }
		}).finally(() => {
			pwdLoading.value = false
		})
	})
}

// 路由跳转
const goTopicDetail = (item) => {
	if (item && item.id) {
		router.push(`/index/xuantishenqingDetail?id=${item.id}`)
	}
}

const goSelectTopic = () => {
	router.push('/index/timuxinxiList')
}

onMounted(() => {
	loadUserInfo()
})
</script>

<style lang="scss" scoped>
// 主题变量
$bg-dark: #0a1929;
$bg-panel: #132f4c;
$bg-darker: #0d2137;
$border-color: #1e4976;
$cyan: #00bcd4;
$cyan-dark: #008ba3;
$text-main: #e0e0e0;
$text-muted: #90a4ae;
$success: #4caf50;
$warning: #ff9800;
$danger: #f44336;

.center_page {
	min-height: 100vh;
	background: $bg-dark;
}

// 顶部横幅
.center_banner {
	background: linear-gradient(135deg, $bg-panel 0%, $bg-darker 100%);
	border-bottom: 1px solid $border-color;
	padding: 24px 32px;
}

.banner_title {
	margin: 0;
	font-size: 24px;
	font-weight: 600;
	color: #fff;
}

.banner_subtitle {
	margin: 6px 0 0;
	font-size: 14px;
	color: $text-muted;
}

// 主内容区
.center_main {
	display: grid;
	grid-template-columns: 240px 1fr 280px;
	gap: 20px;
	padding: 20px 32px;
}

// 左侧边栏
.left_sidebar {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

// 头像卡片
.profile_card {
	background: $bg-panel;
	border: 1px solid $border-color;
	border-radius: 12px;
	padding: 24px 16px;
	text-align: center;
}

.avatar_wrapper {
	position: relative;
	display: inline-block;
	margin-bottom: 12px;
}

.avatar {
	width: 80px;
	height: 80px;
	border-radius: 50%;
	background: rgba($cyan, 0.15);
	border: 3px solid $cyan;
	display: flex;
	align-items: center;
	justify-content: center;
	overflow: hidden;
	color: $cyan;
	font-size: 32px;

	img {
		width: 100%;
		height: 100%;
		object-fit: cover;
	}
}

.online_status {
	position: absolute;
	bottom: 4px;
	right: 4px;
	width: 14px;
	height: 14px;
	background: $success;
	border: 3px solid $bg-panel;
	border-radius: 50%;
}

.profile_name {
	font-size: 18px;
	font-weight: 600;
	color: #fff;
	margin-bottom: 6px;
}

.profile_meta {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 8px;
	font-size: 12px;
	color: $text-muted;
}

.meta_divider {
	color: $border-color;
}

.profile_class {
	margin-top: 8px;
	font-size: 12px;
	color: $cyan;
	background: rgba($cyan, 0.1);
	padding: 4px 12px;
	border-radius: 10px;
	display: inline-block;
}

// 中间主内容
.main_content {
	background: $bg-panel;
	border: 1px solid $border-color;
	border-radius: 12px;
	overflow: hidden;
}

// Tab头部
.tab_header {
	display: flex;
	border-bottom: 1px solid $border-color;
	background: rgba(0, 0, 0, 0.2);
	flex-wrap: wrap;
}

.tab_item {
	flex: 1;
	min-width: 100px;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 8px;
	padding: 14px 8px;
	cursor: pointer;
	font-size: 13px;
	color: $text-muted;
	transition: all 0.2s;
	position: relative;
	.el-icon { font-size: 16px; }

	&::after {
		content: '';
		position: absolute;
		bottom: 0;
		left: 50%;
		transform: translateX(-50%);
		width: 0;
		height: 2px;
		background: $cyan;
		transition: width 0.2s;
	}

	&:hover {
		color: $text-main;
		background: rgba($cyan, 0.05);
	}

	&.active {
		color: $cyan;
		background: rgba($cyan, 0.1);
		&::after { width: 60%; }
	}
}

// Tab内容
.tab_content {
	padding: 20px;
}

.tab_panel {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

// 通用卡片
.panel_card {
	background: rgba(0, 0, 0, 0.2);
	border: 1px solid $border-color;
	border-radius: 10px;
	overflow: hidden;
}

.card_header {
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 14px 16px;
	background: rgba($cyan, 0.05);
	border-bottom: 1px solid $border-color;
	font-size: 14px;
	color: $cyan;

	.header_icon {
		font-size: 16px;
		&.warning { color: $warning; }
	}

	.header_hint {
		font-size: 12px;
		color: $text-muted;
		font-weight: normal;
	}

	.header_count {
		margin-left: auto;
		background: rgba($cyan, 0.15);
		padding: 2px 8px;
		border-radius: 10px;
		font-size: 12px;
	}
}

.card_body {
	padding: 16px;

	&.empty {
		text-align: center;
		padding: 40px 20px;
	}

	.empty_icon {
		font-size: 40px;
		color: rgba($cyan, 0.3);
		margin-bottom: 12px;
	}

	p {
		color: $text-muted;
		margin: 0 0 16px;
	}
}

// 选题信息
.topic_info {
	display: flex;
	flex-direction: column;
	gap: 12px;
}

.info_row {
	display: flex;
	gap: 16px;
}

.info_label {
	width: 80px;
	flex-shrink: 0;
	font-size: 13px;
	color: $text-muted;
}

.info_value {
	font-size: 13px;
	color: $text-main;
	&.topic_title { font-weight: 500; color: #fff; }
}

.status_value {
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	gap: 8px;
}

.reject_reason_inline {
	color: #fecaca;
}

.reject_reason_inline {
	font-size: 12px;
	padding: 2px 8px;
	border-radius: 8px;
	background: rgba($danger, 0.12);
	border: 1px solid rgba($danger, 0.2);
}

.status_tag {
	display: inline-flex;
	align-items: center;
	gap: 4px;
	padding: 3px 10px;
	border-radius: 10px;
	font-size: 12px;
	&.success { background: rgba($success, 0.15); color: $success; }
	&.warning { background: rgba($warning, 0.15); color: $warning; }
	&.danger { background: rgba($danger, 0.15); color: $danger; }
}

.topic_actions {
	display: flex;
	gap: 12px;
	margin-top: 16px;
	padding-top: 16px;
	border-top: 1px solid $border-color;

	.el-button {
		background: rgba($cyan, 0.1);
		border-color: $border-color;
		color: $text-main;
		display: flex;
		align-items: center;
		gap: 6px;
		&:hover { background: rgba($cyan, 0.2); border-color: $cyan; color: $cyan; }
	}
}

// 审核历史时间线
.history_timeline {
	display: flex;
	flex-direction: column;
	gap: 0;
}

.timeline_item {
	display: flex;
	gap: 14px;
	padding: 12px 0;
	position: relative;

	&:not(:last-child)::after {
		content: '';
		position: absolute;
		left: 7px;
		top: 36px;
		bottom: 0;
		width: 2px;
		background: $border-color;
	}
}

.timeline_dot {
	width: 16px;
	height: 16px;
	border-radius: 50%;
	background: $border-color;
	flex-shrink: 0;

	&.completed { background: $success; }
	&.processing { background: $cyan; animation: pulse 2s infinite; }
	&.rejected { background: $danger; }
	&.pending { background: $warning; }
}

@keyframes pulse {
	0%, 100% { box-shadow: 0 0 0 0 rgba($cyan, 0.4); }
	50% { box-shadow: 0 0 0 6px rgba($cyan, 0); }
}

.timeline_content { flex: 1; }
.timeline_time { font-size: 12px; color: $text-muted; margin-bottom: 2px; }
.timeline_action { font-size: 13px; color: $text-main; margin-bottom: 2px; }
.timeline_status {
	font-size: 11px;
	&.completed { color: $success; }
	&.processing { color: $cyan; }
	&.pending { color: $text-muted; }
	&.rejected { color: $danger; }
}

// 个人信息表单
.info_form {
	:deep(.el-form-item) {
		margin-bottom: 16px;
		.el-form-item__label { color: $text-muted; font-size: 13px; }
	}
}

.form_input, .form_select {
	width: 100%;
	:deep(.el-input__wrapper) {
		background: rgba(5, 18, 36, 0.8) !important;
		border: 1px solid $border-color;
		border-radius: 8px;
		box-shadow: none !important;
		&:hover, &.is-focus {
			border-color: $cyan;
			box-shadow: 0 0 0 3px rgba($cyan, 0.12) !important;
		}
	}
	:deep(.el-input__inner) {
		color: $text-main;
		&::placeholder { color: #64748b; }
	}
	:deep(.el-select__wrapper) {
		background-color: rgba(5, 18, 36, 0.8) !important;
		box-shadow: 0 0 0 1px $border-color inset !important;
		border-radius: 8px;
		min-height: 32px;
		&:hover {
			box-shadow: 0 0 0 1px rgba($cyan, 0.35) inset !important;
		}
	}
	:deep(.el-select__wrapper.is-focused) {
		box-shadow: 0 0 0 1px $cyan inset, 0 0 0 3px rgba($cyan, 0.12) !important;
	}
	:deep(.el-select__selected-item) {
		color: $text-main !important;
	}
	:deep(.el-select__placeholder) {
		color: #64748b !important;
	}
	:deep(.el-select__caret) { color: $text-muted; }
	:deep(.el-select__wrapper.is-disabled) {
		background-color: rgba(5, 18, 36, 0.45) !important;
		opacity: 0.75;
	}
}

.password_strength {
	display: flex;
	align-items: center;
	gap: 10px;
	margin-top: 8px;
	font-size: 12px;
	color: $text-muted;
}

.strength_bar {
	flex: 1;
	height: 4px;
	background: rgba($text-muted, 0.2);
	border-radius: 2px;
	overflow: hidden;
}

.strength_level {
	height: 100%;
	border-radius: 2px;
	transition: width 0.3s;
	&.weak { width: 33%; background: $danger; }
	&.medium { width: 66%; background: $warning; }
	&.strong { width: 100%; background: $success; }
}

.form_actions {
	display: flex;
	gap: 12px;
	margin-top: 20px;
	padding-top: 16px;
	border-top: 1px solid $border-color;
	.el-button { display: flex; align-items: center; gap: 6px; }
}

// 安全提示
.security_tips {
	.tips_list {
		margin: 0;
		padding-left: 20px;
		color: $text-muted;
		font-size: 13px;
		line-height: 1.8;
	}
}

// 收藏列表
.collect_table {
	.table_header {
		display: grid;
		grid-template-columns: 2fr 1fr 1fr 1.2fr;
		gap: 12px;
		padding: 10px 12px;
		background: rgba(0, 0, 0, 0.2);
		border-radius: 8px;
		font-size: 12px;
		color: $text-muted;
		margin-bottom: 8px;
	}

	.table_row {
		display: grid;
		grid-template-columns: 2fr 1fr 1fr 1.2fr;
		gap: 12px;
		padding: 12px;
		border-bottom: 1px solid rgba($border-color, 0.5);
		align-items: center;

		&:last-child { border-bottom: none; }

		.col_title {
			font-size: 13px;
			color: $text-main;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}

		.col_type { font-size: 12px; color: $text-muted; }
		.col_teacher { font-size: 12px; color: $cyan; }
		.col_action { display: flex; gap: 8px; }
	}
}

// 选题申请列表
.apply_table {
	.table_header {
		display: grid;
		grid-template-columns: 50px 2fr 1fr 100px 1.5fr 80px;
		gap: 12px;
		padding: 10px 12px;
		background: rgba(0, 0, 0, 0.2);
		border-radius: 8px;
		font-size: 12px;
		color: $text-muted;
		margin-bottom: 8px;
	}

	.table_row {
		display: grid;
		grid-template-columns: 50px 2fr 1fr 100px 1.5fr 80px;
		gap: 12px;
		padding: 12px;
		border-bottom: 1px solid rgba($border-color, 0.5);
		align-items: center;

		&:last-child { border-bottom: none; }

		.col_num {
			font-size: 12px;
			color: $text-muted;
		}

		.col_topic {
			font-size: 13px;
			color: $text-main;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}

		.col_teacher { font-size: 12px; color: $cyan; }
		.col_status { display: flex; align-items: center; }
		.col_time { font-size: 12px; color: $text-muted; }
		.col_action { display: flex; gap: 8px; }
	}
}

// 待办列表
.todo_list {
	display: flex;
	flex-direction: column;
	gap: 10px;
}

.todo_item {
	display: flex;
	align-items: flex-start;
	gap: 12px;
	padding: 14px;
	background: rgba(0, 0, 0, 0.2);
	border-radius: 10px;
	border: 1px solid $border-color;
	transition: all 0.2s;

	&:hover { border-color: rgba($cyan, 0.3); }

	&.done {
		opacity: 0.5;
		.todo_title { text-decoration: line-through; }
	}
}

.todo_content {
	flex: 1;
	display: flex;
	align-items: center;
	gap: 12px;
}

.todo_icon {
	width: 36px;
	height: 36px;
	border-radius: 8px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 16px;

	&.warning { background: rgba($warning, 0.15); color: $warning; }
	&.info { background: rgba($cyan, 0.15); color: $cyan; }
	&.success { background: rgba($success, 0.15); color: $success; }
}

.todo_text { flex: 1; }
.todo_title { font-size: 13px; color: $text-main; font-weight: 500; }
.todo_desc { font-size: 12px; color: $text-muted; margin-top: 4px; }

// 响应式
@media (max-width: 1200px) {
	.center_main {
		grid-template-columns: 1fr;
	}

	.left_sidebar, .right_sidebar {
		display: none;
	}
}
</style>
