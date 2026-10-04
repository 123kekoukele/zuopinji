<template>
  <div class="topic-type-page">
    <div class="app-contain">
      <!-- 搜索与操作区 -->
      <el-card class="search-card" shadow="hover">
        <el-form :model="searchQuery" class="search_form" inline>
          <el-form-item label="题目类型">
            <el-input
              v-model="searchQuery.timuleixing"
              placeholder="输入类型名称搜索"
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
        <div class="page-tip">
          题目类型自动汇总自「题目信息」中的题目；教师仅显示本人题目类型，管理员按专业范围展示。请在「题目信息」模块新增或修改题目时维护类型。
        </div>
      </el-card>

      <!-- 视图切换：表格 / 卡片墙 -->
      <el-card class="content-card" shadow="hover">
        <div class="view-tabs">
          <span
            :class="['tab-item', viewMode==='table'?'active':'']"
            @click="viewMode='table'"
          >
            列表
          </span>
          <span
            :class="['tab-item', viewMode==='card'?'active':'']"
            @click="viewMode='card'"
          >
            卡片
          </span>
        </div>

        <!-- 表格视图 -->
        <div v-show="viewMode==='table'" class="table-wrap">
          <el-table
            v-show="btnAuth('timuleixing','查看')"
            v-loading="listLoading"
            border
            :stripe="true"
            ref="table"
            :data="list || []"
            class="topic-table"
          >
            <el-table-column label="序号" width="70" align="center">
              <template #default="scope">{{ scope.$index + 1 }}</template>
            </el-table-column>
            <el-table-column label="题目类型" prop="timuleixing" min-width="220" />
            <el-table-column label="关联题目数" prop="topicCount" width="120" align="center" />
          </el-table>
        </div>

        <!-- 卡片视图：每种类型一个卡片 -->
        <div v-show="viewMode==='card'" class="card-wrap">
          <div v-loading="listLoading" class="type-cards">
            <div
              v-for="item in (list || [])"
              :key="item.timuleixing"
              class="type-card"
            >
              <div class="type-name">{{ item.timuleixing }}</div>
              <div class="type-meta">
                <span class="topic-count">关联题目 {{ item.topicCount || 0 }}</span>
              </div>
            </div>
            <div v-if="!listLoading && (!list || !list.length)" class="empty-tip">
              暂无题目类型，请先在「题目信息」中发布题目
            </div>
          </div>
        </div>

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
  </div>
</template>

<script setup>
import { ref, getCurrentInstance } from 'vue'

const context = getCurrentInstance()?.appContext.config.globalProperties

const tableName = 'timuleixing'

const list = ref(null)
const listQuery = ref({
  page: 1,
  limit: 20,
  sort: 'id',
  order: 'desc'
})
const searchQuery = ref({})
const listLoading = ref(false)
const total = ref(0)
const layouts = ref(['total','prev','pager','next','sizes'])
const viewMode = ref('table')

const getList = () => {
  listLoading.value = true
  let params = JSON.parse(JSON.stringify(listQuery.value))
  params['sort'] = 'id'
  params['order'] = 'desc'
  if (searchQuery.value.timuleixing && searchQuery.value.timuleixing !== '') {
    params['timuleixing'] = '%' + searchQuery.value.timuleixing + '%'
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

const init = () => getList()
init()
</script>

<style lang="scss" scoped>
.topic-type-page {
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
}

.page-tip {
  margin-top: 12px;
  padding: 10px 12px;
  border-radius: 6px;
  font-size: 13px;
  line-height: 1.6;
  color: var(--text-secondary);
  background: rgba(34, 211, 238, 0.08);
  border: 1px solid rgba(34, 211, 238, 0.16);
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

.view-tabs {
  margin-bottom: 14px;
  display: flex;
  gap: 4px;
}

.tab-item {
  padding: 6px 14px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
}

.tab-item:hover {
  color: #0891b2;
  background: rgba(34, 211, 238, 0.08);
}

.tab-item.active {
  color: #0891b2;
  background: rgba(34, 211, 238, 0.12);
  font-weight: 500;
}

.table-wrap {
  margin-bottom: 12px;
}

.topic-table {
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
            color: #fff;
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

// 卡片视图
.card-wrap {
  min-height: 120px;
}

.type-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
}

.type-card {
  padding: 14px 16px;
  background: var(--background-white);
  border: 1px solid rgba(34, 211, 238, 0.18);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.type-card:hover {
  border-color: #22d3ee;
  box-shadow: 0 4px 12px rgba(34, 211, 238, 0.2);
  transform: translateY(-2px);
}

.type-name {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.type-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.topic-count {
  font-size: 12px;
  color: var(--text-secondary);
}

.type-actions {
  display: flex;
  gap: 8px;
}

.empty-tip {
  grid-column: 1 / -1;
  padding: 40px;
  text-align: center;
  color: var(--text-placeholder);
  font-size: 14px;
}

// 分页器样式
.pagination {
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
}
</style>
