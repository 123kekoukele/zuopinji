import { getSiteOrigin } from "@/utils/siteOrigin";

const config = {
    get() {
        const origin = getSiteOrigin();
        return {
            url : origin + process.env.VUE_APP_BASE_API + '/',
            name: process.env.VUE_APP_BASE_API,
			menuList:[
				{
					name: '题目与选题',
					icon: '${frontMenu.fontClass}',
					child: [
						{ name: '题目信息', url: '/index/timuxinxiList' },
					]
				},
				{
					name: '毕设流程',
					icon: '${frontMenu.fontClass}',
					child: [
						{ name: '选题申请', url: '/index/xuantishenqingList' },
						{ name: '开题报告', url: '/index/kaitibaogaoAdd' },
						{ name: '论文初稿', url: '/index/lunwenchugaoAdd' },
						{ name: '中期检查', url: '/index/zhongqijianchaAdd' },
						{ name: '答辩论文', url: '/index/dabianlunwenAdd' },
					]
				},
			]
        }
    },
    getProjectName(){
        return {
            projectName: "基于SpringBoot的毕业设计选题系统"
        } 
    }
}
export default config
