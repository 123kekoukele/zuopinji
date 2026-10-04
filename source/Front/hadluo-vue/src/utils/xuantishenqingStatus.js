export const STATUS_PENDING = '未审核'
export const STATUS_APPROVED = '通过'
export const STATUS_REJECTED = '驳回'

const APPLIED_STATUS_VALUES = [STATUS_APPROVED, '已审核', '已通过', '是', '已申请']
const REJECTED_STATUS_VALUES = [STATUS_REJECTED, '否', '已驳回']
const PENDING_STATUS_VALUES = [STATUS_PENDING, '未申请', '待审核']

export function isRejectedStatus(status) {
  if (status == null || String(status).trim() === '') return false
  return REJECTED_STATUS_VALUES.includes(String(status).trim())
}

export function isAppliedStatus(status) {
  if (status == null || String(status).trim() === '') return false
  return APPLIED_STATUS_VALUES.includes(String(status).trim())
}

export function isPendingStatus(status) {
  if (status == null || String(status).trim() === '') return true
  const value = String(status).trim()
  if (PENDING_STATUS_VALUES.includes(value)) return true
  return !isAppliedStatus(value) && !isRejectedStatus(value)
}

/** 仍占用选题名额的申请（未审核或通过） */
export function isActiveApplication(status) {
  return isPendingStatus(status) || isAppliedStatus(status)
}

export function isActiveApplicationRecord(record) {
  return !!(record && isActiveApplication(record.shenhezhuangtai))
}

export function normalizeApplicationStatus(status) {
  if (isAppliedStatus(status)) return STATUS_APPROVED
  if (isRejectedStatus(status)) return STATUS_REJECTED
  return STATUS_PENDING
}

export function auditTagType(status) {
  if (isAppliedStatus(status)) return 'success'
  if (isRejectedStatus(status)) return 'danger'
  return 'warning'
}

/** 四流程模块（开题报告等）审核意见，存于 shenheyuanyin */
export function extractWorkflowAuditReason(record) {
  if (!record || record.shenheyuanyin == null) return ''
  return String(record.shenheyuanyin).trim()
}

export function getWorkflowAuditReasonLabel(status) {
  if (isAppliedStatus(status)) return '通过理由'
  if (isRejectedStatus(status)) return '驳回理由'
  return '审核意见'
}

export function hasWorkflowAuditFeedback(record) {
  const status = record?.shenhezhuangtai
  return isAppliedStatus(status) || isRejectedStatus(status)
}

/** 从申请记录中提取教师驳回原因（优先审核日志，其次申请原因字段） */
export function extractRejectReason(record) {
  if (!record) return ''

  if (record.shenhejilu) {
    try {
      const log = JSON.parse(record.shenhejilu)
      if (Array.isArray(log)) {
        for (let i = log.length - 1; i >= 0; i--) {
          const entry = log[i]
          if (entry && entry.action === 'reject' && entry.reason) {
            return String(entry.reason).trim()
          }
        }
      }
    } catch (e) {
      // ignore invalid json
    }
  }

  const reason = record.shenqingyuanyin
  if (reason == null || String(reason).trim() === '') return ''
  const marker = '[驳回原因]:'
  const index = String(reason).lastIndexOf(marker)
  if (index < 0) return ''
  return String(reason).substring(index + marker.length).trim()
}

/** 个人中心展示用：优先有效申请，否则展示最近一条驳回记录 */
export function pickDisplayApplicationRecord(records) {
  if (!records || !records.length) return null
  const sorted = [...records].sort((a, b) => {
    const timeA = new Date(a.shenqingshijian || a.addtime || 0).getTime()
    const timeB = new Date(b.shenqingshijian || b.addtime || 0).getTime()
    return timeB - timeA
  })
  const active = sorted.find(isActiveApplicationRecord)
  if (active) return active
  return sorted.find(record => isRejectedStatus(record.shenhezhuangtai)) || null
}

/** 申请原因正文（去掉驳回原因后缀） */
export function extractApplicationReason(record) {
  if (!record || record.shenqingyuanyin == null) return ''
  const marker = '[驳回原因]:'
  const raw = String(record.shenqingyuanyin)
  const index = raw.lastIndexOf(marker)
  const text = index >= 0 ? raw.substring(0, index) : raw
  return text.trim()
}
