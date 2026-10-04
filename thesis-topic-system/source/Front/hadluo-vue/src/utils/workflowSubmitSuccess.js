/**
 * 学生端毕设流程表单提交成功后，退出编辑态并刷新为「已提交」展示。
 */
export async function handleStudentWorkflowSubmitSuccess({
	router,
	routePath,
	typeRef,
	idRef,
	isAddRef,
	loadExistingRecord,
	messageUtil,
	successMessage,
}) {
	if (typeRef) typeRef.value = 'add'
	if (idRef) idRef.value = 0
	if (isAddRef) isAddRef.value = true
	await router.replace({ path: routePath, query: {} })
	if (loadExistingRecord) {
		await loadExistingRecord()
	}
	messageUtil?.message(successMessage, 'success')
	window.scrollTo({ top: 0, behavior: 'smooth' })
}
