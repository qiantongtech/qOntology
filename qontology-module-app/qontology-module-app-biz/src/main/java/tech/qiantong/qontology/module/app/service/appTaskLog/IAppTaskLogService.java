package tech.qiantong.qontology.module.app.service.appTaskLog;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog.*;
import tech.qiantong.qontology.module.app.extEnum.*;
import java.util.List;

/** 抽取任务执行日志 Service 接口。 */
public interface IAppTaskLogService extends IService<AppTaskLogDO> {
    /** 获得抽取任务执行日志分页列表。 */
    PageResult<AppTaskLogDO> getAppTaskLogPage(AppTaskLogPageReqVO pageReqVO);
    /** 获得抽取任务执行日志详情分页列表。 */
    PageResult<AppTaskLogDetailDO> getLogDetailPage(AppTaskLogDetailPageReqVO pageReqVO);
    /** 创建抽取任务执行日志。 */
    Long createAppTaskLog(AppTaskLogSaveReqVO createReqVO);
    /** 获得抽取任务执行日志详情。 */
    AppTaskLogDO getAppTaskLogById(Long id);
    /** 开始执行日志。 */
    Long startInvoke(Long workspaceId, Long taskId, String taskName, AppTaskTypeEnum taskType, String createBy, Long creatorId);
    /** 记录执行步骤。 */
    void recordStep(Long logId, String step);
    /** 结束执行日志。 */
    void endInvoke(Long logId, AppLogStatusEnum status, String errorMsg);
    /** 删除抽取任务执行日志。 */
    Integer removeAppTaskById(List<Long> idList);
}
