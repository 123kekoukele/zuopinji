<template>
	<div>
		<el-scrollbar wrap-class="scrollbar-wrapper" class="menu_scrollbar">
			<el-menu :default-openeds="[]" :unique-opened="true" default-active="0" class="menu_view">
				<el-menu-item :index="0" @click="menuHandler('')">
					<i class="iconfont icon-zhuye2"></i>
					<span>首页</span>
				</el-menu-item>
				<el-sub-menu v-for=" (menu,index) in menuList.backMenu" :key="menu.menu" :index="index+2+''">
					<template #title>
						<i class="iconfont" :class="menu.fontClass"></i>
						<span>{{ menu.menu }}</span>
					</template>
					<el-menu-item class="menu_item_view" v-for=" (child,sort) in menu.child" :key="sort"
						:index="(index+2)+'-'+sort" @click="menuHandler(child.tableName,child.menuJump)">{{ child.menu }}
					</el-menu-item>
				</el-sub-menu>
			</el-menu>
		</el-scrollbar>
	</div>
</template>

<script setup>
	import menu from '@/utils/menu'
	import {
		ref,
		getCurrentInstance,
		nextTick
	} from 'vue';
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	//data
	const menuList = ref([])
	const role = ref('')
	const styleChange = () => {
		nextTick(() => {
			document.querySelectorAll('.el-menu-vertical-demo .el-sub-menu .el-menu').forEach(el => {
				el.removeAttribute('style')
				const icon = {
					"border": "none",
					"padding": "0",
					"margin": "10px auto 0",
					"borderRadius": "0px",
					"background": "none",
					"display": "none",
					"width": "100%"
				}
				Object.keys(icon).forEach((key) => {
					el.style[key] = icon[key]
				})
			})
		})
	}
	//权限验证
	const btnAuth = (e,a)=>{
		return context?.$toolUtil.isAuth(e,a)
	}
	const init = async () => {
		const menus = menu.list()
		if (menus) {
			menuList.value = menus
		}
		role.value = context?.$toolUtil.storageGet('role')

		for (let i = 0; i < menuList.value.length; i++) {
			if (menuList.value[i].roleName == role.value) {
				menuList.value = menuList.value[i];
				break;
			}
		}
		const sessionTable = context?.$toolUtil.storageGet('sessionTable')
		if (sessionTable === 'users' && role.value === '管理员') {
			let adminMajor = ''
			try {
				const res = await context?.$http({
					url: '/users/session',
					method: 'get'
				})
				adminMajor = (res?.data?.data?.zhuanye || '').trim()
			} catch {
				adminMajor = ''
			}
			if (adminMajor) {
				menuList.value.backMenu = (menuList.value.backMenu || []).map((group) => ({
					...group,
					child: (group.child || []).filter((child) => child.tableName !== 'users')
				})).filter((group) => (group.child || []).length > 0)
			}
		}
		// styleChange()
	}
	const menuHandler = (name,menuJump) => {
		if(name == 'center'){
			name = `${role.value}Center`
		}
		if(name == 'storeup'){
			name = `storeup?type=${menuJump}`
		}
		if(name == 'exampaper' && menuJump == '12'){
			name = 'exampaperlist'
		}
		if(name == 'examrecord' && menuJump == '22'){
			name = 'examfailrecord'
		}
		let router = context?.$router
		name = '/' + name
		router.push(name)
	}
	init()
</script>

<style lang="scss" scoped>
	// 总盒子
	:deep(.menu_scrollbar) {

		// 菜单盒子-展开样式
		.menu_view {
			padding: 12px 0;
			color: #fff;
			background: none;
			height: 100%;
			position: relative;
			z-index: 1;

			// 无二级菜单
			.el-menu-item {
				padding: 0 20px;
				color: rgba(226, 232, 240, 0.88);
				background: transparent;
				line-height: 48px;
				height: 48px;
				margin: 4px 8px;
				border-radius: 8px;
				transition: background 0.2s, color 0.2s, box-shadow 0.2s;
				border: 1px solid transparent;
				
				.iconfont {
					margin: 0 12px 0 0;
					color: inherit;
					width: 20px;
					vertical-align: middle;
					font-size: 16px;
					text-align: center;
				}
			}

			// 无二级悬浮
			.el-menu-item:hover {
				color: var(--tech-cyan);
				background: rgba(34, 211, 238, 0.08);
				border-color: rgba(34, 211, 238, 0.15);
			}

			// 无二级选中
			.el-menu-item.is-active {
				color: #fff;
				background: linear-gradient(90deg, rgba(2, 132, 199, 0.65), rgba(34, 211, 238, 0.25));
				border-color: rgba(34, 211, 238, 0.45);
				box-shadow: 0 0 18px rgba(34, 211, 238, 0.15);
			}

			// 有二级盒子
			.el-sub-menu {
				cursor: pointer;
				margin: 4px 8px;
				border-radius: 8px;
				color: rgba(226, 232, 240, 0.88);
				white-space: nowrap;
				background: transparent;
				position: relative;
				transition: none;

				// 有二级item
				.el-sub-menu__title {
					padding: 0 20px;
					color: inherit;
					background: transparent;
					line-height: 48px;
					height: 48px;
					border-radius: 8px;
					transition: background 0.2s, color 0.2s;
					border: 1px solid transparent;
					
					.iconfont {
						margin: 0 12px 0 0;
						color: inherit;
						width: 20px;
						vertical-align: middle;
						font-size: 16px;
						text-align: center;
					}
					
					.el-sub-menu__icon-arrow {
						margin-left: auto;
						color: rgba(148, 163, 184, 0.9);
						vertical-align: middle;
						font-size: 12px;
						transition: none;
					}
				}

				// 有二级item悬浮
				.el-sub-menu__title:hover {
					color: var(--tech-cyan);
					background: rgba(34, 211, 238, 0.08);
					border-color: rgba(34, 211, 238, 0.12);
				}
			}
			
			//二级选中
			.is-active {
				.el-sub-menu__title {
					color: #fff;
					background: linear-gradient(90deg, rgba(2, 132, 199, 0.55), rgba(34, 211, 238, 0.22));
					border-color: rgba(34, 211, 238, 0.35);
				}
			}
			// 二级盒子
			.el-menu--inline {
				border: none;
				padding: 8px 0;
				background: rgba(6, 22, 48, 0.65);
				margin: 4px 8px;
				border-radius: 8px;
				border: 1px solid rgba(34, 211, 238, 0.12);
				
				// 二级菜单
				.menu_item_view {
					padding: 0 20px 0 48px;
					color: rgba(203, 213, 225, 0.92);
					background: transparent;
					line-height: 40px;
					height: 40px;
					margin: 2px 8px;
					border-radius: 6px;
					transition: none;
					border: none;
					font-size: 14px;
				}
				
				// 二级悬浮
				.menu_item_view:hover {
					color: var(--tech-cyan);
					background: rgba(34, 211, 238, 0.08);
				}
				
				// 二级选中
				.is-active.menu_item_view {
					color: var(--tech-cyan);
					background: rgba(34, 211, 238, 0.14);
					font-weight: 600;
					box-shadow: inset 3px 0 0 var(--tech-cyan);
				}
			}
		}

	}
</style>
