package tech.qiantong.qontology.module.app.dal.mapper.appUnstructTask;

import org.apache.ibatis.annotations.Update;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTask.vo.AppUnstructTaskPageReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTask.AppUnstructTaskDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;
import java.util.*;

/** 非结构化抽取任务 Mapper 接口。 */
public interface AppUnstructTaskMapper extends BaseMapperX<AppUnstructTaskDO> {
    /** 查询非结构化抽取任务分页列表。 */
    default PageResult<AppUnstructTaskDO> selectPage(AppUnstructTaskPageReqVO reqVO) {
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "publish_time", "create_time", "update_time"));
        return selectPage(reqVO, new LambdaQueryWrapperX<AppUnstructTaskDO>()
                .eqIfPresent(AppUnstructTaskDO::getId, reqVO.getId())
                .eqIfPresent(AppUnstructTaskDO::getWorkspaceId, reqVO.getWorkspaceId())
                .likeIfPresent(AppUnstructTaskDO::getName, reqVO.getName())
                .likeIfPresent(AppUnstructTaskDO::getTtlFileName, reqVO.getTtlFileName())
                .eqIfPresent(AppUnstructTaskDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AppUnstructTaskDO::getPublishStatus, reqVO.getPublishStatus())
                .betweenIfPresent(AppUnstructTaskDO::getCreateTime, reqVO.getParamByKey("beginCreateTime"), reqVO.getParamByKey("endCreateTime"))
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
    /** 选择性更新任务执行状态。 */
    @Update("<script>UPDATE app_unstruct_task <set><if test='status != null'>status=#{status},</if></set> WHERE id=#{id}</script>")
    int updateAppUnstructTask(AppUnstructTaskDO taskDO);
}
