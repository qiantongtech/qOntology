package tech.qiantong.qontology.module.app.dal.mapper.appTaskLog;

import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo.AppTaskLogDetailPageReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog.AppTaskLogDetailDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;
import java.util.*;

/** 抽取任务执行日志详情 Mapper 接口。 */
public interface AppTaskLogDetailMapper extends BaseMapperX<AppTaskLogDetailDO> {
    /** 查询抽取任务执行日志详情分页列表。 */
    default PageResult<AppTaskLogDetailDO> selectPage(AppTaskLogDetailPageReqVO reqVO) {
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));
        return selectPage(reqVO, new LambdaQueryWrapperX<AppTaskLogDetailDO>()
                .eqIfPresent(AppTaskLogDetailDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(AppTaskLogDetailDO::getLogId, reqVO.getLogId())
                .likeIfPresent(AppTaskLogDetailDO::getStep, reqVO.getStep())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}
