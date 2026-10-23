import request from '@/utils/request';

/** 查询抽取任务执行日志列表。 */
export function listTaskLog(params) { return request({ url: '/app/taskLog/list', method: 'get', params }); }
/** 查询抽取任务执行日志步骤。 */
export function getDetailPage(params) { return request({ url: '/app/taskLog/detail/page', method: 'get', params }); }
/** 删除抽取任务执行日志。 */
export function delTaskLog(ids) { return request({ url: `/app/taskLog/${ids}`, method: 'delete' }); }
