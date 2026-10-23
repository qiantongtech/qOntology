package tech.qiantong.qontology.module.app.dal.mapper.appUnstructTaskText;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText.vo.AppUnstructTaskTextPageReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskText.AppUnstructTaskTextDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;
import java.util.*;

/** 任务文件段落关联 Mapper 接口。 */
public interface AppUnstructTaskTextMapper extends BaseMapperX<AppUnstructTaskTextDO> {
    /** 查询任务文件段落关联分页列表。 */
    default PageResult<AppUnstructTaskTextDO> selectPage(AppUnstructTaskTextPageReqVO reqVO) {
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("id", "create_time", "update_time"));
        return selectPage(reqVO, new LambdaQueryWrapperX<AppUnstructTaskTextDO>()
                .eqIfPresent(AppUnstructTaskTextDO::getWorkspaceId, reqVO.getWorkspaceId())
                .eqIfPresent(AppUnstructTaskTextDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(AppUnstructTaskTextDO::getDocId, reqVO.getDocId())
                .eqIfPresent(AppUnstructTaskTextDO::getParagraphIndex, reqVO.getParagraphIndex())
                .likeIfPresent(AppUnstructTaskTextDO::getText, reqVO.getText())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), allowedColumns));
    }
    /** 根据任务 ID 删除旧的段落数据。 */
    @Delete("DELETE FROM app_unstruct_task_text WHERE task_id=#{taskId}")
    int deleteByTaskId(@Param("taskId") Long taskId);
}
