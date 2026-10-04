<template>
	<div>
		<el-dialog v-model="formVisible" :title="formTitle" width="80%" destroy-on-close :fullscreen='false'>
			<el-form class="formModel_form" ref="formRef" :model="form" label-width="$template2.back.add.form.base.labelWidth" :rules="rules">
				<el-row>
					<el-col :span="24">
						<el-form-item label="题目编号" prop="timubianhao">
							<el-input class="list_inp" v-model="form.timubianhao" placeholder="题目编号"
								 type="text" 								:readonly="!isAdd||disabledForm.timubianhao?true:false" />
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
							<el-input class="list_inp" v-model="form.timuleixing" placeholder="题目类型"
								 type="text" 								:readonly="!isAdd||disabledForm.timuleixing?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="专业" prop="zhuanye">
							<el-input class="list_inp" v-model="form.zhuanye" placeholder="专业"
								 type="text" 								:readonly="!isAdd||disabledForm.zhuanye?true:false" />
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
						<el-form-item label="学号" prop="xuehao">
							<el-input class="list_inp" v-model="form.xuehao" placeholder="学号"
								 type="text" 								:readonly="!isAdd||disabledForm.xuehao?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="学生姓名" prop="xueshengxingming">
							<el-input class="list_inp" v-model="form.xueshengxingming" placeholder="学生姓名"
								 type="text" 								:readonly="!isAdd||disabledForm.xueshengxingming?true:false" />
						</el-form-item>
					</el-col>

					<el-col :span="24">
						<el-form-item label="初稿附件" prop="chugaofujian">
							<div class="file_readonly_box">
								<template v-if="form.chugaofujian">
									<span class="file_name">{{ chugaofujianFileName }}</span>
									<el-button type="primary" link @click="downloadChugaofujian">下载</el-button>
								</template>
								<span v-else class="file_empty">暂无文件（由学生上传）</span>
							</div>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="初稿时间" prop="chugaoshijian">
							<el-date-picker
								class="list_date"
								v-model="form.chugaoshijian"
								format="YYYY-MM-DD HH:mm:ss"
								value-format="YYYY-MM-DD HH:mm:ss"
								type="datetime"
								:readonly="!isAdd||disabledForm.chugaoshijian?true:false"
								placeholder="请选择初稿时间" />
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="审核状态" prop="shenhezhuangtai">
							<el-select
								v-model="form.shenhezhuangtai"
								placeholder="请选择审核状态"
								:disabled="!isAdd||disabledForm.shenhezhuangtai?true:false"
								style="width: 100%"
							>
								<el-option v-for="item in shenhezhuangtaiLists" :key="item" :label="item" :value="item" />
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="24" v-if="showAuditReasonField(form.shenhezhuangtai)">
						<el-form-item :label="getAuditReasonLabel(form.shenhezhuangtai)" prop="shenheyuanyin">
							<el-input
								v-model="form.shenheyuanyin"
								type="textarea"
								:rows="3"
								:placeholder="getAuditReasonLabel(form.shenhezhuangtai)"
								:readonly="!isAdd||disabledForm.shenheyuanyin?true:false"
							/>
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
						<el-form-item label="初稿简介" prop="chugaojianjie">
							<el-input v-model="form.chugaojianjie" placeholder="初稿简介" type="textarea"
							:readonly="!isAdd||disabledForm.chugaojianjie?true:false"
							/>
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
	import { downloadStoredFile, normalizeFileName } from '@/utils/fileDownload'
	import { AUDIT_STATUS_OPTIONS, showAuditReasonField, getAuditReasonLabel } from '@/utils/workflowAuditStatus'
	//基础信息
	const tableName = 'lunwenchugao'
	const formName = '论文初稿'
	//基础信息
	//form表单
	const form = ref({})
	const disabledForm = ref({
		timubianhao : false,
		ketimingcheng : false,
		timuleixing : false,
		zhuanye : false,
		ketixingzhi : false,
		jiaoshigonghao : false,
		jiaoshixingming : false,
		xuehao : false,
		xueshengxingming : false,
		chugaojianjie : false,
		chugaofujian : false,
		chugaoshijian : false,
		shenhezhuangtai : false,
		shenheyuanyin : false,
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
		],
		ketixingzhi: [
		],
		jiaoshigonghao: [
		],
		jiaoshixingming: [
		],
		xuehao: [
		],
		xueshengxingming: [
		],
		chugaojianjie: [
		],
		chugaofujian: [
		],
		chugaoshijian: [
		],
		shenhezhuangtai: [
		],
		shenheyuanyin: [
		],
	})
	//表单验证
	
	const formRef = ref(null)
	const id = ref(0)
	const type = ref('')
	const shenhezhuangtaiLists = ref(AUDIT_STATUS_OPTIONS)
	const chugaofujianFileName = computed(() => {
		const path = form.value?.chugaofujian || ''
		return path ? normalizeFileName(path) : ''
	})
	const downloadChugaofujian = () => {
		if (!form.value?.chugaofujian) {
			context?.$toolUtil.message('文件不存在', 'error')
			return
		}
		downloadStoredFile(form.value.chugaofujian).catch(err => {
			context?.$toolUtil.message(err?.message || '下载失败', 'error')
		})
	}

	//获取唯一标识
	const getUUID =()=> {
      return new Date().getTime();
    }
	//重置
	const resetForm = () => {
		form.value = {
			timubianhao: '',
			ketimingcheng: '',
			timuleixing: '',
			zhuanye: '',
			ketixingzhi: '',
			jiaoshigonghao: '',
			jiaoshixingming: '',
			xuehao: '',
			xueshengxingming: '',
			chugaojianjie: '',
			chugaofujian: '',
			chugaoshijian: '',
			shenhezhuangtai: '未审核',
			shenheyuanyin: '',
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
			form.value.chugaoshijian = context?.$toolUtil.getCurDateTime()
		if(formId){
			id.value = formId
			type.value = formType
		}
		if(formType == 'add'){
			isAdd.value = true
			formTitle.value = '新增' + formName
			form.value.shenhezhuangtai = '未审核'
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
				if(x=='ketixingzhi'){
					form.value.ketixingzhi = row[x];
					disabledForm.value.ketixingzhi = true;
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
				if(x=='xuehao'){
					form.value.xuehao = row[x];
					disabledForm.value.xuehao = true;
					continue;
				}
				if(x=='xueshengxingming'){
					form.value.xueshengxingming = row[x];
					disabledForm.value.xueshengxingming = true;
					continue;
				}
				if(x=='chugaojianjie'){
					form.value.chugaojianjie = row[x];
					disabledForm.value.chugaojianjie = true;
					continue;
				}
				if(x=='chugaofujian'){
					form.value.chugaofujian = row[x];
					disabledForm.value.chugaofujian = true;
					continue;
				}
				if(x=='chugaoshijian'){
					form.value.chugaoshijian = row[x];
					disabledForm.value.chugaoshijian = true;
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
			var json = res.data.data
			if(json.hasOwnProperty('xuehao')&& context?.$toolUtil.storageGet("role")!="管理员"){
				form.value.xuehao = json.xuehao
				disabledForm.value.xuehao = true;
			}
			if(json.hasOwnProperty('xueshengxingming')&& context?.$toolUtil.storageGet("role")!="管理员"){
				form.value.xueshengxingming = json.xueshengxingming
				disabledForm.value.xueshengxingming = true;
			}
		})
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
		if(form.value.chugaofujian!=null) {
			form.value.chugaofujian = form.value.chugaofujian.replace(new RegExp(context?.$config.url,"g"),"");
		}
		if(!form.value.shenhezhuangtai){
			form.value.shenhezhuangtai='未审核'
		}
		if (!showAuditReasonField(form.value.shenhezhuangtai)) {
			form.value.shenheyuanyin = ''
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
	// 表单
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
					border: 1px solid #ddd;
					padding: 0 10px;
					width: 300px;
					line-height: 36px;
					box-sizing: border-box;
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
					border: 1px solid #ddd;
					border-radius: 0;
					width: 400px;
					line-height: 36px;
					box-sizing: border-box;
					//去掉默认样式
					.el-input__wrapper{
						border: none;
						box-shadow: none;
						background: none;
						border-radius: 0;
						height: 100%;
					}
				}
				// 长文本
				.el-textarea__inner {
					border: 1px solid #ddd;
					border-radius: 0;
					padding: 12px;
					outline: none;
					color: #333;
					width: 400px;
					font-size: 14px;
					min-height: 120px;
				}
				.file_readonly_box {
					display: flex;
					align-items: center;
					gap: 12px;
					min-height: 36px;
				}
				.file_name {
					color: #333;
					font-size: 14px;
				}
				.file_empty {
					color: #999;
					font-size: 14px;
				}
				//文件上传样式
				.upload-demo {
					width: 100%;
					//外部盒子
					.el-upload-dragger {
						border: 1px dashed rgba(75,137,243,1);
						cursor: pointer;
						background-color: #fff;
						border-radius: 6px;
						overflow: hidden;
						flex-direction: column;
						display: flex;
						width: 360px;
						align-items: center;
						box-sizing: border-box;
						text-align: center;
						height: 120px;
					}
					//图标
					.el-icon--upload {
						color: rgba(75,137,243,1);
						font-size: 67px;
						line-height: 0;
					}
					//提示文字
					.el-upload__text {
						margin: 7px 0 0;
						color: #999;
						font-size: 12px;
						em {
							color: #409EFF;
						}
					}
					//提示文字
					.el-upload__tip {
						margin: 7px 0 0;
						color: #999;
						font-size: 12px;
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
			border: 0;
			cursor: pointer;
			border-radius: 4px;
			padding: 0 24px;
			box-shadow: inset 0 0 10px 0 rgba(0, 0, 0, 0.30);
			margin: 0 10px 0 0;
			outline: none;
			color: #333;
			background: linear-gradient(0deg, rgba(255,255,255,1) 0%, rgba(227,225,224,1) 100%, rgba(255,255,255,1) 100%);
			width: auto;
			font-size: 14px;
			height: 32px;
		}
		.formModel_cancel:hover {
			background: linear-gradient(180deg, rgba(255,255,255,1) 0%, rgba(227,225,224,1) 100%, rgba(255,255,255,1) 100%);
		}
		
		.formModel_confirm {
			border: 0;
			cursor: pointer;
			border-radius: 4px;
			padding: 0 24px;
			box-shadow: inset 0 4px 10px 0 rgba(0,0,0,.3);
			margin: 0 10px 0 0;
			outline: none;
			color: #fff;
			background: linear-gradient(0deg, rgba(67,125,205,1) 0%, rgba(30,103,183,1) 100%, rgba(227,225,224,1) 100%);
			width: auto;
			font-size: 14px;
			height: 32px;
		}
		.formModel_confirm:hover {
			background: linear-gradient(180deg, rgba(67,125,205,1) 0%, rgba(30,103,183,1) 100%, rgba(227,225,224,1) 100%);
		}
	}
</style>