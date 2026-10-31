package tech.qiantong.qontology.module.om.dal.mapper.ai.aiDataRule;

import java.util.Set;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiDataRule.vo.OmAiDataRulePageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataRule.OmAiDataRuleDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/** AI 候选数据规则 Mapper。 */
public interface OmAiDataRuleMapper extends BaseMapperX<OmAiDataRuleDO> {

    /** 根据分页条件查询 AI 候选数据规则。 */
    default PageResult<OmAiDataRuleDO> selectPage(OmAiDataRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OmAiDataRuleDO>()
                .eqIfPresent(OmAiDataRuleDO::getTaskId, reqVO.getTaskId())
                .likeIfPresent(OmAiDataRuleDO::getName, reqVO.getName())
                .eqIfPresent(OmAiDataRuleDO::getType, reqVO.getType())
                .eqIfPresent(OmAiDataRuleDO::getAdoptStatus, reqVO.getAdoptStatus())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(),
                        Set.of("id", "create_time", "update_time")));
    }
}
