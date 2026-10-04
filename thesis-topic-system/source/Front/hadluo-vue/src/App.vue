<template>
	<router-view />
</template>

<script>
const debounce = (fn, delay) => {
	let timer = null;
	return function () {
		const context = this;
		const args = arguments;
		clearTimeout(timer);
		timer = setTimeout(() => fn.apply(context, args), delay);
	};
};

const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
	constructor(callback) {
		super(debounce(callback, 16));
	}
};
</script>

<style lang="scss">
/* ========== 科技蓝主题变量 ========== */
:root,
html.dark {
	--tech-cyan: #22d3ee;
	--tech-cyan-dark: #0891b2;
	--tech-cyan-light: #67e8f9;
	--tech-panel: rgba(8, 26, 54, 0.98);
	--tech-border-soft: rgba(34, 211, 238, 0.18);
	--tech-glow: rgba(34, 211, 238, 0.15);
	--background-white: rgba(8, 26, 54, 0.98);
	--text-primary: rgba(255, 255, 255, 0.92);
	--text-secondary: rgba(203, 213, 225, 0.75);
	--text-placeholder: #94a3b8;
	--border-color: rgba(34, 211, 238, 0.2);
	--el-bg-color-overlay: rgba(8, 26, 54, 0.98);
	--el-fill-color-blank: rgba(5, 18, 36, 0.8);
	--el-text-color-regular: rgba(255, 255, 255, 0.88);
	--el-text-color-placeholder: #64748b;
	--el-border-color: rgba(34, 211, 238, 0.18);
	--el-border-color-light: rgba(34, 211, 238, 0.18);
	--el-border-color-lighter: rgba(34, 211, 238, 0.12);
}

* {
	box-sizing: border-box;
}

body {
	margin: 0;
	min-width: 1280px;
	background: #061630;
	font-family: "PingFang SC", "Microsoft YaHei", system-ui, sans-serif;
	-webkit-font-smoothing: antialiased;
	-moz-osx-font-smoothing: grayscale;
}

#app {
	min-height: 100vh;
	color: var(--text-primary);
	background: #061630;
}

/* 通用区块标题 */
.section_title {
	padding: 16px 20px;
	margin: 0 auto;
	color: var(--text-primary);
	font-weight: 600;
	font-size: 20px;
	letter-spacing: 0.5px;
	text-align: center;
	background: var(--background-white);
	border-radius: 10px;
	box-shadow: 0 2px 8px rgba(34, 211, 238, 0.1);
	border: 1px solid var(--tech-border-soft);
}

.app-contain {
	width: 100%;
}

/* 路由链接 */
nav {
	padding: 24px;
	a {
		font-weight: 500;
		color: var(--text-secondary);
		transition: color 0.25s ease;
		&.router-link-exact-active {
			color: var(--tech-cyan);
		}
	}
}

/* ========== 下拉选择器（Element Plus 2.14+） ========== */
.el-select__popper.el-popper,
.el-popper.is-light.el-select__popper,
.el-popper.is-pure.el-select__popper {
	background: rgba(8, 26, 54, 0.98) !important;
	border: 1px solid var(--tech-border-soft) !important;
	box-shadow: 0 8px 28px rgba(0, 0, 0, 0.35), 0 0 20px rgba(34, 211, 238, 0.08) !important;
}

.el-select-dropdown {
	background: transparent !important;
	border: none !important;
	box-shadow: none !important;
}

.el-select-dropdown__list {
	padding: 6px 0 !important;
}

.el-select-dropdown__item {
	color: rgba(255, 255, 255, 0.88) !important;
}

.el-select-dropdown__item.hover,
.el-select-dropdown__item:hover,
.el-select-dropdown__item.is-hovering {
	background: rgba(34, 211, 238, 0.12) !important;
}

.el-select-dropdown__item.is-selected,
.el-select-dropdown__item.selected {
	color: var(--tech-cyan) !important;
	font-weight: 600;
}

.el-select-dropdown__item.is-disabled {
	color: rgba(148, 163, 184, 0.6) !important;
}

.el-select__wrapper {
	background-color: rgba(5, 18, 36, 0.8) !important;
	box-shadow: 0 0 0 1px rgba(34, 211, 238, 0.18) inset !important;
}

.el-select__wrapper:hover {
	box-shadow: 0 0 0 1px rgba(34, 211, 238, 0.35) inset !important;
}

.el-select__wrapper.is-focused,
.el-select__wrapper.is-hovering {
	box-shadow: 0 0 0 1px var(--tech-cyan) inset, 0 0 0 3px rgba(34, 211, 238, 0.12) !important;
}

.el-select__wrapper.is-disabled {
	background-color: rgba(5, 18, 36, 0.45) !important;
	opacity: 0.75;
}

.el-select__placeholder {
	color: var(--text-placeholder) !important;
}

.el-select__selected-item {
	color: var(--text-primary) !important;
}

.el-select__caret {
	color: var(--text-secondary) !important;
}

/* 兼容旧版 el-select 结构 */
.el-select .el-input__wrapper {
	background: rgba(5, 18, 36, 0.8) !important;
	border: 1px solid var(--tech-border-soft) !important;
	box-shadow: none !important;
}

.el-select .el-input__wrapper:hover {
	border-color: rgba(34, 211, 238, 0.35) !important;
}

.el-select .el-input__wrapper.is-focus {
	border-color: var(--tech-cyan) !important;
	box-shadow: 0 0 0 3px rgba(34, 211, 238, 0.12) !important;
}

.el-select .el-input__inner {
	color: var(--text-primary) !important;
}

.el-select .el-input__inner::placeholder {
	color: var(--text-placeholder) !important;
}

.el-select .is-disabled .el-input__wrapper {
	background: rgba(5, 18, 36, 0.45) !important;
	opacity: 0.75;
}
</style>
