package tech.qiantong.qontology.module.om.service.ai.aiAxiom.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAxiom.vo.OmAiAxiomPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAxiom.vo.OmAiAxiomSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAxiom.OmAiAxiomDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiAxiom.OmAiAxiomMapper;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.module.om.service.ai.aiAxiom.IOmAiAxiomService;

/** AI候选模型公理服务实现。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiAxiomServiceImpl extends ServiceImpl<OmAiAxiomMapper, OmAiAxiomDO> implements IOmAiAxiomService {

    @Override
    public PageResult<OmAiAxiomDO> getPage(OmAiAxiomPageReqVO reqVO) {
        return baseMapper.selectPage(reqVO);
    }

    @Override
    public Long create(OmAiAxiomSaveReqVO reqVO) {
        OmAiAxiomDO data = BeanUtils.toBean(reqVO, OmAiAxiomDO.class);
        data.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
        data.setDelFlag(false);
        baseMapper.insert(data);
        return data.getId();
    }

    @Override
    public int update(OmAiAxiomSaveReqVO reqVO) {
        OmAiAxiomDO existing = get(reqVO.getId());
        if (OmAiAdoptStatusEnum.ADOPTED.getCode().equals(existing.getAdoptStatus())) {
            throw new ServiceException("已采纳数据不可修改");
        }
        OmAiAxiomDO data = BeanUtils.toBean(reqVO, OmAiAxiomDO.class);
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
    public OmAiAxiomDO get(Long id) {
        OmAiAxiomDO data = baseMapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI候选模型公理不存在");
        }
        return data;
    }
}
