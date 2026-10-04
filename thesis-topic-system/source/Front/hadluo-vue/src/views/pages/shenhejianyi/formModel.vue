<template>
	<div class="app-contain" :style='{"padding":"20px 0px","margin":"20px auto 40px","alignItems":"flex-start","borderRadius":"20px","flexWrap":"wrap","background":"#fff","display":"flex","width":"64%","position":"relative","justifyContent":"space-between"}'>
		<div class="bread_view">
			<el-breadcrumb separator="/" class="breadcrumb">
				<el-breadcrumb-item class="first_breadcrumb" :to="{ path: '/' }">首页</el-breadcrumb-item>
				<el-breadcrumb-item class="second_breadcrumb" v-for="(item,index) in breadList" :key="index">{{item.name}}</el-breadcrumb-item>
			</el-breadcrumb>
		</div>
		<div class="back_view">
			<el-button class="back_btn" @click="backClick" type="primary">返回</el-button>
		</div>
		<div class="detail_view">
			
			
			<div class="info_view">
				<div class="title_view">
					<div class="detail_title">
						{{detail.ketimingcheng}}
					</div>
				</div>
				<div class="info_item">
					<div class="info_label">题目编号</div>
					<div  class="info_text" >{{detail.timubianhao}}</div>
				</div>
				<div class="info_item">
					<div class="info_label">题目类型</div>
					<div  class="info_text" >{{detail.timuleixing}}</div>
				</div>
				<div class="info_item">
					<div class="info_label">专业</div>
					<div  class="info_text" >{{detail.zhuanye}}</div>
				</div>
				<div class="info_item">
					<div class="info_label">课题性质</div>
					<div  class="info_text" >{{detail.ketixingzhi}}</div>
				</div>
				<div class="info_item">
					<div class="info_label">教师工号</div>
					<div  class="info_text" >{{detail.jiaoshigonghao}}</div>
				</div>
				<div class="info_item">
					<div class="info_label">教师姓名</div>
					<div  class="info_text" >{{detail.jiaoshixingming}}</div>
				</div>
				<div class="info_item">
					<div class="info_label">学号</div>
					<div  class="info_text" >{{detail.xuehao}}</div>
				</div>
				<div class="info_item">
					<div class="info_label">学生姓名</div>
					<div  class="info_text" >{{detail.xueshengxingming}}</div>
				</div>
				<div class="info_item">
					<div class="info_label">审批结果</div>
					<div  class="info_text" >{{detail.shenpijieguo}}</div>
				</div>
				<div class="info_item">
					<div class="info_label">建议内容</div>
					<div  class="info_text" >{{detail.jianyineirong}}</div>
				</div>
				<div class="info_item">
					<div class="info_label">指导意见</div>
					<div  class="info_text" >{{detail.zhidaoyijian}}</div>
				</div>
				<div class="info_item">
					<div class="info_label">审核时间</div>
					<div  class="info_text" >{{detail.shenheshijian}}</div>
				</div>
				<div class="btn_view">
					<el-button class="cross_btn" v-if="btnFrontAuth(tableName,'开题报告')" @click="kaitibaogaoonAcross('开题报告','kaitibaogao','','','')" type="warning">开题报告</el-button>
					<el-button v-if="centerType&&(detail.ispay=='未支付'||!detail.ispay)&&btnFrontAuth('shenhejianyi','支付')" class="approval_btn" @click="payClick">支付</el-button>
					<el-button class="edit_btn" v-if="centerType&&btnAuth('shenhejianyi','修改')" type="primary" @click="editClick">修改</el-button>
					<el-button class="del_btn" v-if="centerType&&btnAuth('shenhejianyi','删除')" type="danger" @click="delClick">删除</el-button>
				</div>
			</div>
		</div>
		<el-tabs type="border-card" v-model="activeName" class="tabs_view">
		</el-tabs>
	</div>
</template>
<script setup>
	import { downloadStoredFile } from '@/utils/fileDownload'
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
		ElMessageBox
	} from 'element-plus'
	import {
		useRoute,
		useRouter
	} from 'vue-router';
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const route = useRoute()
	const router = useRouter()
	//基础信息
	const tableName = 'shenhejianyi'
	const formName = '审核建议'
	//基础信息
	const breadList = ref([{
		name: formName
	}])
	//权限验证
	const btnAuth = (e,a)=>{
		if(centerType.value){
			return context?.$toolUtil.isBackAuth(e,a)
		}else{
			return context?.$toolUtil.isAuth(e,a)
		}
	}
	//查看权限验证
	const btnFrontAuth = (e,a)=>{
		if(centerType.value){
			return context?.$toolUtil.isBackAuth(e,a)
		}else{
			return context?.$toolUtil.isFrontAuth(e,a)
		}
	}
	// 返回
	const backClick = () =>{
		history.back()
	}
	
	// 详情
	const title = ref('')
	const detail = ref({})
    const activeName = ref('first')
	const getDetail = () => {
		context?.$http({
			url: `${tableName}/detail/${route.query.id}`,
			method: 'get'
		}).then(res => {
			title.value = res.data.data.ketimingcheng
			detail.value = res.data.data
		})
	}
	// 下载文件
	const downClick = (file) => {
		if (!file) {
			context?.$toolUtil.message('文件不存在', 'error')
			return
		}
		downloadStoredFile(file).catch(err => {
			context?.$toolUtil.message(err?.message || '下载失败', 'error')
		})
	}
	// 判断是否从个人中心跳转
	const centerType = ref(false)
	const init = () => {
		if(route.query.centerType){
			centerType.value = true
		}
		getDetail()
	}
	//修改
	const editClick = () => {
		router.push(`/index/${tableName}Add?id=${detail.value.id}&&type=edit`)
	}
	//删除
	const delClick = () => {
		ElMessageBox.confirm(`是否删除此${formName}？`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(()=>{
			context?.$http({
				url: `${tableName}/delete`,
				method: 'post',
				data: [detail.value.id]
			}).then(res=>{
				context?.$toolUtil.message('删除成功','success',()=>{
					history.back()
				})
			})
			
		})
	}
	const kaitibaogaoonAcross = (btnType,table,crossOptAudit,statusColumnName,tips,statusColumnValue) => {
		if(!context?.$toolUtil.storageGet('frontToken')){
			context?.$toolUtil.message('请登录后再操作！','error')
			return false
		}
		if(!btnAuth(tableName,btnType)){
			context?.$toolUtil.message('暂无权限操作！','error')
			return false
		}
		context?.$toolUtil.storageSet('crossObj',JSON.stringify(detail.value))
		context?.$toolUtil.storageSet('crossTable',tableName)
		context?.$toolUtil.storageSet('crossStatusColumnName',statusColumnName)
		context?.$toolUtil.storageSet('crossTips',tips)
		context?.$toolUtil.storageSet('crossStatusColumnValue',statusColumnValue)
		if(statusColumnName!=''&&!statusColumnName.startsWith("[")) {
			var obj = detail.value
			for (var o in obj){
				if(o==statusColumnName && obj[o]==statusColumnValue){
					context?.$toolUtil.message(tips,'error')
					return;
				}
			}
		}
		nextTick(()=>{
			router.push(`/index/${table}Add?type=cross&id=${detail.value.id}`)
		})
	}
	onMounted(()=>{
		init()
	})
</script>
<style lang="scss" scoped>
	// 返回盒子
	.back_view {
		border-radius: 0px;
		padding: 0 20px;
		margin: 20px auto;
		background: none;
		display: block;
		width: 100%;
		text-align: right;
		// 返回按钮
		.back_btn {
			border: 1px solid #eee;
			cursor: pointer;
			border-radius: 4px;
			padding: 0 30px;
			outline: none;
			color: #666;
			background: #f9f9f9;
			width: auto;
			font-size: 14px;
			height: 32px;
		}
		// 返回按钮-悬浮
		.back_btn:hover {
		}
	}
	// 面包屑盒子
	.bread_view {
		border-radius: 0px;
		padding: 10px 20px;
		margin: 0px auto;
		background: none;
		width: 100%;
		border-color: #eee;
		border-width: 0 0 1px;
		position: relative;
		border-style: solid;
		:deep(.breadcrumb) {
			font-size: 14px;
			line-height: 1;
			.el-breadcrumb__separator {
				margin: 0 9px;
				color: #999;
				font-weight: 500;
			}
			.first_breadcrumb {
				.el-breadcrumb__inner {
					color: #999;
					display: inline-block;
				}
			}
			.second_breadcrumb {
				.el-breadcrumb__inner {
					color: #999;
					display: inline-block;
				}
			}
		}
	}
	
	.detail_view{
		border-radius: 0;
		padding: 20px 0;
		background: linear-gradient(180deg, rgba(255,255,255,1) 0%, rgba(242,254,255,1) 100%);
		display: flex;
		width: 100%;
		border-color: #27bacc30;
		border-width: 1px;
		justify-content: space-between;
		position: relative;
		border-style: solid;
		flex-wrap: wrap;
		
		
		// 文字区
		.info_view {
			border: 0px solid #eee;
			padding: 0px 7%;
			margin: 0 0 20px;
			background: none;
			width: 100%;
			box-sizing: border-box;
			float: right;
		
			.title_view {
				border: 0px solid #eee;
				padding: 0;
				margin: 0 0 12px;
				background: none;
				display: flex;
				width: 100%;
				line-height: 40px;
				justify-content: center;
				align-items: center;
		
				.detail_title {
					color: #666;
					font-weight: 600;
					font-size: 18px;
				}
				.follow {
					border: 0px solid #ffffff50;
					cursor: pointer;
					padding: 4px 10px;
					color: #fff;
					display: flex;
					line-height: 1;
					right: 20px;
					border-radius: 4px;
					background: none;
					width: auto;
					justify-content: center;
					align-items: center;
					position: absolute;
					.iconfont {
						margin: 0 4px 0 0;
						color: #999;
						font-size: 24px;
					}
					.iconfontActive {
						margin: 0 4px 0 0;
						color: #f79d1c;
						font-size: 24px;
					}
					span {
						color: #999;
						font-size: 16px;
					}
					.textActive {
						color: #f79d1c;
						font-size: 16px;
					}
				}
				.follow:hover {
				}
				.follow:active {
					transform: scale(0.9);
				}
			}
		
			.info_item {
					border-radius: 0px;
					padding: 0;
					margin: 0 0 10px;
					background: none;
					display: flex;
					border-color: #eee;
					border-width: 0px;
					align-items: center;
					border-style: solid;
		
				.info_label {
					margin: 0 12px 0 0;
					color: #666;
					font-weight: 500;
					width: auto;
				}
				.info_text {
				}
			}
			.btn_view {
				padding: 0;
				margin: 20px 0 20px;
				display: flex;
				flex-wrap: wrap;
				// 跨表-按钮
				.cross_btn {
					border: 1px solid rgba(170, 0, 0, 1.0);
					padding: 0 10px;
					color: rgba(170, 0, 0, 1.0);
					background: none;
					line-height: 32px;
					height: 32px;
				}
				// 悬浮
				.cross_btn:hover {
				}
				// 修改-按钮
				.edit_btn {
					border: none;
					padding: 0 10px;
					color: #fff;
					background: #27bacc;
					line-height: 32px;
					height: 32px;
				}
				// 悬浮
				.edit_btn:hover {
				}
				// 删除-按钮
				.del_btn {
					border: none;
					padding: 0 10px;
					color: #fff;
					background: #f68b8b;
					line-height: 32px;
					height: 32px;
				}
				// 悬浮
				.del_btn:hover {
				}
			}
		}
	}
	

	//底部盒子
	.tabs_view {
		border: 0px solid #ddd;
		border-radius: 0px;
		padding: 0;
		box-shadow: none;
		margin: 20px auto;
		background: none;
		width: 100%;
		:deep(.el-tabs__header) {
			background: transparent;
			border: none;
		}
		// 头部
		:deep(.el-tabs__nav-scroll) {
			border-radius: 0;
			padding: 2px 20px 0;
			margin: 0;
			background: linear-gradient(180deg, rgba(255,255,255,1) 0%, rgba(243,243,243,1) 100%);
			display: flex;
			border-color: #ddd;
			border-width: 1px 1px 4px;
			justify-content: center;
			border-style: solid;
			height: 50px;
			.el-tabs__nav {
				.el-tabs__item {
					border: 0;
					padding: 0 30px;
					margin: 0 10px;
					color: #333;
					font-weight: 500;
					display: inline-block;
					font-size: 14px;
					line-height: 50px;
					background: none;
					position: relative;
					list-style: none;
					text-align: center;
					min-width: 60px;
					height: 50px;
				}
				.el-tabs__item:hover {
					border: 0;
					border-radius: 8px 8px 0 0;
					color: #fff;
					background: #27bacc;
				}
				.is-active {
					border: 0;
					border-radius: 8px 8px 0 0;
					padding: 0 30px;
					color: #fff;
					background: #27bacc;
					text-align: center;
					min-width: 60px;
				}
			}
		}
		// 内容区
		:deep(.el-tabs__content) {
			border-radius: 0 0 4px 4px;
			padding: 20px 0;
			color: #666;
			background: none;
			font-size: 14px;
			border-color: #eee;
			border-width: 0;
			border-style: solid;
		}
	}
	


</style>