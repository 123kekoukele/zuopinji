import { getSiteOrigin } from "@/utils/siteOrigin";

const config = {
    get() {
        const origin = getSiteOrigin();
        /** 本地开发：管理端与用户端不同端口时，在 .env.development 配置 VUE_APP_USER_SITE_URL */
        const userSite =
            (process.env.VUE_APP_USER_SITE_URL || "").trim().replace(/\/$/, "") || origin;
        return {
            url : origin + process.env.VUE_APP_BASE_API + '/',
            name: process.env.VUE_APP_BASE_API,
            // 退出到用户端首页（用户端在站点根路径，hash 路由）
            indexUrl: userSite + '/#/index/home'
        }
    },
    getProjectName(){
        return {
            projectName: "地空院毕业设计选题系统"
        } 
    }
}
export default config
