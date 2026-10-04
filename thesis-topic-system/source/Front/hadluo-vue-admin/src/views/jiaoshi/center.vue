
<template>
	<div class="center_admin_page">
		<div class="center_card">
			<h2 class="center_card_title">个人中心</h2>
			<el-form class="userinfo_form" ref="userinfoFormRef" :model="form" label-width="100px">
				<el-row>
					<el-col :span="24">
						<el-form-item label="教师工号" prop="jiaoshigonghao">
							<el-input class="list_inp" v-model="user.jiaoshigonghao" readonly placeholder="教师工号" clearable />
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="教师姓名" prop="jiaoshixingming">
							<el-input class="list_inp" v-model="user.jiaoshixingming"  placeholder="教师姓名" clearable />
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="联系电话" prop="lianxidianhua">
							<el-input class="list_inp" v-model="user.lianxidianhua"  placeholder="联系电话" clearable />
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="性别" prop="xingbie">
							<el-select 
								class="list_sel" 
								v-model="user.xingbie" 
								placeholder="请选择性别"
								>
								<el-option v-for="item in jiaoshixingbieLists" :key="item" :label="item" :value="item"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<el-form-item label="专业" prop="zhuanye">
							<el-select
								class="list_sel"
								v-model="user.zhuanye"
								placeholder="请选择专业"
							>
								<el-option v-for="item in zhuanyeLists" :key="item" :label="item" :value="item"></el-option>
							</el-select>
						</el-form-item>
					</el-col>
					<el-col :span="24">
						<div class="userinfo_form_btn_box">
							<el-button class="userinfo_confirm" type="primary" @click="onSubmit">保存</el-button>
						</div>
					</el-col>
				</el-row>
			</el-form>
		</div>
	</div>
</template>

<script setup>
	import { isNumber,isIntNumer,isEmail,isMobile,isPhone,isURL,checkIdCard } from "@/utils/toolUtil";
	import {
		reactive,
		ref,
		getCurrentInstance
	} from 'vue'
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const tableName = ref('jiaoshi')
	const user = ref({})
	const jiaoshixingbieLists = ref([])
	const zhuanyeLists = ref([])
	const init = () => {
		jiaoshixingbieLists.value = "男,女".split(',')
		zhuanyeLists.value = "地理信息科学,地理科学,风景园林,测绘工程,城乡规划".split(',')
	}
	const onSubmit = () => {
		if((!user.value.jiaoshigonghao)){
			context?.$toolUtil.message(`教师工号不能为空`,'error')
			return false
		}
		if((!user.value.mima)){
			context?.$toolUtil.message(`密码不能为空`,'error')
			return false
		}
		if((!user.value.jiaoshixingming)){
			context?.$toolUtil.message(`教师姓名不能为空`,'error')
			return false
		}
		if((user.value.lianxidianhua)&&(!context?.$toolUtil.isMobile(user.value.lianxidianhua))){
			context?.$toolUtil.message(`联系电话应输入手机格式`,'error')
			return false
		}
		if((!user.value.zhuanye)){
			context?.$toolUtil.message(`请选择专业`,'error')
			return false
		}
		context?.$http({
			url: `${tableName.value}/update`,
			method: 'post',
			data: user.value
		}).then(res => {
			context?.$toolUtil.message('修改成功','success')
		})

	}
	const getInfo = () => {
		context?.$http({
			url: `${tableName.value}/session`,
			method: 'get'
		}).then(res => {
			user.value = res.data.data
			init()
		})
	}
	getInfo()
</script>

<style lang="scss" scoped>
// 科技蓝主题 - 教师个人中心样式
.center_admin_page { 
  padding: 16px; 
  background: transparent;
}

.center_card {
  background: var(--tech-panel);
  border: 1px solid var(--tech-border-soft);
  border-radius: 12px;
  box-shadow: 0 0 24px var(--tech-glow);
  padding: 24px 32px 32px;
  max-width: 640px;
}

.center_card_title {
  margin: 0 0 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid var(--tech-cyan);
  font-size: 18px;
  font-weight: 600;
  color: var(--tech-cyan);
}

.userinfo_form {
  :deep(.el-form-item) {
    margin-bottom: 20px;
    .el-form-item__label { 
      color: var(--text-regular); 
    }
    .el-form-item__content {
      width: calc(100% - 100px);
      max-width: 400px;
    }
    .list_inp .el-input__wrapper {
      border: 1px solid var(--tech-border-soft);
      border-radius: 8px;
      background: rgba(8, 28, 58, 0.55);
      box-shadow: 0 0 0 1px var(--tech-border-soft) inset;
    }
    .list_inp .el-input__wrapper:hover {
      border-color: var(--primary-blue-light);
    }
    .list_inp.is-focus .el-input__wrapper { 
      border-color: var(--primary-blue-light);
      box-shadow: 0 0 0 2px rgba(34, 211, 238, 0.15);
    }
    .list_inp .el-input__inner {
      color: var(--text-primary);
    }
    .list_sel .el-select__wrapper {
      border: 1px solid var(--tech-border-soft);
      border-radius: 8px;
      background: rgba(8, 28, 58, 0.55);
    }
    .el-upload--picture-card {
      border: 1px dashed var(--tech-border);
      background: rgba(34, 211, 238, 0.05);
      border-radius: 10px;
      width: 100px;
      height: 100px;
      .el-icon { color: var(--tech-cyan); font-size: 28px; }
    }
    .el-upload-list__item {
      border: 1px solid var(--tech-border-soft);
      border-radius: 10px;
      width: 100px;
      height: 100px;
    }
  }
  .userinfo_form_btn_box {
    margin-top: 24px;
    padding-top: 20px;
    border-top: 1px solid var(--tech-border-soft);
    display: flex;
    justify-content: center;
    .userinfo_confirm {
      border: none;
      border-radius: 10px;
      padding: 0 32px;
      color: #fff;
      background: linear-gradient(135deg, var(--primary-blue-dark), var(--primary-blue));
      font-size: 14px;
      height: 40px;
      transition: all 0.3s;
      box-shadow: 0 0 16px rgba(34, 211, 238, 0.2);
    }
    .userinfo_confirm:hover { 
      background: linear-gradient(135deg, var(--primary-blue), var(--primary-blue-light));
      box-shadow: 0 0 20px rgba(34, 211, 238, 0.35);
    }
  }
}
</style>
