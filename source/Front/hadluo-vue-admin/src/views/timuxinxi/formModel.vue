<template>
	<div>
		<el-dialog v-model="formVisible" :title="formTitle" width="80%" destroy-on-close :fullscreen='false'>
			<el-form class="formModel_form" ref="formRef" :model="form" label-width="$template2.back.add.form.base.labelWidth" :rules="rules">
				<el-row>
					<el-col :span="24">
						<el-form-item label="题目编" prop="timubianhao">
							<el-input class="list_inp" v-model="form.timubianhao" :readonly="true" placeholder="题目编号" />
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="课题名称" prop="ketimingcheng">
							<el-input class="list_inp" v-model="form.ketimingcheng" placeholder="课题名称"
								 type="text" 								:readonly="!isAdd||disabledForm.ketimingcheng?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="题目类型" prop="timuleixing">
							<el-select
								class="list_sel"
								:disabled="!isAdd||disabledForm.timuleixing?true:false"
								v-model="form.timuleixing" 
								placeholder="请选择题目类型"
								>
								<el-option v-for="item in timuleixingLists" :key="item" :label="item" :value="item">
								</el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="专业" prop="zhuanye">
							<el-select
								class="list_sel"
								:disabled="!isAdd||disabledForm.zhuanye?true:false"
								v-model="form.zhuanye"
								placeholder="请选择专业"
							>
								<el-option v-for="item in zhuanyeLists" :key="item" :label="item" :value="item" />
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="题目封面" prop="timufengmian">
							<uploads
								:disabled="!isAdd||disabledForm.timufengmian?true:false"
								action="file/upload" 
								tip="请上传题目封面" 
								:limit="3" 
								style="width: 100%;text-align: left;"
								:fileUrls="form.timufengmian?form.timufengmian:''" 
								@change="timufengmianUploadSuccess">
							</uploads>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="选题时间" prop="xuantishijian">
							<el-input class="list_inp" v-model="form.xuantishijian" placeholder="选题时间"
								 type="text" 								:readonly="!isAdd||disabledForm.xuantishijian?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="教师工号" prop="jiaoshigonghao">
							<el-input class="list_inp" v-model="form.jiaoshigonghao" placeholder="教师工号"
								 type="text" 								:readonly="!isAdd||disabledForm.jiaoshigonghao?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="教师姓名" prop="jiaoshixingming">
							<el-input class="list_inp" v-model="form.jiaoshixingming" placeholder="教师姓名"
								 type="text" 								:readonly="!isAdd||disabledForm.jiaoshixingming?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="发布时间" prop="fabushijian">
							<el-date-picker
								class="list_date"
								v-model="form.fabushijian"
								format="YYYY-MM-DD HH:mm:ss"
								value-format="YYYY-MM-DD HH:mm:ss"
								type="datetime"
								:readonly="!isAdd||disabledForm.fabushijian?true:false"
								placeholder="请选择发布时间" />
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="课题性质" prop="ketixingzhi">
							<el-input v-model="form.ketixingzhi" placeholder="课题性质" type="textarea"
							:readonly="!isAdd||disabledForm.ketixingzhi?true:false"
							/>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="题目范围" prop="timufanwei">
							<editor :value="form.timufanwei" placeholder="请输入题目范围" :readonly="!isAdd||disabledForm.timufanwei?true:false"
								class="list_editor" @change="(e)=>editorChange(e,'timufanwei')"></editor>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<template #footer v-if="isAdd||type=='logistics'||type=='reply'">
				<span class="formModel_btn_box">
					<el-button class="formModel_cancel" @click="closeClick">取消</el-button>
					<el-button class="formModel_confirm" type="primary" @click="save()">
						提交
					</el-button>
					<el-button
						v-if="isAdd && type === 'add'"
						class="formModel_confirm"
						type="primary"
						@click="save(true)"
					>
						提交并继续新增
					</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>
<script setup>
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		computed,
		defineEmits
	} from 'vue'
	const context = getCurrentInstance()?.appContext.config.globalProperties;	
	const emit = defineEmits(['formModelChange'])
	//基础信息
	const tableName = 'timuxinxi'
	const formName = '题目信息'
	//基础信息
	//form表单
	const form = ref({})
	const disabledForm = ref({
		timubianhao : false,
		ketimingcheng : false,
		timuleixing : false,
		zhuanye : false,
		timufengmian : false,
		ketixingzhi : false,
		timufanwei : false,
		xuantishijian : false,
		jiaoshigonghao : false,
		jiaoshixingming : false,
		fabushijian : false,
		storeupnum : false,
	})
	const formVisible = ref(false)
	const isAdd = ref(false)
	const formTitle = ref('')
	//表单验证
	//匹配整数
	const validateIntNumber = (rule, value, callback) => {
		if (!value) {
			callback();
		} else if (!context?.$toolUtil.isIntNumer(value)) {
			callback(new Error("请输入整数"));
		} else {
			callback();
		}
	}
	//匹配数字
	const validateNumber = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.isNumber(value)) {
			callback(new Error("请输入数字"));
		} else {
			callback();
		}
	}
	//匹配手机号码
	const validateMobile = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.isMobile(value)) {
			callback(new Error("请输入正确的手机号码"));
		} else {
			callback();
		}
	}
	//匹配电话号码
	const validatePhone = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.isPhone(value)) {
			callback(new Error("请输入正确的电话号码"));
		} else {
			callback();
		}
	}
	//匹配邮箱
	const validateEmail = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.isEmail(value)) {
			callback(new Error("请输入正确的邮箱地址"));
		} else {
			callback();
		}
	}
	//匹配身份证
	const validateIdCard = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.checkIdCard(value)) {
			callback(new Error("请输入正确的身份证号码"));
		} else {
			callback();
		}
	}
	//匹配网站地址
	const validateUrl = (rule, value, callback) => {
		if(!value){
			callback();
		} else if (!context?.$toolUtil.isURL(value)) {
			callback(new Error("请输入正确的URL地址"));
		} else {
			callback();
		}
	}
	const rules = ref({
		timubianhao: [
		],
		ketimingcheng: [
		],
		timuleixing: [
		],
		zhuanye: [
			{ required: true, message: '请选择专业', trigger: 'change' },
		],
		timufengmian: [
		],
		ketixingzhi: [
		],
		timufanwei: [
		],
		xuantishijian: [
		],
		jiaoshigonghao: [
		],
		jiaoshixingming: [
		],
		fabushijian: [
		],
		storeupnum: [
			{ validator: validateIntNumber, trigger: 'blur' },
		],
	})
	//表单验证
	
	const formRef = ref(null)
	const id = ref(0)
	const type = ref('')
	//题目类型列表
	const timuleixingLists = ref([])
	const zhuanyeLists = ref([])
	//题目封面上传回调
	const timufengmianUploadSuccess=(e)=>{
		form.value.timufengmian = e
	}
	//methods

	//获取唯一标识
	const getUUID =()=> {
      return new Date().getTime();
    }
	//重置
	const resetForm = () => {
		form.value = {
			timubianhao: getUUID(),
			ketimingcheng: '',
			timuleixing: '',
			zhuanye: '',
			timufengmian: '',
			ketixingzhi: '',
			timufanwei: '',
			xuantishijian: '',
			jiaoshigonghao: '',
			jiaoshixingming: '',
			fabushijian: '',
			storeupnum: '0',
		}
	}
	//获取info
	const getInfo = ()=>{
		context?.$http({
			url: `${tableName}/info/${id.value}`,
			method: 'get'
		}).then(res => {
			const data = res?.data?.data
			// 后端没有返回数据时，避免空指针错误
			if (!data) {
				context?.$toolUtil.message('当前题目信息不存在或已被删除', 'error', () => {
					formVisible.value = false
				})
				return
			}
			let reg = new RegExp('../../../file','g')
			data.timufanwei = data.timufanwei ? data.timufanwei.replace(reg,'../../../hadluo-xt/file') : ''
			form.value = data
			formVisible.value = true
		})
	}
	const crossRow = ref('')
	const crossTable = ref('')
	const crossTips = ref('')
	const crossColumnName = ref('')
	const crossColumnValue = ref('')
	//初始化
	const init=(formId=null,formType='add',formNames='',row=null,table=null,statusColumnName=null,tips=null,statusColumnValue=null)=>{
		resetForm()
			form.value.fabushijian = context?.$toolUtil.getCurDateTime()
		if(formId){
			id.value = formId
			type.value = formType
		}
		if(formType == 'add'){
			isAdd.value = true
			formTitle.value = '新增' + formName
			formVisible.value = true
		}else if(formType == 'info'){
			isAdd.value = false
			formTitle.value = '查看' + formName
			getInfo()
		}else if(formType == 'edit'){
			isAdd.value = true
			formTitle.value = '修改' + formName
			getInfo()
		}
		else if(formType == 'cross'){
			isAdd.value = true
			formTitle.value = formNames
			// getInfo()
			for(let x in row){
				if(x=='timubianhao'){
					form.value.timubianhao = row[x];
					disabledForm.value.timubianhao = true;
					continue;
				}
				if(x=='ketimingcheng'){
					form.value.ketimingcheng = row[x];
					disabledForm.value.ketimingcheng = true;
					continue;
				}
				if(x=='timuleixing'){
					form.value.timuleixing = row[x];
					disabledForm.value.timuleixing = true;
					continue;
				}
				if(x=='zhuanye'){
					form.value.zhuanye = row[x];
					disabledForm.value.zhuanye = true;
					continue;
				}
				if(x=='timufengmian'){
					form.value.timufengmian = row[x];
					disabledForm.value.timufengmian = true;
					continue;
				}
				if(x=='ketixingzhi'){
					form.value.ketixingzhi = row[x];
					disabledForm.value.ketixingzhi = true;
					continue;
				}
				if(x=='timufanwei'){
					form.value.timufanwei = row[x];
					disabledForm.value.timufanwei = true;
					continue;
				}
				if(x=='xuantishijian'){
					form.value.xuantishijian = row[x];
					disabledForm.value.xuantishijian = true;
					continue;
				}
				if(x=='jiaoshigonghao'){
					form.value.jiaoshigonghao = row[x];
					disabledForm.value.jiaoshigonghao = true;
					continue;
				}
				if(x=='jiaoshixingming'){
					form.value.jiaoshixingming = row[x];
					disabledForm.value.jiaoshixingming = true;
					continue;
				}
				if(x=='fabushijian'){
					form.value.fabushijian = row[x];
					disabledForm.value.fabushijian = true;
					continue;
				}
				if(x=='storeupnum'){
					form.value.storeupnum = row[x];
					disabledForm.value.storeupnum = true;
					continue;
				}
			}
			if(row){
				crossRow.value = row
			}
			if(table){
				crossTable.value = table
			}
			if(tips){
				crossTips.value = tips
			}
			if(statusColumnName){
				crossColumnName.value = statusColumnName
			}
			if(statusColumnValue){
				crossColumnValue.value = statusColumnValue
			}
			form.value.storeupnum='0'
			formVisible.value = true
		}

		context?.$http({
			url: `${context?.$toolUtil.storageGet('sessionTable')}/session`,
			method: 'get'
		}).then(res => {
			var json = res.data.data
			if(json.hasOwnProperty('jiaoshigonghao')&& context?.$toolUtil.storageGet("role")!="管理员"){
				form.value.jiaoshigonghao = json.jiaoshigonghao
				disabledForm.value.jiaoshigonghao = true;
			}
			if(json.hasOwnProperty('jiaoshixingming')&& context?.$toolUtil.storageGet("role")!="管理员"){
				form.value.jiaoshixingming = json.jiaoshixingming
				disabledForm.value.jiaoshixingming = true;
			}
			if(json.hasOwnProperty('zhuanye')&& context?.$toolUtil.storageGet("role")!="管理员"){
				form.value.zhuanye = json.zhuanye
				disabledForm.value.zhuanye = true;
			}
		})
		context?.$http({
			url: `option/timuleixing/timuleixing`,
			method: 'get'
		}).then(res=>{
			timuleixingLists.value = res.data.data
		})
		zhuanyeLists.value = "地理信息科学,地理科学,风景园林,测绘工程,城乡规划".split(',')
	}
	//初始化
	//声明父级调用
	defineExpose({
		init
	})
	//关闭
	const closeClick = () => {
		formVisible.value = false
	}
	//富文本
	const editorChange = (e,name) =>{
		form.value[name] = e
	}
	//提交
	const save=(keepOpen = false)=>{
		if(form.value.timufengmian!=null) {
			form.value.timufengmian = form.value.timufengmian.replace(new RegExp(context?.$config.url,"g"),"");
		}
		var table = crossTable.value
		var objcross = JSON.parse(JSON.stringify(crossRow.value))
		let crossUserId = ''
		let crossRefId = ''
		let crossOptNum = ''
		if(type.value == 'cross'){
			if(crossColumnName.value!=''){
				if(!crossColumnName.value.startsWith('[')){
					for(let o in objcross){
						if(o == crossColumnName.value){
							objcross[o] = crossColumnValue.value
						}
					}
					//修改跨表数据
					changeCrossData(objcross)
				}else{
					crossUserId = context?.$toolUtil.storageGet('userid')
					crossRefId = objcross['id']
					crossOptNum = crossColumnName.value.replace(/\[/,"").replace(/\]/,"")
				}
			}
		}
		formRef.value.validate((valid)=>{
			if(valid){
				if(crossUserId&&crossRefId){
					form.value.crossuserid = crossUserId
					form.value.crossrefid = crossRefId
					let params = {
						page: 1,
						limit: 1000, 
						crossuserid:form.value.crossuserid,
						crossrefid:form.value.crossrefid,
					}
					context?.$http({
						url: `${tableName}/page`,
						method: 'get', 
						params: params 
					}).then(res=>{
						if(res.data.data.total>=crossOptNum){
							context?.$toolUtil.message(`${crossTips.value}`,'error')
							return false
						}else{
							context?.$http({
								url: `${tableName}/${!form.value.id ? "save" : "update"}`,
								method: 'post', 
								data: form.value 
							}).then(res=>{
								context?.$toolUtil.message(`操作成功`,'success',()=>{
									if (keepOpen && type.value === 'add') {
										// 重置表单，保持对话框打开，便于连续新增多个题目
										resetForm()
										form.value.fabushijian = context?.$toolUtil.getCurDateTime()
										// 重新填充当前教师信息
										context?.$http({
											url: `${context?.$toolUtil.storageGet('sessionTable')}/session`,
											method: 'get'
										}).then(res2 => {
											const json = res2.data.data || {}
											if(json.hasOwnProperty('jiaoshigonghao')&& context?.$toolUtil.storageGet("role")!="管理员"){
												form.value.jiaoshigonghao = json.jiaoshigonghao
												disabledForm.value.jiaoshigonghao = true;
											}
											if(json.hasOwnProperty('jiaoshixingming')&& context?.$toolUtil.storageGet("role")!="管理员"){
												form.value.jiaoshixingming = json.jiaoshixingming
												disabledForm.value.jiaoshixingming = true;
											}
										})
									} else {
										formVisible.value = false
									}
									emit('formModelChange')
								})
							})
						}
					})
				}else{
					context?.$http({
						url: `${tableName}/${!form.value.id ? "save" : "update"}`,
						method: 'post', 
						data: form.value 
					}).then(res=>{
						context?.$toolUtil.message(`操作成功`,'success',()=>{
							if (keepOpen && type.value === 'add') {
								resetForm()
								form.value.fabushijian = context?.$toolUtil.getCurDateTime()
								context?.$http({
									url: `${context?.$toolUtil.storageGet('sessionTable')}/session`,
									method: 'get'
								}).then(res2 => {
									const json = res2.data.data || {}
									if(json.hasOwnProperty('jiaoshigonghao')&& context?.$toolUtil.storageGet("role")!="管理员"){
										form.value.jiaoshigonghao = json.jiaoshigonghao
										disabledForm.value.jiaoshigonghao = true;
									}
									if(json.hasOwnProperty('jiaoshixingming')&& context?.$toolUtil.storageGet("role")!="管理员"){
										form.value.jiaoshixingming = json.jiaoshixingming
										disabledForm.value.jiaoshixingming = true;
									}
								})
							} else {
								formVisible.value = false
							}
							emit('formModelChange')
						})
					})
				}
			}
		})
	}
	//修改跨表数据
	const changeCrossData=(row)=>{
		context?.$http({
			url: `${crossTable.value}/update`,
			method: 'post',
			data: row
		}).then(res=>{})
	}
</script>
<style lang="scss" scoped>
// 科技蓝主题 - 表单弹窗样式
.formModel_form{
	border-radius: 6px;
	padding: 30px;
	background: transparent;
	// form item
	:deep(.el-form-item) {
		margin: 0 0 20px 0;
		display: flex;
		justify-content: space-between;
		//label
		.el-form-item__label {
			background: transparent;
			display: block;
			width: 90px;
			text-align: right;
			color: var(--text-regular);
		}
		// 内容盒子
		.el-form-item__content {
			display: flex;
			width: calc(100% - 160px);
			justify-content: flex-start;
			align-items: center;
			flex-wrap: wrap;
			// 输入框
			.list_inp {
				border: 1px solid var(--tech-border-soft);
				padding: 0 10px;
				width: 300px;
				line-height: 36px;
				box-sizing: border-box;
				height: 36px;
				border-radius: 6px;
				background: rgba(8, 28, 58, 0.55);
				//去掉默认样式
				.el-input__wrapper{
					border: none;
					box-shadow: none;
					background: none;
					border-radius: 0;
					height: 100%;
					padding: 0;
				}
				.el-input__wrapper:hover {
					border-color: var(--primary-blue-light);
				}
				.is-focus {
					box-shadow: none !important;
					border-color: var(--primary-blue-light);
				}
				.el-input__inner {
					color: var(--text-primary);
				}
			}
			//日期选择器
			.list_date {
				border: 1px solid var(--tech-border-soft);
				border-radius: 6px;
				width: 400px;
				line-height: 36px;
				box-sizing: border-box;
				background: rgba(8, 28, 58, 0.55);
				//去掉默认样式
				.el-input__wrapper{
					border: none;
					box-shadow: none;
					background: none;
					border-radius: 0;
					height: 100%;
				}
			}
			// 下拉框
			.list_sel {
				border: 1px solid var(--tech-border-soft);
				border-radius: 6px;
				padding: 0 10px;
				width: 400px;
				line-height: 36px;
				box-sizing: border-box;
				background: rgba(8, 28, 58, 0.55);
				//去掉默认样式
				.select-trigger{
					height: 100%;
					.el-input{
						height: 100%;
						.el-input__wrapper{
							border: none;
							box-shadow: none;
							background: none;
							border-radius: 0;
							height: 100%;
							padding: 0;
						}
						.is-focus {
							box-shadow: none !important;
						}
					}
				}
			}
			// 富文本（具体样式见 Editor 组件 admin-editor 主题）
			.list_editor {
				width: 100%;
			}
			// 长文本
			.el-textarea__inner {
				border: 1px solid var(--tech-border-soft);
				border-radius: 6px;
				padding: 12px;
				outline: none;
				color: var(--text-primary);
				width: 400px;
				font-size: 14px;
				min-height: 120px;
				background: rgba(8, 28, 58, 0.55);
			}
			//图片上传样式
			.el-upload-list  {
				//提示语
				.el-upload__tip {
					margin: 7px 0 0;
					color: var(--text-secondary);
					display: flex;
					font-size: 12px;
					justify-content: flex-start;
					align-items: center;
				}
				//外部盒子
				.el-upload--picture-card {
					border: 1px dashed var(--tech-border);
					cursor: pointer;
					background-color: rgba(34, 211, 238, 0.05);
					border-radius: 8px;
					width: 100px;
					line-height: 110px;
					text-align: center;
					height: 100px;
					//图标
					.el-icon{
						color: var(--tech-cyan);
						font-size: 32px;
					}
				}
				.el-upload-list__item {
					border: 1px solid var(--tech-border-soft);
					cursor: pointer;
					background-color: rgba(8, 28, 58, 0.55);
					border-radius: 8px;
					width: 100px;
					line-height: 110px;
					text-align: center;
					height: 100px;
				}
			}
		}
	}
}
// 按钮盒子
.formModel_btn_box {
	display: flex;
	width: 100%;
	justify-content: center;
	align-items: center;
	.formModel_cancel {
		border: 1px solid var(--tech-border-soft);
		cursor: pointer;
		border-radius: 6px;
		padding: 0 24px;
		margin: 0 10px 0 0;
		outline: none;
		color: var(--text-regular);
		background: rgba(8, 28, 58, 0.65);
		width: auto;
		font-size: 14px;
		height: 36px;
		transition: all 0.3s;
	}
	.formModel_cancel:hover {
		border-color: var(--tech-cyan);
		color: var(--tech-cyan);
	}
	
	.formModel_confirm {
		border: 1px solid var(--primary-blue);
		cursor: pointer;
		border-radius: 6px;
		padding: 0 24px;
		margin: 0 10px 0 0;
		outline: none;
		color: #fff;
		background: var(--primary-blue);
		width: auto;
		font-size: 14px;
		height: 36px;
		transition: all 0.3s;
	}
	.formModel_confirm:hover {
		background: var(--primary-blue-dark);
		border-color: var(--primary-blue-dark);
		box-shadow: 0 0 16px rgba(34, 211, 238, 0.3);
	}
}
</style>