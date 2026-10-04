<template>
	<div class="top_view">
		<div class="top_left_view">
			<div class="projectTitle">
				<span>地空院毕业设计选题系统-后台</span>
			</div>
		</div>

		<div class="top_right_view">
			<el-dropdown
				trigger="click"
				:hide-on-click="false"
				popper-class="open-time-dropdown-popper"
				ref="openTimeDropdownRef"
			>
				<el-button
					type="primary"
					size="small"
					link
					:disabled="!canSetOpenTime()"
				>
					系统开放时间设置
				</el-button>
				<template #dropdown>
					<el-dropdown-menu class="open-time-dropdown-menu">
						<el-dropdown-item @click.stop>
							<div class="open-time-dropdown" @click.stop @mousedown.stop>
								<div class="open-time-header">系统开放时间设置</div>
								<el-form :model="openTimeForm" label-width="80px" size="small">
									<div class="open-time-hint">
										当前设置范围：{{ openTimeScopeText }}
									</div>
									<el-form-item label="开始时间">
										<div class="datetime-picker-group">
											<el-date-picker
												v-model="openTimeForm.startDate"
												type="date"
												value-format="YYYY-MM-DD"
												placeholder="选择日期"
												:teleported="false"
												style="width: 100%;"
											/>
											<el-time-picker
												v-model="openTimeForm.startTime"
												value-format="HH:mm:ss"
												placeholder="选择时间"
												:teleported="false"
												style="width: 100%;"
											/>
										</div>
									</el-form-item>
									<el-form-item label="结束时间">
										<div class="datetime-picker-group">
											<el-date-picker
												v-model="openTimeForm.endDate"
												type="date"
												value-format="YYYY-MM-DD"
												placeholder="选择日期"
												:teleported="false"
												style="width: 100%;"
											/>
											<el-time-picker
												v-model="openTimeForm.endTime"
												value-format="HH:mm:ss"
												placeholder="选择时间"
												:teleported="false"
												style="width: 100%;"
											/>
										</div>
									</el-form-item>
									<div style="display: flex; justify-content: flex-end;">
										<el-button
											type="primary"
											size="small"
											@click.stop="handleSaveAndClose"
										>
											保存设置
										</el-button>
									</div>
								</el-form>
							</div>
						</el-dropdown-item>
					</el-dropdown-menu>
				</template>
			</el-dropdown>

			<el-dropdown class="avatar-container right-menu-item" trigger="hover">
				<div class="avatar-wrapper">
					<div class="nickname">{{$toolUtil.storageGet('adminName')}}</div>
					<img class="user-avatar" src="@/assets/img/avatar.png">
					<el-icon class="el-icon--right">
						<arrow-down />
					</el-icon>
				</div>
				<template #dropdown>
					<el-dropdown-menu slot="dropdown">
						<el-dropdown-item @click="centerClick" v-if="roleName!='管理员'">
							个人中心
						</el-dropdown-item>
						<el-dropdown-item @click="updatepasswordClick">
							修改密码
						</el-dropdown-item>
						<el-dropdown-item>
							<span style="display:block;" @click="onLogout">退出登录</span>
						</el-dropdown-item>
					</el-dropdown-menu>
				</template>
			</el-dropdown>
		</div>
	</div>
</template>

<script setup>
	import axios from 'axios'
	import {
		ElMessageBox
	} from 'element-plus'
	import {
		getCurrentInstance,
		ref,
		onMounted
	} from 'vue';
	import { useStore } from 'vuex'
	const store = useStore()
	import {
		useRouter
	} from 'vue-router';
	
	const router = useRouter()
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const role = context?.$toolUtil.storageGet('sessionTable')
	const roleName = context?.$toolUtil.storageGet('role')

	const btnAuth = (tableName, action) => {
		return context?.$toolUtil?.isAuth?.(tableName, action)
	}

	// 检查是否为管理员或教师（可设置系统开放时间）
	const canSetOpenTime = () => {
		const role = context?.$toolUtil.storageGet('role')
		// 管理员（包括超级管理员和专业管理员）和教师都可以设置开放时间
		return role === '管理员' || role === '教师'
	}

	// Dropdown 的 ref
	const openTimeDropdownRef = ref(null)

	const MAJOR_CODE_MAP = Object.freeze({
		'地理信息科学': 'm1',
		'地理科学': 'm2',
		'风景园林': 'm3',
		'测绘工程': 'm4',
		'城乡规划': 'm5'
	})

	const tableName = 'config'
	const openTimeForm = ref({
		startDate: '',
		startTime: '',
		endDate: '',
		endTime: ''
	})
	const adminMajor = ref('')
	const openTimeScopeText = ref('全体成员（管理员/教师/学生）')

	const resolveOpenTimeConfigKeys = () => {
		const major = (adminMajor.value || '').trim()
		const majorCode = MAJOR_CODE_MAP[major]
		if (majorCode) {
			openTimeScopeText.value = `${major}专业学生`
			return {
				start: `system_open_start_major_${majorCode}`,
				end: `system_open_end_major_${majorCode}`
			}
		}
		openTimeScopeText.value = '全体成员（管理员/教师/学生）'
		return {
			start: 'system_open_start',
			end: 'system_open_end'
		}
	}

	const getConfigByName = async (name) => {
		try {
			const res = await context?.$http({
				url: `${tableName}/info`,
				method: 'get',
				params: { name }
			})
			return res?.data?.data || null
		} catch (e) {
			return null
		}
	}

	const loadOpenTime = async () => {
		const keys = resolveOpenTimeConfigKeys()
		const startCfg = await getConfigByName(keys.start)
		const endCfg = await getConfigByName(keys.end)
		// 解析开始时间
		if (startCfg?.value) {
			const [date, time] = startCfg.value.split(' ')
			openTimeForm.value.startDate = date || ''
			openTimeForm.value.startTime = time || ''
		} else {
			openTimeForm.value.startDate = ''
			openTimeForm.value.startTime = ''
		}
		// 解析结束时间
		if (endCfg?.value) {
			const [date, time] = endCfg.value.split(' ')
			openTimeForm.value.endDate = date || ''
			openTimeForm.value.endTime = time || ''
		} else {
			openTimeForm.value.endDate = ''
			openTimeForm.value.endTime = ''
		}
	}

	const upsertConfigByName = async (name, value) => {
		const cfg = await getConfigByName(name)
		if (!cfg) {
			return context?.$http({
				url: `${tableName}/save`,
				method: 'post',
				data: { name, value }
			})
		}
		return context?.$http({
			url: `${tableName}/update`,
			method: 'post',
			data: { id: cfg.id, name, value }
		})
	}

	const saveOpenTime = async () => {
		// 组合日期和时间
		const startDateTime = openTimeForm.value.startDate && openTimeForm.value.startTime
			? `${openTimeForm.value.startDate} ${openTimeForm.value.startTime}`
			: ''
		const endDateTime = openTimeForm.value.endDate && openTimeForm.value.endTime
			? `${openTimeForm.value.endDate} ${openTimeForm.value.endTime}`
			: ''

		if (!openTimeForm.value.startDate || !openTimeForm.value.startTime) {
			context?.$toolUtil.message('请选择开放开始时间', 'error')
			return false
		}
		if (!openTimeForm.value.endDate || !openTimeForm.value.endTime) {
			context?.$toolUtil.message('请选择开放结束时间', 'error')
			return false
		}
		if (endDateTime < startDateTime) {
			context?.$toolUtil.message('结束时间不能早于开始时间', 'error')
			return false
		}
		const keys = resolveOpenTimeConfigKeys()
		await upsertConfigByName(keys.start, startDateTime)
		await upsertConfigByName(keys.end, endDateTime)
		context?.$toolUtil.message(`${openTimeScopeText.value}开放时间保存成功`, 'success')
		await loadOpenTime()
		return true
	}

	// 保存并关闭下拉菜单
	const handleSaveAndClose = async () => {
		const saved = await saveOpenTime()
		if (!saved) return
		if (openTimeDropdownRef.value) {
			openTimeDropdownRef.value.handleClose()
		}
	}

	const getSession = async () => {
		const res = await context?.$http({
			url: `${context?.$toolUtil.storageGet('sessionTable')}/session`,
			method: 'get'
		})
		context?.$toolUtil.storageSet('userid',res?.data?.data?.id)
		adminMajor.value = res?.data?.data?.zhuanye || ''
	}
	// 退出登录
	const onLogout = () => {
		let toolUtil = context?.$toolUtil
		store.dispatch('delAllCachedViews')
		store.dispatch('delAllVisitedViews')
		toolUtil.storageClear()
		router.replace({
			name: "login"
		});
	}
	// 个人中心
	const centerClick = () => {
		router.push(`/${role}Center`)
	}
	// 修改密码
	const updatepasswordClick = () => {
		router.push(`/updatepassword`)
	}
	onMounted(() => {
		getSession().then(() => {
			loadOpenTime()
		})
	})
</script>

<style lang="scss" scoped>
// 总盒子
.top_view {
	z-index: 998;
	color: #fff;
	top: 0;
	left: 0;
	background: linear-gradient(90deg, #082f49 0%, #0c4a6e 45%, #082f49 100%);
	box-shadow: 0 2px 16px rgba(0, 0, 0, 0.35), inset 0 -1px 0 rgba(34, 211, 238, 0.35);
	display: flex;
	width: 100%;
	font-size: 16px;
	justify-content: space-between;
	align-items: center;
	position: fixed;
	height: 64px;
	padding: 0 !important;
	margin: 0 !important;

	// 左边盒子
	.top_left_view {
		display: flex;
		flex: 1;
		align-items: center;
		height: 100%;
		padding: 0 !important;
		margin: 0 !important;
		position: relative;

		// 标题
		.projectTitle {
			font-size: 20px;
			font-weight: 600;
			color: #fff;
			letter-spacing: 1px;
			text-shadow: 0 0 18px rgba(34, 211, 238, 0.35);
			margin: 0 !important;
			padding: 0 !important;
			position: absolute;
			left: 28px !important;
			top: 50%;
			transform: translateY(-50%);
			white-space: nowrap;
			width: auto !important;
			display: flex !important;
			align-items: center;
			gap: 14px;
		}
	}

	// 右部盒子
	.top_right_view {
		display: flex;
		justify-content: flex-end;
		align-items: center;
		height: 100%;
		padding-right: 20px;

		// 头像盒子
		.avatar-container {
			cursor: pointer;
			margin: 0 20px 0 0;
			color: #fff;
			display: flex;
			align-items: center;
			height: 100%;
			padding: 0 16px;
			border-radius: 6px;
			transition: none;

			&:hover {
				background: rgba(255, 255, 255, 0.15);
			}

			.avatar-wrapper {
				display: flex;
				position: relative;
				align-items: center;
				gap: 8px;

				// 昵称
				.nickname {
					cursor: pointer;
					color: #fff;
					font-size: 14px;
					font-weight: 400;
				}

				// 头像
				.user-avatar {
					cursor: pointer;
					border-radius: 50%;
					width: 32px;
					height: 32px;
					border: 2px solid rgba(255, 255, 255, 0.3);
				}

				// 图标
				.el-icon--right {
					color: #fff;
					font-size: 12px;
				}
			}
		}
	}
}

:deep(.el-button.is-link) {
	color: rgba(167, 243, 252, 0.95);
}

:deep(.el-button.is-link:hover) {
	color: #fff;
}

// 下拉盒子
.el-dropdown-menu {
	background: var(--el-bg-color-overlay) !important;
	border: 1px solid var(--tech-border-soft) !important;
	border-radius: 8px;
	box-shadow: 0 8px 28px rgba(0, 0, 0, 0.35), 0 0 20px rgba(34, 211, 238, 0.08);
	padding: 0 !important;
	min-width: auto !important;

	// 下拉盒子item - 移除悬浮效果
	:deep(.el-dropdown-menu__item) {
		color: var(--text-regular);
		background: transparent;
		padding: 0;
		font-size: 14px;
		transition: none;
	}

	// item悬浮 - 保持透明
	:deep(.el-dropdown-menu__item:hover) {
		color: var(--tech-cyan);
		background: transparent;
	}
}
</style>

<style lang="scss">
/* Popover 挂载到 body，需非 scoped */
.open-time-hint {
	margin: 0 0 10px 0;
	color: var(--text-secondary);
	font-size: 13px;
}

/* 打开时间设置下拉框样式 */
.open-time-dropdown-popper {
	max-height: none !important;
	overflow: visible !important;
}

.open-time-dropdown-popper .el-dropdown-menu {
	max-height: none !important;
	overflow: visible !important;
}

.open-time-dropdown-menu {
	overflow: visible !important;
	max-height: none !important;
	min-height: 420px;
}

.open-time-dropdown {
	padding: 16px 20px 20px;
	min-width: 420px;
	min-height: 400px;
	overflow: visible;

	.open-time-header {
		font-size: 15px;
		font-weight: 600;
		color: var(--text-primary);
		margin-bottom: 12px;
		padding-bottom: 8px;
		border-bottom: 1px solid var(--el-border-color-lighter);
	}

	.datetime-picker-group {
		display: flex;
		flex-direction: column;
		align-items: stretch;
		gap: 10px;
		width: 100%;
		min-height: 72px;
		position: relative;
	}
}

.open-time-dropdown-menu .el-dropdown-menu__item,
.open-time-dropdown-popper .el-dropdown-menu__item {
	overflow: visible !important;
	max-height: none !important;
	height: auto !important;
	padding: 0 !important;
}

.open-time-dropdown .el-picker-panel,
.open-time-dropdown-popper .el-picker-panel {
	z-index: 3000;
}

.open-time-dropdown .el-time-panel,
.open-time-dropdown-popper .el-time-panel {
	width: 100%;
}

.open-time-dropdown .el-time-spinner__wrapper,
.open-time-dropdown-popper .el-time-spinner__wrapper {
	max-height: 240px;
}

:deep(.el-dropdown-menu) {
	padding: 0 !important;
}

:deep(.el-form-item__error) {
	left: 0 !important;
}
</style>
