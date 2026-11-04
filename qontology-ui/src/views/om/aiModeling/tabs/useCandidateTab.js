import { adoptCandidate, deleteCandidate, getCandidateEvidence, listCandidates } from '@/api/om/aiModeling'

/** AI 候选页签通用分页、选择、批量采纳和批量删除逻辑。 */
export function useCandidateTab(type, taskId, validateAdopt = () => [], options = {}) {
  const { proxy } = getCurrentInstance()
  const rows = ref([])
  const total = ref(0)
  const loading = ref(false)
  const selectedIds = ref([])
  const selectedRows = ref([])
  const pageSize = options.pageSize ?? 10
  const query = reactive({
    taskId,
    pageNum: 1,
    pageSize,
    orderByColumn: 'createTime',
    isAsc: 'desc'
  })
  const evidenceVisible = ref(false)
  const evidences = ref([])
  const tableRef = ref(null)

  /**
   * 供 QtTable 使用的取数方法；强制携带任务编号，避免无上下文查询。
   * @param {Object} params QtTable 合并分页、排序后的查询参数
   * @returns {Promise<Object>} 候选列表接口返回体
   */
  function fetchRows(params) {
    return listCandidates(type, { ...params, taskId })
  }

  function refresh() {
    return tableRef.value ? tableRef.value.getList() : load()
  }

  async function load() {
    loading.value = true
    try {
      const response = await listCandidates(type, query)
      rows.value = response.data?.rows || []
      total.value = Number(response.data?.total || 0)
      selectedIds.value = []
      selectedRows.value = []
    } finally {
      loading.value = false
    }
  }

  function handleSortChange({ prop, order }) {
    query.orderByColumn = order ? prop : 'createTime'
    query.isAsc = order === 'ascending' ? 'asc' : 'desc'
    query.pageNum = 1
    load()
  }

  function select(selection) {
    selectedRows.value = selection
    selectedIds.value = selection.map(item => item.id)
  }

  function missingMessage(row) {
    const missing = validateAdopt(row).filter(Boolean)
    return missing.length ? `“${missing.join('、')}”未填写，请先修改补充后再采纳` : ''
  }

  async function adoptOne(row) {
    const message = missingMessage(row)
    if (message) { proxy.$modal.msgWarning(message); return }
    await adoptCandidate(type, row.id)
    proxy.$modal.msgSuccess('采纳成功')
    await refresh()
  }

  async function adoptBatch() {
    const candidates = selectedRows.value.filter(row => row.adoptStatus !== 1)
    if (!candidates.length) return
    const invalid = candidates.find(row => missingMessage(row))
    if (invalid) { proxy.$modal.msgWarning(missingMessage(invalid)); return }
    await proxy.$modal.confirm(`是否确认采纳选中的 ${candidates.length} 条候选数据？`)
    let success = 0
    let failed = 0
    const failureMessages = []
    for (const row of candidates) {
      try {
        await adoptCandidate(type, row.id)
        success++
      } catch (error) {
        failed++
        const reason = error?.message || error?.msg || '关联数据尚未采纳或候选数据不完整'
        if (!failureMessages.includes(reason)) failureMessages.push(reason)
      }
    }
    const reasonText = failureMessages.length ? `；失败原因：${failureMessages.slice(0, 3).join('；')}` : ''
    proxy.$modal[failed ? 'msgWarning' : 'msgSuccess'](`批量采纳完成：成功 ${success} 条，失败 ${failed} 条${reasonText}`)
    await refresh()
  }

  async function remove(row) {
    const ids = row ? [row.id] : selectedRows.value.filter(item => item.adoptStatus !== 1).map(item => item.id)
    if (!ids.length) return
    await proxy.$modal.confirm(`是否确认删除选中的 ${ids.length} 条候选数据？`)
    await deleteCandidate(type, ids.join(','))
    proxy.$modal.msgSuccess('删除成功')
    await refresh()
  }

  async function showEvidence(row) {
    const response = await getCandidateEvidence(type, row.id)
    evidences.value = response.data || []
    evidenceVisible.value = true
  }

  return { rows, total, loading, selectedIds, selectedRows, query, evidenceVisible, evidences, tableRef, pageSize, fetchRows, refresh, load, select, handleSortChange, adoptOne, adoptBatch, remove, showEvidence }
}
