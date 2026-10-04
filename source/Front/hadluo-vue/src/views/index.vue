<template>
	<div class="user_layout">
		<!-- 左侧侧边栏 -->
		<aside class="sidebar">
			<div class="sidebar-header">
				<div class="sidebar-logo">
					<i class="iconfont icon-diqiu"></i>
					<span class="sidebar-title">毕业设计选题系统</span>
				</div>
				<div class="sidebar-subtitle">学生端</div>
			</div>

			<div class="sidebar-menu">
				<el-scrollbar wrap-class="scrollbar-wrapper" class="menu_scrollbar">
					<el-menu
						:default-openeds="[]"
						:unique-opened="true"
						:default-active="menuIndex"
						class="menu_view"
						@select="menuChange"
					>
						<el-menu-item index="0" @click="menuHandler('index/home')">
							<i class="iconfont icon-zhuye2"></i>
							<span>首页</span>
						</el-menu-item>
						<el-sub-menu v-for="(menu, index) in menuList" :key="'sub-' + index" :index="String(index + 2)">
							<template #title>
								<i class="iconfont" :class="menu.icon || 'icon-fenlei'"></i>
								<span>{{ menu.name }}</span>
							</template>
							<el-menu-item
								class="menu_item_view"
								v-for="(child, sort) in menu.child"
								:key="sort"
								:index="`${index + 2}-${sort}`"
								@click="menuHandler(child.url)"
							>
								{{ child.name }}
							</el-menu-item>
						</el-sub-menu>
						<el-menu-item v-if="Token" index="-1" @click="menuHandler('center')">
							<i class="iconfont icon-wode"></i>
							<span>个人中心</span>
						</el-menu-item>
					</el-menu>
				</el-scrollbar>
			</div>
		</aside>

		<!-- 右侧主内容区 -->
		<div class="main-wrapper">
			<!-- 顶部导航 -->
			<header class="top_header">
				<div class="header-right">
					<template v-if="Token">
						<div class="user-info">
							<el-avatar :size="36" :icon="UserFilled" class="user-avatar" />
							<span class="user-name">{{ userName }}</span>
						</div>
					</template>
					<el-button v-if="!Token" type="primary" size="small" @click="loginClick" class="btn-login">
						登录
					</el-button>
					<el-button v-if="Token" type="danger" size="small" @click="loginOut" circle class="btn-logout">
						<el-icon><SwitchButton /></el-icon>
					</el-button>
				</div>
			</header>

			<!-- 主内容 -->
			<main class="main_content">
				<router-view />
				<el-backtop :right="24" :bottom="24" />
			</main>
		</div>
	</div>
</template>

<script setup>
import menu from '@/utils/menu'
import config from '@/utils/config'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { SwitchButton, UserFilled } from '@element-plus/icons-vue'

const router = useRouter()

const Token = ref('')
const userName = ref('')
const menuIndex = ref('0')
const menuList = ref([])

const getMenu = () => {
	const defaultMenus = config.get().menuList
	const localMenus = menu.frontMenus()
	const hasProcessMenu = (menus) => menus.some(item => item.name === '毕设流程'
		&& item.child?.some(child => child.url === '/index/xuantishenqingList'))
	if (localMenus && localMenus.length > 0 && hasProcessMenu(localMenus)) {
		menuList.value = localMenus
	} else {
		menuList.value = defaultMenus
	}
}

const menuChange = (index) => {
	menuIndex.value = index
}

const menuHandler = (name) => {
		// 个人中心特殊处理
		if (name == 'center') {
			name = `${localStorage.getItem('frontSessionTable')}Center`
			router.push('/index/' + name)
			return
		}
		// 如果已经是完整路径（/index/开头），直接跳转
		if (name.startsWith('/index/')) {
			router.push(name)
			return
		}
		// 转换路由格式：index/home -> home
		const routeName = name.replace(/^\//, '').replace(/\/$/, '')
		const routeMap = {
			'index/home': 'home',
			'timuxinxi': 'timuxinxiList',
			'kaitibaogao': 'kaitibaogaoAdd',
			'kaitibaogaoMy': 'kaitibaogaoMy',
			'zhongqijiancha': 'zhongqijianchaAdd',
			'zhongqijianchaMy': 'zhongqijianchaAdd',
			'lunwenchugao': 'lunwenchugaoAdd',
			'lunwenchugaoMy': 'lunwenchugaoAdd',
			'dabianlunwen': 'dabianlunwenAdd',
			'dabianlunwenMy': 'dabianlunwenAdd',
			'shenhejianyi': 'shenhejianyiList',
			'pingfenshenhe': 'pingfenshenheList',
			'xuantishenqing': 'xuantishenqingList',
			'xuesheng': 'xueshengList',
			'jiaoshi': 'jiaoshiList',
			'timuleixing': 'timuleixingList',
		}
		const targetRoute = routeMap[routeName] || routeName
		router.push('/index/' + targetRoute)
	}

const loginClick = () => {
	localStorage.setItem('toPath', window.history.state.current)
	router.push('/login')
}

const loginOut = () => {
	localStorage.removeItem('frontToken')
	localStorage.removeItem('frontSessionTable')
	localStorage.removeItem('frontName')
	localStorage.removeItem('userid')
	localStorage.removeItem('menus')
	router.replace('/index/home')
	Token.value = ''
	userName.value = ''
}

const getSession = () => {
	const http = window.axios || window.parent?.axios
	if (!http) return
	const table = localStorage.getItem('frontSessionTable')
	if (!table) return
	http({
		url: `${table}/session`,
		method: 'get'
	}).then(res => {
		const data = res.data?.data || {}
		localStorage.setItem('userid', data.id)
		if (table === 'xuesheng') {
			localStorage.setItem('frontName', data.xuehao)
			userName.value = data.xueshengxingming || data.xuehao
		}
		if (table === 'jiaoshi') {
			localStorage.setItem('frontName', data.jiaoshigonghao)
			userName.value = data.jiaoshixingming || data.jiaoshigonghao
		}
	})
}

onMounted(() => {
	Token.value = localStorage.getItem('frontToken') || ''
	userName.value = localStorage.getItem('frontName') || ''
	menuIndex.value = localStorage.getItem('menuIndex') || '0'
	getMenu()
	if (Token.value) {
		getSession()
	}
})
</script>

<style lang="scss" scoped>
.user_layout {
	display: flex;
	height: 100vh;
	overflow: hidden;
	background: #061630;
}

// 侧边栏
.sidebar {
	position: fixed;
	left: 0;
	top: 0;
	bottom: 0;
	width: 210px;
	background: linear-gradient(180deg, rgba(8, 26, 54, 0.98) 0%, rgba(6, 22, 48, 0.99) 100%);
	border-right: 1px solid rgba(34, 211, 238, 0.22);
	z-index: 1001;
	box-shadow: 4px 0 24px rgba(0, 0, 0, 0.35), inset -1px 0 0 rgba(34, 211, 238, 0.2);

	&::before {
		content: '';
		position: absolute;
		inset: 0;
		background-image:
			linear-gradient(rgba(34, 211, 238, 0.03) 1px, transparent 1px),
			linear-gradient(90deg, rgba(34, 211, 238, 0.03) 1px, transparent 1px);
		background-size: 24px 24px;
		pointer-events: none;
		opacity: 0.7;
	}
}

.sidebar-header {
	padding: 16px;
	border-bottom: 1px solid rgba(34, 211, 238, 0.15);
}

.sidebar-logo {
	display: flex;
	align-items: center;
	gap: 10px;

	.icon-diqiu {
		font-size: 24px;
		color: var(--tech-cyan);
		text-shadow: 0 0 12px rgba(34, 211, 238, 0.5);
	}

	.sidebar-title {
		font-size: 15px;
		font-weight: 600;
		color: #fff;
		letter-spacing: 0.5px;
	}
}

.sidebar-subtitle {
	font-size: 12px;
	color: rgba(34, 211, 238, 0.8);
	margin-top: 6px;
	padding-left: 34px;
}

.sidebar-menu {
	height: calc(100vh - 90px);
	overflow: hidden;
}

// 菜单样式
.menu_scrollbar {
	height: 100%;

	:deep(.menu_view) {
		padding: 12px 0;
		color: #fff;
		background: none;
		height: 100%;
		border: none;

		.el-menu-item {
			padding: 0 20px !important;
			color: rgba(226, 232, 240, 0.88);
			background: transparent;
			line-height: 48px;
			height: 48px;
			margin: 4px 8px;
			border-radius: 8px;
			border: 1px solid transparent;
			transition: background 0.2s, color 0.2s;

			.iconfont {
				margin-right: 12px;
				color: inherit;
				font-size: 16px;
			}

			&:hover {
				color: var(--tech-cyan);
				background: rgba(34, 211, 238, 0.08);
				border-color: rgba(34, 211, 238, 0.15);
			}

			&.is-active {
				color: #fff;
				background: linear-gradient(90deg, rgba(2, 132, 199, 0.65), rgba(34, 211, 238, 0.25));
				border-color: rgba(34, 211, 238, 0.45);
				box-shadow: 0 0 18px rgba(34, 211, 238, 0.15);
			}
		}

		.el-sub-menu {
			margin: 4px 8px;
			border-radius: 8px;

			.el-sub-menu__title {
				padding: 0 20px !important;
				color: rgba(226, 232, 240, 0.88);
				background: transparent;
				line-height: 48px;
				height: 48px;
				border-radius: 8px;
				border: 1px solid transparent;
				transition: background 0.2s, color 0.2s;

				.iconfont {
					margin-right: 12px;
					color: inherit;
					font-size: 16px;
				}

				.el-sub-menu__icon-arrow {
					color: rgba(148, 163, 184, 0.9);
				}

				&:hover {
					color: var(--tech-cyan);
					background: rgba(34, 211, 238, 0.08);
				}
			}

			&.is-active .el-sub-menu__title {
				color: #fff;
				background: linear-gradient(90deg, rgba(2, 132, 199, 0.55), rgba(34, 211, 238, 0.22));
				border-color: rgba(34, 211, 238, 0.35);
			}
		}

		.el-menu--inline {
			border: none;
			padding: 8px 0;
			background: rgba(6, 22, 48, 0.65);
			margin: 4px 8px;
			border-radius: 8px;
			border: 1px solid rgba(34, 211, 238, 0.12);

			.menu_item_view {
				padding: 0 20px 0 48px !important;
				color: rgba(203, 213, 225, 0.92);
				background: transparent;
				line-height: 40px;
				height: 40px;
				margin: 2px 8px;
				border-radius: 6px;
				border: none;
				font-size: 14px;

				&:hover {
					color: var(--tech-cyan);
					background: rgba(34, 211, 238, 0.08);
				}

				&.is-active {
					color: var(--tech-cyan);
					background: rgba(34, 211, 238, 0.14);
					font-weight: 600;
					box-shadow: inset 3px 0 0 var(--tech-cyan);
				}
			}
		}
	}
}

// 主内容区
.main-wrapper {
	margin-left: 210px;
	flex: 1;
	display: flex;
	flex-direction: column;
	height: 100vh;
	min-height: 0;
	overflow: hidden;
}

// 顶部导航
.top_header {
	position: sticky;
	top: 0;
	z-index: 999;
	flex-shrink: 0;
	height: 64px;
	background: #082f49;
	box-shadow: 0 2px 16px rgba(0, 0, 0, 0.4), inset 0 -1px 0 rgba(34, 211, 238, 0.5);
	display: flex;
	align-items: center;
	justify-content: flex-end;
	padding: 0 24px;
}

.header-right {
	display: flex;
	align-items: center;
	gap: 16px;
}

.user-info {
	display: flex;
	align-items: center;
	gap: 10px;
	color: #fff;

	.user-avatar {
		background: rgba(34, 211, 238, 0.2);
		border: 2px solid rgba(34, 211, 238, 0.5);
	}

	.user-name {
		font-size: 14px;
		font-weight: 400;
	}
}

.btn-login {
	background: linear-gradient(135deg, #22d3ee 0%, #0891b2 100%);
	border: none;
	color: #fff;

	&:hover {
		background: linear-gradient(135deg, #67e8f9 0%, #22d3ee 100%);
	}
}

.btn-logout {
	background: rgba(255, 255, 255, 0.15);
	border: 1px solid rgba(255, 255, 255, 0.3);
	color: #fff;

	&:hover {
		background: rgba(255, 255, 255, 0.25);
	}
}

// 主内容
.main_content {
	flex: 1;
	min-height: 0;
	padding: 0;
	background: #061630;
	overflow-x: hidden;
	overflow-y: auto;
	display: flex;
	flex-direction: column;

	:deep(> *) {
		flex: 0 0 auto;
		width: 100%;
		min-height: 100%;
	}
}
</style>
