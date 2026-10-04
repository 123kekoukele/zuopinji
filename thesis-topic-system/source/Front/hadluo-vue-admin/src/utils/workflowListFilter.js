import { ref, computed } from 'vue'

export const createWorkflowSearchQuery = () => ({
  timubianhao: '',
  ketimingcheng: '',
  shenhezhuangtai: '',
  pingfenzhuangtai: '',
  xueshengxingming: '',
  jiaoshixingming: '',
  zhuanye: '',
  timuleixing: ''
})

export const appendWorkflowSearchParams = (params, searchQuery = {}) => {
  const likeFields = ['timubianhao', 'ketimingcheng', 'xueshengxingming', 'jiaoshixingming']
  likeFields.forEach((field) => {
    const value = searchQuery[field]?.trim?.() ?? searchQuery[field]
    if (value) {
      params[field] = `%${value}%`
    }
  })

  ;['shenhezhuangtai', 'zhuanye', 'timuleixing'].forEach((field) => {
    if (searchQuery[field]) {
      params[field] = searchQuery[field]
    }
  })

  return params
}

export const useWorkflowListFilter = (context) => {
  const searchQuery = ref(createWorkflowSearchQuery())
  const zhuanyeOptions = ref([])
  const timuleixingOptions = ref([])
  const currentUser = ref(null)
  const sessionTable = ref(context?.$toolUtil.storageGet('sessionTable') || '')
  const isTeacherRole = computed(() => sessionTable.value === 'jiaoshi')

  const getCurrentUser = () => {
    sessionTable.value = context?.$toolUtil.storageGet('sessionTable') || ''
    const sessionRequest = sessionTable.value === 'jiaoshi'
      ? context?.$http({ url: '/jiaoshi/session', method: 'get' })
      : sessionTable.value === 'users'
        ? context?.$http({ url: '/users/session', method: 'get' })
        : null

    if (!sessionRequest) {
      currentUser.value = null
      return Promise.resolve()
    }

    return sessionRequest
      .then((res) => {
        if (res.data && res.data.code === 0) {
          currentUser.value = res.data.data
        }
      })
      .catch(() => {
        currentUser.value = null
      })
  }

  const getFilterOptions = () => {
    if (currentUser.value?.zhuanye) {
      zhuanyeOptions.value = [currentUser.value.zhuanye]
    } else {
      context?.$http({ url: '/xuesheng/lists', method: 'get' })
        .then((res) => {
          if (res.data && res.data.code === 0) {
            const list = res.data.data || []
            const zhuanyes = [...new Set(list.map((item) => item.zhuanye).filter(Boolean))]
            zhuanyeOptions.value = zhuanyes.sort()
          }
        })
        .catch(() => {})
    }

    context?.$http({ url: '/timuleixing/lists', method: 'get' })
      .then((res) => {
        if (res.data && res.data.code === 0) {
          const list = res.data.data || []
          timuleixingOptions.value = list.map((item) => item.timuleixing).filter(Boolean).sort()
        }
      })
      .catch(() => {})
  }

  const resetWorkflowSearch = () => {
    searchQuery.value = createWorkflowSearchQuery()
  }

  const initWorkflowFilter = () => {
    getCurrentUser().finally(() => {
      getFilterOptions()
    })
  }

  return {
    searchQuery,
    zhuanyeOptions,
    timuleixingOptions,
    isTeacherRole,
    resetWorkflowSearch,
    initWorkflowFilter
  }
}
