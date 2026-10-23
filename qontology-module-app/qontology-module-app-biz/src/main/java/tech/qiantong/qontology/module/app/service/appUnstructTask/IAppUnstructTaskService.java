package tech.qiantong.qontology.module.app.service.appUnstructTask;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTask.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTask.AppUnstructTaskDO;
import java.util.Collection;
import java.util.List;

/** 非结构化抽取任务 Service 接口。 */
public interface IAppUnstructTaskService extends IService<AppUnstructTaskDO> {
    /** 消费非结构化抽取任务队列。 */
    void consumeQueue();
    /** 执行非结构化抽取任务。 */
    AjaxResult executeExtraction(AppUnstructTaskSaveReqVO reqVO);
    /** 获得非结构化抽取任务分页列表。 */
    PageResult<AppUnstructTaskDO> getAppUnstructTaskPage(AppUnstructTaskPageReqVO pageReqVO);
    /** 创建非结构化抽取任务。 */
    Long createAppUnstructTask(AppUnstructTaskSaveReqVO createReqVO);
    /** 更新非结构化抽取任务。 */
    int updateAppUnstructTask(AppUnstructTaskSaveReqVO updateReqVO);
    /** 删除非结构化抽取任务。 */
    int removeAppUnstructTask(Collection<Long> idList);
    /** 获得非结构化抽取任务详情。 */
    AppUnstructTaskDO getAppUnstructTaskById(Long id);
    /** 导入非结构化抽取任务。 */
    String importAppUnstructTask(List<AppUnstructTaskRespVO> importExcelList, boolean isUpdateSupport, String operName);
    /** 发布任务并同步发布 Neo4j 图谱数据。 */
    AjaxResult releaseByTaskId(AppUnstructTaskDO taskDO);
    /** 取消发布任务并同步取消发布 Neo4j 图谱数据。 */
    AjaxResult cancelReleaseByTaskId(AppUnstructTaskDO taskDO);
}
