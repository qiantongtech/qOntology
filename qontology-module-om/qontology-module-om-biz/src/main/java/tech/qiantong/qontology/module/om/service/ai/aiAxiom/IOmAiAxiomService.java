package tech.qiantong.qontology.module.om.service.ai.aiAxiom;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAxiom.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAxiom.OmAiAxiomDO;

/** AI候选模型公理服务。 */
public interface IOmAiAxiomService extends IService<OmAiAxiomDO> {
    PageResult<OmAiAxiomDO> getPage(OmAiAxiomPageReqVO reqVO);
    Long create(OmAiAxiomSaveReqVO reqVO);
    int update(OmAiAxiomSaveReqVO reqVO);
    int remove(Collection<Long> ids);
    OmAiAxiomDO get(Long id);
}
