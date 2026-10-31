package tech.qiantong.qontology.module.om.service.ai.aiDataRule;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.Collection;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiDataRule.vo.OmAiDataRulePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiDataRule.vo.OmAiDataRuleSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataRule.OmAiDataRuleDO;

/** AI候选数据规则服务。 */
public interface IOmAiDataRuleService extends IService<OmAiDataRuleDO> {

    PageResult<OmAiDataRuleDO> getPage(OmAiDataRulePageReqVO req);

    Long create(OmAiDataRuleSaveReqVO req);

    int update(OmAiDataRuleSaveReqVO req);

    int remove(Collection<Long> ids);

    OmAiDataRuleDO get(Long id);
}
