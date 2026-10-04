<template>
  <div class="teacher_page">
    <div class="app-contain">
      <!-- 查询与操作区域 -->
      <div class="list_search_view">
        <el-form :model="searchQuery" class="search_form">
          <div class="search_view">
            <div class="search_label">
              教师姓名：
            </div>
            <div class="search_box">
              <el-input
                class="search_inp"
                v-model="searchQuery.jiaoshixingming"
                placeholder="教师姓名"
                clearable
              />
            </div>
          </div>
          <div class="search_view">
            <div class="search_label">
              专业：
            </div>
            <div class="search_box">
              <el-select v-model="searchQuery.zhuanye" placeholder="请选择专业" clearable class="search_inp">
                <el-option v-for="item in zhuanyeOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </div>
          </div>
          <div class="search_btn_view">
            <el-button
              class="search_btn"
              type="primary"
              @click="searchClick()"
              size="small"
            >
              搜索
            </el-button>
          </div>
        </el-form>

        <div class="btn_view">
          <el-button
            type="success"
            @click="addClick"
            v-if="btnAuth('jiaoshi','新增')"
          >
            新增
          </el-button>
          <el-button
            type="warning"
            @click="importClick"
            v-if="btnAuth('jiaoshi','新增')"
          >
            批量导入
          </el-button>
          <el-button
            v-if="btnAuth('jiaoshi','查看')"
            type="info"
            :disabled="selRows.length!==1"
            @click="infoClick(null)"
          >
            详情
          </el-button>
          <el-button
            type="primary"
            :disabled="selRows.length!==1"
            @click="editClick"
            v-if="btnAuth('jiaoshi','修改')"
          >
            修改
          </el-button>
          <el-button
            type="danger"
            :disabled="!selRows.length"
            @click="delClick(null)"
            v-if="btnAuth('jiaoshi','删除')"
          >
            删除
          </el-button>
        </div>
      </div>

      <!-- 教师列表区域：复用原有增删改查接口 -->
      <el-table
        class="teacher_table"
        v-loading="listLoading"
        border
        :stripe="false"
        @selection-change="handleSelectionChange"
        ref="table"
        v-if="btnAuth('jiaoshi','查看')"
        :data="list"
        @row-click="listChange"
      >
        <el-table-column
          type="selection"
          width="55"
          align="center"
          header-align="center"
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
          label="教师工号"
        >
          <template #default="scope">
            {{ scope.row.jiaoshigonghao }}
          </template>
        </el-table-column>
        <el-table-column
          :resizable="true"
          :sortable="false"
          align="left"
          header-align="left"
          label="教师姓名"
        >
          <template #default="scope">
            {{ scope.row.jiaoshixingming }}
          </template>
        </el-table-column>
        <el-table-column
          :resizable="true"
          :sortable="false"
          align="left"
          header-align="left"
          label="联系电话"
        >
          <template #default="scope">
            {{ scope.row.lianxidianhua }}
          </template>
        </el-table-column>
        <el-table-column
          :resizable="true"
          :sortable="false"
          align="left"
          header-align="left"
          label="性别"
          width="80"
        >
          <template #default="scope">
            {{ scope.row.xingbie }}
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
          width="120"
          :resizable="true"
          :sortable="false"
          align="center"
          header-align="center"
        >
          <template #default="scope">
            <el-button
              type="info"
              size="small"
              v-if="btnAuth('jiaoshi','查看')"
              @click="infoClick(scope.row.id)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="teacher_pagination"
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

    <formModel ref="formRef" @formModelChange="formModelChange"></formModel>
    <importForm
      ref="importRef"
      :tableName="tableName"
      :action="tableName"
      tip="请上传包含“教师工号、密码、教师姓名、联系电话、性别、专业”列的 Excel 文件；密码有值则按表中值导入，密码为空则默认取联系电话后六位"
      @importChange="getList"
    />
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
import importForm from '@/components/common/importForm.vue'

// 基础信息
const tableName = 'jiaoshi'
const formName = '教师'
const route = useRoute()

onMounted(()=>{
})

// 列表数据
const list = ref(null)
const table = ref(null)
const listQuery = ref({
  page: 1,
  limit: 20,
  sort: 'id',
  order: 'desc'
})
const searchQuery = ref({})
const zhuanyeOptions = ref(['地理信息科学', '地理科学', '风景园林', '测绘工程', '城乡规划'])
const selRows = ref([])
const listLoading = ref(false)
const importRef = ref(null)

const listChange = (row) =>{
  nextTick(()=>{
    table.value.clearSelection()
    table.value.toggleRowSelection(row)
  })
}

// 列表
const getList = () => {
  listLoading.value = true
  let params = JSON.parse(JSON.stringify(listQuery.value))
  params['sort'] = 'id'
  params['order'] = 'desc'
  if (searchQuery.value.jiaoshixingming && searchQuery.value.jiaoshixingming !== '') {
    params['jiaoshixingming'] = '%' + searchQuery.value.jiaoshixingming + '%'
  }
  if (searchQuery.value.zhuanye && searchQuery.value.zhuanye !== '') {
    params['zhuanye'] = searchQuery.value.zhuanye
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

// 删
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

// 多选
const handleSelectionChange = (e) => {
  selRows.value = e
}

// 分页
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

// 权限验证
const btnAuth = (e,a)=>{
  return context?.$toolUtil.isAuth(e,a)
}

// 搜索
const searchClick = () => {
  listQuery.value.page = 1
  getList()
}

// 表单
const formRef = ref(null)
const formModelChange=()=>{
  searchClick()
}
const addClick = ()=>{
  formRef.value.init()
}
const importClick = ()=>{
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

// 预览文件
const preClick = (file) =>{
  if(!file){
    context?.$toolUtil.message('文件不存在','error')
  }
  window.open(context?.$config.url + file)
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
  }).then(({ data }) => {
    const binaryData = [];
    binaryData.push(data);
    const objectUrl = window.URL.createObjectURL(new Blob(binaryData, {
      type: 'application/pdf;chartset=UTF-8'
    }))
    const a = document.createElement('a')
    a.href = objectUrl
    a.download = arr
    a.dispatchEvent(new MouseEvent('click', {
      bubbles: true,
      cancelable: true,
      view: window
    }))
    window.URL.revokeObjectURL(data)
  })
}

// 初始化
const init = () => {
  getList()
}
init()
</script>

<style lang="scss" scoped>
// 教师列表页面样式 - 融入青蓝主题
.teacher_page {
  padding: 16px 20px;
  background: transparent;
  min-height: 100%;
  box-sizing: border-box;
}

/* 查询与按钮区域 - 融入白色主题 */
.list_search_view {
  margin: 0 0 24px;
  padding: 20px;
  background: var(--background-white);
  border-radius: 10px;
  border: 1px solid rgba(34, 211, 238, 0.18);
  box-shadow: 0 0 18px rgba(34, 211, 238, 0.06);

  .search_form {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 16px;

    .search_view {
      display: flex;
      align-items: center;
      gap: 8px;

      .search_label {
        color: var(--text-primary);
        font-size: 14px;
        font-weight: 500;
        white-space: nowrap;
      }

      .search_box {
        width: 200px;

        :deep(.search_inp .el-input__wrapper) {
          border: 1px solid rgba(34, 211, 238, 0.2);
          border-radius: 6px;
          box-shadow: none;
          transition: all 0.2s;
          height: 36px;
          padding: 0 12px;
        }

        :deep(.search_inp .el-input__wrapper:hover) {
          border-color: rgba(34, 211, 238, 0.4);
        }

        :deep(.search_inp.is-focus .el-input__wrapper) {
          border-color: #22d3ee;
          box-shadow: 0 0 0 2px rgba(34, 211, 238, 0.15);
        }
      }
    }

    .search_btn_view {
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
  }

  .btn_view {
    margin-top: 16px;
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
}

/* 表格整体样式 */
.teacher_table {
  background: transparent;
  border: none;
  border-radius: 8px;
  overflow: hidden;
  padding: 0;
  width: 100%;

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

/* 分页器 */
.teacher_pagination {
  margin: 16px 0 0;
  text-align: center;

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
</style>