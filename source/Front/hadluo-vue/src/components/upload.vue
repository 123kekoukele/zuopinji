<template>
	<div>
		<el-upload v-if="type=='img'" :action="uploadUrl" list-type="picture-card" :limit="limit" :multiple="multiple"
			v-model:file-list="fileList" :before-upload="beforeUpload" :on-success="uploadSuccess" :on-exceed="uploadExceed" :disabled="disabled"
			:headers="uploadHeaders" :data="uploadData">
			<el-icon>
				<Plus />
			</el-icon>
			<template #file="{ file }">
				<div style="width: 100%;">
					<img class="el-upload-list__item-thumbnail" :src="file.url" alt="" style="object-fit: cover;" />
					<span class="el-upload-list__item-actions">
						<span class="el-upload-list__item-preview" @click="handlePictureCardPreview(file)">
							<el-icon>
								<zoom-in />
							</el-icon>
						</span>
						<span v-if="!disabled" class="el-upload-list__item-delete" @click="handleRemove(file)">
							<el-icon>
								<Delete />
							</el-icon>
						</span>
					</span>
				</div>
			</template>
			<template #tip>
				<div class="el-upload__tip">
					{{tips}}
				</div>
			</template>
		</el-upload>
		<el-upload v-else class="upload-demo" drag :action="uploadUrl" :headers="uploadHeaders" :limit="limit"
			:multiple="multiple" v-model:file-list="fileList" :before-upload="beforeUpload" :on-preview="uploadPreview" multiple
			:on-success="uploadSuccess" :on-error="uploadError" :on-exceed="uploadExceed" :on-remove="handleRemove" :data="uploadData">
			<el-icon class="el-icon--upload">
				<upload-filled />
			</el-icon>
			<div class="el-upload__text">
				将文件拖到此处，或<em>点击上传</em>
			</div>
			<template #tip>
				<div class="el-upload__tip">
					{{tips}}
				</div>
			</template>
		</el-upload>
		<el-dialog v-model="dialogVisible" width="60%">
			<img w-full :src="dialogImageUrl" alt="Preview Image" style="width: 100%;" />
		</el-dialog>
	</div>
</template>

<script setup>
	import {
		ref,
		getCurrentInstance,
		toRefs,
		defineEmits,
		watch,
		computed
	} from 'vue'
	import { toPublicFilePath } from '@/utils/graduationYear'
	const context = getCurrentInstance()?.appContext.config.globalProperties;
	const emit = defineEmits(['change'])
	//props
	const props = defineProps({
		action: String,
		limit: {
			type: Number,
			default: 3
		},
		multiple: {
			type: Boolean,
			default: false
		},
		fileUrls: String,
		tip: {
			type: String,
			default: '请上传'
		},
		type: {
			type: String,
			default: 'img'
		},
		disabled: {
			type: Boolean,
			default: false
		},
		bizType: {
			type: String,
			default: ''
		}
	})
	//data
	const tips = ref('请上传')
	const uploadUrl = ref('')
	const fileList = ref([])
	const fileUrlList = ref([])
	const {
		action,
		fileUrls,
		limit,
		multiple,
		tip,
		type,
		disabled,
		bizType
	} = toRefs(props)
	const uploadData = computed(() => ({
		bizType: bizType.value || ''
	}))
	const dialogImageUrl = ref('')
	const dialogVisible = ref(false)
	const uploadHeaders = ref({})

	watch(fileUrls, () => {
		init()
	})
	// // methods

	//移除
	const handleRemove = (e) => {
		fileUrlList.value.splice(e.uid - 1, 1)
		fileUrlsChange(fileUrlList.value)
	}
	//查看图片
	const handlePictureCardPreview = (e) => {
		dialogImageUrl.value = e.url
		dialogVisible.value = true
	}
	//允许 0 字节空文件上传
	const beforeUpload = () => true
	//成功回调
	const uploadSuccess = (response) => {
		let payload = response
		if (typeof payload === 'string') {
			try {
				payload = JSON.parse(payload)
			} catch (_) {
				payload = null
			}
		}
		if (!payload || (payload.code !== undefined && payload.code !== 0)) {
			context?.$toolUtil.message(payload?.msg || '上传失败', 'error')
			return
		}
		const fileName = payload.file
		if (!fileName) {
			context?.$toolUtil.message('上传失败，未返回文件信息', 'error')
			return
		}
		fileUrlList.value.push(toPublicFilePath(fileName))
		fileUrlsChange(fileUrlList.value)
	}
	const uploadError = (err) => {
		context?.$toolUtil.message(err?.message || '上传失败，请检查网络或文件大小', 'error')
	}
	const uploadPreview = (e) => {
		window.open(e.url)
	}
	//处理上传图片
	const fileUrlsChange = (list) => {
		var token = context?.$toolUtil.storageGet('frontToken');
		var list1 = []
		var list2 = []
		list.forEach(function(item, index) {
			var url = item.split("?")[0];
			var url1 = ''
			if (!url.startsWith("http")) {
				url1 = context?.$config.url + url
			}
			var name = url.substring(url.lastIndexOf('/') + 1);
			try {
				name = decodeURIComponent(name)
			} catch (_) {
				// keep raw name
			}
			if (!name) {
				name = String(index + 1)
			}
			var file = {
				name: name,
				url: url1 + "?token=" + token,
				uid: index + 1
			};
			list1.push(file);
			list2.push(url);
		});
		fileList.value = list1;
		fileUrlList.value = list2;
		emit('change', fileUrlList.value.join(','))
	}
	//超出数量
	const uploadExceed = () => {
		context?.$toolUtil.message(`最多上传${limit.value}个文件`, 'error')
	}
	//created
	const init = () => {
		console.log(fileUrls.value)
		uploadUrl.value = `${context?.$config.name}/${action.value}`.replace(/\/+/g, '/')
		uploadHeaders.value = {
			Token: context?.$toolUtil.storageGet('frontToken') || ''
		}
		if (tip.value) {
			tips.value = tip.value
		}
		let list = []
		if (fileUrls.value) {
			list = fileUrls.value.split(',')
		}
		fileUrlsChange(list)
	}
	init()
</script>

<style>
	.el-upload__tip {
		display: flex;
		align-items: center;
		justify-content: flex-start;
	}
</style>