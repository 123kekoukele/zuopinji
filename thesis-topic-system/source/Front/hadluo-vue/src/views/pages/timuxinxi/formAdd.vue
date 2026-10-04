
<template>
	<div class="form_add_page app-contain">
		<header class="form_add_header">
			<el-breadcrumb separator="/" class="breadcrumb">
				<el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
				<el-breadcrumb-item v-for="item in breadList" :key="item.name">{{ item.name }}</el-breadcrumb-item>
			</el-breadcrumb>
		</header>
		<div class="form_card">
		<el-form ref="formRef" :model="form" class="add_form" label-width="120px" :rules="rules">
			<el-row>
				<el-col :span="12">
					<el-form-item label="题目编号" prop="timubianhao">
						<el-input class="list_inp" v-model="form.timubianhao" placeholder="请输入题目编号" readonly></el-input>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="课题名称" prop="ketimingcheng">
						<el-input class="list_inp" v-model="form.ketimingcheng" placeholder="课题名称"
							 type="text" 							:readonly="!isAdd||disabledForm.ketimingcheng?true:false" />
					</el-form-item>
				</el-col>

				<el-col :span="12">
					<el-form-item label="题目类型" prop="timuleixing">
						<el-select
							class="list_sel"
							:disabled="!isAdd||disabledForm.timuleixing?true:false"
							v-model="form.timuleixing" 
							placeholder="请选择题目类型"
							style="width:100%;"
							>
							<el-option v-for="item in timuleixingLists" :key="item" :label="item" :value="item">
							</el-option>
						</el-select>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="专业" prop="zhuanye">
						<el-select
							class="list_sel"
							:disabled="!isAdd||disabledForm.zhuanye?true:false"
							v-model="form.zhuanye"
							placeholder="请选择专业"
							style="width:100%;"
						>
							<el-option v-for="item in zhuanyeLists" :key="item" :label="item" :value="item" />
						</el-select>
					</el-form-item>
				</el-col>

				<el-col :span="12">
					<el-form-item label="题目封面" prop="timufengmian">
						<uploads
							:disabled="!isAdd||disabledForm.timufengmian?true:false"
							action="file/upload"
							bizType="timuxinxi" 
							tip="请上传题目封面" 
							:limit="3" 
							style="width: 100%;text-align: left;"
							:fileUrls="form.timufengmian?form.timufengmian:''" 
							@change="timufengmianUploadSuccess">
						</uploads>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item label="选题时间" prop="xuantishijian">
						<el-input class="list_inp" v-model="form.xuantishijian" placeholder="选题时间"
							 type="text" 							:readonly="!isAdd||disabledForm.xuantishijian?true:false" />
					</el-form-item>
				</el-col>

				<el-col :span="12">
					<el-form-item label="教师工号" prop="jiaoshigonghao">
						<el-input class="list_inp" v-model="form.jiaoshigonghao" placeholder="教师工号"
							 type="text" 							:readonly="!isAdd||disabledForm.jiaoshigonghao?true:false" />
					</el-form-item>
				</el-col>

				<el-col :span="12">
					<el-form-item label="教师姓名" prop="jiaoshixingming">
						<el-input class="list_inp" v-model="form.jiaoshixingming" placeholder="教师姓名"
							 type="text" 							:readonly="!isAdd||disabledForm.jiaoshixingming?true:false" />
					</el-form-item>
				</el-col>

				<el-col :span="12">
					<el-form-item label="发布时间" prop="fabushijian">
						<el-date-picker
							class="list_date"
							v-model="form.fabushijian"
							format="YYYY-MM-DD HH:mm:ss"
							value-format="YYYY-MM-DD HH:mm:ss"
							type="datetime"
							style="width:100%;"
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
						<editor class="list_editor" :value="form.timufanwei" placeholder="请输入题目范围" :readonly="!isAdd||disabledForm.timufanwei?true:false"
							@change="(e)=>editorChange(e,'timufanwei')"></editor>
					</el-form-item>
				</el-col>
			</el-row>
			<div class="form_actions">
				<el-button class="form_cancel" @click="backClick">取消</el-button>
				<el-button class="form_confirm" type="primary" @click="save">保存</el-button>
			</div>
		</el-form>
		</div>
	</div>
</template>
<script setup>
	import {
		ref,
		getCurrentInstance,
		watch,
		onUnmounted,
		onMounted,
		nextTick,
		computed
	} from 'vue';
	import {
		useRoute,
		useRouter
	} from 'vue-router';
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const route = useRoute()
	const router = useRouter()
	//基础信息
	const tableName = 'timuxinxi'
	const formName = '题目信息'
	//基础信息
	const breadList = ref([{
		name: formName
	}])
	//获取唯一标识
	const getUUID =()=> {
      return new Date().getTime();
    }
	//form表单
	const form = ref({
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
	})
	const formRef = ref(null)
	const id = ref(0)
	const type = ref('')
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
	})
	const isAdd = ref(false)
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
	})
	//题目类型列表
	const timuleixingLists = ref([])
	const zhuanyeLists = ref([])
	//题目封面上传回调
	const timufengmianUploadSuccess=(e)=>{
		form.value.timufengmian = e
	}
	//methods

	//methods
	//获取info
	const getInfo = ()=>{
		context?.$http({
			url: `${tableName}/info/${id.value}`,
			method: 'get'
		}).then(res => {
			let reg=new RegExp('../../../file','g')
			res.data.data.timufanwei = res.data.data.timufanwei.replace(reg,'../../../hadluo-xt/file');
			form.value = res.data.data
		})
	}
	const crossRow = ref('')
	const crossTable = ref('')
	const crossTips = ref('')
	const crossColumnName = ref('')
	const crossColumnValue = ref('')
	//初始化
	const init = (formId=null,formType='add',formNames='',row=null,table=null,statusColumnName=null,tips=null,statusColumnValue=null) => {
			form.value.fabushijian = context?.$toolUtil.getCurDateTime()
		if(formId){
			id.value = formId
			type.value = formType
		}
		if(formType == 'add'){
			isAdd.value = true
		}else if(formType == 'info'){
			isAdd.value = false
			getInfo()
		}else if(formType == 'edit'){
			isAdd.value = true
			getInfo()
		}
		else if(formType == 'cross'){
			isAdd.value = true
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
	}
		context?.$http({
			url: `${context?.$toolUtil.storageGet('frontSessionTable')}/session`,
			method: 'get'
		}).then(res => {
			var json = res.data.data
			if(json.hasOwnProperty('jiaoshigonghao') && context?.$toolUtil.storageGet("frontRole")!="管理员"){
				form.value.jiaoshigonghao = json.jiaoshigonghao
				disabledForm.value.jiaoshigonghao = true;
			}
			if(json.hasOwnProperty('jiaoshixingming') && context?.$toolUtil.storageGet("frontRole")!="管理员"){
				form.value.jiaoshixingming = json.jiaoshixingming
				disabledForm.value.jiaoshixingming = true;
			}
			if(json.hasOwnProperty('zhuanye') && context?.$toolUtil.storageGet("frontRole")!="管理员"){
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
	//取消
	const backClick = () => {
		history.back()
	}
	//富文本数据回调
	const editorChange = (e,name) =>{
		form.value[name] = e
	}
	//提交
	const save=()=>{
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
									history.back()
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
							history.back()
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
	onMounted(()=>{
		type.value = route.query.type?route.query.type:'add'
		let row = null
		let table = null
		let statusColumnName = null
		let tips = null
		let statusColumnValue = null
		if(type.value == 'cross'){
			row = context?.$toolUtil.storageGet('crossObj')?JSON.parse(context?.$toolUtil.storageGet('crossObj')):{}
			table = context?.$toolUtil.storageGet('crossTable')
			statusColumnName = context?.$toolUtil.storageGet('crossStatusColumnName')
			tips = context?.$toolUtil.storageGet('crossTips')
			statusColumnValue = context?.$toolUtil.storageGet('crossStatusColumnValue')
		}
		init(route.query.id?route.query.id:null, type.value,'', row, table, statusColumnName, tips, statusColumnValue)
	})
	
</script>
<style lang="scss" scoped>
	.form_add_page {
		max-width: 64%;
		margin: 20px auto 60px;
		padding: 0;
		min-height: 100vh;
	}
	.form_add_header {
		margin-bottom: 20px;
		:deep(.breadcrumb) {
			font-size: 14px;
			color: #64748b;
		}
	}
	.form_card {
		background: #fff;
		border-radius: 12px;
		border: 1px solid #e0f2fe;
		box-shadow: 0 2px 12px rgba(2, 132, 199, 0.08);
		padding: 28px 40px 0;
	}
	// 表单
	.add_form{
		border: none;
		border-radius: 0;
		padding: 0;
		background: none;
		// form item
		:deep(.el-form-item) {
			border: 0px solid #eee;
			padding: 6px 0;
			margin: 0 0 20px 0;
			background: none;
			display: flex;
			//label
			.el-form-item__label {
			 background: none;
			 display: block;
			 width: auto;
			 min-width: 150px;
			 text-align: right;
			}
			// 内容盒子
			.el-form-item__content {
				display: flex;
				width: calc(100% - 150px);
				justify-content: flex-start;
				align-items: center;
				flex-wrap: wrap;
				// 输入框
				.list_inp {
					padding: 0 10px;
					background: none;
					width: auto;
					border-color: #ddd;
					border-width: 0 0 1px;
					line-height: 36px;
					box-sizing: border-box;
					border-style: solid;
					min-width: 350px;
					height: 36px;
					//去掉默认样式
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
				//日期选择器
				.list_date {
					border-radius: 0px;
					background: none;
					width: auto;
					border-color: #ddd;
					border-width: 0 0 1px;
					line-height: 36px;
					box-sizing: border-box;
					border-style: solid;
					min-width: 300px;
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
					border-radius: 0px;
					padding: 0 10px;
					background: none;
					width: auto;
					border-color: #ddd;
					border-width: 0 0 1px;
					line-height: 36px;
					box-sizing: border-box;
					border-style: solid;
					min-width: 300px;
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
				// 富文本
				.list_editor {
					border-radius: 0;
					padding: 0;
					margin: 0;
					background: none;
					width: 99%;
					min-height: 250px;
					border-color: #eee;
					border-width: 0;
					border-style: solid;
					height: auto;
				}
				// 长文本
				.el-textarea__inner {
					border: 1px solid #ddd;
					border-radius: 8px;
					padding: 12px;
					color: #666;
					background: none;
					width: 100%;
					font-size: 14px;
					min-height: 120px;
				}
				//图片上传样式
				.el-upload-list  {
					//提示语
					.el-upload__tip {
						margin: 7px 0 0;
						color: #999;
						display: flex;
						font-size: 14px;
						justify-content: flex-start;
						align-items: center;
					}
					//外部盒子
					.el-upload--picture-card {
						border: 1px solid #ddd;
						cursor: pointer;
						border-radius: 8px;
						background: none;
						width: 150px;
						line-height: 90px;
						text-align: center;
						height: 80px;
						//图标
						.el-icon{
							color: #999;
							font-size: 24px;
						}
					}
					.el-upload-list__item {
						border: 1px solid #ddd;
						cursor: pointer;
						border-radius: 8px;
						background: none;
						width: 150px;
						line-height: 90px;
						text-align: center;
						height: 80px;
					}
				}
			}
		}
	}
	.form_actions {
		margin: 24px 0 0;
		padding: 20px 0;
		border-top: 1px solid #e0f2fe;
		display: flex;
		justify-content: center;
		gap: 16px;
		.form_cancel {
			border: 1px solid #0284c7;
			color: #0284c7;
			background: #fff;
			border-radius: 10px;
			padding: 0 32px;
			font-size: 14px;
			line-height: 40px;
			height: 40px;
			transition: all 0.25s ease;
		}
		.form_cancel:hover {
			color: #fff;
			background: #0284c7;
		}
		.form_confirm {
			border: none;
			border-radius: 10px;
			padding: 0 32px;
			color: #fff;
			background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 100%);
			font-size: 14px;
			line-height: 40px;
			height: 40px;
			transition: opacity 0.25s ease;
		}
		.form_confirm:hover {
			opacity: 0.95;
		}
	}
</style>