package tech.qiantong.qontology.module.om.service.ai.aiDataRule.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiDataRule.vo.OmAiDataRulePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiDataRule.vo.OmAiDataRuleSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataRule.OmAiDataRuleDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiDataRule.OmAiDataRuleMapper;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.module.om.service.ai.aiDataRule.IOmAiDataRuleService;

/** AI候选数据规则服务实现。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiDataRuleServiceImpl extends ServiceImpl<OmAiDataRuleMapper, OmAiDataRuleDO>
        implements IOmAiDataRuleService {

    @Override
    public PageResult<OmAiDataRuleDO> getPage(OmAiDataRulePageReqVO req) {
        return baseMapper.selectPage(req);
    }

    @Override
    public Long create(OmAiDataRuleSaveReqVO req) {
        OmAiDataRuleDO data = BeanUtils.toBean(req, OmAiDataRuleDO.class);
        data.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
        data.setDelFlag(false);
        baseMapper.insert(data);
        return data.getId();
    }

    @Override
    public int update(OmAiDataRuleSaveReqVO req) {
        OmAiDataRuleDO existing = get(req.getId());
        if (OmAiAdoptStatusEnum.ADOPTED.getCode().equals(existing.getAdoptStatus())) {
            throw new ServiceException("已采纳数据不可修改");
        }
        OmAiDataRuleDO data = BeanUtils.toBean(req, OmAiDataRuleDO.class);
        data.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
        data.setAdoptedId(null);
        data.setAdoptMessage(null);
        return baseMapper.updateById(data);
    }

    @Override
    public int remove(Collection<Long> ids) {
        for (Long id : ids) {
            if (OmAiAdoptStatusEnum.ADOPTED.getCode().equals(get(id).getAdoptStatus())) {
                throw new ServiceException("已采纳数据不可删除");
            }
        }
        return baseMapper.deleteBatchIds(ids);
    }

    @Override
    public OmAiDataRuleDO get(Long id) {
        OmAiDataRuleDO data = baseMapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI候选数据规则不存在");
        }
        return data;
    }
}
