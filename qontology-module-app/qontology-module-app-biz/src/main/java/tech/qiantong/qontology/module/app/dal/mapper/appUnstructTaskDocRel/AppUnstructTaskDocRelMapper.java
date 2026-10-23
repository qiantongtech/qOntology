package tech.qiantong.qontology.module.app.dal.mapper.appUnstructTaskDocRel;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskDocRel.vo.AppUnstructTaskDocRelPageReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskDocRel.AppUnstructTaskDocRelDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;
import java.util.*;

/** 任务文件关联 Mapper 接口。 */
public interface AppUnstructTaskDocRelMapper extends BaseMapperX<AppUnstructTaskDocRelDO> {
    /** 物理删除指定任务的旧文件关联，释放任务-文件唯一键。 */
    @Delete("DELETE FROM app_unstruct_task_doc_rel WHERE task_id = #{taskId}")
    int deleteByTaskId(@Param("taskId") Long taskId);

    /** 查询任务文件关联分页列表。 */
    default PageResult<AppUnstructTaskDocRelDO> selectPage(AppUnstructTaskDocRelPageReqVO reqVO) {
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));
        return selectPage(reqVO, new LambdaQueryWrapperX<AppUnstructTaskDocRelDO>()
                .eqIfPresent(AppUnstructTaskDocRelDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(AppUnstructTaskDocRelDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(AppUnstructTaskDocRelDO::getDocId, reqVO.getDocId())
                .likeIfPresent(AppUnstructTaskDocRelDO::getDocName, reqVO.getDocName())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
}
