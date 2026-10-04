<template>
	<div>
		<el-dialog v-model="importVisible" :title="'批量导入'" width="80%" destroy-on-close>
			<el-tabs v-model="mode">
				<el-tab-pane label="Excel 导入" name="excel">
					<el-form ref="ruleFormRef" :model="importForm" label-width="120px">
						<el-row>
							<el-col :span="24">
								<el-upload class="upload-demo" drag accept=".xls,.xlsx" :action="uploadUrl" :headers="uploadHeaders"
									multiple :on-success="uploadSuccess" :on-error="uploadError">
									<el-icon class="el-icon--upload">
										<upload-filled />
									</el-icon>
									<div class="el-upload__text">
										将文件拖到此处，或<em>点击上传</em>
									</div>
									<template #tip>
									  <div class="el-upload__tip">
									    {{tips}}
									  </div>
									</template>
								</el-upload>
							</el-col>
						</el-row>
					</el-form>
				</el-tab-pane>
				<el-tab-pane v-if="supportsManualImport" label="手工批量录入" name="manual">
					<div class="manual-tip">
						<template v-if="tableName==='timuxinxi'">
							<template v-if="isTeacherSession">
								教师录入仅需填写课题名称、题目类型、课题性质、题目范围；题目编号、专业及教师信息将按当前账号自动关联。
							</template>
							<template v-else>
								可在下方一次录入多条题目信息，点击“添加一行”增加题目，提交后系统将逐条保存；题目编号可留空（系统自动生成）。
							</template>
						</template>
						<template v-else-if="tableName==='xuesheng'">
							可在下方一次录入多条学生信息，点击“添加一行”增加学生，提交后系统将逐条保存；密码为空时默认取联系电话后六位。
						</template>
						<template v-else-if="tableName==='jiaoshi'">
							可在下方一次录入多条教师信息，点击“添加一行”增加教师，提交后系统将逐条保存；密码为空时默认取联系电话后六位。
						</template>
						<template v-else-if="tableName==='timuleixing'">
							可在下方一次录入多条题目类型，点击“添加一行”增加类型，提交后系统将逐条保存。
						</template>
					</div>
					<el-table v-if="tableName==='timuxinxi'" :data="manualList" border class="manual-table">
						<el-table-column type="index" label="#" width="50" />
						<el-table-column v-if="!isTeacherSession" label="题目编号" width="140">
							<template #default="scope">
								<el-input v-model="scope.row.timubianhao" placeholder="可留空自动生成" />
							</template>
						</el-table-column>
						<el-table-column label="课题名称" min-width="180">
							<template #default="scope">
								<el-input v-model="scope.row.ketimingcheng" placeholder="课题名称" />
							</template>
						</el-table-column>
						<el-table-column label="题目类型" width="120">
							<template #default="scope">
								<el-input v-model="scope.row.timuleixing" placeholder="题目类型" />
							</template>
						</el-table-column>
						<el-table-column v-if="!isTeacherSession" label="专业" width="120">
							<template #default="scope">
								<el-input
									v-model="scope.row.zhuanye"
									placeholder="专业"
								/>
							</template>
						</el-table-column>
						<el-table-column label="课题性质" min-width="150">
							<template #default="scope">
								<el-input v-model="scope.row.ketixingzhi" placeholder="课题性质" />
							</template>
						</el-table-column>
						<el-table-column label="题目范围" min-width="200">
							<template #default="scope">
								<el-input v-model="scope.row.timufanwei" type="textarea" placeholder="题目范围" />
							</template>
						</el-table-column>
						<el-table-column label="操作" width="90" align="center">
							<template #default="scope">
								<el-button type="danger" link size="small" @click="removeRow(scope.$index)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
					<el-table v-else-if="tableName==='xuesheng'" :data="manualList" border class="manual-table">
						<el-table-column type="index" label="#" width="50" />
						<el-table-column label="学号" width="160">
							<template #default="scope">
								<el-input v-model="scope.row.xuehao" placeholder="学号" />
							</template>
						</el-table-column>
						<el-table-column label="密码" width="120">
							<template #default="scope">
								<el-input v-model="scope.row.mima" placeholder="为空取电话后六位" maxlength="6" show-password />
							</template>
						</el-table-column>
						<el-table-column label="学生姓名" width="120">
							<template #default="scope">
								<el-input v-model="scope.row.xueshengxingming" placeholder="学生姓名" />
							</template>
						</el-table-column>
						<el-table-column label="联系电话" width="140">
							<template #default="scope">
								<el-input v-model="scope.row.shoujihaoma" placeholder="联系电话" />
							</template>
						</el-table-column>
						<el-table-column label="性别" width="100">
							<template #default="scope">
								<el-select v-model="scope.row.xingbie" placeholder="性别" clearable>
									<el-option label="男" value="男" />
									<el-option label="女" value="女" />
								</el-select>
							</template>
						</el-table-column>
						<el-table-column label="专业" min-width="140">
							<template #default="scope">
								<el-select
									v-model="scope.row.zhuanye"
									:placeholder="isMajorLockedSession ? '自动带入管理员专业' : '请选择专业'"
									:disabled="isMajorLockedSession"
									clearable
								>
									<el-option v-for="item in zhuanyeOptions" :key="item" :label="item" :value="item" />
								</el-select>
							</template>
						</el-table-column>
						<el-table-column label="年级" width="120">
							<template #default="scope">
								<el-select v-model="scope.row.nianji" placeholder="年级" clearable>
									<el-option v-for="item in nianjiOptions" :key="item.value" :label="item.label" :value="item.value" />
								</el-select>
							</template>
						</el-table-column>
						<el-table-column label="班级" width="100">
							<template #default="scope">
								<el-input v-model="scope.row.banji" placeholder="班级" />
							</template>
						</el-table-column>
						<el-table-column label="操作" width="90" align="center">
							<template #default="scope">
								<el-button type="danger" link size="small" @click="removeRow(scope.$index)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
					<el-table v-else-if="tableName==='jiaoshi'" :data="manualList" border class="manual-table">
						<el-table-column type="index" label="#" width="50" />
						<el-table-column label="教师工号" width="140">
							<template #default="scope">
								<el-input v-model="scope.row.jiaoshigonghao" placeholder="教师工号" />
							</template>
						</el-table-column>
						<el-table-column label="密码" width="120">
							<template #default="scope">
								<el-input v-model="scope.row.mima" placeholder="为空取电话后六位" show-password />
							</template>
						</el-table-column>
						<el-table-column label="教师姓名" width="120">
							<template #default="scope">
								<el-input v-model="scope.row.jiaoshixingming" placeholder="教师姓名" />
							</template>
						</el-table-column>
						<el-table-column label="联系电话" width="140">
							<template #default="scope">
								<el-input v-model="scope.row.lianxidianhua" placeholder="联系电话" />
							</template>
						</el-table-column>
						<el-table-column label="性别" width="100">
							<template #default="scope">
								<el-select v-model="scope.row.xingbie" placeholder="性别" clearable>
									<el-option label="男" value="男" />
									<el-option label="女" value="女" />
								</el-select>
							</template>
						</el-table-column>
						<el-table-column label="专业" min-width="140">
							<template #default="scope">
								<el-select
									v-model="scope.row.zhuanye"
									:placeholder="isMajorLockedSession ? '自动带入管理员专业' : '请选择专业'"
									:disabled="isMajorLockedSession"
									clearable
								>
									<el-option v-for="item in zhuanyeOptions" :key="item" :label="item" :value="item" />
								</el-select>
							</template>
						</el-table-column>
						<el-table-column label="操作" width="90" align="center">
							<template #default="scope">
								<el-button type="danger" link size="small" @click="removeRow(scope.$index)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
					<el-table v-else-if="tableName==='timuleixing'" :data="manualList" border class="manual-table">
						<el-table-column type="index" label="#" width="50" />
						<el-table-column label="题目类型" min-width="220">
							<template #default="scope">
								<el-input v-model="scope.row.timuleixing" placeholder="题目类型" />
							</template>
						</el-table-column>
						<el-table-column label="操作" width="90" align="center">
							<template #default="scope">
								<el-button type="danger" link size="small" @click="removeRow(scope.$index)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
					<div v-if="supportsManualImport" class="manual-actions">
						<el-button type="primary" @click="addRow">添加一行</el-button>
						<el-button type="success" @click="submitManual">提交保存</el-button>
					</div>
				</el-tab-pane>
			</el-tabs>
			<template #footer>
				<span class="formModel_btn_box">
					<el-button class="formModel_cancel" @click="importVisible=false">关闭</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script setup>
	import {
		ref,
		toRefs,
		computed,
		getCurrentInstance,
		defineEmits
	} from 'vue';
	import { ElMessageBox } from 'element-plus'
	const emit = defineEmits(['importChange'])
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	
	//props

	const props = defineProps({
		tableName: String,
		action: String,
		tip:String
	})
	const {
		tableName,
		action,
		tip
	} = toRefs(props)
	//props
	//data
	const importVisible = ref(false)
	const importForm = ref({})
	const uploadHeaders = ref({})
	const uploadUrl = ref('')
	const tips = ref('')
	// 导入方式：excel 或 manual
	const mode = ref('excel')
	const supportsManualImport = computed(() => ['timuxinxi', 'xuesheng', 'jiaoshi'].includes(tableName.value))
	// 手工批量录入的数据列表
	const manualList = ref([])
	const currentSessionMajor = ref('')
	const isTeacherSession = ref(false)
	const isMajorLockedSession = ref(false)
	const zhuanyeOptions = Object.freeze(['地理信息科学', '地理科学', '风景园林', '测绘工程', '城乡规划'])
	const nianjiOptions = Object.freeze(
		Array.from({ length: 10 }, (_, i) => {
			const year = String(2018 + i)
			return { value: year, label: `${year}级` }
		})
	)
	const createTimuxinxiRow = () => ({
		timubianhao: '',
		ketimingcheng: '',
		timuleixing: '',
		zhuanye: currentSessionMajor.value || '',
		ketixingzhi: '',
		timufanwei: ''
	})
	const createXueshengRow = () => ({
		xuehao: '',
		mima: '',
		xueshengxingming: '',
		shoujihaoma: '',
		xingbie: '',
		zhuanye: currentSessionMajor.value || '',
		nianji: '',
		banji: ''
	})
	const createJiaoshiRow = () => ({
		jiaoshigonghao: '',
		mima: '',
		jiaoshixingming: '',
		lianxidianhua: '',
		xingbie: '',
		zhuanye: currentSessionMajor.value || ''
	})
	const createTimuleixingRow = () => ({
		timuleixing: ''
	})
	const createManualRow = () => {
		if (tableName.value === 'xuesheng') {
			return createXueshengRow()
		}
		if (tableName.value === 'jiaoshi') {
			return createJiaoshiRow()
		}
		if (tableName.value === 'timuleixing') {
			return createTimuleixingRow()
		}
		return createTimuxinxiRow()
	}
	const resolvePassword = (password, phone) => {
		const pwd = password?.trim()
		if (pwd) return pwd
		const mobileDigits = String(phone || '').replace(/\D/g, '')
		if (mobileDigits.length < 6) return ''
		return mobileDigits.slice(-6)
	}
	const loadSessionMajor = () => {
		const sessionTable = context?.$toolUtil.storageGet('sessionTable')
		isTeacherSession.value = sessionTable === 'jiaoshi'
		isMajorLockedSession.value = false
		currentSessionMajor.value = ''
		if (!sessionTable) {
			return Promise.resolve()
		}
		return context?.$http({
			url: `${sessionTable}/session`,
			method: 'get'
		}).then((res) => {
			const major = res?.data?.data?.zhuanye
			currentSessionMajor.value = major ? String(major).trim() : ''
			if (tableName.value === 'timuxinxi') {
				isMajorLockedSession.value = isTeacherSession.value
			} else if (tableName.value === 'xuesheng' || tableName.value === 'jiaoshi') {
				isMajorLockedSession.value = sessionTable === 'users' && !!currentSessionMajor.value
			}
		}).catch(() => {
			currentSessionMajor.value = ''
			isMajorLockedSession.value = false
		})
	}
	const importClick=()=>{
		init()
		importVisible.value = true
	}
	//声明父级调用
	defineExpose({
		importClick
	})
	const extractErrorMessage = (payload) => {
		if (!payload) return '导入失败'
		if (typeof payload === 'string') return payload
		if (payload.msg) return payload.msg
		if (payload.message) return payload.message
		try {
			if (payload.response && payload.response.data) {
				if (typeof payload.response.data === 'string') return payload.response.data
				if (payload.response.data.msg) return payload.response.data.msg
			}
			if (payload.target && payload.target.responseText) {
				const text = payload.target.responseText
				try {
					const json = JSON.parse(text)
					return json.msg || text
				} catch (e) {
					return text
				}
			}
		} catch (e) {}
		return '导入失败'
	}
	const uploadSuccess=(response)=>{
		if (response && Number(response.code) === 0) {
			const msg = response.msg || '导入成功'
			const isDuplicateOnly = /未导入新学生|已跳过已存在学号|未导入新题目|已跳过非本专业/.test(msg)
			const finishImport = () => {
				emit('importChange')
				importVisible.value = false
			}
			if (msg.length > 100 || isDuplicateOnly) {
				ElMessageBox.alert(msg.replace(/；/g, '；\n'), isDuplicateOnly ? '导入提示' : '导入完成', {
					confirmButtonText: '确定',
					type: isDuplicateOnly ? 'warning' : 'success',
					customClass: 'import-result-message-box'
				}).then(finishImport).catch(finishImport)
			} else {
				context?.$toolUtil.message(msg, isDuplicateOnly ? 'warning' : 'success', finishImport)
			}
			return
		}
		const msg = extractErrorMessage(response)
		context?.$toolUtil.message(msg,'error')
	}
	const uploadError=(e)=>{
		const msg = extractErrorMessage(e)
		context?.$toolUtil.message(msg,'error')
	}
	const init = () => {
		mode.value = 'excel'
		uploadUrl.value = context?.$config.name + '/' + `${tableName.value}/importExcel`
		uploadHeaders.value = {
			'Token': context?.$toolUtil.storageGet("Token")
		}
		if (tip.value) {
			tips.value = tip.value
		}
		// 初始化手工录入列表，至少一行
		loadSessionMajor().finally(() => {
			if (supportsManualImport.value) {
				manualList.value = [createManualRow()]
			} else {
				manualList.value = []
			}
		})
	}
	// 手工录入：添加一行
	const addRow = () => {
		manualList.value.push(createManualRow())
	}
	// 手工录入：删除一行
	const removeRow = (index) => {
		if (manualList.value.length <= 1) {
			manualList.value.splice(0, 1)
			manualList.value.push(createManualRow())
		} else {
			manualList.value.splice(index, 1)
		}
	}
	// 手工录入：提交保存
	const submitManual = () => {
		if (tableName.value === 'timuxinxi') {
			submitTimuxinxiManual()
			return
		}
		if (tableName.value === 'xuesheng') {
			submitXueshengManual()
			return
		}
		if (tableName.value === 'jiaoshi') {
			submitJiaoshiManual()
			return
		}
		if (tableName.value === 'timuleixing') {
			submitTimuleixingManual()
		}
	}
	const submitTimuxinxiManual = async () => {
		const rows = manualList.value.filter(r =>
			r.ketimingcheng?.trim() ||
			r.timuleixing?.trim() ||
			r.ketixingzhi?.trim() ||
			r.timufanwei?.trim() ||
			(!isTeacherSession.value && r.timubianhao?.trim())
		)
		if (!rows.length) {
			context?.$toolUtil.message('请至少填写一条完整的题目信息', 'error')
			return
		}
		if (isTeacherSession.value && !currentSessionMajor.value) {
			context?.$toolUtil.message('当前教师专业未配置，请先在个人信息中设置专业', 'error')
			return
		}
		if (isTeacherSession.value) {
			rows.forEach((r) => {
				r.zhuanye = currentSessionMajor.value
			})
		}
		for (let i = 0; i < rows.length; i++) {
			const r = rows[i]
			if (!r.ketimingcheng?.trim()) {
				context?.$toolUtil.message(`第 ${i + 1} 行课题名称不能为空`, 'error')
				return
			}
			if (!r.timuleixing?.trim()) {
				context?.$toolUtil.message(`第 ${i + 1} 行题目类型不能为空`, 'error')
				return
			}
			if (!r.ketixingzhi?.trim()) {
				context?.$toolUtil.message(`第 ${i + 1} 行课题性质不能为空`, 'error')
				return
			}
			if (!r.timufanwei?.trim()) {
				context?.$toolUtil.message(`第 ${i + 1} 行题目范围不能为空`, 'error')
				return
			}
		}
		for (let i = 0; i < rows.length; i++) {
			try {
				await context?.$http({
					url: `${tableName.value}/save`,
					method: 'post',
					data: rows[i]
				})
			} catch (e) {
				context?.$toolUtil.message(`第 ${i + 1} 行保存失败：${extractErrorMessage(e)}`, 'error')
				return
			}
		}
		context?.$toolUtil.message(`批量保存成功，共 ${rows.length} 条`, 'success', () => {
			importVisible.value = false
			emit('importChange')
		})
	}
	const submitXueshengManual = () => {
		const rows = manualList.value.filter(r =>
			r.xuehao?.trim() ||
			r.xueshengxingming?.trim() ||
			r.shoujihaoma?.trim()
		)
		if (!rows.length) {
			context?.$toolUtil.message('请至少填写一条完整的学生信息', 'error')
			return
		}
		if (isMajorLockedSession.value && !currentSessionMajor.value) {
			context?.$toolUtil.message('当前管理员专业未配置，请先维护专业信息', 'error')
			return
		}
		const payloadRows = []
		for (let i = 0; i < rows.length; i++) {
			const r = rows[i]
			const xuehao = r.xuehao?.trim() || ''
			const xueshengxingming = r.xueshengxingming?.trim() || ''
			const shoujihaoma = r.shoujihaoma?.trim() || ''
			const xingbie = r.xingbie?.trim() || ''
			const zhuanye = isMajorLockedSession.value ? currentSessionMajor.value : (r.zhuanye?.trim() || '')
			const nianji = r.nianji?.trim() || ''
			const banji = r.banji?.trim() || ''
			if (!xuehao || !xueshengxingming || !shoujihaoma || !zhuanye) {
				context?.$toolUtil.message(`第 ${i + 1} 行学号、学生姓名、联系电话、专业不能为空`, 'error')
				return
			}
			if (xingbie !== '男' && xingbie !== '女') {
				context?.$toolUtil.message(`第 ${i + 1} 行性别不能为空，且仅支持“男”或“女”`, 'error')
				return
			}
			const mima = resolvePassword(r.mima, shoujihaoma)
			if (!mima || mima.length !== 6) {
				context?.$toolUtil.message(`第 ${i + 1} 行密码为空时联系电话需至少 6 位数字，且密码长度须为 6 位`, 'error')
				return
			}
			payloadRows.push({
				xuehao,
				mima,
				xueshengxingming,
				shoujihaoma,
				xingbie,
				zhuanye,
				nianji,
				banji
			})
		}
		const requests = payloadRows.map(r => {
			return context?.$http({
				url: `${tableName.value}/save`,
				method: 'post',
				data: r
			})
		})
		Promise.all(requests).then(() => {
			context?.$toolUtil.message('批量保存成功', 'success', () => {
				importVisible.value = false
				emit('importChange')
			})
		}).catch(() => {
			context?.$toolUtil.message('部分数据保存失败，请检查填写内容', 'error')
		})
	}
	const submitJiaoshiManual = () => {
		const rows = manualList.value.filter(r =>
			r.jiaoshigonghao?.trim() ||
			r.jiaoshixingming?.trim() ||
			r.lianxidianhua?.trim()
		)
		if (!rows.length) {
			context?.$toolUtil.message('请至少填写一条完整的教师信息', 'error')
			return
		}
		if (isMajorLockedSession.value && !currentSessionMajor.value) {
			context?.$toolUtil.message('当前管理员专业未配置，请先维护专业信息', 'error')
			return
		}
		const payloadRows = []
		for (let i = 0; i < rows.length; i++) {
			const r = rows[i]
			const jiaoshigonghao = r.jiaoshigonghao?.trim() || ''
			const jiaoshixingming = r.jiaoshixingming?.trim() || ''
			const lianxidianhua = r.lianxidianhua?.trim() || ''
			const xingbie = r.xingbie?.trim() || ''
			const zhuanye = isMajorLockedSession.value ? currentSessionMajor.value : (r.zhuanye?.trim() || '')
			if (!jiaoshigonghao || !jiaoshixingming || !lianxidianhua || !zhuanye) {
				context?.$toolUtil.message(`第 ${i + 1} 行教师工号、教师姓名、联系电话、专业不能为空`, 'error')
				return
			}
			if (xingbie !== '男' && xingbie !== '女') {
				context?.$toolUtil.message(`第 ${i + 1} 行性别不能为空，且仅支持“男”或“女”`, 'error')
				return
			}
			const mima = resolvePassword(r.mima, lianxidianhua)
			if (!mima) {
				context?.$toolUtil.message(`第 ${i + 1} 行密码为空时联系电话需至少 6 位数字`, 'error')
				return
			}
			payloadRows.push({
				jiaoshigonghao,
				mima,
				jiaoshixingming,
				lianxidianhua,
				xingbie,
				zhuanye
			})
		}
		const requests = payloadRows.map(r => {
			return context?.$http({
				url: `${tableName.value}/save`,
				method: 'post',
				data: r
			})
		})
		Promise.all(requests).then(() => {
			context?.$toolUtil.message('批量保存成功', 'success', () => {
				importVisible.value = false
				emit('importChange')
			})
		}).catch(() => {
			context?.$toolUtil.message('部分数据保存失败，请检查填写内容', 'error')
		})
	}
	const submitTimuleixingManual = () => {
		const rows = manualList.value.filter(r => r.timuleixing?.trim())
		if (!rows.length) {
			context?.$toolUtil.message('请至少填写一条题目类型', 'error')
			return
		}
		const seen = new Set()
		for (let i = 0; i < rows.length; i++) {
			const name = rows[i].timuleixing.trim()
			if (seen.has(name)) {
				context?.$toolUtil.message(`第 ${i + 1} 行题目类型重复（${name}）`, 'error')
				return
			}
			seen.add(name)
		}
		const requests = rows.map(r => {
			return context?.$http({
				url: `${tableName.value}/save`,
				method: 'post',
				data: { timuleixing: r.timuleixing.trim() }
			})
		})
		Promise.all(requests).then(() => {
			context?.$toolUtil.message('批量保存成功', 'success', () => {
				importVisible.value = false
				emit('importChange')
			})
		}).catch(() => {
			context?.$toolUtil.message('部分数据保存失败，请检查填写内容', 'error')
		})
	}
	
</script>

<style scoped>
.manual-tip {
	margin-bottom: 10px;
	font-size: 13px;
	color: #666;
}
.manual-table {
	margin-top: 10px;
}
.manual-actions {
	margin-top: 12px;
	display: flex;
	justify-content: flex-start;
	gap: 10px;
}
</style>
