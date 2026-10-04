import {
    createRouter,
    createWebHashHistory
} from 'vue-router'
import xuesheng from '@/views/xuesheng/list'
import dabianlunwen from '@/views/dabianlunwen/list'
import timuxinxi from '@/views/timuxinxi/list'
import jiaoshi from '@/views/jiaoshi/list'
import storeup from '@/views/storeup/list'
import kaitibaogao from '@/views/kaitibaogao/list'
import pingfenshenhe from '@/views/pingfenshenhe/list'
import lunwenchugao from '@/views/lunwenchugao/list'
import zhongqijiancha from '@/views/zhongqijiancha/list'
import timuleixing from '@/views/timuleixing/list'
import banjixinxi from '@/views/banjixinxi/list'
import xuantishenqing from '@/views/xuantishenqing/list'
import users from '@/views/users/list'
import jiaoshiRegister from '@/views/jiaoshi/register'
import jiaoshiCenter from '@/views/jiaoshi/center'

export const routes = [{
    path: '/login',
    name: 'login',
    component: () => import('../views/login.vue')
},{
    path: '/',
    name: '首页1',
    component: () => import('../views/index'),
    children: [{
        path: '/',
        name: '首页',
        component: () => import('../views/HomeView.vue'),
        meta: {
            affix: true
        }
    }, {
        path: '/updatepassword',
        name: '修改密码',
        component: () => import('../views/updatepassword.vue')
    }
    
    ,{
        path: '/jiaoshiCenter',
        name: '教师个人中心',
        component: jiaoshiCenter
    }
    ,{
        path: '/xuesheng',
        name: '学生',
        component: xuesheng
    }
    ,{
        path: '/dabianlunwen',
        name: '答辩论文',
        component: dabianlunwen
    }
    ,{
        path: '/timuxinxi',
        name: '题目信息',
        component: timuxinxi
    }
    ,{
        path: '/jiaoshi',
        name: '教师',
        component: jiaoshi
    }
    ,{
        path: '/storeup',
        name: '我的收藏',
        component: storeup
    }
    ,{
        path: '/kaitibaogao',
        name: '开题报告',
        component: kaitibaogao
    }
    ,{
        path: '/pingfenshenhe',
        name: '评分审核',
        component: pingfenshenhe
    }
    ,{
        path: '/lunwenchugao',
        name: '论文初稿',
        component: lunwenchugao
    }
    ,{
        path: '/zhongqijiancha',
        name: '中期检查',
        component: zhongqijiancha
    }
    ,{
        path: '/timuleixing',
        name: '题目类型',
        component: timuleixing
    }
    ,{
        path: '/banjixinxi',
        name: '班级信息',
        component: banjixinxi
    }
    ,{
        // 兼容菜单中使用的 /banji 路径，指向同一班级信息页面
        path: '/banji',
        name: '班级信息管理',
        component: banjixinxi
    }
    ,{
        path: '/xuantishenqing',
        name: '选题申请',
        component: xuantishenqing
    }
    ,{
        path: '/users',
        name: '专业管理员',
        component: users
    }
    ]
},{
    path: '/jiaoshiRegister',
    name: '教师注册',
    component: jiaoshiRegister
}]

const router = createRouter({
    history: createWebHashHistory(process.env.BASE_URL),
    routes
})

const publicPaths = ['/login', '/jiaoshiRegister']

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('Token')
    if (!publicPaths.includes(to.path) && !token) {
        next('/login')
        return
    }
    if (to.path === '/login' && token) {
        next('/')
        return
    }
    next()
})

export default router