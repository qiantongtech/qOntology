import request from '@/utils/request'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { getToken } from '@/utils/auth'

/** 候选数据类型与后端驼峰资源路径的映射。 */
const candidatePaths = {
  target: 'aiTarget',
  term: 'aiTerm',
  enum_type: 'aiEnumType',
  enum_data: 'aiEnumData',
  object: 'aiObject',
  attribute: 'aiAttribute',
  relation: 'aiRelation',
  axiom: 'aiAxiom',
  data_rule: 'aiDataRule'
}

/** 候选数据类型与关键字查询字段的映射。 */
const keywordFields = {
  target: 'question',
  term: 'stdName',
  enum_type: 'enumName',
  enum_data: 'enumLabel',
  object: 'name',
  attribute: 'name',
  relation: 'name',
  axiom: 'name',
  data_rule: 'name'
}

/** 获取候选类型对应的后端资源路径。 */
function getCandidatePath(type) {
  const path = candidatePaths[type]
  if (!path) {
    throw new Error(`不支持的候选数据类型：${type}`)
  }
  return path
}

/** 查询 AI 辅助建模任务分页列表。 */
export function listAiTasks(params) {
  return request({ url: '/om/aiTask/list', method: 'get', params })
}

/** 查询 AI 辅助建模任务详情。 */
export function getAiTask(id) {
  return request({ url: `/om/aiTask/${id}`, method: 'get' })
}

/** 使用统一文件服务返回的文件元数据创建 AI 辅助建模任务。 */
export function createAiTask(data) {
  return request({ url: '/om/aiTask', method: 'post', data })
}

/** 修改 AI 辅助建模任务基本信息。 */
export function updateAiTask(data) {
  return request({ url: '/om/aiTask', method: 'put', data })
}

/** 更新文件列表但不触发解析，解析仅由“重新解析”操作启动。 */
export function updateAiTaskFiles(data) {
  return request({ url: '/om/aiTask/files', method: 'post', data })
}

/** 启动 AI 解析并逐条接收任务、阶段和模型输出事件。 */
export function streamAiTaskAnalysis(taskId, ctrl, onMessage, onError, onClose) {
  return fetchEventSource(
    `${import.meta.env.VITE_APP_BASE_API}/om/aiTask/${taskId}/analyze-stream`,
    {
      method: 'post',
      headers: {
        Authorization: `Bearer ${getToken()}`,
        Accept: 'text/event-stream'
      },
      openWhenHidden: true,
      signal: ctrl.signal,
      async onopen(response) {
        if (!response.ok) {
          throw new Error(`AI解析流连接失败（${response.status}）`)
        }
        const contentType = response.headers.get('content-type') || ''
        if (!contentType.startsWith('text/event-stream')) {
          throw new Error(`AI解析流响应类型异常（${contentType || '未知'}）`)
        }
      },
      onmessage: onMessage,
      onerror: onError,
      onclose: onClose
    }
  )
}

/** 删除 AI 辅助建模任务。 */
export function deleteAiTask(ids) {
  return request({ url: `/om/aiTask/${ids}`, method: 'delete' })
}

/** 清空旧结果并全量重新解析任务。 */
export function reparseAiTask(id) {
  return request({ url: `/om/aiTask/${id}/reparse`, method: 'post' })
}

/** 查询任务上传文件。 */
export function getAiFiles(id) {
  return request({ url: `/om/aiTask/${id}/files`, method: 'get' })
}

/** 查询任务解析过程时间线。 */
export function getAiStages(id) {
  return request({ url: `/om/aiTask/${id}/stages`, method: 'get' })
}

/** 查询一键采纳前置条件。 */
export function getAdoptionRequirements(id) {
  return request({ url: `/om/aiTask/${id}/adoption-requirements`, method: 'get' })
}

/** 按依赖顺序采纳任务全部候选数据。 */
export function adoptAll(id) {
  return request({ url: `/om/aiTask/${id}/adopt-all`, method: 'post' })
}

/** 查询指定类型的候选数据。 */
export function listCandidates(type, params) {
  const query = { ...params }
  if (query.keyword) {
    query[keywordFields[type]] = query.keyword
  }
  delete query.keyword
  return request({ url: `/om/${getCandidatePath(type)}/list`, method: 'get', params: query })
}

/** 查询指定类型的单条候选数据。 */
export function getCandidate(type, id) {
  return request({ url: `/om/${getCandidatePath(type)}/${id}`, method: 'get' })
}

/** 修改单条候选数据。 */
export function updateCandidate(type, data) {
  return request({ url: `/om/${getCandidatePath(type)}`, method: 'put', data })
}

/** 新增单条候选数据。 */
export function createCandidate(type, data) {
  return request({ url: `/om/${getCandidatePath(type)}`, method: 'post', data })
}

/** 删除单条未采纳候选数据。 */
export function deleteCandidate(type, id) {
  return request({ url: `/om/${getCandidatePath(type)}/${id}`, method: 'delete' })
}

/** 查询候选数据的来源证据和文件位置。 */
export function getCandidateEvidence(type, id) {
  return request({ url: `/om/${getCandidatePath(type)}/${id}/evidence`, method: 'get' })
}

/** 采纳单条候选数据。 */
export function adoptCandidate(type, id) {
  return request({ url: `/om/${getCandidatePath(type)}/${id}/adopt`, method: 'post' })
}

export function getCandidateTargets(type, id) {
  return request({ url: '/om/aiTask/candidate-targets', method: 'get', params: { type, id } })
}

export function saveCandidateTargets(type, id, targetIds) {
  return request({ url: '/om/aiTask/candidate-targets', method: 'put', data: { type, id, targetIds } })
}

export function listAiAttributeBusinessTypes(taskId, source) {
  return request({ url: `/om/aiTask/${taskId}/attribute-business-types`, method: 'get', params: { source } })
}

export function listAiAttributeEnums(taskId, source) {
  return request({ url: `/om/aiTask/${taskId}/attribute-enum-options`, method: 'get', params: { source } })
}
