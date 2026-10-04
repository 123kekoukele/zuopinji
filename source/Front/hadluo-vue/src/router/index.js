import { createRouter, createWebHashHistory } from 'vue-router'
import index from '../views'
import home from '../views/pages/home.vue'
import login from '../views/pages/login.vue'
import xueshengList from '@/views/pages/xuesheng/list'
import xueshengDetail from '@/views/pages/xuesheng/formModel'
import xueshengAdd from '@/views/pages/xuesheng/formAdd'
import xueshengRegister from '@/views/pages/xuesheng/register'
import xueshengCenter from '@/views/pages/xuesheng/center'
import jiaoshiList from '@/views/pages/jiaoshi/list'
import jiaoshiDetail from '@/views/pages/jiaoshi/formModel'
import jiaoshiAdd from '@/views/pages/jiaoshi/formAdd'
import jiaoshiRegister from '@/views/pages/jiaoshi/register'
import timuleixingList from '@/views/pages/timuleixing/list'
import timuleixingDetail from '@/views/pages/timuleixing/formModel'
import timuleixingAdd from '@/views/pages/timuleixing/formAdd'
import timuxinxiList from '@/views/pages/timuxinxi/list'
import timuxinxiDetail from '@/views/pages/timuxinxi/formModel'
import timuxinxiAdd from '@/views/pages/timuxinxi/formAdd'
import shenhejianyiList from '@/views/pages/shenhejianyi/list'
import shenhejianyiDetail from '@/views/pages/shenhejianyi/formModel'
import shenhejianyiAdd from '@/views/pages/shenhejianyi/formAdd'
import kaitibaogaoList from '@/views/pages/kaitibaogao/list'
import kaitibaogaoDetail from '@/views/pages/kaitibaogao/formModel'
import kaitibaogaoAdd from '@/views/pages/kaitibaogao/formAdd'
import dabianlunwenList from '@/views/pages/dabianlunwen/list'
import dabianlunwenDetail from '@/views/pages/dabianlunwen/formModel'
import dabianlunwenAdd from '@/views/pages/dabianlunwen/formAdd'
import pingfenshenheList from '@/views/pages/pingfenshenhe/list'
import pingfenshenheDetail from '@/views/pages/pingfenshenhe/formModel'
import pingfenshenheAdd from '@/views/pages/pingfenshenhe/formAdd'
import lunwenchugaoList from '@/views/pages/lunwenchugao/list'
import lunwenchugaoDetail from '@/views/pages/lunwenchugao/formModel'
import lunwenchugaoAdd from '@/views/pages/lunwenchugao/formAdd'
import zhongqijianchaAdd from '@/views/pages/zhongqijiancha/formAdd'
import xuantishenqingList from '@/views/pages/xuantishenqing/list'
import xuantishenqingDetail from '@/views/pages/xuantishenqing/formModel'
import xuantishenqingAdd from '@/views/pages/xuantishenqing/formAdd'
import toolUtil from '@/utils/toolUtil'

const routes = [{
		path: '/',
		redirect: '/index/home'
	},
	{
		path: '/index',
		component: index,
		children: [		{
			path: 'home',
			component: home
		}
		, {
			path: 'xueshengList',
			component: xueshengList
		}, {
			path: 'xueshengDetail',
			component: xueshengDetail
		}, {
			path: 'xueshengAdd',
			component: xueshengAdd
		}
		, {
			path: 'xueshengCenter',
			component: xueshengCenter
		}
		, {
			path: 'jiaoshiList',
			component: jiaoshiList
		}, {
			path: 'jiaoshiDetail',
			component: jiaoshiDetail
		}, {
			path: 'jiaoshiAdd',
			component: jiaoshiAdd
		}, {
			path: 'timuleixingList',
			component: timuleixingList
		}, {
			path: 'timuleixingDetail',
			component: timuleixingDetail
		}, {
			path: 'timuleixingAdd',
			component: timuleixingAdd
		}
		, {
			path: 'timuxinxiList',
			component: timuxinxiList
		}, {
			path: 'timuxinxiDetail',
			component: timuxinxiDetail
		}, 		{
			path: 'timuxinxiAdd',
			component: timuxinxiAdd
		}
		, {
			path: 'shenhejianyiList',
			component: shenhejianyiList
		}, {
			path: 'shenhejianyiDetail',
			component: shenhejianyiDetail
		}, {
			path: 'shenhejianyiAdd',
			component: shenhejianyiAdd
		}
		, {
			path: 'kaitibaogaoList',
			component: kaitibaogaoList
		}, {
			path: 'kaitibaogaoDetail',
			component: kaitibaogaoDetail
		}, {
			path: 'kaitibaogaoAdd',
			component: kaitibaogaoAdd
		}
		, {
			path: 'dabianlunwenList',
			component: dabianlunwenList
		}, {
			path: 'dabianlunwenDetail',
			component: dabianlunwenDetail
		}, {
			path: 'dabianlunwenAdd',
			component: dabianlunwenAdd
		}
		, {
			path: 'pingfenshenheList',
			component: pingfenshenheList
		}, {
			path: 'pingfenshenheDetail',
			component: pingfenshenheDetail
		}, {
			path: 'pingfenshenheAdd',
			component: pingfenshenheAdd
		}
		, {
			path: 'lunwenchugaoList',
			component: lunwenchugaoList
		}, {
			path: 'lunwenchugaoDetail',
			component: lunwenchugaoDetail
		}, 		{
			path: 'lunwenchugaoAdd',
			component: lunwenchugaoAdd
		}
		, {
			path: 'kaitibaogaoMy',
			redirect: '/index/kaitibaogaoAdd'
		}, {
			path: 'zhongqijianchaAdd',
			component: zhongqijianchaAdd
		}, {
			path: 'zhongqijianchaMy',
			redirect: '/index/zhongqijianchaAdd'
		}, {
			path: 'lunwenchugaoMy',
			redirect: '/index/lunwenchugaoAdd'
		}, {
			path: 'dabianlunwenMy',
			redirect: '/index/dabianlunwenAdd'
		}
		, {
			path: 'xuantishenqingList',
			component: xuantishenqingList
		}, {
			path: 'xuantishenqingDetail',
			component: xuantishenqingDetail
		}, {
			path: 'xuantishenqingAdd',
			component: xuantishenqingAdd
		}
		]
	},
	{
		path: '/login',
		component: login
	}
	, {
		path: '/xueshengRegister',
		component: xueshengRegister
	}, {
		path: '/jiaoshiRegister',
		component: jiaoshiRegister
	}
]

const router = createRouter({
  history: createWebHashHistory(process.env.BASE_URL),
  routes
})

// 前台路由鉴权：未登录禁止访问除登录/注册外的所有页面
const publicPaths = ['/login', '/xueshengRegister', '/jiaoshiRegister'];

router.beforeEach((to, from, next) => {
	const path = to.path || '/';
	const token = toolUtil.storageGet('frontToken');

	// 已登录用户访问登录/注册页，重定向到首页
	if (publicPaths.includes(path) && token) {
		return next('/index/home');
	}

	// 公开页面直接放行
	if (publicPaths.includes(path)) {
		return next();
	}

	// 需要登录的页面：无 token 则跳转登录
	if (!token) {
		const redirectPath = to.fullPath || to.path || '/index/home';
		toolUtil.storageSet('toPath', redirectPath);
		return next('/login');
	}

	return next();
});

export default router
