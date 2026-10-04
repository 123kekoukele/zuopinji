<template>
  <div>
    <el-dialog v-model="formVisible" :title="formTitle" width="80%" destroy-on-close :fullscreen="false">
      <el-form
        class="formModel_form"
        ref="formRef"
        :model="form"
        label-width="120px"
        :rules="rules"
      >
        <el-row>
          <el-col :span="24">
            <el-form-item label="所属专业" prop="zhuanye">
              <el-select
                class="list_sel"
                v-model="form.zhuanye"
                placeholder="请选择所属专业"
                :disabled="!isAdd || disabledForm.zhuanye || isMajorLocked"
                clearable
              >
                <el-option v-for="item in zhuanyeOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="班级名称" prop="banjimingcheng">
              <el-input
                class="list_inp"
                v-model="form.banjimingcheng"
                placeholder="须与学生档案中的「班级」字段一致，便于统计本班学生"
                type="text"
                :readonly="type === 'info' || disabledForm.banjimingcheng"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer v-if="type !== 'info'">
        <span class="formModel_btn_box">
          <el-button class="formModel_cancel" @click="closeClick">取消</el-button>
          <el-button class="formModel_confirm" type="primary" @click="save">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance, defineEmits, onMounted } from 'vue'

const context = getCurrentInstance()?.appContext.config.globalProperties
const emit = defineEmits(['formModelChange'])

const tableName = 'banjixinxi'
const formName = '班级信息'

const form = ref({})
const disabledForm = ref({
  zhuanye: false,
  banjimingcheng: false
})
const zhuanyeOptions = Object.freeze(['地理信息科学', '风景园林', '城乡规划', '地理科学', '测绘工程'])
const isMajorLocked = ref(false)
const formVisible = ref(false)
const isAdd = ref(false)
const formTitle = ref('')
const rules = ref({
  zhuanye: [{ required: true, message: '请选择所属专业', trigger: 'change' }],
  banjimingcheng: [{ required: true, message: '请输入班级名称', trigger: 'blur' }]
})

const formRef = ref(null)
const id = ref(0)
const type = ref('')

const crossRow = ref('')
const crossTable = ref('')
const crossTips = ref('')
const crossColumnName = ref('')
const crossColumnValue = ref('')

const resetForm = () => {
  form.value = {
    zhuanye: '',
    banjimingcheng: ''
  }
}

const getInfo = () => {
  context?.$http({
    url: `${tableName}/info/${id.value}`,
    method: 'get'
  }).then(res => {
    form.value = res.data.data
    if (form.value.zhuanye != null && typeof form.value.zhuanye !== 'string') {
      form.value.zhuanye = String(form.value.zhuanye)
    }
    const sessionTable = context?.$toolUtil.storageGet('sessionTable')
    context?.$http({
      url: `${sessionTable}/session`,
      method: 'get'
    }).then(sessionRes => {
      const major = sessionRes?.data?.data?.zhuanye ? String(sessionRes.data.data.zhuanye).trim() : ''
      if (major && (sessionTable === 'users' || sessionTable === 'jiaoshi')) {
        isMajorLocked.value = true
        disabledForm.value.zhuanye = true
      }
      formVisible.value = true
    }).catch(() => {
      formVisible.value = true
    })
  })
}

const init = (
  formId = null,
  formType = 'add',
  formNames = '',
  row = null,
  table = null,
  statusColumnName = null,
  tips = null,
  statusColumnValue = null
) => {
  resetForm()
  disabledForm.value = { zhuanye: false, banjimingcheng: false }
  isMajorLocked.value = false
  type.value = formType
  if (formId) {
    id.value = formId
  }
  if (formType == 'add') {
    isAdd.value = true
    formTitle.value = '新增' + formName
    loadSessionMajor()
    formVisible.value = true
  } else if (formType == 'info') {
    isAdd.value = false
    formTitle.value = '查看' + formName
    getInfo()
  } else if (formType == 'edit') {
    isAdd.value = false
    formTitle.value = '修改' + formName
    getInfo()
  } else if (formType == 'cross') {
    isAdd.value = true
    formTitle.value = formNames
    for (let x in row) {
      if (x == 'zhuanye') {
        form.value.zhuanye = row[x]
        disabledForm.value.zhuanye = true
      }
      if (x == 'banjimingcheng') {
        form.value.banjimingcheng = row[x]
        disabledForm.value.banjimingcheng = true
      }
    }
    if (row) crossRow.value = row
    if (table) crossTable.value = table
    if (tips) crossTips.value = tips
    if (statusColumnName) crossColumnName.value = statusColumnName
    if (statusColumnValue) crossColumnValue.value = statusColumnValue
    formVisible.value = true
  }
}

defineExpose({ init })

const loadSessionMajor = () => {
  const sessionTable = context?.$toolUtil.storageGet('sessionTable')
  if (sessionTable !== 'users' && sessionTable !== 'jiaoshi') {
    return
  }
  context?.$http({
    url: `${sessionTable}/session`,
    method: 'get'
  }).then(res => {
    const major = res?.data?.data?.zhuanye ? String(res.data.data.zhuanye).trim() : ''
    if (major) {
      form.value.zhuanye = major
      isMajorLocked.value = true
      disabledForm.value.zhuanye = true
    }
  }).catch(() => {})
}

onMounted(() => {
  loadSessionMajor()
})

const closeClick = () => {
  formVisible.value = false
}

const changeCrossData = row => {
  context?.$http({
    url: `${crossTable.value}/update`,
    method: 'post',
    data: row
  }).then(() => {})
}

const save = () => {
  var objcross = JSON.parse(JSON.stringify(crossRow.value))
  let crossUserId = ''
  let crossRefId = ''
  let crossOptNum = ''
  if (type.value == 'cross') {
    if (crossColumnName.value != '') {
      if (!crossColumnName.value.startsWith('[')) {
        for (let o in objcross) {
          if (o == crossColumnName.value) {
            objcross[o] = crossColumnValue.value
          }
        }
        changeCrossData(objcross)
      } else {
        crossUserId = context?.$toolUtil.storageGet('userid')
        crossRefId = objcross['id']
        crossOptNum = crossColumnName.value.replace(/\[/, '').replace(/\]/, '')
      }
    }
  }
  formRef.value.validate(valid => {
    if (valid) {
      if (crossUserId && crossRefId) {
        form.value.crossuserid = crossUserId
        form.value.crossrefid = crossRefId
        let params = {
          page: 1,
          limit: 1000,
          crossuserid: form.value.crossuserid,
          crossrefid: form.value.crossrefid
        }
        context?.$http({
          url: `${tableName}/page`,
          method: 'get',
          params: params
        }).then(res => {
          if (res.data.data.total >= crossOptNum) {
            context?.$toolUtil.message(`${crossTips.value}`, 'error')
          } else {
            context?.$http({
              url: `${tableName}/${!form.value.id ? 'save' : 'update'}`,
              method: 'post',
              data: form.value
            }).then(res => {
              context?.$toolUtil.message('操作成功', 'success', () => {
                formVisible.value = false
                emit('formModelChange')
              })
            })
          }
        })
      } else {
        context?.$http({
          url: `${tableName}/${!form.value.id ? 'save' : 'update'}`,
          method: 'post',
          data: form.value
        }).then(res => {
          context?.$toolUtil.message('操作成功', 'success', () => {
            formVisible.value = false
            emit('formModelChange')
          })
        })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.formModel_form {
  border-radius: 6px;
  padding: 30px;
  background: transparent;
  :deep(.el-form-item) {
    margin: 0 0 20px 0;
    display: flex;
    justify-content: space-between;
    .el-form-item__label {
      background: transparent;
      display: block;
      width: 90px;
      text-align: right;
      color: var(--text-regular);
    }
    .el-form-item__content {
      display: flex;
      width: calc(100% - 160px);
      justify-content: flex-start;
      align-items: center;
      .list_inp {
        border: 1px solid var(--tech-border-soft);
        padding: 0 10px;
        width: 300px;
        line-height: 36px;
        box-sizing: border-box;
        height: 36px;
        background: rgba(8, 28, 58, 0.55);
        border-radius: 4px;
        :deep(.el-input__wrapper) {
          border: none;
          box-shadow: none;
          background: none;
          border-radius: 0;
          height: 100%;
          padding: 0;
        }
      }
      .list_sel {
        border: 1px solid var(--tech-border-soft);
        border-radius: 4px;
        padding: 0 10px;
        width: 300px;
        line-height: 36px;
        box-sizing: border-box;
        background: rgba(8, 28, 58, 0.55);
      }
    }
  }
}

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
    margin: 0 10px 0 0;
    color: #333;
    background: linear-gradient(0deg, #fff 0%, #e3e1e0 100%);
    height: 32px;
  }
  .formModel_confirm {
    border: 0;
    cursor: pointer;
    border-radius: 4px;
    padding: 0 24px;
    margin: 0 10px 0 0;
    color: #fff;
    background: linear-gradient(0deg, #437dcd 0%, #1e67b7 100%);
    height: 32px;
  }
}
</style>
