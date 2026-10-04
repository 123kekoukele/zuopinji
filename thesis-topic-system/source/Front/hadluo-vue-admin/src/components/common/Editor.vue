<template>
	<div class="admin-editor">
		<div class="admin-editor__shell">
			<Toolbar class="admin-editor__toolbar" :editor="editorRef" :defaultConfig="toolbarConfig"
				:mode="mode" />
			<Editor class="admin-editor__content" v-model="valueHtml" :defaultConfig="editorConfig"
				:mode="mode" @onCreated="handleCreated" @onChange="handleChange" />
		</div>
	</div>
</template>
<script setup>
	import '@wangeditor/editor/dist/css/style.css' // 引入 css

	import {
		onBeforeUnmount,
		ref,
		shallowRef,
		onMounted,
		getCurrentInstance,
		defineEmits,
		watch
	} from 'vue'
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	import {
		Editor,
		Toolbar
	} from '@wangeditor/editor-for-vue'
	const emit = defineEmits(['change'])
	// 编辑器实例，必须用 shallowRef
	const editorRef = shallowRef()

	// 内容 HTML
	const valueHtml = ref('')
	const props = defineProps({
		value: {
			type: String,
			default: ''
		},
		placeholder: {
			type: String,
			default: '请输入'
		},
		readonly: {
			type: Boolean, // 是否只读
			default: false
		}
	})
	// 模拟 ajax 异步获取内容
	onMounted(() => {
		setTimeout(() => {
			valueHtml.value = props.value
		}, 1500)
	})
	watch(()=>props.value, (n) => {
    	valueHtml.value = n
  	});
	const mode = 'default' // 或 'simple'
	const toolbarConfig = {
		excludeKeys: ['fullScreen', 'group-video', 'insertLink', 'insertImage']
	}
	const editorConfig = {
		placeholder: props.placeholder,
		readOnly: props.readonly,
		fontSize: ['12px', '14px', '16px'],
		MENU_CONF: {
			uploadImage: {
				server: context?.$config.name + '/file/upload',
				fieldName: 'file',
				// 单个文件的最大体积限制，默认为 2M
				maximgSize: 10 * 1024 * 1024, // 10M
				// 最多可上传几个文件，默认为 100
				maxNumberOfimgs: 10,
				// 选择文件时的类型限制，默认为 ['image/*'] 。如不想限制，则设置为 []
				allowedimgTypes: ['image/*'],
				// 自定义上传参数，例如传递验证的 token 等。参数会被添加到 formData 中，一起上传到服务端。
				meta: {
					// token: 'xxx',
					// otherKey: 'yyy'
					// img:''
				},
				// 将 meta 拼接到 url 参数中，默认 false
				metaWithUrl: false,

				// 自定义增加 http  header
				headers: {
					'Token': context?.$toolUtil.storageGet('Token')
				},

				// 跨域是否传递 cookie ，默认为 false
				withCredentials: true,

				// 超时时间，默认为 10 秒
				timeout: 10 * 1000, //10 秒

				// 上传前
				onBeforeUpload(imgs) {
					context?.$toolUtil.message('图片正在上传中,请耐心等待', 'warning')
					return imgs;
				},
				// 自定义插入图片
				customInsert(res, insertFn) {
					// 因为自定义插入导致onSuccess与onFailed回调函数不起作用,自己手动处理

					if (res.code === 0) {
						context?.$toolUtil.message('图片上传成功', 'success')
					} else {
						context?.$toolUtil.message('图片上传失败，请重新尝试', 'error')
					}
					// 从 res 中找到 url alt href ，然后插入图片
					insertFn(context?.$config.url + 'file/' + res.file);
					// console.log(res, "res.data")
				},

				// 单个文件上传成功之后
				onSuccess(img, res) {},

				// 单个文件上传失败
				onFailed(img, res) {},

				// 上传进度的回调函数
				onProgress(progress) {
					// console.log('progress', progress);
					// progress 是 0-100 的数字
				},

				// 上传错误，或者触发 timeout 超时
				onError(img, err, res) {
					// console.log(`${img.name} 上传出错`, err, res);
				}
			}
		}
	}

	// 组件销毁时，也及时销毁编辑器
	onBeforeUnmount(() => {
		const editor = editorRef.value
		if (editor == null) return
		editor.destroy()
	})
	const handleChange = (e) => {
		if (editorRef.value) {
			emit('change', editorRef.value.getHtml())
		}
	}
	const handleCreated = (editor) => {
		editorRef.value = editor // 记录 editor 实例，重要！
	}
</script>

<style>
	.admin-editor {
		width: 100%;
		line-height: normal !important;
	}

	.admin-editor__shell {
		border: 1px solid var(--tech-border-soft);
		border-radius: 6px;
		overflow: hidden;
		background: rgba(8, 28, 58, 0.55);
	}

	.admin-editor__toolbar {
		border-bottom: 1px solid var(--tech-border-soft) !important;
	}

	.admin-editor__content {
		height: 500px;
		overflow-y: hidden;
	}

	.admin-editor .w-e-toolbar {
		background-color: rgba(8, 28, 58, 0.92) !important;
		border-bottom: 1px solid var(--tech-border-soft) !important;
	}

	.admin-editor .w-e-text-container {
		background-color: rgba(8, 28, 58, 0.55) !important;
	}

	.admin-editor .w-e-text-container [data-slate-editor] {
		color: var(--text-primary) !important;
	}

	.admin-editor .w-e-text-placeholder {
		color: var(--text-placeholder) !important;
		font-style: normal;
	}

	.admin-editor .w-e-bar-item button {
		color: var(--text-regular) !important;
	}

	.admin-editor .w-e-bar-item button:hover {
		background-color: rgba(34, 211, 238, 0.12) !important;
	}

	.admin-editor .w-e-bar-item-active button {
		background-color: rgba(34, 211, 238, 0.18) !important;
	}

	.admin-editor .w-e-bar svg {
		fill: var(--text-regular) !important;
	}

	.admin-editor .w-e-bar-item-active svg {
		fill: var(--tech-cyan) !important;
	}

	.admin-editor .w-e-bar-divider {
		background-color: var(--tech-border-soft) !important;
	}

	.admin-editor .w-e-select-list {
		background-color: rgba(8, 22, 48, 0.98) !important;
		border: 1px solid var(--tech-border-soft) !important;
	}

	.admin-editor .w-e-select-list li {
		color: var(--text-primary) !important;
	}

	.admin-editor .w-e-select-list li:hover {
		background-color: rgba(34, 211, 238, 0.12) !important;
	}

	/* 下拉面板挂载到 body 时仍保持深色 */
	.w-e-drop-panel,
	.w-e-panel-content,
	.w-e-bar-item-menus-container {
		background-color: rgba(8, 22, 48, 0.98) !important;
		border: 1px solid var(--tech-border-soft) !important;
		color: var(--text-primary) !important;
	}

	.w-e-drop-panel .w-e-panel-content,
	.w-e-bar-item-menus-container .w-e-bar-item button {
		color: var(--text-primary) !important;
	}

	.w-e-drop-panel .w-e-bar-item button:hover,
	.w-e-bar-item-menus-container .w-e-bar-item button:hover {
		background-color: rgba(34, 211, 238, 0.12) !important;
	}

	.w-e-drop-panel svg,
	.w-e-bar-item-menus-container svg {
		fill: var(--text-regular) !important;
	}
</style>
