
<template>
	<div class="admin-users-page">
		<div class="app-contain admin-users-content fade-in-up">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form">
					<div class="search_view">
						<div class="search_label">用户名：</div>
						<div class="search_box">
							<el-input
								class="search_inp"
								v-model="searchQuery.username"
								placeholder="请输入专业管理员用户名"
								clearable
							/>
						</div>
					</div>
					<div class="search_btn_view">
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">
							搜索
						</el-button>
						<el-button class="search_btn reset" @click="resetSearch" size="small">
							重置
						</el-button>
					</div>
				</el-form>
				<div class="btn_view">
					<el-button type="success" @click="addClick" v-if="btnAuth('users','新增')">
						新增专业管理员
					</el-button>
					<el-button
						type="primary"
						:disabled="selRows.length!==1"
						@click="editClick"
						v-if="btnAuth('users','修改')"
					>
						编辑信息
					</el-button>
					<el-button
						type="danger"
						:disabled="!selRows.length"
						@click="delClick(null)"
						v-if="btnAuth('users','删除')"
					>
						批量删除
					</el-button>
				</div>
			</div>

			<div class="table-panel fade-in-up">
				<div class="table-card-header">
					<div class="table-title">专业管理员列表</div>
					<div class="table-sub">勾选后可进行批量删除，支持单条编辑</div>
				</div>

				<el-table
					class="users-table"
					v-loading="listLoading"
					border
					:stripe="false"
					@selection-change="handleSelectionChange"
					ref="table"
					v-if="btnAuth('users','查看')"
					:data="list"
					@row-click="listChange"
				>
					<el-table-column
						:resizable="true"
						align="center"
						header-align="center"
						type="selection"
						width="55"
					/>
					<el-table-column
						label="序号"
						width="70"
						:resizable="true"
						:sortable="false"
						align="center"
						header-align="center"
					>
						<template #default="scope">{{ scope.$index + 1 }}</template>
					</el-table-column>
					<el-table-column
						:resizable="true"
						:sortable="false"
						align="left"
						header-align="left"
						label="用户名"
					>
						<template #default="scope">
							<span class="username-cell">{{ scope.row.username }}</span>
						</template>
					</el-table-column>
					<el-table-column
						:resizable="true"
						:sortable="false"
						align="left"
						header-align="left"
						label="角色"
						width="160"
					>
						<template #default="scope">
							<el-tag type="info" effect="light">
								专业管理员
							</el-tag>
						</template>
					</el-table-column>
					<el-table-column
						:resizable="true"
						:sortable="false"
						align="left"
						header-align="left"
						label="专业"
						width="140"
					>
						<template #default="scope">
							{{ scope.row.zhuanye }}
						</template>
					</el-table-column>
					<el-table-column
						label="操作"
						width="200"
						:resizable="true"
						:sortable="false"
						align="center"
						header-align="center"
					>
						<template #default="scope">
							<el-button
								type="primary"
								link
								size="small"
								@click.stop="editClickRow(scope.row)"
								v-if="btnAuth('users','修改')"
							>
								编辑
							</el-button>
							<el-button
								type="danger"
								link
								size="small"
								@click.stop="delClick(scope.row.id)"
								v-if="btnAuth('users','删除')"
							>
								删除
							</el-button>
						</template>
					</el-table-column>
				</el-table>

				<el-pagination
					class="users-pagination"
					background
					:layout="layouts.join(',')"
					:total="total"
					:page-size="listQuery.limit"
					prev-text="上一页"
					next-text="下一页"
					:hide-on-single-page="true"
					@size-change="sizeChange"
					@current-change="currentChange"
					@prev-click="prevClick"
					@next-click="nextClick"
				/>
			</div>
		</div>

		<formModel ref="formRef" @formModelChange="formModelChange"></formModel>
	</div>
</template>
<script setup>
	import axios from 'axios'
	import {
		reactive,
		ref,
		getCurrentInstance,
		nextTick,
		onMounted,
		watch,
	} from 'vue'
	import {
		useRoute,
		useRouter
	} from 'vue-router'
	import {
		ElMessageBox
	} from 'element-plus'
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	import formModel from './formModel.vue'
	
	//基础信息
	const tableName = 'users'
	const formName = '专业管理员'
	const route = useRoute()
	//基础信息
	onMounted(()=>{
	})
	//列表数据
	const list = ref(null)
	const table = ref(null)
	const listQuery = ref({
		page: 1,
		limit: 20,
		sort: 'id',
		order: 'desc'
	})
	const searchQuery = ref({})
	const selRows = ref([])
	const listLoading = ref(false)
	const listChange = (row) =>{
		nextTick(()=>{
			table.value.clearSelection()
			table.value.toggleRowSelection(row)
		})
	}
	//列表
	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		if(searchQuery.value.username&&searchQuery.value.username!=''){
			params['username'] = '%' + searchQuery.value.username + '%'
		}
		context?.$http({
			url: `${tableName}/page`,
			method: 'get',
			params: params
		}).then(res => {
			listLoading.value = false
			list.value = res.data.data.list
			total.value = Number(res.data.data.total)
		})
	}
	//删
	const delClick = (id) => {
		let ids = []
		if (id) {
			ids = [id]
		} else {
			if (selRows.value.length) {
				for (let x in selRows.value) {
					ids.push(selRows.value[x].id)
				}
			} else {
				return false
			}
		}
		ElMessageBox.confirm(`是否删除选中${formName}`, '提示', {
			confirmButtonText: '是',
			cancelButtonText: '否',
			type: 'warning',
		}).then(() => {
			context?.$http({
				url: `${tableName}/delete`,
				method: 'post',
				data: ids
			}).then(res => {
				context?.$toolUtil.message('删除成功', 'success',()=>{
					getList()
				})
			})
		})
	}
	//多选
	const handleSelectionChange = (e) => {
		selRows.value = e
	}
	//列表数据
	//分页
	const total = ref(0)
	const layouts = ref(["total","prev","pager","next","sizes"])
	const sizeChange = (size) => {
		listQuery.value.limit = size
		getList()
	}
	const currentChange = (page) => {
		listQuery.value.page = page
		getList()
	}
	const prevClick = () => {
		listQuery.value.page = listQuery.value.page - 1
		getList()
	}
	const nextClick = () => {
		listQuery.value.page = listQuery.value.page + 1
		getList()
	}
	//分页
	//权限验证
	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}
	//搜索
	const searchClick = () => {
		listQuery.value.page = 1
		getList()
	}
	// 重置搜索条件：仅前端逻辑
	const resetSearch = () => {
		searchQuery.value = {}
		listQuery.value.page = 1
		getList()
	}
	//表单
	const formRef = ref(null)
	const formModelChange=()=>{
		searchClick()
	}
	const addClick = ()=>{
		formRef.value.init()
	}
	const editClick = ()=>{
		if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'edit')
		}
	}
	// 行内编辑：不改变原有表单调用方式
	const editClickRow = (row)=>{
		if(row && row.id){
			formRef.value.init(row.id,'edit')
		}
	}
	
	const infoClick = (id=null)=>{
		if(id){
			formRef.value.init(id,'info')
		}
		else if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'info')
		}
	}
	// 表单
	// 预览文件
	const preClick = (file) =>{
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		window.open(context?.$config.url + file)
		// const a = document.createElement('a');
		// a.style.display = 'none';
		// a.setAttribute('target', '_blank');
		// file && a.setAttribute('download', file);
		// a.href = context?.$config.url + file;
		// document.body.appendChild(a);
		// a.click();
		// document.body.removeChild(a);
	}
	// 下载文件
	const download = (file) => {
		if(!file){
			context?.$toolUtil.message('文件不存在','error')
		}
		let arr = file.replace(new RegExp('file/', "g"), "")
		axios.get((location.href.split(context?.$config.name).length>1 ? location.href.split(context?.$config.name)[0] :'') + context?.$config.name + '/file/download?fileName=' + arr, {
			headers: {
				token: context?.$toolUtil.storageGet('Token')
			},
			responseType: "blob"
		}).then(({
			data
		}) => {
			const binaryData = [];
			binaryData.push(data);
			const objectUrl = window.URL.createObjectURL(new Blob(binaryData, {
				type: 'application/pdf;chartset=UTF-8'
			}))
			const a = document.createElement('a')
			a.href = objectUrl
			a.download = arr
			// a.click()
			// 下面这个写法兼容火狐
			a.dispatchEvent(new MouseEvent('click', {
				bubbles: true,
				cancelable: true,
				view: window
			}))
			window.URL.revokeObjectURL(data)
		})
	}


	//初始化
	const init = () => {
		getList()
	}
	init()
</script>
<style lang="scss" scoped>
// 科技蓝主题 - 管理员用户列表页面样式
.admin-users-page {
	padding: 16px 20px;
	background: transparent;
	min-height: 100%;
	box-sizing: border-box;
}

.admin-users-content {
	display: flex;
	flex-direction: column;
	gap: 16px;
}

.list_search_view,
.table-panel {
	border-radius: 12px;
	background: var(--tech-panel);
	border: 1px solid var(--tech-border-soft);
	box-shadow: 0 0 16px var(--tech-glow);
}

.list_search_view {
	padding: 16px 20px;
	margin: 0;
}

.table-panel {
	padding: 16px 20px 14px;
}

.table-card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 12px;
}

.table-title {
	font-size: 14px;
	font-weight: 600;
	color: var(--tech-cyan);
}

.table-sub {
	font-size: 12px;
	color: var(--text-secondary);
}

.users-table {
	margin-top: 0;
	background: transparent;
}

.username-cell {
	font-weight: 500;
	color: var(--text-primary);
}

.users-pagination {
	padding: 0;
	margin: 12px 0 0;
	text-align: center;
	font-weight: 500;
}

.search_btn.reset {
	margin-left: 8px;
}

.list_search_view {
	.search_form {
		display: flex;
		align-items: center;
		flex-wrap: wrap;
		gap: 12px;

		.search_view {
			display: flex;
			align-items: center;
			gap: 8px;

			.search_label {
				color: var(--text-regular);
				font-weight: 500;
				font-size: 14px;
				white-space: nowrap;
			}

			.search_box {
				width: 240px;

				:deep(.search_inp .el-input__wrapper) {
					border: 1px solid var(--tech-border-soft);
					border-radius: 6px;
					box-shadow: none;
					background: rgba(8, 28, 58, 0.55);
					height: 36px;
					padding: 0 12px;
				}

				:deep(.search_inp .el-input__wrapper:hover) {
					border-color: var(--tech-border);
				}

				:deep(.search_inp.is-focus .el-input__wrapper) {
					border-color: var(--primary-blue-light);
					box-shadow: 0 0 0 2px rgba(34, 211, 238, 0.12);
				}
			}
		}

		.search_btn_view {
			display: flex;
			align-items: center;

			.search_btn {
				border: 1px solid var(--primary-blue);
				border-radius: 6px;
				padding: 0 20px;
				color: #fff;
				background: var(--primary-blue);
				font-size: 14px;
				height: 36px;
			}

			.search_btn:hover {
				background: var(--primary-blue-dark);
				border-color: var(--primary-blue-dark);
			}
		}
	}

	.btn_view {
		margin-top: 14px;
		display: flex;
		flex-wrap: wrap;
		gap: 10px;

		:deep(.el-button--success) {
			border: 1px solid #67C23A;
			color: #fff;
			background: #67C23A;
			border-radius: 6px;
			padding: 0 20px;
			font-size: 14px;
			height: 36px;
		}

		:deep(.el-button--primary) {
			border: 1px solid var(--primary-blue);
			color: #fff;
			background: var(--primary-blue);
			border-radius: 6px;
			padding: 0 20px;
			font-size: 14px;
			height: 36px;
		}

		:deep(.el-button--danger) {
			border: 1px solid #F56C6C;
			color: #fff;
			background: #F56C6C;
			border-radius: 6px;
			padding: 0 20px;
			font-size: 14px;
			height: 36px;
		}
	}
}

.users-table {
	padding: 0;
	background: transparent;
	width: 100%;
	border-color: var(--tech-border-soft);

	:deep(.el-table__header-wrapper) {
		thead tr {
			background: rgba(6, 22, 48, 0.98);

			th {
				padding: 12px 0;
				background: rgba(6, 22, 48, 0.98) !important;
				color: var(--tech-cyan) !important;
				border-color: var(--tech-border-soft);
			}
		}
	}

	:deep(.el-table__body-wrapper) {
		tbody tr {
			background: rgba(8, 28, 58, 0.3);
			transition: background 0.2s;

			td {
				padding: 12px 0;
				color: var(--text-primary);
				background: rgba(8, 28, 58, 0.25) !important;
				border-color: var(--tech-border-soft);
			}
		}

		tbody tr:nth-child(even) td {
			background: rgba(12, 36, 72, 0.42) !important;
		}

		tbody tr:hover td {
			color: var(--tech-cyan);
			background: rgba(34, 211, 238, 0.08) !important;
		}

		.el-button--primary {
			color: var(--tech-cyan);
		}

		.el-button--danger {
			color: #f89898;
		}
	}

	:deep(.el-table__row--striped) td.el-table__cell {
		background: rgba(12, 36, 72, 0.42) !important;
	}
}
// 分页器
.el-pagination {
	// 总页码
	:deep(.el-pagination__total) {
		padding: 0 10px;
		margin: 0 10px 0 0;
		color: var(--text-primary);
		background: rgba(8, 28, 58, 0.65);
		font-weight: 400;
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		height: 28px;
		border: 1px solid var(--tech-border-soft);
		border-radius: 4px;
	}
	// 上一页
	:deep(.btn-prev) {
		border: 1px solid var(--tech-border-soft);
		border-radius: 4px;
		padding: 0;
		margin: 0 5px;
		color: var(--text-primary);
		background: rgba(8, 28, 58, 0.65);
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		min-width: 35px;
		height: 28px;
	}
	// 下一页
	:deep(.btn-next) {
		border: 1px solid var(--tech-border-soft);
		border-radius: 4px;
		padding: 0;
		margin: 0 5px;
		color: var(--text-primary);
		background: rgba(8, 28, 58, 0.65);
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		min-width: 35px;
		height: 28px;
	}
	// 上一页禁用
	:deep(.btn-prev:disabled) {
		border: 1px solid var(--tech-border-soft);
		cursor: not-allowed;
		border-radius: 4px;
		padding: 0;
		margin: 0 5px;
		color: var(--text-placeholder);
		background: rgba(8, 28, 58, 0.4);
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		height: 28px;
	}
	// 下一页禁用
	:deep(.btn-next:disabled) {
		border: 1px solid var(--tech-border-soft);
		cursor: not-allowed;
		border-radius: 4px;
		padding: 0;
		margin: 0 5px;
		color: var(--text-placeholder);
		background: rgba(8, 28, 58, 0.4);
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		height: 28px;
	}
	// 页码
	:deep(.el-pager) {
		padding: 0;
		margin: 0;
		display: inline-block;
		vertical-align: top;
		// 数字
		li {
			cursor: pointer;
			padding: 0 4px;
			margin: 0 5px;
			color: var(--text-primary);
			display: inline-block;
			vertical-align: top;
			font-size: 13px;
			line-height: 28px;
			border-radius: 4px;
			background: rgba(8, 28, 58, 0.65);
			border: 1px solid var(--tech-border-soft);
			text-align: center;
			min-width: 30px;
			height: 28px;
		}
		// 数字悬浮
		li:hover {
			color: var(--tech-cyan);
			border-color: var(--tech-border);
		}
		// 选中
		li.is-active {
			cursor: default;
			color: #fff;
			display: inline-block;
			vertical-align: top;
			font-size: 13px;
			line-height: 28px;
			border-radius: 4px;
			background: linear-gradient(135deg, var(--primary-blue-dark), var(--primary-blue));
			border-color: var(--primary-blue-light);
			text-align: center;
			min-width: 30px;
			height: 28px;
		}
	}
	// sizes
	:deep(.el-pagination__sizes) {
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		height: 28px;
		.el-select {
			border: 1px solid var(--tech-border-soft);
			cursor: pointer;
			padding: 0;
			color: var(--text-primary);
			display: inline-block;
			font-size: 13px;
			line-height: 28px;
			border-radius: 4px;
			outline: 0;
			background: rgba(8, 28, 58, 0.65);
			width: 100%;
			text-align: center;
			height: 28px;
		}
	}
	// 跳页
	:deep(.el-pagination__jump) {
		margin: 0 0 0 24px;
		color: var(--text-primary);
		display: inline-block;
		vertical-align: top;
		font-size: 13px;
		line-height: 28px;
		height: 28px;
		// 输入框
		.el-input {
			border: 1px solid var(--tech-border-soft);
			cursor: pointer;
			padding: 0 3px;
			color: var(--text-primary);
			display: inline-block;
			font-size: 14px;
			line-height: 28px;
			border-radius: 4px;
			outline: 0;
			background: rgba(8, 28, 58, 0.65);
			width: 100%;
			text-align: center;
			height: 28px;
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
	}
}

// 表格内标签样式
:deep(.el-tag--info) {
	background: rgba(34, 211, 238, 0.1);
	border-color: rgba(34, 211, 238, 0.2);
	color: var(--tech-cyan);
}
</style>
