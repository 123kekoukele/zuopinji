
<template>
	<div class="student_page">
		<div class="app-contain">
			<div class="list_search_view">
				<el-form :model="searchQuery" class="search_form" inline @keyup.enter="searchClick">
					<el-form-item label="学号">
						<el-input v-model="searchQuery.xuehao" placeholder="学号" clearable style="width: 140px" />
					</el-form-item>
					<el-form-item label="学生姓名">
						<el-input v-model="searchQuery.xueshengxingming" placeholder="学生姓名" clearable style="width: 140px" />
					</el-form-item>
					<el-form-item label="性别">
						<el-select v-model="searchQuery.xingbie" placeholder="全部" clearable style="width: 100px">
							<el-option v-for="item in xingbieOptions" :key="item" :label="item" :value="item" />
						</el-select>
					</el-form-item>
					<el-form-item label="手机号码">
						<el-input v-model="searchQuery.shoujihaoma" placeholder="手机号码" clearable style="width: 150px" />
					</el-form-item>
					<el-form-item label="年级">
						<el-select v-model="searchQuery.nianji" placeholder="全部" clearable style="width: 110px">
							<el-option v-for="item in nianjiOptions" :key="item.value" :label="item.label" :value="item.value" />
						</el-select>
					</el-form-item>
					<el-form-item label="毕业届别">
						<el-select v-model="searchQuery.biyejie" placeholder="全部" clearable style="width: 120px">
							<el-option v-for="item in biyejieOptions" :key="item.value" :label="item.label" :value="item.value" />
						</el-select>
					</el-form-item>
					<el-form-item label="专业">
						<el-select v-model="searchQuery.zhuanye" placeholder="全部" clearable style="width: 150px">
							<el-option v-for="item in zhuanyeOptions" :key="item" :label="item" :value="item" />
						</el-select>
					</el-form-item>
					<el-form-item label="班级">
						<el-input v-model="searchQuery.banji" placeholder="班级" clearable style="width: 140px" />
					</el-form-item>
					<el-form-item>
						<el-button class="search_btn" type="primary" @click="searchClick()" size="small">搜索</el-button>
						<el-button @click="resetSearch" size="small">重置</el-button>
					</el-form-item>
				</el-form>
				<div class="btn_view">
					<el-button type="success" @click="addClick" v-if="btnAuth('xuesheng','新增')">新增</el-button>
					<el-button type="warning" @click="importClick" v-if="btnAuth('xuesheng','新增')">批量导入</el-button>
					<el-button  v-if=" btnAuth('xuesheng','查看')" type="info"  :disabled="selRows.length==1?false:true" @click="infoClick(null)">详情</el-button>
					<!-- 大管理员可能只配置了“新增”权限，这里复用“新增”授权来展示已选/未选列表入口 -->
					<el-button
						type="primary"
						@click="openSelectedDialog"
						v-if="btnAuth('xuesheng','查看') || btnAuth('xuesheng','新增')"
					>
						已选题的学生
					</el-button>
					<el-button
						type="warning"
						@click="openUnselectedDialog"
						v-if="btnAuth('xuesheng','查看') || btnAuth('xuesheng','新增')"
					>
						未选题的学生
					</el-button>
					<el-button type="primary" :disabled="selRows.length==1?false:true" @click="editClick" v-if=" btnAuth('xuesheng','修改')">修改</el-button>
					<el-button type="danger" :disabled="selRows.length?false:true" @click="delClick(null)"  v-if="btnAuth('xuesheng','删除')">删除</el-button>
				</div>
			</div>
			<br>
			<el-table
				v-loading="listLoading"
				border 
				:stripe='false'
				@selection-change="handleSelectionChange" 
				ref="table"
				v-if="btnAuth('xuesheng','查看')"
				:data="list"
				@row-click="listChange">
				<el-table-column :resizable='true' align="left" header-align="left" type="selection" width="55" />
				<el-table-column label="序号" width="70" :resizable='true' :sortable='false' align="left" header-align="left">
					<template #default="scope">{{ scope.$index + 1}}</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='false' 
					 align="left" 
					 header-align="left"
					label="学号">
					<template #default="scope">
						{{scope.row.xuehao}}
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='false' 
					 align="left" 
					 header-align="left"
					label="学生姓名">
					<template #default="scope">
						{{scope.row.xueshengxingming}}
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='false' 
					 align="left" 
					 header-align="left"
					label="性别">
					<template #default="scope">
						{{scope.row.xingbie}}
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='false' 
					 align="left" 
					 header-align="left"
					label="手机号码">
					<template #default="scope">
						{{scope.row.shoujihaoma}}
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='false' 
					 align="left" 
					 header-align="left"
					label="年级">
					<template #default="scope">
						{{ scope.row.nianji ? scope.row.nianji + '级' : '-' }}
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='false' 
					 align="left" 
					 header-align="left"
					label="毕业届别">
					<template #default="scope">
						{{ scope.row.biyejie ? scope.row.biyejie + '届' : '-' }}
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='false' 
					 align="left" 
					 header-align="left"
					label="专业">
					<template #default="scope">
						{{scope.row.zhuanye}}
					</template>
				</el-table-column>
				<el-table-column
					 :resizable='true' 
					 :sortable='false' 
					 align="left" 
					 header-align="left"
					label="班级">
					<template #default="scope">
						{{scope.row.banji}}
					</template>
				</el-table-column>
				<el-table-column label="操作" width="300" :resizable='true' :sortable='false' align="left" header-align="left">
					<template #default="scope">
						<el-button type="info" v-if=" btnAuth('xuesheng','查看')" @click="infoClick(scope.row.id)">详情</el-button>
					</template>
				</el-table-column>
			</el-table>
			<el-pagination 
				background
				:layout="layouts.join(',')"
				:total="total" 
				:page-size="listQuery.limit"
				prev-text="上一页"
				next-text="下一页"
				:hide-on-single-page="true"
				:style='{"padding":"0","margin":"20px 0 0","whiteSpace":"nowrap","color":"#333","textAlign":"center","width":"100%","fontWeight":"500"}'
				@size-change="sizeChange"
				@current-change="currentChange" 
				@prev-click="prevClick"
				@next-click="nextClick"  />
		</div>
		<formModel ref="formRef" @formModelChange="formModelChange"></formModel>
		<importForm
			ref="importRef"
			:tableName="tableName"
			:action="tableName"
			tip="请上传包含“学号、密码、学生姓名、联系电话、性别、专业、年级、班级”列的 Excel 文件；密码有值则按表中值导入，密码为空则默认取联系电话后六位；年级、班级可空（年级将按届别推算）"
			@importChange="searchClick"
		/>
		<el-dialog v-model="selectedDialogVisible" title="已选题的学生列表" width="960">
			<div style="margin-bottom: 12px; display: flex; flex-wrap: wrap; gap: 8px;">
				<el-tag type="primary">总数：{{ selectedSummary.total }}</el-tag>
				<el-tag type="success">已选：{{ selectedSummary.selectedCount }}</el-tag>
				<el-tag>比例：{{ selectedSummary.total > 0 ? ((selectedSummary.selectedCount / selectedSummary.total) * 100).toFixed(2) + '%' : '0%' }}</el-tag>
				<el-tag type="info">筛选结果：{{ filteredSelectedList.length }}</el-tag>
			</div>
			<el-form :model="selectedDialogSearch" class="dialog_search_form" inline @keyup.enter="applySelectedDialogFilter">
				<el-form-item label="学号">
					<el-input v-model="selectedDialogSearch.xuehao" placeholder="学号" clearable style="width: 130px" />
				</el-form-item>
				<el-form-item label="姓名">
					<el-input v-model="selectedDialogSearch.xueshengxingming" placeholder="学生姓名" clearable style="width: 130px" />
				</el-form-item>
				<el-form-item label="性别">
					<el-select v-model="selectedDialogSearch.xingbie" placeholder="全部" clearable style="width: 90px">
						<el-option v-for="item in xingbieOptions" :key="item" :label="item" :value="item" />
					</el-select>
				</el-form-item>
				<el-form-item label="手机">
					<el-input v-model="selectedDialogSearch.shoujihaoma" placeholder="手机号码" clearable style="width: 130px" />
				</el-form-item>
				<el-form-item label="年级">
					<el-select v-model="selectedDialogSearch.nianji" placeholder="全部" clearable style="width: 100px">
						<el-option v-for="item in nianjiOptions" :key="item.value" :label="item.label" :value="item.value" />
					</el-select>
				</el-form-item>
				<el-form-item label="班级">
					<el-input v-model="selectedDialogSearch.banji" placeholder="班级" clearable style="width: 100px" />
				</el-form-item>
				<el-form-item>
					<el-button type="primary" size="small" @click="applySelectedDialogFilter">搜索</el-button>
					<el-button size="small" @click="resetSelectedDialogSearch">重置</el-button>
				</el-form-item>
			</el-form>
			<el-table :data="filteredSelectedList" stripe height="400">
				<el-table-column prop="xuehao" label="学号" width="140" />
				<el-table-column prop="xueshengxingming" label="姓名" width="120" />
				<el-table-column prop="xingbie" label="性别" width="70" />
				<el-table-column prop="shoujihaoma" label="手机号码" width="130" />
				<el-table-column label="年级" width="90">
					<template #default="scope">
						{{ scope.row.nianji ? scope.row.nianji + '级' : '-' }}
					</template>
				</el-table-column>
				<el-table-column prop="zhuanye" label="专业" width="140" />
				<el-table-column prop="banji" label="班级" />
			</el-table>
		</el-dialog>
		<el-dialog v-model="unselectedDialogVisible" title="未选题的学生列表" width="960">
			<div style="margin-bottom: 12px; display: flex; flex-wrap: wrap; gap: 8px;">
				<el-tag type="primary">总数：{{ unselectedSummary.total }}</el-tag>
				<el-tag type="warning">未选：{{ unselectedSummary.unselectedCount }}</el-tag>
				<el-tag>比例：{{ unselectedSummary.total > 0 ? ((unselectedSummary.unselectedCount / unselectedSummary.total) * 100).toFixed(2) + '%' : '0%' }}</el-tag>
				<el-tag type="info">筛选结果：{{ filteredUnselectedList.length }}</el-tag>
			</div>
			<el-form :model="unselectedDialogSearch" class="dialog_search_form" inline @keyup.enter="applyUnselectedDialogFilter">
				<el-form-item label="学号">
					<el-input v-model="unselectedDialogSearch.xuehao" placeholder="学号" clearable style="width: 130px" />
				</el-form-item>
				<el-form-item label="姓名">
					<el-input v-model="unselectedDialogSearch.xueshengxingming" placeholder="学生姓名" clearable style="width: 130px" />
				</el-form-item>
				<el-form-item label="性别">
					<el-select v-model="unselectedDialogSearch.xingbie" placeholder="全部" clearable style="width: 90px">
						<el-option v-for="item in xingbieOptions" :key="item" :label="item" :value="item" />
					</el-select>
				</el-form-item>
				<el-form-item label="手机">
					<el-input v-model="unselectedDialogSearch.shoujihaoma" placeholder="手机号码" clearable style="width: 130px" />
				</el-form-item>
				<el-form-item label="年级">
					<el-select v-model="unselectedDialogSearch.nianji" placeholder="全部" clearable style="width: 100px">
						<el-option v-for="item in nianjiOptions" :key="item.value" :label="item.label" :value="item.value" />
					</el-select>
				</el-form-item>
				<el-form-item label="班级">
					<el-input v-model="unselectedDialogSearch.banji" placeholder="班级" clearable style="width: 100px" />
				</el-form-item>
				<el-form-item>
					<el-button type="primary" size="small" @click="applyUnselectedDialogFilter">搜索</el-button>
					<el-button size="small" @click="resetUnselectedDialogSearch">重置</el-button>
				</el-form-item>
			</el-form>
			<el-table :data="filteredUnselectedList" stripe height="400">
				<el-table-column prop="xuehao" label="学号" width="140" />
				<el-table-column prop="xueshengxingming" label="姓名" width="120" />
				<el-table-column prop="xingbie" label="性别" width="70" />
				<el-table-column prop="shoujihaoma" label="手机号码" width="130" />
				<el-table-column label="年级" width="90">
					<template #default="scope">
						{{ scope.row.nianji ? scope.row.nianji + '级' : '-' }}
					</template>
				</el-table-column>
				<el-table-column prop="zhuanye" label="专业" width="140" />
				<el-table-column prop="banji" label="班级" />
			</el-table>
		</el-dialog>
	</div>
</template>
<script setup>
	import axios from 'axios'
	import {
		reactive,
		ref,
		computed,
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
	import importForm from '@/components/common/importForm.vue'
	
	//基础信息
	const tableName = 'xuesheng'
	const formName = '学生'
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
	const createEmptySearchQuery = () => ({
		xuehao: '',
		xueshengxingming: '',
		xingbie: '',
		shoujihaoma: '',
		nianji: '',
		biyejie: '',
		zhuanye: '',
		banji: ''
	})
	const searchQuery = ref(createEmptySearchQuery())
	const LIKE_SEARCH_FIELDS = ['xuehao', 'xueshengxingming', 'shoujihaoma', 'banji']
	const EQ_SEARCH_FIELDS = ['xingbie', 'nianji', 'biyejie', 'zhuanye']
	const xingbieOptions = Object.freeze(['男', '女'])
	const zhuanyeOptions = Object.freeze(['地理信息科学', '地理科学', '风景园林', '测绘工程', '城乡规划'])
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
	const selRows = ref([])
	const listLoading = ref(false)
	const listChange = (row) =>{
		nextTick(()=>{
			table.value.clearSelection()
			table.value.toggleRowSelection(row)
		})
	}

	// 本专业已选/未选题学生弹窗
	const selectedDialogVisible = ref(false)
	const unselectedDialogVisible = ref(false)
	const selectedListSource = ref([])
	const unselectedListSource = ref([])
	const selectedSummary = ref({ total: 0, selectedCount: 0 })
	const unselectedSummary = ref({ total: 0, unselectedCount: 0 })
	const selectedDialogSearch = ref(createEmptySearchQuery())
	const unselectedDialogSearch = ref(createEmptySearchQuery())
	const selectedDialogQueryApplied = ref(createEmptySearchQuery())
	const unselectedDialogQueryApplied = ref(createEmptySearchQuery())

	const matchLike = (value, keyword) => {
		if (!keyword) return true
		return String(value || '').toLowerCase().includes(String(keyword).trim().toLowerCase())
	}

	const matchEq = (value, expected) => {
		if (expected === '' || expected == null) return true
		return String(value || '') === String(expected)
	}

	const filterStudentList = (list, query) => {
		if (!list?.length) return []
		return list.filter((row) => {
			if (!matchLike(row.xuehao, query.xuehao)) return false
			if (!matchLike(row.xueshengxingming, query.xueshengxingming)) return false
			if (!matchLike(row.shoujihaoma, query.shoujihaoma)) return false
			if (!matchLike(row.banji, query.banji)) return false
			if (!matchEq(row.xingbie, query.xingbie)) return false
			if (!matchEq(row.nianji, query.nianji)) return false
			if (!matchEq(row.biyejie, query.biyejie)) return false
			if (!matchEq(row.zhuanye, query.zhuanye)) return false
			return true
		})
	}

	const filteredSelectedList = computed(() => {
		return filterStudentList(selectedListSource.value, selectedDialogQueryApplied.value)
	})

	const filteredUnselectedList = computed(() => {
		return filterStudentList(unselectedListSource.value, unselectedDialogQueryApplied.value)
	})

	const resetSelectedDialogSearch = () => {
		selectedDialogSearch.value = createEmptySearchQuery()
		selectedDialogQueryApplied.value = createEmptySearchQuery()
	}

	const resetUnselectedDialogSearch = () => {
		unselectedDialogSearch.value = createEmptySearchQuery()
		unselectedDialogQueryApplied.value = createEmptySearchQuery()
	}

	const applySelectedDialogFilter = () => {
		selectedDialogQueryApplied.value = { ...selectedDialogSearch.value }
	}

	const applyUnselectedDialogFilter = () => {
		unselectedDialogQueryApplied.value = { ...unselectedDialogSearch.value }
	}

	const openSelectedDialog = () => {
		selectedDialogSearch.value = createEmptySearchQuery()
		selectedDialogQueryApplied.value = createEmptySearchQuery()
		context?.$http({
			url: 'xuesheng/selectedByMajor',
			method: 'get',
		}).then(res => {
			selectedListSource.value = res?.data?.data || []
			selectedSummary.value.total = Number(res?.data?.total || 0)
			selectedSummary.value.selectedCount = Number(res?.data?.selectedCount || 0)
			selectedDialogVisible.value = true
		}).catch(() => {
			context?.$toolUtil.message('获取已选题学生失败', 'error')
		})
	}

	const openUnselectedDialog = () => {
		unselectedDialogSearch.value = createEmptySearchQuery()
		unselectedDialogQueryApplied.value = createEmptySearchQuery()
		context?.$http({
			url: 'xuesheng/unselectedByMajor',
			method: 'get',
		}).then(res => {
			unselectedListSource.value = res?.data?.data || []
			unselectedSummary.value.total = Number(res?.data?.total || 0)
			unselectedSummary.value.unselectedCount = Number(res?.data?.unselectedCount || 0)
			unselectedDialogVisible.value = true
		}).catch(() => {
			context?.$toolUtil.message('获取未选题学生失败', 'error')
		})
	}
	//列表
	const getList = () => {
		listLoading.value = true
		let params = JSON.parse(JSON.stringify(listQuery.value))
		params['sort'] = 'id'
		params['order'] = 'desc'
		LIKE_SEARCH_FIELDS.forEach((field) => {
			const value = (searchQuery.value[field] || '').trim()
			if (value) {
				params[field] = `%${value}%`
			}
		})
		EQ_SEARCH_FIELDS.forEach((field) => {
			const value = searchQuery.value[field]
			if (value !== '' && value != null) {
				params[field] = value
			}
		})
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
		let ids = ref([])
		if (id) {
			ids.value = [id]
		} else {
			if (selRows.value.length) {
				for (let x in selRows.value) {
					ids.value.push(selRows.value[x].id)
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
				data: ids.value
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
	const resetSearch = () => {
		searchQuery.value = createEmptySearchQuery()
		searchClick()
	}
	//表单
	const formRef = ref(null)
	const importRef = ref(null)
	const formModelChange=()=>{
		searchClick()
	}
	const addClick = ()=>{
		formRef.value.init()
	}
	const importClick = () => {
		if (importRef.value) {
			importRef.value.importClick()
		}
	}
	const editClick = ()=>{
		if(selRows.value.length){
			formRef.value.init(selRows.value[0].id,'edit')
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
// 学生列表页面样式 - 融入青蓝主题
.student_page {
	padding: 16px 20px;
	background: transparent;
	min-height: 100%;
	box-sizing: border-box;
}

// 操作盒子 - 融入白色主题
.list_search_view {
	margin: 0 0 24px;
	padding: 20px;
	background: var(--background-white);
	border-radius: 10px;
	border: 1px solid rgba(34, 211, 238, 0.18);
	box-shadow: 0 0 18px rgba(34, 211, 238, 0.06);

	.search_form {
		display: flex;
		align-items: flex-start;
		flex-wrap: wrap;
		gap: 4px 8px;

		:deep(.el-form-item) {
			margin-bottom: 8px;
			margin-right: 0;
		}

		:deep(.el-form-item__label) {
			color: var(--text-primary);
			font-weight: 500;
			font-size: 14px;
		}

		:deep(.el-input__wrapper),
		:deep(.el-select__wrapper) {
			border: 1px solid rgba(34, 211, 238, 0.2);
			border-radius: 6px;
			box-shadow: none;
			transition: all 0.2s;
			min-height: 36px;
		}

		:deep(.el-input__wrapper:hover),
		:deep(.el-select__wrapper:hover) {
			border-color: rgba(34, 211, 238, 0.4);
		}

		:deep(.el-input.is-focus .el-input__wrapper),
		:deep(.el-select.is-focused .el-select__wrapper) {
			border-color: #22d3ee;
			box-shadow: 0 0 0 2px rgba(34, 211, 238, 0.15);
		}

		.search_btn {
			border: none;
			border-radius: 6px;
			padding: 0 16px;
			color: #fff;
			background: linear-gradient(135deg, #22d3ee 0%, #0891b2 100%);
			font-size: 14px;
			height: 36px;
			font-weight: 400;
			transition: all 0.2s;
		}

		.search_btn:hover {
			background: linear-gradient(135deg, #67e8f9 0%, #22d3ee 100%);
			transform: translateY(-1px);
			box-shadow: 0 4px 12px rgba(34, 211, 238, 0.3);
		}
	}

	.btn_view {
		margin: 16px 0 0;
		display: flex;
		gap: 12px;
		flex-wrap: wrap;

		:deep(.el-button) {
			border-radius: 6px;
			padding: 8px 16px;
			font-size: 14px;
			height: 36px;
			transition: all 0.2s;
			font-weight: 400;
			margin: 0;
		}

		:deep(.el-button--success) {
			background: linear-gradient(135deg, #22d3ee 0%, #0891b2 100%);
			border: none;
			color: #fff;
		}

		:deep(.el-button--success:hover) {
			background: linear-gradient(135deg, #67e8f9 0%, #22d3ee 100%);
			transform: translateY(-1px);
			box-shadow: 0 4px 12px rgba(34, 211, 238, 0.3);
		}

		:deep(.el-button--primary) {
			background: linear-gradient(135deg, #22d3ee 0%, #0891b2 100%);
			border: none;
			color: #fff;
		}

		:deep(.el-button--primary:hover) {
			background: linear-gradient(135deg, #67e8f9 0%, #22d3ee 100%);
			transform: translateY(-1px);
			box-shadow: 0 4px 12px rgba(34, 211, 238, 0.3);
		}

		:deep(.el-button--info) {
			border: 1px solid rgba(34, 211, 238, 0.2);
			color: var(--text-primary);
			background: var(--background-white);
		}

		:deep(.el-button--info:hover) {
			color: #0891b2;
			border-color: #22d3ee;
			background: rgba(34, 211, 238, 0.08);
		}

		:deep(.el-button--warning) {
			background: linear-gradient(135deg, #fbbf24 0%, #d97706 100%);
			border: none;
			color: #fff;
		}

		:deep(.el-button--warning:hover) {
			background: linear-gradient(135deg, #fcd34d 0%, #fbbf24 100%);
			transform: translateY(-1px);
			box-shadow: 0 4px 12px rgba(251, 191, 36, 0.3);
		}

		:deep(.el-button--danger) {
			border: 1px solid #F56C6C;
			color: #fff;
			background: #F56C6C;
		}

		:deep(.el-button--danger:hover) {
			background: #F78989;
			border-color: #F78989;
		}

		:deep(.el-button:disabled) {
			color: var(--text-placeholder);
			background: rgba(34, 211, 238, 0.08);
			border-color: rgba(34, 211, 238, 0.15);
			cursor: not-allowed;
		}
	}
}
	// 表格样式
	.el-table {
		padding: 0;
		background: transparent;
		width: 100%;
		border: none;
		border-radius: 8px;
		overflow: hidden;
		
		:deep(.el-table__header-wrapper) {
			thead {
				width: 100%;
				
				tr {
					background: linear-gradient(135deg, rgba(34, 211, 238, 0.9) 0%, rgba(8, 145, 178, 0.95) 100%);
					
					th {
						padding: 12px 0;
						background: transparent;
						border: none;
						text-align: center;
						
						.cell {
							padding: 0 10px;
							word-wrap: normal;
							word-break: break-all;
							white-space: normal;
							font-weight: 600;
							display: inline-block;
							vertical-align: middle;
							width: 100%;
							line-height: 24px;
							position: relative;
							text-overflow: ellipsis;
						}
					}
				}
			}
		}
		
		:deep(.el-table__body-wrapper) {
			tbody {
				width: 100%;
				
				tr {
					background: var(--background-white);
					transition: all 0.2s;
					
					td {
						padding: 12px 0;
						color: var(--text-primary);
						background: transparent;
						border-bottom: 1px solid rgba(34, 211, 238, 0.1);
						text-align: center;
						
						.cell {
							padding: 0 10px;
							overflow: hidden;
							word-break: break-all;
							white-space: normal;
							line-height: 24px;
							text-overflow: ellipsis;
						}
					}
				}
				
				tr:nth-child(even) {
					td {
						background: rgba(34, 211, 238, 0.03);
					}
				}
				
				tr:hover {
					td {
						background: rgba(34, 211, 238, 0.08);
						color: #0891b2;
					}
				}
			}
		}
	}

	// 分页器样式
	.el-pagination {
		:deep(.el-pagination__total) {
			padding: 0 10px;
			margin: 0 10px 0 0;
			color: var(--text-primary);
			font-weight: 500;
			font-size: 13px;
			line-height: 28px;
			height: 28px;
			background: var(--background-white);
			border: 1px solid rgba(34, 211, 238, 0.2);
			border-radius: 6px;
		}
		
		:deep(.btn-prev),
		:deep(.btn-next) {
			border: 1px solid rgba(34, 211, 238, 0.2);
			border-radius: 6px;
			padding: 0;
			margin: 0 4px;
			color: var(--text-primary);
			background: var(--background-white);
			display: inline-block;
			vertical-align: top;
			font-size: 13px;
			line-height: 28px;
			min-width: 32px;
			height: 32px;
		}
		
		:deep(.btn-prev:hover:not(.disabled)),
		:deep(.btn-next:hover:not(.disabled)) {
			color: #0891b2;
			border-color: #22d3ee;
			background: rgba(34, 211, 238, 0.08);
		}
		
		:deep(.btn-prev:disabled),
		:deep(.btn-next:disabled) {
			border: 1px solid rgba(34, 211, 238, 0.15);
			color: var(--text-placeholder);
			background: var(--background-white);
			cursor: not-allowed;
		}
		
		:deep(.el-pager) {
			padding: 0;
			margin: 0;
			display: inline-block;
			vertical-align: top;
			
			.number {
				cursor: pointer;
				padding: 0 4px;
				margin: 0 4px;
				color: var(--text-primary);
				display: inline-block;
				vertical-align: top;
				font-size: 13px;
				line-height: 28px;
				border-radius: 6px;
				background: var(--background-white);
				border: 1px solid rgba(34, 211, 238, 0.2);
				text-align: center;
				min-width: 32px;
				height: 32px;
			}
			
			.number:hover {
				color: #0891b2;
				border-color: #22d3ee;
				background: rgba(34, 211, 238, 0.08);
			}
			
			.number.is-active {
				cursor: default;
				color: #fff;
				background: linear-gradient(135deg, #22d3ee 0%, #0891b2 100%);
				border-color: #22d3ee;
			}
		}
		
		:deep(.el-pagination__sizes) {
			display: inline-block;
			vertical-align: top;
			font-size: 13px;
			line-height: 28px;
			height: 32px;
			
			.el-select {
				border: 1px solid rgba(34, 211, 238, 0.2);
				border-radius: 6px;
				cursor: pointer;
				padding: 0;
				color: var(--text-primary);
				display: inline-block;
				font-size: 13px;
				line-height: 30px;
				background: var(--background-white);
				width: 100%;
				text-align: center;
				height: 32px;
			}
		}
		
		:deep(.el-pagination__jump) {
			margin: 0 0 0 16px;
			color: var(--text-primary);
			display: inline-block;
			vertical-align: top;
			font-size: 13px;
			line-height: 28px;
			height: 28px;
			
			.el-input {
				border: 1px solid rgba(34, 211, 238, 0.2);
				cursor: pointer;
				padding: 0 3px;
				color: var(--text-primary);
				display: inline-block;
				font-size: 14px;
				line-height: 28px;
				border-radius: 6px;
				background: var(--background-white);
				width: 100%;
				text-align: center;
				height: 32px;
				
				.el-input__wrapper {
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

	// 弹窗内标签样式
	:deep(.el-tag) {
		border-color: rgba(34, 211, 238, 0.2);
		background: rgba(34, 211, 238, 0.08);
		color: #0891b2;
	}

	:deep(.el-tag--success) {
		background: rgba(103, 194, 58, 0.1);
		border-color: rgba(103, 194, 58, 0.3);
		color: #67C23A;
	}

	:deep(.el-tag--warning) {
		background: rgba(230, 162, 60, 0.1);
		border-color: rgba(230, 162, 60, 0.3);
		color: #E6A23C;
	}

	.dialog_search_form {
		margin-bottom: 12px;
		padding: 12px 12px 4px;
		background: rgba(34, 211, 238, 0.05);
		border: 1px solid rgba(34, 211, 238, 0.15);
		border-radius: 8px;

		:deep(.el-form-item) {
			margin-bottom: 8px;
			margin-right: 8px;
		}

		:deep(.el-form-item__label) {
			color: var(--text-primary);
			font-size: 13px;
		}
	}
</style>
