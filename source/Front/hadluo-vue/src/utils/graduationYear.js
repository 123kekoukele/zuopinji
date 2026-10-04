/** 毕业届别选项（年份），展示时加「届」 */
export const COLLEGE_NAME = '地理与空间信息学院'

export function buildGraduationYearOptions(startYear = 2024, count = 7) {
	const options = []
	for (let i = 0; i < count; i++) {
		const year = String(startYear + i)
		options.push({ value: year, label: `${year}届` })
	}
	return options
}

export function formatBiyejieLabel(biyejie) {
	if (!biyejie) return '-'
	const year = String(biyejie).replace(/[^0-9]/g, '').slice(0, 4)
	return year ? `${year}届` : biyejie
}

/** 年级选项（入学年份），展示时加「级」 */
export function buildEnrollmentGradeOptions(startYear = 2018, count = 10) {
	const options = []
	for (let i = 0; i < count; i++) {
		const year = String(startYear + i)
		options.push({ value: year, label: `${year}级` })
	}
	return options
}

export function formatNianjiLabel(nianji) {
	if (!nianji) return '-'
	const year = String(nianji).replace(/[^0-9]/g, '').slice(0, 4)
	return year ? `${year}级` : nianji
}

export function normalizeStoredFilePath(filePath) {
	if (!filePath) return ''
	let normalized = String(filePath).replace(/\\/g, '/').trim()
	if (/^https?:\/\//i.test(normalized)) {
		try {
			normalized = new URL(normalized).pathname
		} catch (_) {
			// keep
		}
	}
	if (normalized.includes('/file/')) {
		normalized = normalized.slice(normalized.lastIndexOf('/file/') + '/file/'.length)
	} else if (normalized.startsWith('file/')) {
		normalized = normalized.slice('file/'.length)
	}
	return normalized
}

export function toPublicFilePath(storedPath) {
	if (!storedPath) return ''
	const normalized = normalizeStoredFilePath(storedPath)
	return normalized.startsWith('file/') ? normalized : `file/${normalized}`
}
