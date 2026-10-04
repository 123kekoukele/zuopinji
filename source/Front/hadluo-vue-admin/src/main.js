import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
// import '@/icons' // icon
import store from './store'

// ★ 重要：Element Plus 样式必须在自定义样式之前导入
// 这样自定义样式才能覆盖 Element Plus 的默认样式
import 'element-plus/dist/index.css'

// 自定义主题样式（放在 Element Plus 样式之后，确保覆盖生效）
import '@/assets/css/style.scss'

const app = createApp(App)

import http from './utils/http.js'
// 基础配置
import config from './utils/config'
//公共方法
import toolUtil from './utils/toolUtil.js'
// import SvgIcon from '@/components/SvgIcon'// svg component
// app.component('svg-icon', SvgIcon)

//element-plus
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import ElementPlus from 'element-plus'
app.use(ElementPlus,{
	locale:zhCn
})

//echarts
import * as echarts from 'echarts'

//打印
import printJS from 'print-js'

//富文本
import Editor from "@/components/common/Editor";
app.component('editor', Editor)
//上传组件
import upload from "@/components/common/upload";
app.component('uploads', upload)

//md5
import md5 from 'js-md5'

app.config.globalProperties.$config = config.get()
app.config.globalProperties.$project = config.getProjectName()
app.config.globalProperties.$echarts = echarts
app.config.globalProperties.$toolUtil = toolUtil
app.config.globalProperties.$md5 = md5
app.config.globalProperties.$http = http // ajax请求方法



app.use(store)
app.use(router)
app.mount('#app')
