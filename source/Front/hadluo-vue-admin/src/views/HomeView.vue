<template>
	<div class="home_view">
		<DataCenterDashboard
			v-if="ready"
			:scope-major="adminMajor"
			:is-teacher="isTeacher"
			:teacher-gonghao="teacherGonghao"
			:can-switch-scope="canSwitchScope"
		/>
	</div>
</template>

<script setup>
import { ref, computed, getCurrentInstance, onMounted } from 'vue'
import DataCenterDashboard from '@/components/dashboard/DataCenterDashboard.vue'

const context = getCurrentInstance()?.appContext.config.globalProperties
const role = ref(context?.$toolUtil.storageGet('role') || '')
const sessionTable = ref(context?.$toolUtil.storageGet('sessionTable') || '')
const adminMajor = ref('')
const teacherGonghao = ref('')
const ready = ref(false)

const isTeacher = computed(() => role.value === '教师')
const isAdmin = computed(() => role.value === '管理员')
const canSwitchScope = computed(() => isAdmin.value && !adminMajor.value)

const loadSession = async () => {
	const table = sessionTable.value
	if (!table || !context?.$http) {
		ready.value = true
		return
	}
	try {
		const res = await context.$http({
			url: `${table}/session`,
			method: 'get'
		})
		const data = res?.data?.data || {}
		if (isTeacher.value) {
			teacherGonghao.value = data.jiaoshigonghao || ''
			adminMajor.value = (data.zhuanye || '').trim()
		} else if (isAdmin.value) {
			adminMajor.value = (data.zhuanye || '').trim()
		}
	} catch {
		// 会话获取失败时仍展示全院数据
	}
	ready.value = true
}

onMounted(() => {
	loadSession()
})
</script>

<style lang="scss" scoped>
.home_view {
	width: 100%;
	min-height: calc(100vh - 120px);
	padding: 0;
	margin: 0;
	background: #061630;
	box-sizing: border-box;
}
</style>
