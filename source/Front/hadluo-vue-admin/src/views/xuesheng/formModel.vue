<template>
	<div>
		<el-dialog v-model="formVisible" :title="formTitle" width="80%" destroy-on-close :fullscreen='false'>
			<el-form class="formModel_form" ref="formRef" :model="form" label-width="120px" :rules="rules">
				<el-row>
					<el-col :span="24">
						<el-form-item label="学号" prop="xuehao">
							<el-input class="list_inp" v-model="form.xuehao" placeholder="学号"
								type="text" :readonly="!isAdd||disabledForm.xuehao?true:false" />
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="密码" prop="mima">
							<el-input class="list_inp" v-model="form.mima" placeholder="密码"
								type="password" :readonly="!isAdd||disabledForm.mima?true:false" />
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="学生姓名" prop="xueshengxingming">
							<el-input class="list_inp" v-model="form.xueshengxingming" placeholder="学生姓名"
								 type="text" 								:readonly="!isAdd||disabledForm.xueshengxingming?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="性别" prop="xingbie">
							<el-select
								class="list_sel"
								:disabled="!isAdd||disabledForm.xingbie?true:false"
								v-model="form.xingbie"
								placeholder="请选择性别"
								clearable
							>
								<el-option
									v-for="item in xingbieOptions"
									:key="item"
									:label="item"
									:value="item"
								/>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="手机号码" prop="shoujihaoma">
							<el-input class="list_inp" v-model="form.shoujihaoma" placeholder="手机号码"
								 type="text" 								:readonly="!isAdd||disabledForm.shoujihaoma?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="年级" prop="nianji">
							<el-select
								class="list_sel"
								:disabled="!isAdd||disabledForm.nianji?true:false"
								v-model="form.nianji"
								placeholder="请选择年级"
								clearable
							>
								<el-option v-for="item in nianjiOptions" :key="item.value" :label="item.label" :value="item.value" />
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="毕业届别" prop="biyejie">
							<el-select
								class="list_sel"
								:disabled="!isAdd||disabledForm.biyejie?true:false"
								v-model="form.biyejie"
								placeholder="请选择毕业届别"
								clearable
							>
								<el-option v-for="item in biyejieOptions" :key="item.value" :label="item.label" :value="item.value" />
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="专业" prop="zhuanye">
							<el-select
								class="list_sel"
								:disabled="!isAdd||disabledForm.zhuanye||isMajorLocked?true:false"
								v-model="form.zhuanye"
								:placeholder="isMajorLocked ? '自动带入当前账号专业' : '请选择专业'"
							>
								<el-option v-for="item in zhuanyeLists" :key="item" :label="item" :value="item" />
							</el-select>
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="班级" prop="banji">
							<el-input class="list_inp" v-model="form.banji" placeholder="班级"
								 type="text" 								:readonly="!isAdd||disabledForm.banji?true:false" />
						</el-form-item>
					</el-col>

				</el-row>
			</el-form>
			<template #footer v-if="isAdd||type=='logistics'||type=='reply'">
				<span class="formModel_btn_box">
					<el-button class="formModel_cancel" @click="closeClick">取消</el-button>
					<el-button class="formModel_confirm" type="primary" @click="save"
						>
						提交
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
	const tableName = 'xuesheng'
	const formName = '学生'
	//基础信息
	//form表单
	const form = ref({})
	const disabledForm = ref({
		xuehao : false,
		mima : false,
		xueshengxingming : false,
		xingbie : false,
		shoujihaoma : false,
		nianji : false,
		biyejie : false,
		zhuanye : false,
		banji : false,
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
		xuehao: [
			{required: true,message: '请输入',trigger: 'blur'}, 
		],
		mima: [
			{required: true,message: '请输入',trigger: 'blur'}, 
		],
		xueshengxingming: [
			{required: true,message: '请输入',trigger: 'blur'}, 
		],
		xingbie: [
		],
		shoujihaoma: [
			{ validator: validateMobile, trigger: 'blur' },
		],
		zhuanye: [
			{ required: true, message: '请选择专业', trigger: 'change' },
		],
		nianji: [
			{ required: true, message: '请选择年级', trigger: 'change' },
		],
		biyejie: [
			{ required: true, message: '请选择毕业届别', trigger: 'change' },
		],
		banji: [
		],
	})
	//表单验证
	
	const formRef = ref(null)
	const id = ref(0)
	const type = ref('')
	const isMajorLocked = ref(false)
	// 性别下拉（固定选项，避免 ref 未初始化或 v-for 缺 key 导致 Element Plus 报错）
	const xingbieOptions = Object.freeze(['男', '女'])
	const zhuanyeLists = Object.freeze(['地理信息科学', '地理科学', '风景园林', '测绘工程', '城乡规划'])
	const biyejieOptions = Object.freeze(
		Array.from({ length: 7 }, (_, i) => {
			const year = String(2024 + i)
			return { value: year, label: `${year}届` }
		})
	)
	const nianjiOptions = Object.freeze(
		Array.from({ length: 10 }, (_, i) => {
			const year = String(2018 + i)
			return { value: year, label: `${year}级` }
		})
	)
	//methods

	//获取唯一标识
	const getUUID =()=> {
      return new Date().getTime();
    }
	//重置
	const resetForm = () => {
		form.value = {
			xuehao: '',
			mima: '',
			xueshengxingming: '',
			xingbie: '',
			shoujihaoma: '',
			nianji: '',
			biyejie: '',
			zhuanye: '',
			banji: '',
		}
	}
	//获取info
	const getInfo = ()=>{
		context?.$http({
			url: `${tableName}/info/${id.value}`,
			method: 'get'
		}).then(res => {
			let reg=new RegExp('../../../file','g')
			form.value = res.data.data
			// el-select 的 value 须与 option 同类型；接口若返回非字符串会导致异常或显示异常
			if (form.value.xingbie != null && typeof form.value.xingbie !== 'string') {
				form.value.xingbie = String(form.value.xingbie)
			}
			if (form.value.nianji != null && typeof form.value.nianji !== 'string') {
				form.value.nianji = String(form.value.nianji)
			}
			if (form.value.biyejie != null && typeof form.value.biyejie !== 'string') {
				form.value.biyejie = String(form.value.biyejie)
			}
			formVisible.value = true
		}).catch(() => {
			context?.$toolUtil.message('获取学生信息失败', 'error')
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
				if(x=='xuehao'){
					form.value.xuehao = row[x];
					disabledForm.value.xuehao = true;
					continue;
				}
				if(x=='mima'){
					form.value.mima = row[x];
					disabledForm.value.mima = true;
					continue;
				}
				if(x=='xueshengxingming'){
					form.value.xueshengxingming = row[x];
					disabledForm.value.xueshengxingming = true;
					continue;
				}
				if(x=='xingbie'){
					form.value.xingbie = row[x];
					disabledForm.value.xingbie = true;
					continue;
				}
				if(x=='shoujihaoma'){
					form.value.shoujihaoma = row[x];
					disabledForm.value.shoujihaoma = true;
					continue;
				}
				if(x=='nianji'){
					form.value.nianji = row[x];
					disabledForm.value.nianji = true;
					continue;
				}
				if(x=='biyejie'){
					form.value.biyejie = row[x];
					disabledForm.value.biyejie = true;
					continue;
				}
				if(x=='zhuanye'){
					form.value.zhuanye = row[x];
					disabledForm.value.zhuanye = true;
					continue;
				}
				if(x=='banji'){
					form.value.banji = row[x];
					disabledForm.value.banji = true;
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
			formVisible.value = true
		}

		context?.$http({
			url: `${context?.$toolUtil.storageGet('sessionTable')}/session`,
			method: 'get'
		}).then(res => {
			const json = res?.data?.data || {}
			const sessionTable = context?.$toolUtil.storageGet('sessionTable')
			const major = json.zhuanye ? String(json.zhuanye).trim() : ''
			if (major && (sessionTable === 'jiaoshi' || sessionTable === 'users')) {
				form.value.zhuanye = major
				isMajorLocked.value = true
				disabledForm.value.zhuanye = true
			} else {
				isMajorLocked.value = false
			}
		}).catch(() => {})
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
	const save=()=>{
		if((form.value.xuehao.length<6)){
			context?.$toolUtil.message('学号长度不能小于6','error')
			return false
		}
		if((form.value.xuehao.length>6)){
			context?.$toolUtil.message('学号长度不能大于6','error')
			return false
		}
		if((form.value.mima.length<6)){
			context?.$toolUtil.message('密码长度不能小于6','error')
			return false
		}
		if((form.value.mima.length>6)){
			context?.$toolUtil.message('密码长度不能大于6','error')
			return false
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
									formVisible.value = false
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
							formVisible.value = false
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