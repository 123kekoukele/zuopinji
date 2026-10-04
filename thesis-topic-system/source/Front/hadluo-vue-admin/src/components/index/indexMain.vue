<template>
	<div style="height: 100%;">
		<index-aside class="index-aside"></index-aside>
		<el-main class="main_view" style="max-width:100%">
			<index-header class="index_header" :style="{'width':'100%','max-width':'100%'}">
			</index-header>
			<index-tags class="index_tags" :style="{'width':'100%','max-width':'100%'}">
			</index-tags>
			<router-view class="router-view index_transition"
				style="background: transparent;max-width:100%" v-slot="{Component}">
				<keep-alive>
					<transition name="el-fade-in-linear" mode="out-in">
						<component :is="Component" />
					</transition>
				</keep-alive>
			</router-view>
		</el-main>
	</div>
</template>

<script setup>
	import IndexAside from '@/components/index/indexMenu'
	import IndexHeader from '@/components/index/indexTop'
	import IndexTags from '@/components/index/indexTags'
	import menu from "@/utils/menu";
	import router from '../../router'
	import {
		ref,
		getCurrentInstance
	} from 'vue'
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const menuList = ref(null)
	const role = ref('')
	const init = () => {
		const menus = menu.list()
		if (menus) {
			menuList.value = menus
		}
		role.value = context?.$toolUtil.storageGet('role')
		for (let i = 0; i < menuList.value.length; i++) {
			if (menuList.value[i].roleName == role.value) {
				menuList.value = menuList.value[i].backMenu;
				break;
			}
		}
		let arr = makeMenu(menuList.value)

		router.addRoute(arr)
	}
	const makeMenu = (menu) => {
		let brr = {
			path: '/1',
			component: () => import('../../views/index'),
			children: []
		}
		for (let x in menu) {
			for (let i in menu[x].child) {
				brr.children.push({
					path: '/' + menu[x].child[i].tableName,
					name: menu[x].child[i].menu,
					component: () => import(`../../views/${menu[x].child[i].tableName}/list.vue`)
				})
			}
		}
		return brr
	}
	// init()
</script>
<style lang="scss" scoped>
	a:hover {
		background: none;
	}

	.el-main {
		padding: 0;
		margin: 0 0 0 210px;
		background: transparent;
		min-height: calc(100vh - 64px);
	}
	
	.main_view {
		position: relative;
		padding: 0;
	}

	.index-aside {
		box-shadow: 4px 0 24px rgba(0, 0, 0, 0.35), inset -1px 0 0 rgba(34, 211, 238, 0.2);
		z-index: 9;
		overflow: hidden;
		top: 64px;
		left: 0;
		background: linear-gradient(180deg, rgba(8, 26, 54, 0.98) 0%, rgba(6, 22, 48, 0.99) 50%, rgba(6, 22, 48, 1) 100%);
		width: 210px;
		border-right: 1px solid rgba(34, 211, 238, 0.22);
		position: fixed;
		height: calc(100% - 64px);
	}

	.index-aside::before {
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

	.index_header {
		width: 100%;
		z-index: 999;
	}

	.index_tags {
		width: 100%;
		z-index: 999;
	}

	.app-contain {
		padding: 24px;
		background: var(--background-white);
		margin: 16px;
		border-radius: 10px;
		border: 1px solid rgba(34, 211, 238, 0.18);
		box-shadow: 0 0 28px rgba(34, 211, 238, 0.08), inset 0 1px 0 rgba(255, 255, 255, 0.05);
		min-height: calc(100vh - 120px);
	}
	
	.index_transition {
		transition: none;
	}
	
	.router-view {
		background: transparent;
	}
</style>
