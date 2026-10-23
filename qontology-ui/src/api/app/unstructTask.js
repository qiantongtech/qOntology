import request from '@/utils/request';

export function listUnstructTask(params) {
    return request({ url: '/app/unstructTask/list', method: 'get', params });
}

export function getUnstructTask(id) {
    return request({ url: `/app/unstructTask/${id}`, method: 'get' });
}

export function addUnstructTask(data) {
    return request({ url: '/app/unstructTask', method: 'post', data });
}

export function updateUnstructTask(data) {
    return request({ url: '/app/unstructTask', method: 'put', data });
}

export function delUnstructTask(ids) {
    return request({ url: `/app/unstructTask/${ids}`, method: 'delete' });
}

export function executeUnstructTask(id) {
    return request({ url: '/app/unstructTask/executeExtraction', method: 'post', data: { id } });
}

/** 发布非结构化抽取结果。 */
export function releaseUnstructTask(id) {
    return request({ url: '/app/unstructTask/releaseByTaskId', method: 'post', data: { id } });
}

/** 取消发布非结构化抽取结果。 */
export function cancelReleaseUnstructTask(id) {
    return request({ url: '/app/unstructTask/cancelReleaseByTaskId', method: 'post', data: { id } });
}

/** 查询任务关联的抽取片段。 */
export function getUnstructTaskTextList(params) {
    return request({ url: '/app/unstructTask/getTaskTextList', method: 'get', params });
}

export function getUnstructLogs(id) {
    return request({ url: '/app/taskLog/list', method: 'get', params: { taskId: id, taskType: 1, pageNum: 1, pageSize: 100, orderByColumn: 'start_time', isAsc: 'descending' } });
}

export function getUnstructLogDetails(logId) {
    return request({ url: '/app/taskLog/detail/page', method: 'get', params: { logId, pageNum: 1, pageSize: 100, orderByColumn: 'create_time', isAsc: 'ascending' } });
}
