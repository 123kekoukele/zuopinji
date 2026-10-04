<template>
  <div class="class-page">
    <div class="app-contain">
      <!-- 搜索与操作区 -->
      <el-card class="search-card" shadow="hover">
        <el-form :model="searchQuery" class="search_form" inline>
          <el-form-item label="专业">
            <el-input
              v-model="searchQuery.zhuanye"
              placeholder="所属专业"
              clearable
              style="width: 160px"
              @keyup.enter="searchClick"
            />
          </el-form-item>
          <el-form-item label="班级名称">
            <el-input
              v-model="searchQuery.banjimingcheng"
              placeholder="输入班级名称搜索"
              clearable
              style="width: 200px"
              @keyup.enter="searchClick"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchClick" size="default">搜索</el-button>
            <el-button @click="resetSearch" size="default">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="btn-row">
          <el-button type="success" @click="addClick" v-if="btnAuth(authTable,'新增')">
            新增班级
          </el-button>
          <el-button type="info" :disabled="selRows.length!==1" @click="infoClick(null)"
            v-if="btnAuth(authTable,'查看')">
            详情
          </el-button>
          <el-button type="primary" :disabled="selRows.length!==1" @click="editClick"
            v-if="btnAuth(authTable,'修改')">
            修改
          </el-button>
          <el-button type="danger" :disabled="!selRows.length" @click="delClick(null)"
            v-if="btnAuth(authTable,'删除')">
            批量删除
          </el-button>
        </div>
      </el-card>

      <!-- 表格视图 -->
      <el-card class="content-card" shadow="hover">
        <el-table
          v-show="btnAuth(authTable,'查看')"
          v-loading="listLoading"
          border
          :stripe="false"
          @selection-change="handleSelectionChange"
          ref="table"
          :data="list || []"
          @row-click="listChange"
          class="class-table"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" width="70" align="center">
            <template #default="scope">{{ scope.$index + 1 }}</template>
          </el-table-column>
          <el-table-column label="所属专业" prop="zhuanye" min-width="140" show-overflow-tooltip />
          <el-table-column label="班级名称" prop="banjimingcheng" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="240" align="center" fixed="right">
            <template #default="scope">
              <el-button type="success" link size="small" @click.stop="openStudentsDialog(scope.row)"
                v-if="btnAuth(authTable,'查看')">
                查看学生
              </el-button>
              <el-button type="info" link size="small" @click.stop="infoClick(scope.row.id)"
                v-if="btnAuth(authTable,'查看')">
                详情
              </el-button>
              <el-button type="primary" link size="small" @click.stop="editClickRow(scope.row)"
                v-if="btnAuth(authTable,'修改')">
                编辑
              </el-button>
              <el-button type="danger" link size="small" @click.stop="delClick(scope.row.id)"
                v-if="btnAuth(authTable,'删除')">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          class="pagination"
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
      </el-card>
    </div>

    <formModel ref="formRef" @formModelChange="formModelChange"></formModel>

    <el-dialog v-model="studentsDialogVisible" :title="studentsDialogTitle" width="900" destroy-on-close>
      <div v-if="currentClassRow" style="margin-bottom: 12px; color: #606266; font-size: 13px;">
        专业：{{ currentClassRow.zhuanye || '—' }}　班级：{{ currentClassRow.banjimingcheng || '—' }}
        <el-tag type="info" style="margin-left: 8px">共 {{ classStudents.length }} 人</el-tag>
      </div>
      <el-table :data="classStudents" stripe max-height="420" empty-text="暂无学生（请核对学生档案「班级」「专业」是否与班级信息一致）">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="xuehao" label="学号" width="120" />
        <el-table-column prop="xueshengxingming" label="姓名" width="100" />
        <el-table-column prop="xingbie" label="性别" width="70" align="center" />
        <el-table-column label="年级" width="90">
          <template #default="scope">
            {{ scope.row.nianji ? scope.row.nianji + '级' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="zhuanye" label="专业" min-width="120" show-overflow-tooltip />
        <el-table-column prop="banji" label="班级" min-width="120" show-overflow-tooltip />
        <el-table-column prop="shoujihaoma" label="手机" width="120" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, nextTick, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'

const context = getCurrentInstance()?.appContext.config.globalProperties
import formModel from './formModel.vue'

// 后端接口使用的表名
const tableName = 'banjixinxi'
// 权限配置中使用的表名（菜单里配置的是 banji）
const authTable = 'banji'
const formName = '班级信息'

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
const total = ref(0)
const layouts = ref(['total','prev','pager','next','sizes'])

onMounted(() => {})

const listChange = (row) => {
  if (!table.value) return
  nextTick(() => {
    table.value.clearSelection()
    table.value.toggleRowSelection(row)
  })
}

const getList = () => {
  listLoading.value = true
  let params = JSON.parse(JSON.stringify(listQuery.value))
  params['sort'] = 'id'
  params['order'] = 'desc'
  if (searchQuery.value.zhuanye && searchQuery.value.zhuanye !== '') {
    params['zhuanye'] = '%' + searchQuery.value.zhuanye + '%'
  }
  if (searchQuery.value.banjimingcheng && searchQuery.value.banjimingcheng !== '') {
    params['banjimingcheng'] = '%' + searchQuery.value.banjimingcheng + '%'
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
      context?.$toolUtil.message('删除成功', 'success', () => getList())
    })
  })
}

const handleSelectionChange = (e) => {
  selRows.value = e
}

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

const btnAuth = (e, a) => context?.$toolUtil.isAuth(e, a)

const searchClick = () => {
  listQuery.value.page = 1
  getList()
}

const resetSearch = () => {
  searchQuery.value = {}
  listQuery.value.page = 1
  getList()
}

const formRef = ref(null)
const formModelChange = () => searchClick()

// 查看学生弹窗（与模板 studentsDialog / openStudentsDialog 对应）
const studentsDialogVisible = ref(false)
const studentsDialogTitle = ref('本班学生')
const currentClassRow = ref(null)
const classStudents = ref([])

const openStudentsDialog = (row) => {
  if (!row || !row.banjimingcheng) {
    context?.$toolUtil.message('班级名称无效', 'warning')
    return
  }
  currentClassRow.value = row
  studentsDialogTitle.value = '本班学生'
  context?.$http({
    url: 'banjixinxi/students',
    method: 'get',
    params: {
      banjimingcheng: row.banjimingcheng,
      zhuanye: row.zhuanye || ''
    }
  }).then((res) => {
    classStudents.value = res.data?.data || []
    studentsDialogVisible.value = true
  }).catch(() => {
    classStudents.value = []
    studentsDialogVisible.value = true
    context?.$toolUtil.message('加载学生列表失败', 'error')
  })
}

const addClick = () => formRef.value.init()

const editClick = () => {
  if (selRows.value.length) formRef.value.init(selRows.value[0].id, 'edit')
}

const editClickRow = (row) => {
  if (row?.id) formRef.value.init(row.id, 'edit')
}

const infoClick = (id = null) => {
  if (id) formRef.value.init(id, 'info')
  else if (selRows.value.length) formRef.value.init(selRows.value[0].id, 'info')
}

const init = () => getList()
init()
</script>

<style lang="scss" scoped>
.class-page {
  padding: 16px 20px;
  background: transparent;
  min-height: 100%;
  box-sizing: border-box;
}

// 搜索卡片 - 融入白色主题
.search-card {
  margin-bottom: 16px;
  border-radius: 10px;
  background: var(--background-white) !important;
  border: 1px solid rgba(34, 211, 238, 0.18) !important;
  box-shadow: 0 0 18px rgba(34, 211, 238, 0.06);

  :deep(.el-card__body) {
    padding: 16px 20px;
  }

  :deep(.el-form-item__label) {
    color: var(--text-primary) !important;
    font-weight: 500;
  }

  :deep(.el-input__wrapper) {
    border: 1px solid rgba(34, 211, 238, 0.2);
    border-radius: 6px;
    box-shadow: none;
    transition: all 0.2s;
  }

  :deep(.el-input__wrapper:hover) {
    border-color: rgba(34, 211, 238, 0.4);
  }

  :deep(.el-input__wrapper.is-focus) {
    border-color: #22d3ee;
    box-shadow: 0 0 0 2px rgba(34, 211, 238, 0.15);
  }

  :deep(.el-form-item) {
    margin-bottom: 0;
  }
}

.btn-row {
  margin-top: 14px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;

  :deep(.el-button) {
    border-radius: 6px;
    padding: 8px 16px;
    font-size: 14px;
    height: 36px;
    transition: all 0.2s;
    font-weight: 400;
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

// 内容卡片 - 融入白色主题
.content-card {
  border-radius: 10px;
  background: var(--background-white) !important;
  border: 1px solid rgba(34, 211, 238, 0.18) !important;
  box-shadow: 0 0 18px rgba(34, 211, 238, 0.06);

  :deep(.el-card__body) {
    padding: 16px;
  }
}

.class-table {
  background: transparent;
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
  margin-top: 16px;
  display: flex;
  justify-content: center;

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
</style>
