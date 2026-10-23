package tech.qiantong.qontology.module.app.dal.mapper.appTaskLog;

import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo.AppTaskLogPageReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog.AppTaskLogDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;
import java.util.*;

/** 抽取任务执行日志 Mapper 接口。 */
public interface AppTaskLogMapper extends BaseMapperX<AppTaskLogDO> {
    /** 查询抽取任务执行日志分页列表。 */
    default PageResult<AppTaskLogDO> selectPage(AppTaskLogPageReqVO reqVO) {
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "start_time", "end_time", "create_time"));
        return selectPage(reqVO, new LambdaQueryWrapperX<AppTaskLogDO>()
                .eqIfPresent(AppTaskLogDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(AppTaskLogDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(AppTaskLogDO::getTaskType, reqVO.getTaskType())
                .likeIfPresent(AppTaskLogDO::getTaskName, reqVO.getTaskName())
                .eqIfPresent(AppTaskLogDO::getStatus, reqVO.getStatus())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}
