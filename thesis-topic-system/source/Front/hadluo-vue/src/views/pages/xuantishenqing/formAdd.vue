<template>
	<div class="apply_page">
		<header class="page_header">
			<div class="page_header_top">
				<el-breadcrumb separator="/" class="page_breadcrumb">
					<el-breadcrumb-item :to="{ path: '/index/home' }">首页</el-breadcrumb-item>
					<el-breadcrumb-item :to="{ path: '/index/timuxinxiList' }">题目信息</el-breadcrumb-item>
					<el-breadcrumb-item>{{ formName }}</el-breadcrumb-item>
				</el-breadcrumb>
				<el-button class="back_btn" @click="backClick">
					<el-icon><ArrowLeft /></el-icon>
					返回
				</el-button>
			</div>
			<h1 class="page_title">{{ pageTitle }}</h1>
			<p class="page_subtitle">{{ pageSubtitle }}</p>
		</header>

		<el-alert
			v-if="isCrossType"
			class="tip_alert"
			type="info"
			:closable="false"
			show-icon
			title="题目信息已自动带入，请填写申请原因后提交。提交后需等待指导教师审核。"
		/>

		<el-form ref="formRef" :model="form" class="apply_form" label-width="96px" :rules="rules">
			<section class="panel_card">
				<div class="panel_title">
					<el-icon><Document /></el-icon>
					<span>题目信息</span>
				</div>
				<div class="field_grid">
					<el-form-item label="题目编号" prop="timubianhao">
						<el-input v-model="form.timubianhao" placeholder="题目编号" readonly class="field_readonly" />
					</el-form-item>
					<el-form-item label="题目类型" prop="timuleixing">
						<el-input v-model="form.timuleixing" placeholder="题目类型" readonly class="field_readonly" />
					</el-form-item>
					<el-form-item label="课题名称" prop="ketimingcheng" class="field_full">
						<el-input v-model="form.ketimingcheng" placeholder="课题名称" readonly class="field_readonly" />
					</el-form-item>
					<el-form-item label="专业" prop="zhuanye">
						<el-input v-model="form.zhuanye" placeholder="专业" readonly class="field_readonly" />
					</el-form-item>
					<el-form-item label="选题时间" prop="xuantishijian">
						<el-input v-model="form.xuantishijian" placeholder="选题时间" readonly class="field_readonly" />
					</el-form-item>
				</div>
			</section>

			<section class="panel_card">
				<div class="panel_title">
					<el-icon><User /></el-icon>
					<span>指导教师</span>
				</div>
				<div class="field_grid">
					<el-form-item label="教师工号" prop="jiaoshigonghao">
						<el-input v-model="form.jiaoshigonghao" placeholder="教师工号" readonly class="field_readonly" />
					</el-form-item>
					<el-form-item label="教师姓名" prop="jiaoshixingming">
						<el-input v-model="form.jiaoshixingming" placeholder="教师姓名" readonly class="field_readonly" />
					</el-form-item>
				</div>
			</section>

			<section class="panel_card">
				<div class="panel_title">
					<el-icon><EditPen /></el-icon>
					<span>申请信息</span>
				</div>
				<div class="field_grid">
					<el-form-item label="学号" prop="xuehao">
						<el-input v-model="form.xuehao" placeholder="学号" readonly class="field_readonly" />
					</el-form-item>
					<el-form-item label="学生姓名" prop="xueshengxingming">
						<el-input v-model="form.xueshengxingming" placeholder="学生姓名" readonly class="field_readonly" />
					</el-form-item>
					<el-form-item label="申请时间" prop="shenqingshijian">
						<el-date-picker
							v-model="form.shenqingshijian"
							format="YYYY-MM-DD HH:mm:ss"
							value-format="YYYY-MM-DD HH:mm:ss"
							type="datetime"
							class="field_date"
							readonly
							disabled
							placeholder="申请时间"
						/>
					</el-form-item>
					<el-form-item label="审核状态" prop="shenhezhuangtai" v-if="!isCrossType">
						<el-select
							v-model="form.shenhezhuangtai"
							placeholder="请选择审核状态"
							class="field_select"
							:disabled="!isAdd || disabledForm.shenhezhuangtai"
						>
							<el-option v-for="item in shenhezhuangtaiLists" :key="item" :label="item" :value="item" />
						</el-select>
					</el-form-item>
					<el-form-item v-else label="审核状态" class="field_full">
						<span class="status_chip status_pending">未审核</span>
					</el-form-item>
					<el-form-item label="申请原因" prop="shenqingyuanyin" class="field_full">
						<el-input
							v-model="form.shenqingyuanyin"
							type="textarea"
							:rows="4"
							maxlength="500"
							show-word-limit
							placeholder="请简要说明选择该课题的原因、相关基础或兴趣方向（必填）"
							class="field_textarea"
							:readonly="!isAdd || disabledForm.shenqingyuanyin"
						/>
					</el-form-item>
				</div>
			</section>

			<section class="panel_card" v-if="form.ketixingzhi">
				<div class="panel_title">
					<el-icon><Reading /></el-icon>
					<span>课题性质</span>
				</div>
				<div class="text_block">{{ form.ketixingzhi }}</div>
			</section>

			<div class="form_actions">
				<el-button class="btn_cancel" @click="backClick">取消</el-button>
				<el-button class="btn_submit" type="primary" :loading="submitLoading" @click="save">
					<el-icon><CircleCheck /></el-icon>
					{{ submitButtonText }}
				</el-button>
			</div>
		</el-form>
	</div>
</template>

<script setup>
import { ref, computed, getCurrentInstance, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft, Document, User, EditPen, Reading, CircleCheck } from '@element-plus/icons-vue'
import { isActiveApplicationRecord } from '@/utils/xuantishenqingStatus'

const context = getCurrentInstance()?.appContext.config.globalProperties
const route = useRoute()
const tableName = 'xuantishenqing'
const formName = '选题申请'

const form = ref({
	timubianhao: '',
	ketimingcheng: '',
	timuleixing: '',
	zhuanye: '',
	ketixingzhi: '',
	xuantishijian: '',
	jiaoshigonghao: '',
	jiaoshixingming: '',
	xuehao: '',
	xueshengxingming: '',
	shenqingyuanyin: '',
	shenqingshijian: '',
	shenhezhuangtai: '未审核',
	crossuserid: '',
	crossrefid: '',
})
const formRef = ref(null)
const id = ref(0)
const type = ref('')
const submitLoading = ref(false)
const disabledForm = ref({
	timubianhao: false,
	ketimingcheng: false,
	timuleixing: false,
	zhuanye: false,
	ketixingzhi: false,
	xuantishijian: false,
	jiaoshigonghao: false,
	jiaoshixingming: false,
	xuehao: false,
	xueshengxingming: false,
	shenqingyuanyin: false,
	shenqingshijian: false,
	shenhezhuangtai: false,
	crossuserid: false,
	crossrefid: false,
})
const isAdd = ref(false)
const rules = ref({
	shenqingyuanyin: [
		{ required: true, message: '请填写申请原因', trigger: 'blur' },
		{ min: 2, message: '申请原因至少 2 个字', trigger: 'blur' },
	],
})
const shenhezhuangtaiLists = ref([])
const crossRow = ref('')
const crossTable = ref('')
const crossTips = ref('')
const crossColumnName = ref('')
const crossColumnValue = ref('')

const isCrossType = computed(() => type.value === 'cross')
const isStudentSession = computed(() => context?.$toolUtil.storageGet('frontSessionTable') === 'xuesheng')
const pageTitle = computed(() => (isCrossType.value ? '提交选题申请' : formName))
const pageSubtitle = computed(() =>
	isCrossType.value ? '确认题目与个人信息后提交，教师审核通过即完成选题' : '填写并保存选题申请信息'
)
const submitButtonText = computed(() => (isCrossType.value ? '提交申请' : '保存'))

const getInfo = () => {
	context?.$http({
		url: `${tableName}/info/${id.value}`,
		method: 'get',
	}).then(res => {
		form.value = res.data.data
	})
}

const init = (formId = null, formType = 'add', formNames = '', row = null, table = null, statusColumnName = null, tips = null, statusColumnValue = null) => {
	form.value.shenqingshijian = context?.$toolUtil.getCurDateTime()
	if (formId) {
		id.value = formId
		type.value = formType
	}
	if (formType === 'add') {
		isAdd.value = true
	} else if (formType === 'info') {
		isAdd.value = false
		getInfo()
	} else if (formType === 'edit') {
		isAdd.value = true
		getInfo()
	} else if (formType === 'cross') {
		isAdd.value = true
		for (const x in row) {
			if (Object.prototype.hasOwnProperty.call(form.value, x)) {
				form.value[x] = row[x]
				disabledForm.value[x] = true
			}
		}
		if (row) crossRow.value = row
		if (table) crossTable.value = table
		if (tips) crossTips.value = tips
		if (statusColumnName) crossColumnName.value = statusColumnName
		if (statusColumnValue) crossColumnValue.value = statusColumnValue
		form.value.shenhezhuangtai = '未审核'
		disabledForm.value.shenhezhuangtai = true
		disabledForm.value.shenqingyuanyin = false
	}

	context?.$http({
		url: `${context?.$toolUtil.storageGet('frontSessionTable')}/session`,
		method: 'get',
	}).then(res => {
		const json = res.data.data
		if (json.hasOwnProperty('xuehao') && context?.$toolUtil.storageGet('frontRole') !== '管理员') {
			form.value.xuehao = json.xuehao
			disabledForm.value.xuehao = true
		}
		if (json.hasOwnProperty('xueshengxingming') && context?.$toolUtil.storageGet('frontRole') !== '管理员') {
			form.value.xueshengxingming = json.xueshengxingming
			disabledForm.value.xueshengxingming = true
		}
	})
	shenhezhuangtaiLists.value = '未审核,通过,驳回'.split(',')
}

const backClick = () => {
	history.back()
}

const resolveSubmitUrl = () => {
	if (isCrossType.value && isStudentSession.value && !form.value.id) {
		return `${tableName}/add`
	}
	return `${tableName}/${!form.value.id ? 'save' : 'update'}`
}

const submitForm = () => {
	submitLoading.value = true
	const payload = isCrossType.value && isStudentSession.value
		? { timubianhao: form.value.timubianhao, shenqingyuanyin: form.value.shenqingyuanyin }
		: form.value

	context?.$http({
		url: resolveSubmitUrl(),
		method: 'post',
		data: payload,
	}).then(() => {
		context?.$toolUtil.message(isCrossType.value ? '选题申请已提交，请等待教师审核' : '操作成功', 'success', () => {
			history.back()
		})
	}).catch(() => {
		// 业务错误由 http 拦截器统一提示，此处避免 Uncaught (in promise)
	}).finally(() => {
		submitLoading.value = false
	})
}

const save = () => {
	const objcross = crossRow.value ? JSON.parse(JSON.stringify(crossRow.value)) : {}
	let crossUserId = ''
	let crossRefId = ''
	let crossOptNum = ''

	if (type.value === 'cross' && crossColumnName.value !== '') {
		if (!crossColumnName.value.startsWith('[')) {
			for (const o in objcross) {
				if (o === crossColumnName.value) {
					objcross[o] = crossColumnValue.value
				}
			}
			changeCrossData(objcross)
		} else {
			crossUserId = context?.$toolUtil.storageGet('userid')
			crossRefId = objcross.id
			crossOptNum = crossColumnName.value.replace(/\[/, '').replace(/\]/, '')
		}
	}

	formRef.value.validate(valid => {
		if (!valid) return
		if (crossUserId && crossRefId) {
			form.value.crossuserid = crossUserId
			form.value.crossrefid = crossRefId
			const params = {
				page: 1,
				limit: 1000,
				crossuserid: form.value.crossuserid,
				crossrefid: form.value.crossrefid,
			}
			context?.$http({
				url: `${tableName}/page`,
				method: 'get',
				params,
			}).then(res => {
				// 驳回/无效申请不应阻止再次提交（仅统计仍占用名额的有效申请）
				const activeCount = (res.data.data.list || []).filter(isActiveApplicationRecord).length
				if (activeCount >= Number(crossOptNum)) {
					context?.$toolUtil.message(crossTips.value || '您对该题目已有进行中的申请', 'error')
					return
				}
				submitForm()
			})
			return
		}
		submitForm()
	})
}

const changeCrossData = row => {
	context?.$http({
		url: `${crossTable.value}/update`,
		method: 'post',
		data: row,
	}).then(() => {})
}

onMounted(() => {
	type.value = route.query.type ? route.query.type : 'add'
	let row = null
	let table = null
	let statusColumnName = null
	let tips = null
	let statusColumnValue = null
	if (type.value === 'cross') {
		row = context?.$toolUtil.storageGet('crossObj') ? JSON.parse(context?.$toolUtil.storageGet('crossObj')) : {}
		table = context?.$toolUtil.storageGet('crossTable')
		statusColumnName = context?.$toolUtil.storageGet('crossStatusColumnName')
		tips = context?.$toolUtil.storageGet('crossTips')
		statusColumnValue = context?.$toolUtil.storageGet('crossStatusColumnValue')
		const tid = route.query.id
		if (tid && (!row || Object.keys(row).length === 0)) {
			const srcTable = table || 'timuxinxi'
			context?.$http({
				url: `${srcTable}/info/${tid}`,
				method: 'get',
			}).then(res => {
				const loaded = res?.data?.data
				init(tid, type.value, '', loaded || {}, table, statusColumnName, tips, statusColumnValue)
			}).catch(() => {
				init(tid, type.value, '', row, table, statusColumnName, tips, statusColumnValue)
			})
			return
		}
	}
	init(route.query.id ? route.query.id : null, type.value, '', row, table, statusColumnName, tips, statusColumnValue)
})
</script>

<style lang="scss" scoped>
.apply_page {
	min-height: 100%;
	padding: 20px 24px 48px;
	background: transparent;
}

.page_header {
	margin-bottom: 20px;
}

.page_header_top {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 16px;
	margin-bottom: 12px;
}

.page_breadcrumb {
	:deep(.el-breadcrumb__inner) {
		color: rgba(255, 255, 255, 0.55);
		font-weight: 400;
		&.is-link:hover { color: var(--tech-cyan-light); }
	}
	:deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
		color: var(--tech-cyan-light);
	}
	:deep(.el-breadcrumb__separator) { color: rgba(255, 255, 255, 0.3); }
}

.back_btn {
	border: 1px solid var(--tech-border-soft);
	border-radius: 10px;
	color: var(--tech-cyan);
	background: rgba(34, 211, 238, 0.06);
	&:hover {
		border-color: var(--tech-cyan);
		background: rgba(34, 211, 238, 0.12);
		color: var(--tech-cyan-light);
	}
}

.page_title {
	margin: 0;
	font-size: 24px;
	font-weight: 600;
	color: #e2e8f0;
}

.page_subtitle {
	margin: 8px 0 0;
	font-size: 14px;
	color: rgba(255, 255, 255, 0.5);
}

.tip_alert {
	margin-bottom: 20px;
	background: rgba(34, 211, 238, 0.08) !important;
	border: 1px solid rgba(34, 211, 238, 0.22) !important;
	:deep(.el-alert__title) {
		color: #bae6fd;
		font-size: 13px;
		line-height: 1.6;
	}
	:deep(.el-alert__icon) { color: var(--tech-cyan); }
}

.apply_form {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

.panel_card {
	background: var(--tech-panel);
	border: 1px solid var(--tech-border-soft);
	border-radius: 16px;
	padding: 20px 24px;
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

.field_grid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 4px 20px;
}

.field_full {
	grid-column: 1 / -1;
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

.status_chip {
	display: inline-flex;
	align-items: center;
	padding: 4px 12px;
	border-radius: 20px;
	font-size: 12px;
	font-weight: 600;
	&.status_pending {
		color: #fcd34d;
		background: rgba(245, 158, 11, 0.15);
		border: 1px solid rgba(245, 158, 11, 0.35);
	}
}

.apply_form :deep(.el-form-item__label) {
	color: rgba(255, 255, 255, 0.55);
	font-size: 13px;
}

.apply_form :deep(.el-input__wrapper),
.apply_form :deep(.el-textarea__inner) {
	background: rgba(8, 28, 58, 0.65);
	border: 1px solid rgba(34, 211, 238, 0.14);
	box-shadow: none;
	border-radius: 10px;
}

.apply_form :deep(.el-input__inner),
.apply_form :deep(.el-textarea__inner) {
	color: #e2e8f0;
}

.apply_form :deep(.el-input__inner::placeholder),
.apply_form :deep(.el-textarea__inner::placeholder) {
	color: rgba(255, 255, 255, 0.28);
}

.field_readonly :deep(.el-input__wrapper) {
	background: rgba(15, 35, 68, 0.5);
	border-color: rgba(34, 211, 238, 0.08);
}

.field_readonly :deep(.el-input__inner) {
	color: rgba(255, 255, 255, 0.72);
	cursor: default;
}

.field_textarea :deep(.el-textarea__inner) {
	min-height: 110px;
	padding: 12px 14px;
	resize: vertical;
	&:focus {
		border-color: rgba(34, 211, 238, 0.45);
		box-shadow: 0 0 0 2px rgba(34, 211, 238, 0.1);
	}
}

.field_date,
.field_select {
	width: 100%;
}

.apply_form :deep(.el-input.is-disabled .el-input__wrapper) {
	background: rgba(15, 35, 68, 0.5);
}

.apply_form :deep(.el-input__count) {
	background: transparent;
	color: rgba(255, 255, 255, 0.35);
}

.form_actions {
	display: flex;
	justify-content: center;
	align-items: center;
	gap: 16px;
	padding-top: 8px;
}

.btn_cancel {
	min-width: 120px;
	height: 42px;
	border-radius: 10px;
	border: 1px solid var(--tech-border-soft);
	color: var(--tech-cyan);
	background: rgba(34, 211, 238, 0.06);
	&:hover {
		border-color: var(--tech-cyan);
		background: rgba(34, 211, 238, 0.12);
		color: var(--tech-cyan-light);
	}
}

.btn_submit {
	min-width: 140px;
	height: 42px;
	border: none;
	border-radius: 10px;
	background: linear-gradient(135deg, var(--tech-cyan) 0%, var(--tech-cyan-dark) 100%);
	box-shadow: 0 4px 16px rgba(34, 211, 238, 0.25);
	&:hover {
		background: linear-gradient(135deg, #67e8f9 0%, #22d3ee 100%);
	}
}

@media (max-width: 900px) {
	.apply_page { padding: 16px 16px 40px; }
	.field_grid { grid-template-columns: 1fr; }
	.page_header_top { flex-direction: column; align-items: flex-start; }
}
</style>
