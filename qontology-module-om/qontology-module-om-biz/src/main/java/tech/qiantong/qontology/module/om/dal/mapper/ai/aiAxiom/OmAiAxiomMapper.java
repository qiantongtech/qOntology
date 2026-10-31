package tech.qiantong.qontology.module.om.dal.mapper.ai.aiAxiom;

import java.util.Set;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAxiom.vo.OmAiAxiomPageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAxiom.OmAiAxiomDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;
import tech.qiantong.qontology.mybatis.core.query.LambdaQueryWrapperX;

/** AI候选模型公理 Mapper。 */
public interface OmAiAxiomMapper extends BaseMapperX<OmAiAxiomDO> {
    default PageResult<OmAiAxiomDO> selectPage(OmAiAxiomPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OmAiAxiomDO>()
                .eqIfPresent(OmAiAxiomDO::getTaskId, reqVO.getTaskId())
                .likeIfPresent(OmAiAxiomDO::getName, reqVO.getName())
                .eqIfPresent(OmAiAxiomDO::getType, reqVO.getType())
                .eqIfPresent(OmAiAxiomDO::getAdoptStatus, reqVO.getAdoptStatus())
                .orderBy(reqVO.getOrderByColumn(), reqVO.getIsAsc(), Set.of("id", "create_time", "update_time"))
                .orderByDesc(reqVO.getOrderByColumn() == null || reqVO.getOrderByColumn().isBlank(),
                        OmAiAxiomDO::getCreateTime, OmAiAxiomDO::getId));
    }
}
