<template>
	<div class="submit_page">
		<div class="page_container">
			<header class="page_hero">
				<div class="page_header_top">
					<el-breadcrumb separator="/" class="page_breadcrumb">
						<el-breadcrumb-item :to="{ path: '/index/home' }">首页</el-breadcrumb-item>
						<el-breadcrumb-item>答辩论文</el-breadcrumb-item>
						<el-breadcrumb-item>{{ isEditMode ? '重新上传' : '提交' }}</el-breadcrumb-item>
					</el-breadcrumb>
					<el-button class="back_btn" @click="backClick">
						<el-icon><ArrowLeft /></el-icon>
						返回
					</el-button>
				</div>
				<div class="hero_body">
					<div class="hero_icon">
						<el-icon><Trophy /></el-icon>
					</div>
					<div class="hero_text">
						<h1 class="page_title">{{ pageTitle }}</h1>
						<p class="page_subtitle">{{ pageSubtitle }}</p>
					</div>
				</div>
			</header>

			<el-alert
				v-if="isStudentSession && isAdd && !existingRecord"
				class="tip_alert"
				type="info"
				:closable="false"
				show-icon
				title="题目与指导教师信息已自动带入。请上传答辩论文文件并填写简介后提交，支持 .doc、.docx、.pdf 格式，单个文件不超过 50MB。"
			/>

			<el-alert
				v-if="existingRecord && !isEditMode"
				class="submitted_alert"
				type="success"
				:closable="false"
				show-icon
			>
				<template #title>
					<div class="submitted_alert_body">
						<div class="submitted_alert_text">
							<span>您已提交答辩论文：</span>
							<strong>{{ existingFileName }}</strong>
							<span class="submitted_alert_time">（{{ existingRecord.tijiaoshijian || existingRecord.addtime || '' }}）</span>
							<el-tag
								class="audit_status_tag"
								:type="auditTagType(existingRecord.shenhezhuangtai)"
								size="small"
							>
								审核：{{ normalizeApplicationStatus(existingRecord.shenhezhuangtai) }}
							</el-tag>
						</div>
						<div class="submitted_alert_actions">
							<el-button size="small" :loading="downloadLoading" @click="downloadExisting">下载</el-button>
							<el-button size="small" type="warning" @click="startReupload">重新上传</el-button>
						</div>
					</div>
				</template>
			</el-alert>

			<el-form
				v-if="!existingRecord || isEditMode"
				ref="formRef"
				:model="form"
				class="submit_form"
				label-width="88px"
				label-position="left"
				:rules="rules"
			>
				<!-- 课题概览 -->
				<section class="panel_card overview_card">
					<div class="panel_title">
						<el-icon><Document /></el-icon>
						<span>课题概览</span>
					</div>
					<h2 class="topic_name">{{ form.ketimingcheng || '课题名称加载中…' }}</h2>
					<div class="info_grid">
						<div class="info_chip">
							<span class="chip_label">题目编号</span>
							<span class="chip_value">{{ form.timubianhao || '—' }}</span>
						</div>
						<div class="info_chip">
							<span class="chip_label">题目类型</span>
							<span class="chip_value">{{ form.timuleixing || '—' }}</span>
						</div>
						<div class="info_chip">
							<span class="chip_label">专业</span>
							<span class="chip_value">{{ form.zhuanye || '—' }}</span>
						</div>
						<div class="info_chip">
							<span class="chip_label">指导教师</span>
							<span class="chip_value">{{ form.jiaoshixingming || '—' }}（{{ form.jiaoshigonghao || '—' }}）</span>
						</div>
						<div class="info_chip">
							<span class="chip_label">学生</span>
							<span class="chip_value">{{ form.xueshengxingming || '—' }}（{{ form.xuehao || '—' }}）</span>
						</div>
						<div class="info_chip">
							<span class="chip_label">提交时间</span>
							<span class="chip_value">{{ form.tijiaoshijian || '提交时自动生成' }}</span>
						</div>
						<div class="info_chip" v-if="form.pingfenzhuangtai">
							<span class="chip_label">评分状态</span>
							<span class="chip_value">{{ form.pingfenzhuangtai }}</span>
						</div>
						<div class="info_chip">
							<span class="chip_label">审核状态</span>
							<span class="chip_value">{{ normalizeApplicationStatus(form.shenhezhuangtai || existingRecord?.shenhezhuangtai) }}</span>
						</div>
					</div>
					<div v-if="form.ketixingzhi" class="topic_nature">
						<el-icon><Reading /></el-icon>
						<span>{{ form.ketixingzhi }}</span>
					</div>
				</section>

				<!-- 上传与简介 -->
				<section class="panel_card upload_panel">
					<div class="panel_title">
						<el-icon><UploadFilled /></el-icon>
						<span>答辩材料</span>
						<span class="panel_badge required">必填</span>
					</div>

					<div class="upload_section">
						<div class="upload_label">
							<el-icon><FolderOpened /></el-icon>
							<span>论文附件</span>
						</div>
						<el-form-item prop="lunwenfujian" class="upload_field_item" label-width="0">
							<uploads
								:disabled="!isAdd || disabledForm.lunwenfujian"
								type="file"
								action="file/upload"
								bizType="dabianlunwen"
								tip="支持 .doc、.docx、.pdf 格式，单个文件不超过 50MB"
								:limit="1"
								class="upload_component"
								:fileUrls="form.lunwenfujian ? form.lunwenfujian : ''"
								@change="lunwenfujianUploadSuccess"
							/>
						</el-form-item>
					</div>

					<div class="intro_section">
						<div class="upload_label">
							<el-icon><EditPen /></el-icon>
							<span>论文简介</span>
							<span class="label_hint">选填，便于答辩教师了解论文要点</span>
						</div>
						<el-form-item prop="lunwenjianjie" label-width="0" class="intro_field_item">
							<el-input
								v-model="form.lunwenjianjie"
								type="textarea"
								:rows="5"
								placeholder="请简要描述答辩论文的主要内容、创新点或需要答辩组关注的要点…"
								:readonly="!isAdd || disabledForm.lunwenjianjie"
								class="field_textarea"
								maxlength="500"
								show-word-limit
							/>
						</el-form-item>
					</div>
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
	</div>
</template>

<script setup>
import { ref, computed, getCurrentInstance, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { downloadStoredFile, normalizeFileName } from '@/utils/fileDownload'
import { isAppliedStatus, normalizeApplicationStatus, auditTagType } from '@/utils/xuantishenqingStatus'
import { handleStudentWorkflowSubmitSuccess } from '@/utils/workflowSubmitSuccess'
import {
	ArrowLeft, Document, Reading, UploadFilled, CircleCheck,
	Trophy, FolderOpened, EditPen
} from '@element-plus/icons-vue'

const context = getCurrentInstance()?.appContext.config.globalProperties
const route = useRoute()
const router = useRouter()
const tableName = 'dabianlunwen'
const formName = '提交答辩论文'

const form = ref({
	timubianhao: '',
	ketimingcheng: '',
	timuleixing: '',
	zhuanye: '',
	ketixingzhi: '',
	jiaoshigonghao: '',
	jiaoshixingming: '',
	xuehao: '',
	xueshengxingming: '',
	lunwenjianjie: '',
	lunwenfujian: '',
	tijiaoshijian: '',
	pingfenzhuangtai: '未评分',
	shenhezhuangtai: '未审核',
})
const formRef = ref(null)
const id = ref(0)
const type = ref('')
const submitLoading = ref(false)
const downloadLoading = ref(false)
const disabledForm = ref({
	timubianhao: false,
	ketimingcheng: false,
	timuleixing: false,
	zhuanye: false,
	ketixingzhi: false,
	jiaoshigonghao: false,
	jiaoshixingming: false,
	xuehao: false,
	xueshengxingming: false,
	lunwenjianjie: false,
	lunwenfujian: false,
	tijiaoshijian: false,
	pingfenzhuangtai: false,
})
const isAdd = ref(false)
const rules = ref({
	lunwenfujian: [
		{ required: true, message: '请上传答辩论文附件', trigger: 'change' },
	],
})
const crossRow = ref('')
const crossTable = ref('')
const crossTips = ref('')
const crossColumnName = ref('')
const crossColumnValue = ref('')
const existingRecord = ref(null)

const existingFileName = computed(() => {
	const path = existingRecord.value?.lunwenfujian || ''
	return path ? normalizeFileName(path) : '答辩论文'
})

const isStudentSession = computed(() => context?.$toolUtil.storageGet('frontSessionTable') === 'xuesheng')
const isEditMode = computed(() => type.value === 'edit')
const pageTitle = computed(() => (isEditMode.value ? '重新上传答辩论文' : formName))
const pageSubtitle = computed(() =>
	isEditMode.value
		? '更新答辩论文文件后重新提交，等待指导教师审阅'
		: '确认题目信息后上传答辩论文并填写简介后提交'
)
const submitButtonText = computed(() => (isEditMode.value ? '重新提交' : '提交答辩论文'))

const lunwenfujianUploadSuccess = e => {
	form.value.lunwenfujian = e
	formRef.value?.validateField('lunwenfujian')
}

const loadExistingRecord = () => {
	if (!isStudentSession.value || isEditMode.value) {
		existingRecord.value = null
		return Promise.resolve()
	}
	return context?.$http({
		url: `${tableName}/page`,
		method: 'get',
		params: { page: 1, limit: 1, sort: 'id', order: 'desc' },
	}).then(res => {
		const list = res.data?.data?.list || []
		existingRecord.value = list[0] || null
	}).catch(() => {
		existingRecord.value = null
	})
}

const downloadExisting = async () => {
	const filePath = existingRecord.value?.lunwenfujian
	if (!filePath) {
		context?.$toolUtil.message('文件不存在', 'error')
		return
	}
	if (downloadLoading.value) return
	downloadLoading.value = true
	try {
		await downloadStoredFile(filePath)
	} catch (err) {
		context?.$toolUtil.message(err?.message || '下载失败', 'error')
	} finally {
		downloadLoading.value = false
	}
}

const startReupload = () => {
	const row = existingRecord.value
	if (!row?.id) return
	const recordId = String(row.id)
	id.value = recordId
	type.value = 'edit'
	isAdd.value = true
	getInfo()
	router.replace({ path: '/index/dabianlunwenAdd', query: { type: 'edit', id: recordId } })
}

const getInfo = () => {
	context?.$http({
		url: `${tableName}/info/${id.value}`,
		method: 'get',
	}).then(res => {
		form.value = res.data.data
	})
}

const init = (formId = null, formType = 'add', formNames = '', row = null, table = null, statusColumnName = null, tips = null, statusColumnValue = null) => {
	form.value.tijiaoshijian = context?.$toolUtil.getCurDateTime()
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
		form.value.pingfenzhuangtai = '未评分'
	}
	if (formType === 'add') {
		form.value.pingfenzhuangtai = '未评分'
	}
	context?.$http({
		url: `${context?.$toolUtil.storageGet('frontSessionTable')}/session`,
		method: 'get',
	}).then(res => {
		const json = res.data.data || {}
		if (json.hasOwnProperty('xuehao') && context?.$toolUtil.storageGet('frontRole') !== '管理员') {
			form.value.xuehao = json.xuehao
			disabledForm.value.xuehao = true
		}
		if (json.hasOwnProperty('xueshengxingming') && context?.$toolUtil.storageGet('frontRole') !== '管理员') {
			form.value.xueshengxingming = json.xueshengxingming
			disabledForm.value.xueshengxingming = true
		}
		if (isStudentSession.value && !id.value && formType !== 'cross') {
			context?.$http({
				url: 'xuantishenqing/page',
				method: 'get',
				params: {
					page: 1,
					limit: 20,
					sort: 'shenqingshijian',
					order: 'desc',
				},
			}).then(r => {
				const data = r.data?.data
				const rows = (data && data.list) ? data.list : []
				if (!rows.length) return
				const approved = rows.filter(it => isAppliedStatus(it.shenhezhuangtai))
				const rowItem = approved.length ? approved[0] : rows[0]
				if (!rowItem) return
				form.value.timubianhao = rowItem.timubianhao || ''
				form.value.ketimingcheng = rowItem.ketimingcheng || ''
				form.value.timuleixing = rowItem.timuleixing || ''
				form.value.zhuanye = rowItem.zhuanye || ''
				form.value.ketixingzhi = rowItem.ketixingzhi || ''
				form.value.jiaoshigonghao = rowItem.jiaoshigonghao || ''
				form.value.jiaoshixingming = rowItem.jiaoshixingming || ''
				form.value.xuehao = rowItem.xuehao || form.value.xuehao
				form.value.xueshengxingming = rowItem.xueshengxingming || form.value.xueshengxingming
				disabledForm.value.timubianhao = true
				disabledForm.value.ketimingcheng = true
				disabledForm.value.timuleixing = true
				disabledForm.value.zhuanye = true
				disabledForm.value.ketixingzhi = true
				disabledForm.value.jiaoshigonghao = true
				disabledForm.value.jiaoshixingming = true
				disabledForm.value.xuehao = true
				disabledForm.value.xueshengxingming = true
			})
		}
	})
}

const backClick = () => {
	context?.$router.push('/index/home')
}

const changeCrossData = row => {
	context?.$http({
		url: `${crossTable.value}/update`,
		method: 'post',
		data: row,
	}).then(() => {})
}

const save = () => {
	if (form.value.lunwenfujian != null) {
		form.value.lunwenfujian = form.value.lunwenfujian.replace(new RegExp(context?.$config.url, 'g'), '')
	}
	if (!form.value.pingfenzhuangtai) {
		form.value.pingfenzhuangtai = '未评分'
	}
	const table = crossTable.value
	const objcross = JSON.parse(JSON.stringify(crossRow.value))
	let crossUserId = ''
	let crossRefId = ''
	let crossOptNum = ''
	if (type.value === 'cross') {
		if (crossColumnName.value !== '') {
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
	}
	formRef.value.validate(valid => {
		if (!valid) return
		submitLoading.value = true
		const doSubmit = () => {
			const submitPath = form.value.id
				? 'update'
				: (isStudentSession.value ? 'add' : 'save')
			context?.$http({
				url: `${tableName}/${submitPath}`,
				method: 'post',
				data: form.value,
			}).then(async () => {
				const msg = isEditMode.value ? '答辩论文已重新提交' : '答辩论文提交成功'
				if (isStudentSession.value) {
					await handleStudentWorkflowSubmitSuccess({
						router,
						routePath: '/index/dabianlunwenAdd',
						typeRef: type,
						idRef: id,
						isAddRef: isAdd,
						loadExistingRecord,
						messageUtil: context?.$toolUtil,
						successMessage: msg,
					})
				} else {
					context?.$toolUtil.message(msg, 'success', () => {
						context?.$router.push('/index/dabianlunwenAdd')
					})
				}
			}).finally(() => {
				submitLoading.value = false
			})
		}
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
				if (res.data.data.total >= crossOptNum) {
					context?.$toolUtil.message(`${crossTips.value}`, 'error')
					submitLoading.value = false
					return
				}
				doSubmit()
			}).catch(() => {
				submitLoading.value = false
			})
		} else {
			doSubmit()
		}
	})
}

onMounted(async () => {
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
	}
	let formId = route.query.id ? route.query.id : null
	if (type.value === 'edit' && !formId && isStudentSession.value) {
		await loadExistingRecord()
		formId = existingRecord.value?.id || null
	}
	init(formId, type.value, '', row, table, statusColumnName, tips, statusColumnValue)
	if (type.value === 'add') {
		loadExistingRecord()
	}
})
</script>

<style lang="scss" scoped>
.submit_page {
	min-height: 100%;
	padding: 24px 20px 48px;
	background:
		radial-gradient(ellipse 80% 50% at 50% -20%, rgba(34, 211, 238, 0.12), transparent),
		transparent;
}

.page_container {
	max-width: 920px;
	margin: 0 auto;
}

.page_hero {
	margin-bottom: 24px;
	padding: 20px 24px;
	border-radius: 16px;
	background: linear-gradient(135deg, rgba(8, 28, 58, 0.95) 0%, rgba(6, 22, 48, 0.98) 100%);
	border: 1px solid var(--tech-border-soft);
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.25), inset 0 1px 0 rgba(34, 211, 238, 0.08);
}

.page_header_top {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 16px;
	margin-bottom: 16px;
}

.hero_body {
	display: flex;
	align-items: center;
	gap: 18px;
}

.hero_icon {
	flex-shrink: 0;
	width: 56px;
	height: 56px;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 14px;
	background: linear-gradient(135deg, rgba(34, 211, 238, 0.2) 0%, rgba(8, 145, 178, 0.15) 100%);
	border: 1px solid rgba(34, 211, 238, 0.35);
	box-shadow: 0 0 24px rgba(34, 211, 238, 0.15);

	.el-icon {
		font-size: 28px;
		color: var(--tech-cyan-light);
	}
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
	font-size: 22px;
	font-weight: 600;
	color: #f1f5f9;
	letter-spacing: 0.3px;
}

.page_subtitle {
	margin: 6px 0 0;
	font-size: 13px;
	line-height: 1.6;
	color: rgba(255, 255, 255, 0.48);
}

.tip_alert {
	margin-bottom: 20px;
	background: rgba(34, 211, 238, 0.08) !important;
	border: 1px solid rgba(34, 211, 238, 0.22) !important;
	:deep(.el-alert__title) {
		color: #bae6fd;
		font-size: 13px;
		line-height: 1.65;
	}
	:deep(.el-alert__icon) { color: var(--tech-cyan); }
}

.submitted_alert {
	margin-bottom: 20px;
	background: rgba(76, 175, 80, 0.1) !important;
	border: 1px solid rgba(76, 175, 80, 0.28) !important;
	:deep(.el-alert__title) {
		width: 100%;
		color: #d1fae5;
	}
	:deep(.el-alert__icon) { color: #4caf50; }
}

.submitted_alert_body {
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
}

.submitted_alert_text {
	font-size: 14px;
	line-height: 1.6;
}

.submitted_alert_time {
	margin-left: 6px;
	color: rgba(255, 255, 255, 0.55);
	font-weight: 400;
}

.audit_status_tag {
	margin-left: 10px;
	vertical-align: middle;
}

.submitted_alert_actions {
	display: flex;
	flex-wrap: wrap;
	gap: 8px;
}

.submit_form {
	display: flex;
	flex-direction: column;
	gap: 18px;
}

.panel_card {
	background: var(--tech-panel);
	border: 1px solid var(--tech-border-soft);
	border-radius: 16px;
	padding: 22px 24px;
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
	transition: border-color 0.25s;

	&:hover {
		border-color: rgba(34, 211, 238, 0.28);
	}
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

.panel_badge {
	margin-left: auto;
	padding: 2px 10px;
	border-radius: 20px;
	font-size: 11px;
	font-weight: 600;
	&.required {
		color: #fca5a5;
		background: rgba(239, 68, 68, 0.12);
		border: 1px solid rgba(239, 68, 68, 0.3);
	}
}

.overview_card {
	.topic_name {
		margin: 0 0 18px;
		font-size: 17px;
		font-weight: 600;
		line-height: 1.55;
		color: #f1f5f9;
	}
}

.info_grid {
	display: grid;
	grid-template-columns: repeat(2, minmax(0, 1fr));
	gap: 12px;
}

.info_chip {
	padding: 12px 14px;
	border-radius: 10px;
	background: rgba(8, 28, 58, 0.55);
	border: 1px solid rgba(34, 211, 238, 0.1);
	transition: border-color 0.2s;

	&:hover {
		border-color: rgba(34, 211, 238, 0.22);
	}

	.chip_label {
		display: block;
		margin-bottom: 4px;
		font-size: 11px;
		font-weight: 500;
		color: rgba(255, 255, 255, 0.42);
		text-transform: uppercase;
		letter-spacing: 0.5px;
	}

	.chip_value {
		display: block;
		font-size: 14px;
		line-height: 1.45;
		color: rgba(255, 255, 255, 0.88);
		word-break: break-all;
	}
}

.topic_nature {
	display: flex;
	align-items: flex-start;
	gap: 8px;
	margin-top: 16px;
	padding: 14px 16px;
	border-radius: 10px;
	font-size: 13px;
	line-height: 1.65;
	color: rgba(255, 255, 255, 0.75);
	background: rgba(34, 211, 238, 0.06);
	border: 1px solid rgba(34, 211, 238, 0.12);

	.el-icon {
		flex-shrink: 0;
		margin-top: 2px;
		color: var(--tech-cyan);
	}
}

.upload_section,
.intro_section {
	margin-bottom: 8px;
}

.intro_section {
	margin-top: 20px;
	padding-top: 20px;
	border-top: 1px dashed rgba(34, 211, 238, 0.15);
}

.upload_label {
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 8px;
	margin-bottom: 12px;
	font-size: 14px;
	font-weight: 500;
	color: rgba(255, 255, 255, 0.75);

	.el-icon {
		font-size: 16px;
		color: var(--tech-cyan);
	}

	.label_hint {
		font-size: 12px;
		font-weight: 400;
		color: rgba(255, 255, 255, 0.38);
	}
}

.upload_field_item,
.intro_field_item {
	margin-bottom: 0;

	:deep(.el-form-item__content) {
		margin-left: 0 !important;
	}
}

.field_textarea :deep(.el-textarea__inner) {
	min-height: 120px;
	line-height: 1.75;
	resize: vertical;
	background: rgba(8, 28, 58, 0.65);
	border: 1px solid rgba(34, 211, 238, 0.14);
	border-radius: 10px;
	color: #e2e8f0;
	box-shadow: none;

	&::placeholder {
		color: rgba(255, 255, 255, 0.28);
	}

	&:focus {
		border-color: rgba(34, 211, 238, 0.45);
	}
}

.intro_field_item :deep(.el-input__count) {
	background: transparent;
	color: rgba(255, 255, 255, 0.35);
}

.upload_component {
	width: 100%;

	:deep(.upload-demo) {
		width: 100%;
	}

	:deep(.el-upload) {
		width: 100%;
	}

	:deep(.el-upload-dragger) {
		width: 100%;
		padding: 36px 24px;
		border-radius: 12px;
		border: 2px dashed rgba(34, 211, 238, 0.28);
		background: rgba(8, 28, 58, 0.45);
		transition: all 0.25s;

		&:hover {
			border-color: var(--tech-cyan);
			background: rgba(34, 211, 238, 0.08);
			box-shadow: 0 0 24px rgba(34, 211, 238, 0.08);
		}
	}

	:deep(.el-icon--upload) {
		font-size: 44px;
		color: var(--tech-cyan);
		margin-bottom: 10px;
	}

	:deep(.el-upload__text) {
		color: rgba(255, 255, 255, 0.65);
		font-size: 14px;
		em {
			color: var(--tech-cyan-light);
			font-style: normal;
			font-weight: 500;
		}
	}

	:deep(.el-upload__tip) {
		margin-top: 10px;
		color: rgba(255, 255, 255, 0.38);
		font-size: 12px;
	}

	:deep(.el-upload-list) {
		margin-top: 14px;
	}

	:deep(.el-upload-list__item) {
		border-radius: 8px;
		background: rgba(8, 28, 58, 0.55);
		border: 1px solid rgba(34, 211, 238, 0.12);
		color: rgba(255, 255, 255, 0.75);
		transition: border-color 0.2s;

		&:hover {
			border-color: rgba(34, 211, 238, 0.35);
		}
	}

	:deep(.el-upload-list__item-name) {
		color: rgba(255, 255, 255, 0.75);
	}
}

.form_actions {
	display: flex;
	justify-content: center;
	align-items: center;
	gap: 16px;
	padding: 20px 0 8px;
	margin-top: 4px;
}

.btn_cancel {
	min-width: 120px;
	height: 44px;
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
	min-width: 168px;
	height: 44px;
	border: none;
	border-radius: 10px;
	font-weight: 500;
	background: linear-gradient(135deg, var(--tech-cyan) 0%, var(--tech-cyan-dark) 100%);
	box-shadow: 0 4px 20px rgba(34, 211, 238, 0.3);
	&:hover {
		background: linear-gradient(135deg, #67e8f9 0%, #22d3ee 100%);
		box-shadow: 0 6px 24px rgba(34, 211, 238, 0.4);
	}
}

@media (max-width: 768px) {
	.submit_page { padding: 16px 12px 40px; }
	.page_hero { padding: 16px; }
	.hero_body { flex-direction: column; align-items: flex-start; gap: 12px; }
	.page_header_top { flex-direction: column; align-items: flex-start; }
	.info_grid { grid-template-columns: 1fr; }
	.panel_card { padding: 18px 16px; }
}
</style>
