<template>
  <div class="topic-info-page">
    <!-- 搜索与操作区 -->
    <div class="filter-card">
      <div class="search-row">
        <el-input
          v-model="searchQuery.timubianhao"
          placeholder="题目编号"
          clearable
          class="search-input"
          @keyup.enter="searchClick"
        >
          <template #prefix>
            <el-icon><Document /></el-icon>
          </template>
        </el-input>

        <el-input
          v-model="searchQuery.zhuanye"
          placeholder="专业名称"
          clearable
          class="search-input"
          @keyup.enter="searchClick"
        >
          <template #prefix>
            <el-icon><School /></el-icon>
          </template>
        </el-input>

        <el-button type="primary" @click="searchClick" class="search-btn">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>

        <el-button @click="resetSearch" class="reset-btn">
          <el-icon><RefreshRight /></el-icon>
          重置
        </el-button>
      </div>

      <div class="btn-row">
        <div class="btn-row-left">
          <el-button type="success" @click="addClick" v-if="btnAuth('timuxinxi','新增')">
            <el-icon><Plus /></el-icon>
            新增选题
          </el-button>
          <el-button type="warning" @click="importClick" v-if="btnAuth('timuxinxi','新增')">
            <el-icon><Upload /></el-icon>
            批量导入
          </el-button>
          <el-button type="danger" :disabled="!selRows.length" @click="delClick(null)" v-if="btnAuth('timuxinxi','删除')">
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
          <div class="page-select-bar" v-if="list.length">
            <el-checkbox
              :model-value="isAllCurrentPageSelected"
              :indeterminate="isIndeterminateCurrentPage"
              @change="toggleSelectCurrentPage"
            >
              全选当前页
            </el-checkbox>
            <span class="selection-count" v-if="selRows.length">已选 {{ selRows.length }} 条</span>
            <el-button link type="primary" @click="clearSelection" v-if="selRows.length">取消全选</el-button>
          </div>
        </div>
        <div class="btn-row-right" v-if="canViewStudentPickLists">
          <el-button type="primary" plain @click="openSelectedDialog">
            <el-icon><User /></el-icon>
            已选题学生
          </el-button>
          <el-button type="warning" plain @click="openUnselectedDialog">
            <el-icon><UserFilled /></el-icon>
            未选题学生
          </el-button>
        </div>
      </div>
    </div>

    <!-- 卡片列表区域 -->
    <div class="cards-container" v-loading="listLoading">
      <!-- 空状态 -->
      <div v-if="!listLoading && (!list || list.length === 0)" class="empty-state">
        <el-empty description="暂无题目信息" />
      </div>

      <!-- 题目卡片 -->
      <div
        v-for="(item, index) in list"
        :key="item.id"
        class="topic-card"
        :class="{ 'topic-card-disabled': item.yibeixuan, 'topic-card-selected': isSelected(item) }"
        @click="handleCardClick(item)"
      >
        <!-- 卡片左侧：多选框 -->
        <div class="card-checkbox" @click.stop>
          <el-checkbox
            :model-value="isSelected(item)"
            @change="handleSelectionChange(item)"
          />
        </div>

        <!-- 卡片封面 -->
        <div class="card-cover" v-if="item.timufengmian">
          <el-image
            :src="getCoverUrl(item.timufengmian)"
            :preview-src-list="[getCoverUrl(item.timufengmian)]"
            fit="cover"
            preview-teleported
            class="cover-image"
          />
        </div>
        <div class="card-cover card-cover-empty" v-else>
          <el-icon><Picture /></el-icon>
        </div>

        <!-- 卡片主体 -->
        <div class="card-content">
          <div class="card-header">
            <div class="card-index">{{ (listQuery.page - 1) * listQuery.limit + index + 1 }}</div>
            <div class="card-code" @click.stop="copyCode(item.timubianhao)" title="点击复制">
              <el-icon><DocumentCopy /></el-icon>
              <span>{{ item.timubianhao }}</span>
            </div>
            <div class="card-status" :class="item.yibeixuan ? 'status-selected' : 'status-available'">
              <el-icon v-if="!item.yibeixuan"><CircleCheckFilled /></el-icon>
              <el-icon v-else><CircleCloseFilled /></el-icon>
              <span>{{ item.yibeixuan ? '已被选' : '可选' }}</span>
            </div>
          </div>

          <div class="card-body">
            <h3 class="card-title">{{ item.ketimingcheng }}</h3>
          </div>

          <div class="card-meta">
            <div class="meta-item">
              <el-icon><Collection /></el-icon>
              <span>{{ item.timuleixing || '-' }}</span>
            </div>
            <div class="meta-item">
              <el-icon><OfficeBuilding /></el-icon>
              <span>{{ item.zhuanye || '-' }}</span>
            </div>
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>{{ item.jiaoshixingming || '-' }}</span>
            </div>
            <div class="meta-item" v-if="item.xuantishijian">
              <el-icon><Clock /></el-icon>
              <span>{{ item.xuantishijian }}</span>
            </div>
          </div>
        </div>

        <!-- 卡片操作 -->
        <div class="card-actions" @click.stop>
          <el-button type="primary" link size="small" @click.stop="infoClick(item.id)">
            <el-icon><View /></el-icon>
            详情
          </el-button>
          <el-button type="info" link size="small" @click.stop="openTopicStudentsDialog(item)">
            <el-icon><User /></el-icon>
            选题学生
          </el-button>
          <el-button type="primary" link size="small" @click.stop="editClickRow(item)" v-if="btnAuth('timuxinxi','修改')">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button type="danger" link size="small" @click.stop="delClick(item.id)" v-if="btnAuth('timuxinxi','删除')">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页器 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="listQuery.page"
        v-model:page-size="listQuery.limit"
        :page-sizes="[8, 12, 16, 24]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        prev-text="上一页"
        next-text="下一页"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 组件引用 -->
    <formModel ref="formRef" @formModelChange="formModelChange" />
    <importForm
      ref="importRef"
      :tableName="tableName"
      :action="tableName"
      :tip="importTip"
      @importChange="getList"
    />

    <!-- 选题学生列表弹窗 -->
    <el-dialog v-model="topicStudentsDialogVisible" title="选题学生列表" width="900" class="dialog-dark">
      <div class="dialog-stats">
        <el-tag type="primary">
          当前题目：{{ currentTopic?.timubianhao || '' }} / {{ currentTopic?.ketimingcheng || '' }}
        </el-tag>
        <el-tag type="success">总申请数：{{ topicStudentsSummary.total }}</el-tag>
        <el-tag>已审核通过：{{ topicStudentsSummary.approvedCount }}</el-tag>
        <el-tag type="warning">未通过：{{ topicStudentsSummary.total - topicStudentsSummary.approvedCount }}</el-tag>
      </div>
      <el-table :data="topicStudents" :stripe="false" height="400" class="dialog-table">
        <el-table-column prop="xuehao" label="学号" width="160" />
        <el-table-column prop="xueshengxingming" label="姓名" width="180" />
        <el-table-column prop="shenhezhuangtai" label="审核状态" width="140" />
        <el-table-column prop="shenqingshijian" label="申请时间" />
      </el-table>
    </el-dialog>

    <!-- 已选题学生列表弹窗 -->
    <el-dialog v-model="selectedDialogVisible" title="已选题学生列表" width="900" class="dialog-dark">
      <div class="dialog-stats">
        <el-tag type="primary">总数：{{ selectedSummary.total }}</el-tag>
        <el-tag type="success">已选：{{ selectedSummary.selectedCount }}</el-tag>
        <el-tag>比例：{{ selectedSummary.total > 0 ? ((selectedSummary.selectedCount / selectedSummary.total) * 100).toFixed(2) + '%' : '0%' }}</el-tag>
      </div>
      <el-table :data="selectedList" :stripe="false" height="400" class="dialog-table">
        <el-table-column prop="xuehao" label="学号" width="160" />
        <el-table-column prop="xueshengxingming" label="姓名" width="180" />
        <el-table-column prop="zhuanye" label="专业" width="160" />
        <el-table-column prop="banji" label="班级" />
      </el-table>
    </el-dialog>

    <!-- 未选题学生列表弹窗 -->
    <el-dialog v-model="unselectedDialogVisible" title="未选题学生列表" width="900" class="dialog-dark">
      <div class="dialog-stats">
        <el-tag type="primary">总数：{{ unselectedSummary.total }}</el-tag>
        <el-tag type="warning">未选：{{ unselectedSummary.unselectedCount }}</el-tag>
        <el-tag>比例：{{ unselectedSummary.total > 0 ? ((unselectedSummary.unselectedCount / unselectedSummary.total) * 100).toFixed(2) + '%' : '0%' }}</el-tag>
      </div>
      <el-table :data="unselectedList" :stripe="false" height="400" class="dialog-table">
        <el-table-column prop="xuehao" label="学号" width="160" />
        <el-table-column prop="xueshengxingming" label="姓名" width="180" />
        <el-table-column prop="zhuanye" label="专业" width="160" />
        <el-table-column prop="banji" label="班级" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, getCurrentInstance, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  RefreshRight,
  Plus,
  Upload,
  Delete,
  User,
  UserFilled,
  Document,
  DocumentCopy,
  School,
  CircleCheckFilled,
  CircleCloseFilled,
  Collection,
  OfficeBuilding,
  Clock,
  View,
  Edit,
  Picture
} from '@element-plus/icons-vue'

const context = getCurrentInstance()?.appContext.config.globalProperties
import formModel from './formModel.vue'
import importForm from '@/components/common/importForm.vue'

const tableName = 'timuxinxi'
const formName = '题目信息'
const importTip = computed(() => {
  const sessionTable = context?.$toolUtil.storageGet('sessionTable')
  if (sessionTable === 'jiaoshi') {
    return '请上传包含「课题名称、题目类型、课题性质、题目范围」四列的 Excel；题目编号可省略（系统自动生成），专业与教师信息将按当前登录教师账号自动关联'
  }
  return '请上传包含「课题名称、题目类型、课题性质、题目范围、教师工号」五列的 Excel；题目编号可省略。专业管理员导入时，教师工号须为本专业教师，否则该行会跳过且不会出现在当前专业列表中；超级管理员可指定任意专业教师'
})

const list = ref([])
const listQuery = ref({
  page: 1,
  limit: 12,
  sort: 'id',
  order: 'desc'
})
const searchQuery = ref({})
const selRows = ref([])
const listLoading = ref(false)
const total = ref(0)
const importRef = ref(null)

// 查看选题学生弹窗
const topicStudentsDialogVisible = ref(false)
const topicStudents = ref([])
const currentTopic = ref(null)
const topicStudentsSummary = ref({ total: 0, approvedCount: 0 })

const openTopicStudentsDialog = (row) => {
  if (!row || !row.timubianhao) return
  currentTopic.value = row
  context?.$http({
    url: 'xuantishenqing/studentsByTopic',
    method: 'get',
    params: { timubianhao: row.timubianhao }
  }).then(res => {
    topicStudents.value = res?.data?.data || []
    topicStudentsSummary.value.total = Number(res?.data?.total || 0)
    topicStudentsSummary.value.approvedCount = Number(res?.data?.approvedCount || 0)
    topicStudentsDialogVisible.value = true
  }).catch(() => {
    context?.$toolUtil.message('获取选题学生失败', 'error')
  })
}

// 已选/未选题学生
const btnAuthFn = (e, a) => context?.$toolUtil.isAuth(e, a)
const canViewStudentPickLists = computed(() => {
  if (context?.$toolUtil.storageGet('sessionTable') !== 'users') return false
  return btnAuthFn('xuesheng', '查看') || btnAuthFn('xuesheng', '新增') || btnAuthFn('timuxinxi', '查看')
})

const selectedDialogVisible = ref(false)
const unselectedDialogVisible = ref(false)
const selectedList = ref([])
const unselectedList = ref([])
const selectedSummary = ref({ total: 0, selectedCount: 0 })
const unselectedSummary = ref({ total: 0, unselectedCount: 0 })

const openSelectedDialog = () => {
  context?.$http({
    url: 'xuesheng/selectedByMajor',
    method: 'get'
  }).then(res => {
    selectedList.value = res?.data?.data || []
    selectedSummary.value.total = Number(res?.data?.total || 0)
    selectedSummary.value.selectedCount = Number(res?.data?.selectedCount || 0)
    selectedDialogVisible.value = true
  }).catch(() => {
    context?.$toolUtil.message('获取已选题学生失败', 'error')
  })
}

const openUnselectedDialog = () => {
  context?.$http({
    url: 'xuesheng/unselectedByMajor',
    method: 'get'
  }).then(res => {
    unselectedList.value = res?.data?.data || []
    unselectedSummary.value.total = Number(res?.data?.total || 0)
    unselectedSummary.value.unselectedCount = Number(res?.data?.unselectedCount || 0)
    unselectedDialogVisible.value = true
  }).catch(() => {
    context?.$toolUtil.message('获取未选题学生失败', 'error')
  })
}

onMounted(() => {})

// 封面 URL
const getCoverUrl = (val) => {
  if (!val) return ''
  const url = val.split(',')[0]
  return url && url.substring(0, 4) === 'http' ? url : (context?.$config?.url || '') + url
}

// 卡片点击选中
const handleCardClick = (item) => {
  handleSelectionChange(item)
}

const isSelected = (item) => {
  return selRows.value.some(row => row.id === item.id)
}

const isAllCurrentPageSelected = computed(() => {
  if (!list.value.length) return false
  return list.value.every(item => isSelected(item))
})

const isIndeterminateCurrentPage = computed(() => {
  if (!list.value.length) return false
  const selectedOnPage = list.value.filter(item => isSelected(item)).length
  return selectedOnPage > 0 && selectedOnPage < list.value.length
})

const selectAllCurrentPage = () => {
  const existingIds = new Set(selRows.value.map(row => row.id))
  list.value.forEach(item => {
    if (!existingIds.has(item.id)) {
      selRows.value.push(item)
    }
  })
}

const clearSelection = () => {
  selRows.value = []
}

const toggleSelectCurrentPage = (checked) => {
  if (checked) {
    selectAllCurrentPage()
  } else {
    const currentIds = new Set(list.value.map(item => item.id))
    selRows.value = selRows.value.filter(row => !currentIds.has(row.id))
  }
}

const handleSelectionChange = (item) => {
  const index = selRows.value.findIndex(row => row.id === item.id)
  if (index > -1) {
    selRows.value.splice(index, 1)
  } else {
    selRows.value.push(item)
  }
}

const getList = () => {
  listLoading.value = true
  const params = JSON.parse(JSON.stringify(listQuery.value))
  params.sort = 'id'
  params.order = 'desc'
  if (searchQuery.value.timubianhao?.trim()) {
    params.timubianhao = '%' + searchQuery.value.timubianhao.trim() + '%'
  }
  if (searchQuery.value.zhuanye?.trim()) {
    params.zhuanye = '%' + searchQuery.value.zhuanye.trim() + '%'
  }
  context?.$http({
    url: `${tableName}/page`,
    method: 'get',
    params
  }).then(res => {
    listLoading.value = false
    list.value = res.data.data.list
    total.value = Number(res.data.data.total)
  }).catch(() => {
    listLoading.value = false
  })
}

const delClick = (id) => {
  let ids = []
  if (id) {
    ids = [id]
  } else {
    if (selRows.value.length) {
      selRows.value.forEach(x => ids.push(x.id))
    } else {
      return false
    }
  }
  ElMessageBox.confirm(`是否删除选中的${formName}？`, '提示', {
    confirmButtonText: '是',
    cancelButtonText: '否',
    type: 'warning'
  }).then(() => {
    context?.$http({
      url: `${tableName}/delete`,
      method: 'post',
      data: ids
    }).then(res => {
      context?.$toolUtil.message('删除成功', 'success', () => {
        selRows.value = []
        getList()
      })
    })
  })
}

const sizeChange = (size) => {
  listQuery.value.limit = size
  listQuery.value.page = 1
  getList()
}

const handleSizeChange = (size) => {
  listQuery.value.limit = size
  listQuery.value.page = 1
  selRows.value = []
  getList()
}

const currentChange = (page) => {
  listQuery.value.page = page
  selRows.value = []
  getList()
}

const handleCurrentChange = (page) => {
  listQuery.value.page = page
  selRows.value = []
  getList()
}

const btnAuth = (e, a) => context?.$toolUtil.isAuth(e, a)

const searchClick = () => {
  listQuery.value.page = 1
  selRows.value = []
  getList()
}

const resetSearch = () => {
  searchQuery.value = {}
  listQuery.value.page = 1
  selRows.value = []
  getList()
}

const formRef = ref(null)
const formModelChange = () => searchClick()

const addClick = () => formRef.value.init()
const importClick = () => {
  if (importRef.value) {
    importRef.value.importClick()
  }
}

const editClickRow = (row) => {
  if (row?.id) formRef.value.init(row.id, 'edit')
}

const infoClick = (id = null) => {
  if (id) formRef.value.init(id, 'info')
  else if (selRows.value.length) formRef.value.init(selRows.value[0].id, 'info')
}

// 复制编号
const copyCode = (code) => {
  navigator.clipboard.writeText(code).then(() => {
    ElMessage.success('题目编号已复制')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

const init = () => getList()
init()
</script>

<style lang="scss" scoped>
// 页面基础样式
.topic-info-page {
  padding: 16px 20px;
  background: transparent;
  min-height: 100%;
  box-sizing: border-box;
}

// 筛选卡片
.filter-card {
  background: var(--tech-panel);
  border-radius: 10px;
  border: 1px solid var(--tech-border-soft);
  box-shadow: 0 0 16px var(--tech-glow);
  padding: 16px 20px;
  margin-bottom: 20px;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.search-input {
  width: 180px;
  :deep(.el-input__wrapper) {
    background: rgba(34, 211, 238, 0.05);
    border: 1px solid var(--tech-border-soft);
    box-shadow: none;
    border-radius: 8px;
    padding: 0 12px;
    &:hover, &:focus {
      border-color: var(--tech-cyan);
    }
  }
  :deep(.el-input__inner) {
    color: rgba(255, 255, 255, 0.85);
    &::placeholder {
      color: rgba(255, 255, 255, 0.4);
    }
  }
  :deep(.el-input__prefix) {
    color: var(--tech-cyan);
  }
}

.search-btn {
  border: none;
  border-radius: 8px;
  padding: 0 20px;
  height: 36px;
  background: linear-gradient(135deg, var(--tech-cyan) 0%, var(--tech-cyan-dark) 100%);
  color: #fff;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
  &:hover {
    opacity: 0.9;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(34, 211, 238, 0.4);
  }
}

.reset-btn {
  border: 1px solid var(--tech-border-soft);
  border-radius: 8px;
  padding: 0 16px;
  height: 36px;
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  gap: 6px;
  &:hover {
    border-color: var(--tech-cyan);
    color: var(--tech-cyan);
  }
}

.btn-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  padding-top: 12px;
  border-top: 1px solid var(--tech-border-soft);
}

.btn-row-left {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  align-items: center;
}

.page-select-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: 4px;
  padding-left: 12px;
  border-left: 1px solid var(--tech-border-soft);

  :deep(.el-checkbox__label) {
    color: rgba(255, 255, 255, 0.75);
    font-size: 13px;
  }

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner),
  :deep(.el-checkbox__input.is-indeterminate .el-checkbox__inner) {
    background: var(--tech-cyan);
    border-color: var(--tech-cyan);
  }

  :deep(.el-checkbox__inner) {
    background: rgba(34, 211, 238, 0.1);
    border-color: var(--tech-border-soft);
  }
}

.selection-count {
  font-size: 13px;
  color: var(--tech-cyan);
  font-weight: 500;
}

.btn-row-right {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

// 卡片容器
.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
  padding: 4px 0;
}

// 空状态
.empty-state {
  grid-column: 1 / -1;
  padding: 60px 0;
  :deep(.el-empty__description) {
    color: rgba(255, 255, 255, 0.5);
  }
}

// 题目卡片
.topic-card {
  background: var(--tech-panel);
  border-radius: 12px;
  border: 1px solid var(--tech-border-soft);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  transition: all 0.3s ease;
  position: relative;
  display: flex;
  cursor: pointer;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, var(--tech-cyan), var(--tech-cyan-dark));
    opacity: 0;
    transition: opacity 0.3s;
  }
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 30px rgba(34, 211, 238, 0.2);
    border-color: rgba(34, 211, 238, 0.4);
    &::before {
      opacity: 1;
    }
  }
  
  &.topic-card-disabled {
    opacity: 0.65;
    &::before {
      background: linear-gradient(90deg, #64748b, #475569);
      opacity: 1;
    }
    &:hover {
      transform: none;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
      border-color: var(--tech-border-soft);
    }
  }

  &.topic-card-selected {
    border-color: rgba(34, 211, 238, 0.55);
    box-shadow: 0 0 0 1px rgba(34, 211, 238, 0.25), 0 8px 24px rgba(34, 211, 238, 0.15);
    &::before {
      opacity: 1;
    }
  }
}

.card-checkbox {
  display: flex;
  align-items: flex-start;
  padding: 16px 0 16px 16px;
  :deep(.el-checkbox__input) {
    .el-checkbox__inner {
      background: rgba(34, 211, 238, 0.1);
      border-color: var(--tech-border-soft);
    }
    &.is-checked .el-checkbox__inner {
      background: var(--tech-cyan);
      border-color: var(--tech-cyan);
    }
  }
  :deep(.el-checkbox__label) {
    display: none;
  }
}

.card-cover {
  width: 100px;
  min-height: 120px;
  flex-shrink: 0;
  .cover-image {
    width: 100%;
    height: 100%;
    display: block;
  }
}

.card-cover-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(34, 211, 238, 0.05);
  .el-icon {
    font-size: 32px;
    color: var(--tech-border-soft);
  }
}

.card-content {
  flex: 1;
  padding: 14px 14px 14px 10px;
  min-width: 0;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.card-index {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(34, 211, 238, 0.15);
  border-radius: 6px;
  color: var(--tech-cyan);
  font-weight: 600;
  font-size: 12px;
  flex-shrink: 0;
}

.card-code {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 4px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;
  cursor: pointer;
  padding: 3px 6px;
  border-radius: 4px;
  transition: all 0.2s;
  overflow: hidden;
  span {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  &:hover {
    color: var(--tech-cyan);
    background: rgba(34, 211, 238, 0.1);
  }
  .el-icon {
    flex-shrink: 0;
  }
}

.card-status {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 3px 8px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 500;
  flex-shrink: 0;
  
  &.status-available {
    background: rgba(103, 194, 58, 0.15);
    color: #86efac;
    border: 1px solid rgba(103, 194, 58, 0.3);
  }
  
  &.status-selected {
    background: rgba(100, 116, 139, 0.15);
    color: #94a3b8;
    border: 1px solid rgba(100, 116, 139, 0.3);
  }
}

.card-body {
  margin-bottom: 10px;
}

.card-title {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  .el-icon {
    color: var(--tech-cyan);
    font-size: 12px;
  }
  span {
    max-width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.card-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px;
  gap: 4px;
  border-left: 1px solid var(--tech-border-soft);
  background: rgba(34, 211, 238, 0.02);
  :deep(.el-button) {
    font-size: 12px;
    padding: 4px 8px;
    .el-icon {
      margin-right: 2px;
    }
  }
}

// 分页器
.pagination-wrap {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

:deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: rgba(255, 255, 255, 0.7);
  --el-pagination-button-bg-color: rgba(34, 211, 238, 0.08);
  
  .el-pagination__total {
    color: rgba(255, 255, 255, 0.6);
  }
  
  .btn-prev, .btn-next {
    border: 1px solid var(--tech-border-soft);
    border-radius: 8px;
    background: rgba(34, 211, 238, 0.08);
    color: var(--tech-cyan);
    &:hover {
      color: #fff;
      background: var(--tech-cyan);
      border-color: var(--tech-cyan);
    }
    &.is-disabled {
      background: rgba(255, 255, 255, 0.03);
      border-color: rgba(255, 255, 255, 0.1);
      color: rgba(255, 255, 255, 0.3);
    }
  }
  
  .el-pager {
    li {
      border: 1px solid var(--tech-border-soft);
      border-radius: 8px;
      background: rgba(34, 211, 238, 0.08);
      color: var(--tech-cyan);
      margin: 0 4px;
      min-width: 36px;
      height: 36px;
      line-height: 36px;
      font-weight: 500;
      &:hover {
        color: #fff;
        background: var(--tech-cyan);
        border-color: var(--tech-cyan);
      }
      &.is-active {
        background: linear-gradient(135deg, var(--tech-cyan) 0%, var(--tech-cyan-dark) 100%);
        border-color: var(--tech-cyan);
        color: #fff;
      }
    }
  }
  
  .el-pagination__sizes {
    .el-select {
      .el-input__wrapper {
        background: rgba(34, 211, 238, 0.08);
        border-color: var(--tech-border-soft);
        box-shadow: none;
      }
      .el-input__inner {
        color: var(--tech-cyan);
      }
    }
  }
  
  .el-pagination__jump {
    color: rgba(255, 255, 255, 0.6);
    .el-input__wrapper {
      background: rgba(34, 211, 238, 0.08);
      border-color: var(--tech-border-soft);
      box-shadow: none;
    }
    .el-input__inner {
      color: var(--tech-cyan);
    }
  }
}

// 弹窗样式（dialog 挂载到 body，需 :deep 穿透）
:deep(.el-dialog.dialog-dark) {
  background: var(--tech-panel);
  border: 1px solid var(--tech-border-soft);
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.45);

  .el-dialog__header {
    border-bottom: 1px solid var(--tech-border-soft);
    padding: 16px 20px;
    margin-right: 0;
  }

  .el-dialog__title {
    color: var(--tech-cyan);
    font-weight: 600;
  }

  .el-dialog__headerbtn .el-dialog__close {
    color: rgba(255, 255, 255, 0.55);
    &:hover {
      color: var(--tech-cyan);
    }
  }

  .el-dialog__body {
    padding: 20px;
    background: var(--tech-panel);
  }
}

.dialog-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 16px;
}

.dialog-table {
  :deep(.el-table) {
    background: transparent;
    --el-table-bg-color: transparent;
    --el-table-tr-bg-color: transparent;
    --el-table-header-bg-color: rgba(34, 211, 238, 0.12);
    --el-table-row-hover-bg-color: rgba(34, 211, 238, 0.08);
    --el-table-border-color: var(--tech-border-soft);
    --el-table-text-color: rgba(255, 255, 255, 0.88);
    --el-table-header-text-color: var(--tech-cyan);
    color: rgba(255, 255, 255, 0.88);

    &::before,
    &::after {
      background-color: var(--tech-border-soft);
    }

    th.el-table__cell {
      background: rgba(34, 211, 238, 0.12) !important;
      color: var(--tech-cyan);
      font-weight: 600;
      border-bottom-color: var(--tech-border-soft);
    }

    td.el-table__cell {
      border-bottom-color: var(--tech-border-soft);
      background: rgba(8, 28, 58, 0.28) !important;
      color: rgba(255, 255, 255, 0.88);
    }

    .el-table__body-wrapper tbody tr:nth-child(even) td.el-table__cell {
      background: rgba(12, 36, 72, 0.42) !important;
    }

    .el-table__body-wrapper tbody tr:hover > td.el-table__cell {
      background: rgba(34, 211, 238, 0.08) !important;
      color: var(--tech-cyan);
    }

    .el-table__empty-text {
      color: rgba(255, 255, 255, 0.45);
    }

    .el-table__row--striped td.el-table__cell {
      background: rgba(12, 36, 72, 0.42) !important;
    }
  }
}

// 按钮样式
:deep(.el-button--success) {
  border: none;
  color: #fff;
  background: #67C23A;
  display: flex;
  align-items: center;
  gap: 4px;
  &:hover {
    background: #85CE61;
    border-color: #85CE61;
  }
}

:deep(.el-button--warning) {
  border: none;
  color: #fff;
  background: #E6A23C;
  display: flex;
  align-items: center;
  gap: 4px;
  &:hover {
    background: #EBB563;
    border-color: #EBB563;
  }
}

:deep(.el-button--danger) {
  display: flex;
  align-items: center;
  gap: 4px;
}

:deep(.el-button--info) {
  border-color: var(--tech-border-soft);
  color: var(--tech-cyan);
  background: rgba(34, 211, 238, 0.1);
  display: flex;
  align-items: center;
  gap: 4px;
  &:hover {
    background: rgba(34, 211, 238, 0.2);
    border-color: var(--tech-cyan);
  }
}

:deep(.el-tag) {
  border-color: var(--tech-border-soft);
  background: rgba(34, 211, 238, 0.08);
  color: var(--tech-cyan);
}

:deep(.el-tag--success) {
  background: rgba(103, 194, 58, 0.15);
  border-color: rgba(103, 194, 58, 0.3);
  color: #95d475;
}

:deep(.el-tag--warning) {
  background: rgba(230, 162, 60, 0.15);
  border-color: rgba(230, 162, 60, 0.3);
  color: #f0d680;
}

:deep(.el-tag--danger) {
  background: rgba(245, 108, 108, 0.15);
  border-color: rgba(245, 108, 108, 0.3);
  color: #f89898;
}

:deep(.el-tag--primary) {
  background: rgba(34, 211, 238, 0.12);
  border-color: rgba(34, 211, 238, 0.25);
  color: var(--tech-cyan);
}

// 响应式
@media (max-width: 768px) {
  .cards-container {
    grid-template-columns: 1fr;
  }
  .search-row {
    .search-input {
      width: 100%;
    }
  }
  .topic-card {
    flex-wrap: wrap;
  }
  .card-cover {
    width: 100%;
    height: 120px;
  }
  .card-actions {
    flex-direction: row;
    flex-wrap: wrap;
    border-left: none;
    border-top: 1px solid var(--tech-border-soft);
    width: 100%;
    justify-content: flex-start;
    padding: 10px 14px;
  }
}
</style>
