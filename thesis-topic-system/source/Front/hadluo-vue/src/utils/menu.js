import toolUtil from '@/utils/toolUtil'

const ROUTE_OVERRIDES = {
	timuxinxi: '/index/timuxinxiList',
	xuantishenqing: '/index/xuantishenqingList',
	kaitibaogao: '/index/kaitibaogaoAdd',
	lunwenchugao: '/index/lunwenchugaoAdd',
	zhongqijiancha: '/index/zhongqijianchaAdd',
	dabianlunwen: '/index/dabianlunwenAdd',
}

const menu = {
	list() {
		if (toolUtil.storageGet('menus')) {
			return eval('(' + toolUtil.storageGet('menus') + ')')
		}
		return null
	},
	frontMenus() {
		if (!toolUtil.storageGet('menus')) {
			return []
		}
		try {
			const menusData = eval('(' + toolUtil.storageGet('menus') + ')')
			const role = toolUtil.storageGet('frontRole')
			const sessionTable = toolUtil.storageGet('frontSessionTable')
			const roleConfig = Array.isArray(menusData)
				? menusData.find(item => item.roleName === role || item.tableName === sessionTable)
				: menusData
			const frontMenu = roleConfig?.frontMenu
			if (!frontMenu || !frontMenu.length) {
				return []
			}
			return frontMenu.map(group => ({
				name: group.menu,
				icon: group.fontClass || 'icon-fenlei',
				child: (group.child || []).map(child => ({
					name: child.menu || child.name,
					url: ROUTE_OVERRIDES[child.tableName] || `/index/${child.tableName}List`
				}))
			}))
		} catch (e) {
			return []
		}
	}
}
export default menu
