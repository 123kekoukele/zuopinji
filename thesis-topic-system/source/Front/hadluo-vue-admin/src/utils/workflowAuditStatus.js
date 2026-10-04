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

export function formatAuditStatus(status) {
	return normalizeApplicationStatus(status)
}

export const AUDIT_STATUS_OPTIONS = [STATUS_PENDING, STATUS_APPROVED, STATUS_REJECTED]

export function showAuditReasonField(status) {
	return isAppliedStatus(status) || isRejectedStatus(status)
}

export function getAuditReasonLabel(status) {
	if (isAppliedStatus(status)) return '通过理由（选填）'
	if (isRejectedStatus(status)) return '驳回理由（选填）'
	return '审核意见（选填）'
}
