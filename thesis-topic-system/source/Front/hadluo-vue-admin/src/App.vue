<template>
	<router-view />
</template>
<script setup>
	import * as echarts from "echarts";
	import { provide } from "vue";

	// 全局注入 echarts 和 baseUrl
	provide("echarts", echarts);
	provide("baseUrl", process.env.VUE_APP_BASE_API);
</script>
<style lang="scss">
	// 强制重置所有 Element Plus CSS 变量
	:root {
		--el-bg-color: rgba(10, 30, 62, 0.96) !important;
		--el-bg-color-page: #061630 !important;
		--el-bg-color-overlay: rgba(8, 22, 48, 0.97) !important;
		--el-text-color-primary: #e2e8f0 !important;
		--el-text-color-regular: #cbd5e1 !important;
		--el-text-color-secondary: #94a3b8 !important;
		--el-text-color-placeholder: #64748b !important;
		--el-border-color: rgba(34, 211, 238, 0.22) !important;
		--el-border-color-light: rgba(34, 211, 238, 0.14) !important;
		--el-fill-color-blank: rgba(8, 28, 58, 0.45) !important;
		--el-fill-color-light: rgba(34, 211, 238, 0.06) !important;
		--el-mask-color: rgba(6, 22, 48, 0.65) !important;
	}

	#app {
		font-family: 'Microsoft YaHei', Arial, sans-serif;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
		color: var(--text-primary);
		background-color: #061630;
	}

	// 按钮盒子
	.formModel_btn_box {
		display: flex;
		width: 100%;
		justify-content: center;
		align-items: center;
		gap: 12px;
		
		// 取消按钮
		.formModel_cancel {
			border: 1px solid var(--border-color);
			cursor: pointer;
			border-radius: 4px;
			padding: 8px 20px;
			margin: 0;
			outline: none;
			color: var(--text-regular);
			background: rgba(8, 28, 58, 0.6);
			font-size: 14px;
			height: 36px;
			transition: all 0.3s;
		}
		
		.formModel_cancel:hover {
			border-color: var(--tech-cyan);
			color: var(--tech-cyan);
		}
		
		// 确定按钮
		.formModel_confirm {
			border: 1px solid var(--primary-blue);
			cursor: pointer;
			border-radius: 4px;
			padding: 8px 20px;
			margin: 0;
			outline: none;
			color: #fff;
			background: var(--primary-blue);
			font-size: 14px;
			height: 36px;
			transition: all 0.3s;
		}
		
		.formModel_confirm:hover {
			background: var(--primary-blue-dark);
			border-color: var(--primary-blue-dark);
		}
	}

	// Element Plus 全局样式覆盖 - 深蓝+白主题
	.el-button {
		border-radius: 6px;
		font-weight: 400;
		transition: none;
		border: 1px solid var(--border-color);
	}
	
	.el-button--primary {
		background-color: var(--primary-blue);
		border-color: var(--primary-blue);
		color: #fff;
	}
	
	.el-button--primary:hover {
		background-color: var(--primary-blue-light);
		border-color: var(--primary-blue-light);
		box-shadow: 0 0 14px rgba(34, 211, 238, 0.35);
	}

	.el-button--default,
	.el-button:not(.el-button--primary):not(.el-button--success):not(.el-button--warning):not(.el-button--danger):not(.el-button--info):not(.is-link):not(.is-text) {
		background-color: rgba(8, 28, 58, 0.65);
		color: var(--text-regular);
		border-color: var(--tech-border-soft);
	}

	.el-button--default:hover,
	.el-button:not(.el-button--primary):not(.el-button--success):not(.el-button--warning):not(.el-button--danger):not(.el-button--info):not(.is-link):not(.is-text):hover {
		color: var(--tech-cyan);
		border-color: var(--tech-border);
		background-color: rgba(34, 211, 238, 0.08);
	}
	
	.el-input__wrapper {
		border-radius: 6px;
		border: 1px solid var(--border-color);
		transition: none;
		background-color: rgba(8, 28, 58, 0.55) !important;
		box-shadow: 0 0 0 1px var(--tech-border-soft) inset;
	}

	.el-input__wrapper:hover {
		border-color: var(--primary-blue-light);
		box-shadow: 0 0 0 1px rgba(34, 211, 238, 0.25) inset;
	}

	.el-input__wrapper.is-focus {
		border-color: var(--primary-blue-light);
		box-shadow: 0 0 0 2px rgba(34, 211, 238, 0.18);
	}

	.el-input__inner,
	.el-textarea__inner {
		color: var(--text-primary) !important;
	}

	.el-form-item__label {
		color: var(--text-regular) !important;
	}

	.el-card {
		background: var(--background-white) !important;
		border: 1px solid var(--tech-border-soft) !important;
		color: var(--text-primary);
		box-shadow: 0 0 20px var(--tech-glow), inset 0 1px 0 rgba(255, 255, 255, 0.04);
	}

	.el-dialog {
		background: var(--background-white) !important;
		border: 1px solid var(--tech-border);
		box-shadow: 0 0 40px rgba(34, 211, 238, 0.15);
	}

	.el-dialog__header {
		border-bottom: 1px solid var(--tech-border-soft);
	}

	.el-dialog__title {
		color: var(--tech-cyan);
	}

	.el-table {
		--el-table-bg-color: rgba(8, 28, 58, 0.25) !important;
		--el-table-tr-bg-color: rgba(8, 28, 58, 0.22) !important;
		--el-table-header-bg-color: rgba(6, 22, 48, 0.98) !important;
		--el-table-row-hover-bg-color: rgba(34, 211, 238, 0.08) !important;
		--el-table-border-color: rgba(34, 211, 238, 0.15) !important;
		--el-table-text-color: var(--text-regular) !important;
		--el-table-header-text-color: var(--table-header-text) !important;
		border: 1px solid rgba(34, 211, 238, 0.18) !important;
		border-radius: 8px;
		overflow: hidden;
		background: transparent !important;
	}

	// 强制覆盖表格所有边框
	.el-table th.el-table__cell,
	.el-table td.el-table__cell,
	.el-table__header th.el-table__cell {
		border-color: rgba(34, 211, 238, 0.18) !important;
	}

	// 表格表头样式 - 强制覆盖白色边框
	.el-table .el-table__header-wrapper th,
	.el-table__header th.el-table__cell {
		background: linear-gradient(135deg, rgba(34, 211, 238, 0.9) 0%, rgba(8, 145, 178, 0.95) 100%) !important;
		border: none !important;
		border-right: 1px solid rgba(34, 211, 238, 0.3) !important;
		color: #ffffff !important;
	}

	.el-table .el-table__header-wrapper th:last-child,
	.el-table__header th.el-table__cell:last-child {
		border-right: none !important;
	}

	.el-table th.el-table__cell {
		font-weight: 600;
		border-bottom: 1px solid rgba(34, 211, 238, 0.3) !important;
	}

	.el-table td.el-table__cell {
		border-bottom: 1px solid rgba(34, 211, 238, 0.08) !important;
	}

	.el-table__inner-wrapper::before {
		display: none;
	}
	
	.el-pagination {
		padding: 20px 0;
		justify-content: center;
	}
	
	.el-pagination .btn-prev,
	.el-pagination .btn-next,
	.el-pagination .el-pager li {
		border: 1px solid var(--tech-border-soft);
		background-color: rgba(8, 28, 58, 0.65);
		color: var(--text-regular);
		border-radius: 6px;
		transition: none;
	}
	
	.el-pagination .btn-prev:hover,
	.el-pagination .btn-next:hover,
	.el-pagination .el-pager li:hover {
		color: var(--primary-blue);
		border-color: var(--primary-blue);
	}
	
	.el-pagination .el-pager li.is-active {
		background: linear-gradient(135deg, var(--primary-blue-dark), var(--primary-blue));
		color: #fff;
		border-color: var(--primary-blue-light);
		box-shadow: 0 0 12px rgba(34, 211, 238, 0.25);
	}

	.el-select-dropdown {
		background: var(--el-bg-color-overlay) !important;
		border: 1px solid var(--tech-border-soft) !important;
	}

	.el-select-dropdown__item {
		color: var(--text-regular);
	}

	.el-select-dropdown__item.is-selected {
		color: var(--tech-cyan);
	}

	.el-select-dropdown__item.hover,
	.el-select-dropdown__item:hover {
		background: rgba(34, 211, 238, 0.08);
	}

	.el-dropdown-menu {
		background: var(--el-bg-color-overlay) !important;
		border: 1px solid var(--tech-border-soft) !important;
	}

	.el-dropdown-menu__item {
		color: var(--text-regular);
	}

	.el-dropdown-menu__item:not(.is-disabled):focus,
	.el-dropdown-menu__item:not(.is-disabled):hover {
		background: rgba(34, 211, 238, 0.1);
		color: var(--tech-cyan);
	}

	.el-popover.el-popper {
		background: var(--el-bg-color-overlay) !important;
		border: 1px solid var(--tech-border-soft) !important;
		color: var(--text-primary);
	}

	.el-message-box {
		background: var(--background-white) !important;
		border: 1px solid var(--tech-border-soft);
	}

	.el-message-box__title {
		color: var(--tech-cyan);
	}

	.el-message-box__content {
		color: var(--text-regular);
	}

	.el-picker-panel,
	.el-time-panel {
		background: var(--el-bg-color-overlay) !important;
		border: 1px solid var(--tech-border-soft) !important;
		color: var(--text-regular);
		box-shadow: 0 8px 32px rgba(0, 0, 0, 0.35);
	}

	.el-picker-panel__icon-btn,
	.el-date-picker__header-label,
	.el-date-table th {
		color: var(--text-secondary);
	}

	.el-date-table td .el-date-table-cell__text {
		color: var(--text-regular);
	}

	.el-tabs__item {
		color: var(--text-secondary);
	}

	.el-tabs__item.is-active {
		color: var(--tech-cyan);
	}

	.el-tabs__active-bar {
		background-color: var(--tech-cyan);
	}
	
	.el-select .el-input {
		font-size: inherit;
	}
	
	.el-input__inner {
		color: inherit;
	}
	
	nav {
		padding: 30px;

		a {
			font-weight: bold;
			color: var(--text-secondary);

			&.router-link-exact-active {
				color: var(--tech-cyan);
			}
		}
	}

	.app-contain {
		background: var(--tech-panel) !important;
	}

	.form-content,
	.table-content,
	.detail-form-content {
		background: var(--tech-panel) !important;
	}
</style>