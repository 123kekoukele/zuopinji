<template>
	<div class="center_page app-contain">
		<header class="center_header">
			<el-breadcrumb separator="/" class="center_breadcrumb">
				<el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
				<el-breadcrumb-item>个人中心</el-breadcrumb-item>
			</el-breadcrumb>
			<h1 class="center_title">{{ formName }}</h1>
		</header>
		<div class="usersView">
			<div class="usersTabView">
				<div class="usersTab" :class="tabIndex=='center'?'usersTabActive':''" @click="tabClick({tableName:'center'})">个人中心</div>
				<div class="usersTab " :class="tabIndex=='updatepassword'?'usersTabActive':''" @click="tabClick({tableName:'updatepassword'})">修改密码</div>
				<div v-for="(item, index) in menuList" :key="item.menu || index" class="usersTab" @mouseenter="usersTabHover(index)"
					@mouseleave="usersTabLeave">
					{{ item.menu }}
					<el-collapse-transition>
						<div class="usersTabHoverView" v-if="usersTabIndex === index">
							<div class="usersTabHoverTab" v-for="sub in (item.child || [])" :key="sub.menu" @click="tabClick(sub)">
								{{ sub.menu }}
							</div>
						</div>
					</el-collapse-transition>
				</div>
			</div>
			<div class="usersBox" v-if="tabIndex=='center'">
				<el-form class="usersForm" ref="userFormRef" :model="userForm" label-width="120px" :rules="rules">
					<el-row>
						<el-col :span="12">
							<el-form-item prop="jiaoshigonghao" label="教师工号">
								<el-input class="list_inp" v-model="userForm.jiaoshigonghao" placeholder="教师工号" readonly></el-input>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item prop="mima" label="密码">
								<el-input class="list_inp" v-model="userForm.mima" placeholder="密码" type="password"></el-input>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item prop="jiaoshixingming" label="教师姓名">
								<el-input class="list_inp" v-model="userForm.jiaoshixingming" placeholder="教师姓名" ></el-input>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item prop="lianxidianhua" label="联系电话">
								<el-input class="list_inp" v-model="userForm.lianxidianhua" placeholder="联系电话" ></el-input>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="性别" prop="xingbie">
								<el-select
									class="list_sel"
									v-model="userForm.xingbie" 
									placeholder="请选择性别"
									style="width:100%;"
									>
									<el-option v-for="item in xingbieLists" :key="item" :label="item" :value="item" />
								</el-select>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="专业" prop="zhuanye">
								<el-select
									class="list_sel"
									v-model="userForm.zhuanye"
									placeholder="请选择专业"
									style="width:100%;"
								>
									<el-option v-for="item in zhuanyeLists" :key="item" :label="item" :value="item" />
								</el-select>
							</el-form-item>
						</el-col>
					</el-row>
					<div class="formModel_btn_box">
						<el-button class="formModel_confirm" @click="updateSession">更新信息</el-button>
						<el-button class="formModel_cancel" @click="loginout" type="danger">退出登录</el-button>
					</div>
				</el-form>
			</div>
			<div class="usersBox" v-if="tabIndex=='updatepassword'">
				<el-form class="usersForm" ref="passwordFormRef" :model="passwordForm" label-width="120px"
					:rules="passwordRules">
					<el-row>
						<el-col :span="12">
							<el-form-item label="原密码" prop="mima">
								<el-input class="list_inp" v-model="passwordForm.mima" placeholder="原密码"
									type="password"></el-input>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="新密码" prop="newmima">
								<el-input class="list_inp" v-model="passwordForm.newmima" placeholder="新密码"
									type="password"></el-input>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item label="确认密码" prop="newmima2">
								<el-input class="list_inp" v-model="passwordForm.newmima2" placeholder="确认密码"
									type="password"></el-input>
							</el-form-item>
						</el-col>
					</el-row>
					<div class="formModel_btn_box">
						<el-button class="formModel_confirm" @click="updatePassword">修改密码</el-button>
					</div>
				</el-form>
			</div>
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
	} from 'vue';
	import {
		useRoute,
		useRouter
	} from 'vue-router';
	import menu from '@/utils/menu'
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const route = useRoute()
	const router = useRouter()
	//基础信息
	const tableName = 'jiaoshi'
	const formName = '个人中心'
	//基础信息
	//个人信息
	const userFormRef = ref(null)
	const userForm = ref({})
	//修改密码
	const passwordFormRef = ref(null)
	const passwordForm = ref({
		mima: '',
		newmima: '',
		newmima2: ''
	})
	const passwordRules = ref({
		mima: [{
			required: true,
			message: '请输入',
			trigger: 'blur'
		}, ],
		newmima: [{
			required: true,
			message: '请输入',
			trigger: 'blur'
		}, ],
		newmima2: [{
			required: true,
			message: '请输入',
			trigger: 'blur'
		}, ],
	})
	//验证规则
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
		jiaoshigonghao: [
			{required: true,message: '请输入',trigger: 'blur'}, 
		],
		mima: [
			{required: true,message: '请输入',trigger: 'blur'}, 
		],
		jiaoshixingming: [
			{required: true,message: '请输入',trigger: 'blur'}, 
		],
		lianxidianhua: [
			{ validator: validateMobile, trigger: 'blur' },
		],
		xingbie: [
		],
		zhuanye: [
			{ required: true, message: '请选择专业', trigger: 'change' },
		],
	})
	const getSession = () =>{
		context?.$http({
			url: `${context?.$toolUtil.storageGet('frontSessionTable')}/session`,
			method:'get'
		}).then(res=>{
			context?.$toolUtil.storageSet('userid',res.data.data.id)
			context?.$toolUtil.storageSet("frontName", res.data.data.jiaoshigonghao)
			userForm.value = res.data.data
		})
	}
	//菜单跳转
	const tabIndex = ref('center')
	const tabClick = (item) => {
		if (item.tableName == 'center') {
			tabIndex.value = 'center'
			return false
		}
		if (item.tableName == 'updatepassword') {
			passwordForm.value = {
				mima: '',
				newmima: '',
				newmima2: ''
			}
			tabIndex.value = 'updatepassword'
			return false
		}
		if(item.tableName=='examrecord'&&item.menuJump=='22'){
			router.push(`/index/examfailrecord?centerType=1`)
			return false
		}
		if(item.tableName=='forum'&&item.menuJump=='14'){
			router.push(`/index/forumList?centerType=1&&myType=1`)
			return false
		}
		switch (item.menu) {
			default:
				router.push(`/index/${item.tableName}List?centerType=1`)
		}
	}
	// 修改密码
	const updatePassword = async ()=>{
		passwordFormRef.value.validate(async (valid) => {
			if (valid) {
				if(passwordForm.value.mima != userForm.value.mima){
					context?.$toolUtil.message('原密码不正确', 'error')
					return false
				}
				if(passwordForm.value.newmima != passwordForm.value.newmima2){
					context?.$toolUtil.message('两次密码输入不正确', 'error')
					return false
				}
				userForm.value.mima = passwordForm.value.newmima
				context?.$http({
					url: `${tableName}/update`,
					method: 'post',
					data: userForm.value
				}).then(res => {
					context?.$toolUtil.message('更新成功', 'success', () => {
						passwordForm.value = {
							mima: '',
							newmima: '',
							newmima2: ''
						}
						getSession()
					})
				})
			}
		})
	}
	//菜单
	const menuList = ref([])
	const role = ref('')
	//性别列表
	const xingbieLists = ref([])
	const zhuanyeLists = ref([])
	//初始化
	const init = () => {
		const menus = menu.list()
		let arr = []
		let brr = []
		if (menus) {
			menuList.value = menus
		}
		role.value = context?.$toolUtil.storageGet('frontRole')
		for (let i = 0; i < menuList.value.length; i++) {
			if (menuList.value[i].roleName == role.value) {
				arr = menuList.value[i].backMenu
				break;
			}
		}
		for(let x in arr){
			if(arr[x].child){
				if(arr[x].child[0].tableName == 'orders'){
					brr = JSON.parse(JSON.stringify(arr[x].child[0]))
					arr[x].child = [brr]
				}
			}
		}
		menuList.value = arr
		xingbieLists.value = "男,女".split(',')
		zhuanyeLists.value = "地理信息科学,地理科学,风景园林,测绘工程,城乡规划".split(',')
		getSession()
	}
	//菜单悬浮的显示与隐藏
	const usersTabIndex = ref(-1)
	const usersTabHover = (index) => {
		usersTabIndex.value = index
	}
	const usersTabLeave = () => {
		usersTabIndex.value = -1
	}
	//富文本
	const editorChange = (e,name) =>{
		userForm.value[name] = e
	}
	//保存
	const updateSession = () => {
		userFormRef.value.validate((valid)=>{
			if(valid){
				context?.$http({
					url: `${tableName}/update`,
					method: 'post',
					data: userForm.value
				}).then(res=>{
					context?.$toolUtil.message('更新成功','success',()=>{
						getSession()
					})
				})
			}
		})
	}
	//退出登录
	const loginout = () => {
		context?.$toolUtil.message('退出成功', 'success')
		context?.$toolUtil.storageClear()
		router.replace('/index/home')
	}
	init()
</script>

<style lang="scss" scoped>
	.center_page {
		width: 64%;
		max-width: 900px;
		margin: 0 auto 30px;
		padding: 0;
		background: #fff;
		border-radius: 12px;
		border: 1px solid #e0f2fe;
		box-shadow: 0 2px 12px rgba(2, 132, 199, 0.08);
		overflow: hidden;
	}
	.center_header {
		padding: 20px 24px;
		border-bottom: 2px solid #0284c7;
		.center_breadcrumb { font-size: 14px; color: #64748b; }
		.center_title {
			margin: 12px 0 0;
			font-size: 20px;
			font-weight: 600;
			color: #0c4a6e;
		}
	}
	.usersView {
		padding: 24px;
		display: flex;
		flex-wrap: wrap;
		align-items: flex-start;
		background: linear-gradient(180deg, #fff 0%, #f0f9ff 100%);
		min-height: 320px;

		.usersTabView {
			width: 220px;
			flex-shrink: 0;
			background: #fff;
			border: 1px solid #e0f2fe;
			border-radius: 10px;
			padding: 12px;
			margin-right: 24px;

			.usersTab {
				cursor: pointer;
				padding: 12px 14px;
				margin: 0 0 4px;
				color: #0c4a6e;
				font-size: 14px;
				border-radius: 8px;
				background: none;
				width: 100%;
				position: relative;
				text-align: left;
				transition: all 0.25s ease;
				border: none;

				.usersTabHoverView {
					position: absolute;
					left: 100%;
					top: 0;
					margin-left: 4px;
					border: 1px solid #e0f2fe;
					background: #fff;
					border-radius: 8px;
					box-shadow: 0 4px 12px rgba(2, 132, 199, 0.12);
					padding: 8px;
					min-width: 120px;
					z-index: 10;
					.usersTabHoverTab {
						padding: 8px 12px;
						border-radius: 6px;
						color: #0c4a6e;
						font-size: 13px;
						cursor: pointer;
					}
					.usersTabHoverTab:hover {
						color: #fff;
						background: #0284c7;
					}
				}
			}
			.usersTab:hover {
				color: #fff;
				background: #0284c7;
			}
			.usersTabActive {
				color: #fff;
				background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 100%);
			}
		}

		.usersBox {
			flex: 1;
			min-width: 0;
			background: #fff;
			border: 1px solid #e0f2fe;
			border-radius: 10px;
			padding: 28px 32px;
			box-shadow: 0 2px 8px rgba(2, 132, 199, 0.06);
			.usersForm {
				border: 0px solid #eee;
				border-radius: 0px;
				padding: 0;
				background: none;
				// form item
				:deep(.el-form-item) {
					border: 0px solid #eee;
					border-radius: 4px;
					padding: 6px 0;
					margin: 0 2% 20px 0;
					background: none;
					display: flex;
					width: 100%;
					flex-wrap: wrap;
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
							padding: 0 12px;
							background: #fff;
							width: auto;
							border: 1px solid #e0f2fe;
							border-radius: 8px;
							line-height: 36px;
							box-sizing: border-box;
							min-width: 100%;
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
						.list_sel {
							border-radius: 8px;
							padding: 0 12px;
							background: #fff;
							width: auto;
							border: 1px solid #e0f2fe;
							line-height: 36px;
							box-sizing: border-box;
							min-width: 100%;
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
								color: #999;
								display: flex;
								font-size: 14px;
								justify-content: flex-start;
								align-items: center;
							}
							//外部盒子
							.el-upload--picture-card {
								border: 1px dashed #0284c7;
								background: #f0f9ff;
								border-radius: 10px;
								width: 100px;
								height: 100px;
								line-height: 100px;
								.el-icon { color: #0284c7; font-size: 28px; }
							}
							.el-upload-list__item {
								border: 1px solid #e0f2fe;
								border-radius: 10px;
								width: 100px;
								height: 100px;
							}
						}
					}
				}
			}
		}
		.formModel_btn_box {
			margin-top: 24px;
			padding-top: 20px;
			border-top: 1px solid #e0f2fe;
			display: flex;
			gap: 12px;
			flex-wrap: wrap;
			.formModel_cancel {
				border: 1px solid #dc2626;
				color: #dc2626;
				background: #fff;
				border-radius: 10px;
				padding: 0 24px;
				font-size: 14px;
				height: 40px;
				transition: all 0.25s ease;
			}
			.formModel_cancel:hover {
				background: #dc2626;
				color: #fff;
			}
			.formModel_confirm {
				border: none;
				border-radius: 10px;
				padding: 0 24px;
				color: #fff;
				background: linear-gradient(135deg, #0ea5e9 0%, #0284c7 100%);
				font-size: 14px;
				height: 40px;
				transition: opacity 0.25s ease;
			}
			.formModel_confirm:hover {
				opacity: 0.95;
				box-shadow: 0 4px 12px rgba(2, 132, 199, 0.3);
			}
		}
	}
</style>