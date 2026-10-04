<template>
  <div class="zhongqijiancha-page">
    <div class="app-contain">
      <!-- 搜索与操作区 -->
      <el-card class="search-card" shadow="hover">
        <el-form :model="searchQuery" class="search_form" inline>
          <el-form-item label="题目编号">
            <el-input
              v-model="searchQuery.timubianhao"
              placeholder="题目编号"
              clearable
              style="width: 160px"
              @keyup.enter="searchClick"
            />
          </el-form-item>
          <el-form-item label="课题名称">
            <el-input
              v-model="searchQuery.ketimingcheng"
              placeholder="课题名称"
              clearable
              style="width: 160px"
              @keyup.enter="searchClick"
            />
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select v-model="searchQuery.shenhezhuangtai" placeholder="全部" clearable style="width: 120px">
              <el-option v-for="item in AUDIT_STATUS_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="学生姓名">
            <el-input
              v-model="searchQuery.xueshengxingming"
              placeholder="学生姓名"
              clearable
              style="width: 120px"
              @keyup.enter="searchClick"
            />
          </el-form-item>
          <el-form-item label="指导教师">
            <el-input
              v-model="searchQuery.jiaoshixingming"
              placeholder="指导教师"
              clearable
              :disabled="isTeacherRole"
              style="width: 120px"
              @keyup.enter="searchClick"
            />
          </el-form-item>
          <el-form-item label="专业">
            <el-select v-model="searchQuery.zhuanye" placeholder="全部" clearable style="width: 140px">
              <el-option v-for="item in zhuanyeOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="题目类型">
            <el-select v-model="searchQuery.timuleixing" placeholder="全部" clearable style="width: 120px">
              <el-option v-for="item in timuleixingOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchClick" size="default">搜索</el-button>
            <el-button @click="resetSearch" size="default">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="btn-row">
          <el-button type="success" @click="addClick" v-if="btnAuth('zhongqijiancha','新增')">
            新增
          </el-button>
          <el-button type="info" :disabled="selRows.length!==1" @click="infoClick(null)"
            v-if="btnAuth('zhongqijiancha','查看')">
            详情
          </el-button>
          <el-button type="primary" :disabled="selRows.length!==1" @click="editClick"
            v-if="btnAuth('zhongqijiancha','修改')">
            修改
          </el-button>
          <el-button type="danger" :disabled="!selRows.length" @click="delClick(null)"
            v-if="btnAuth('zhongqijiancha','删除')">
            批量删除
          </el-button>
        </div>
      </el-card>

      <!-- 表格内容区 -->
      <el-card class="content-card" shadow="hover">
        <el-table
          v-show="btnAuth('zhongqijiancha','查看')"
          v-loading="listLoading"
          border
          :stripe="false"
          @selection-change="handleSelectionChange"
          ref="table"
          :data="list || []"
          @row-click="listChange"
          class="zhongqijiancha-table"
        >
          <el-table-column type="selection" width="50" align="center" />
          <el-table-column label="序号" width="70" align="center">
            <template #default="scope">{{ scope.$index + 1 }}</template>
          </el-table-column>
          <el-table-column label="题目编号" prop="timubianhao" min-width="100" show-overflow-tooltip />
          <el-table-column label="课题名称" min-width="180" show-overflow-tooltip>
            <template #default="scope">
              <div class="topic-name-cell">
                <span>{{ scope.row.ketimingcheng || '-' }}</span>
                <el-tag v-if="isTopicDeleted(scope.row)" type="danger" size="small" effect="dark" class="topic-deleted-tag">
                  题目已删除
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="题目类型" prop="timuleixing" width="90" />
          <el-table-column label="专业" prop="zhuanye" width="90" />
          <el-table-column label="指导教师" min-width="110">
            <template #default="scope">
              {{ scope.row.jiaoshigonghao }} / {{ scope.row.jiaoshixingming }}
            </template>
          </el-table-column>
          <el-table-column label="学生" min-width="110">
            <template #default="scope">
              {{ scope.row.xuehao }} / {{ scope.row.xueshengxingming }}
            </template>
          </el-table-column>
          <el-table-column label="检查附件" width="90" align="center">
            <template #default="scope">
              <el-button
                v-if="scope.row.zhongqifujian"
                type="primary"
                link
                size="small"
                @click.stop="download(scope.row.zhongqifujian)"
              >
                下载
              </el-button>
              <span v-else class="text-muted">无</span>
            </template>
          </el-table-column>
          <el-table-column label="提交时间" prop="zhongqishijian" width="110" />
          <el-table-column label="审核状态" width="96" align="center">
            <template #default="scope">
              <el-tag :type="auditTagType(scope.row.shenhezhuangtai)" size="small">
                {{ formatAuditStatus(scope.row.shenhezhuangtai) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template #default="scope">
              <el-button type="primary" link size="small" @click.stop="infoClick(scope.row.id)"
                v-if="btnAuth('zhongqijiancha','查看')">
                详情
              </el-button>
              <el-button type="primary" link size="small" @click.stop="editClickRow(scope.row)"
                v-if="btnAuth('zhongqijiancha','修改')">
                编辑
              </el-button>
              <el-button type="danger" link size="small" @click.stop="delClick(scope.row.id)"
                v-if="btnAuth('zhongqijiancha','删除')">
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

    <formModel ref="formRef" @formModelChange="formModelChange" />
  </div>
</template>

<script setup>
import { downloadStoredFile } from '@/utils/fileDownload'
import { auditTagType, formatAuditStatus, AUDIT_STATUS_OPTIONS } from '@/utils/workflowAuditStatus'
import { appendWorkflowSearchParams, useWorkflowListFilter } from '@/utils/workflowListFilter'
import { isTopicDeleted } from '@/utils/topicDeleted'
import { ref, getCurrentInstance, nextTick, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'

const context = getCurrentInstance()?.appContext.config.globalProperties
import formModel from './formModel.vue'

const tableName = 'zhongqijiancha'
const formName = '中期检查'

const list = ref(null)
const table = ref(null)
const listQuery = ref({
  page: 1,
  limit: 20,
  sort: 'id',
  order: 'desc'
})
const {
  searchQuery,
  zhuanyeOptions,
  timuleixingOptions,
  isTeacherRole,
  resetWorkflowSearch,
  initWorkflowFilter
} = useWorkflowListFilter(context)
const selRows = ref([])
const listLoading = ref(false)
const total = ref(0)
const layouts = ref(['total','prev','pager','next','sizes'])

onMounted(() => {
  initWorkflowFilter()
})

const listChange = (row) => {
  if (!table.value) return
  nextTick(() => {
    table.value.clearSelection()
    table.value.toggleRowSelection(row)
  })
}

const getList = () => {
  listLoading.value = true
  const params = JSON.parse(JSON.stringify(listQuery.value))
  params.sort = 'id'
  params.order = 'desc'
  appendWorkflowSearchParams(params, searchQuery.value)
  context?.$http({
    url: `${tableName}/page`,
    method: 'get',
    params
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
  resetWorkflowSearch()
  listQuery.value.page = 1
  getList()
}

const formRef = ref(null)
const formModelChange = () => searchClick()

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

const download = (file) => {
  if (!file) {
    context?.$toolUtil.message('文件不存在', 'error')
    return
  }
  downloadStoredFile(file)
    .catch((err) => {
      context?.$toolUtil.message(err?.message || '下载失败，请稍后重试', 'error')
    })
}

const init = () => getList()
init()
</script>

<style lang="scss" scoped>
.zhongqijiancha-page {
  padding: 16px 20px;
  background: transparent;
  min-height: 100%;
  box-sizing: border-box;
}

.topic-name-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
}

.topic-deleted-tag {
  flex-shrink: 0;
}

.search-card {
  margin-bottom: 16px;
  border-radius: 10px;
  background: var(--tech-panel) !important;
  border: 1px solid var(--tech-border-soft) !important;
  box-shadow: 0 0 16px var(--tech-glow);
}

.search-card :deep(.el-card__body) {
  padding: 14px 16px;
}

.search-card :deep(.el-form-item__label) {
  color: var(--text-regular) !important;
}

.btn-row {
  margin-top: 12px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.content-card {
  border-radius: 10px;
  background: var(--tech-panel) !important;
  border: 1px solid var(--tech-border-soft) !important;
  box-shadow: 0 0 16px var(--tech-glow);
}

.content-card :deep(.el-card__body) {
  padding: 16px;
}

.zhongqijiancha-table {
  background: transparent;
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: rgba(8, 28, 58, 0.22);
  --el-table-row-hover-bg-color: rgba(34, 211, 238, 0.08);
}

// 表格样式 - 统一深蓝主题，避免白底相间
.el-table {
  padding: 0;
  background: transparent;
  width: 100%;
  border: none;
  border-radius: 8px;
  overflow: hidden;

  :deep(.el-table__header-wrapper) {
    thead {
      tr {
        background: linear-gradient(135deg, rgba(34, 211, 238, 0.9) 0%, rgba(8, 145, 178, 0.95) 100%);

        th {
          padding: 12px 0;
          background: transparent;
          border: none;
          text-align: center;

          .cell {
            padding: 0 10px;
            font-weight: 600;
            color: #fff;
          }
        }
      }
    }
  }

  :deep(.el-table__body-wrapper) {
    tbody {
      tr {
        background: var(--background-white);
        transition: all 0.2s;

        td {
          padding: 12px 0;
          color: var(--text-primary);
          background: var(--background-white) !important;
          border-bottom: 1px solid rgba(34, 211, 238, 0.1);
          text-align: center;
        }
      }

      tr:hover td {
        background: rgba(34, 211, 238, 0.08) !important;
        color: var(--text-primary);
      }
    }
  }

  :deep(.el-table__row--striped) td.el-table__cell {
    background: var(--background-white) !important;
  }
}

.text-muted {
  color: var(--text-secondary);
  font-size: 12px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;

  :deep(.el-pagination__total),
  :deep(.btn-prev),
  :deep(.btn-next),
  :deep(.el-pager .number) {
    color: var(--text-primary);
    background: var(--background-white);
    border: 1px solid rgba(34, 211, 238, 0.2);
    border-radius: 6px;
  }

  :deep(.el-pager .number.is-active) {
    color: #fff;
    background: linear-gradient(135deg, #22d3ee 0%, #0891b2 100%);
    border-color: #22d3ee;
  }
}
</style>
