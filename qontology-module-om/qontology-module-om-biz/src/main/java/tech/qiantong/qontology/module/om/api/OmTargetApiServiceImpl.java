package tech.qiantong.qontology.module.om.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.module.om.api.service.IOmTargetApiService;
import tech.qiantong.qontology.module.om.api.service.Repeatable;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetDO;
import tech.qiantong.qontology.module.om.enums.AcceptStatusEnum;
import tech.qiantong.qontology.module.om.service.omtarget.IOmTargetService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OmTargetApiServiceImpl implements IOmTargetApiService, Repeatable {

    @Resource
    private IOmTargetService omTargetService;

    /**
     * 复制
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 是否复制成功
     */
    @Override
    public Boolean copy(Long sourceVersionId, Long targetVersionId) {
        LambdaQueryWrapper<OmTargetDO> queryWrapper = Wrappers.lambdaQuery(OmTargetDO.class)
                .eq(OmTargetDO::getVersionId, sourceVersionId);
        List<OmTargetDO> sourceList = omTargetService.list(queryWrapper);
        if (CollUtil.isEmpty(sourceList)){
            return true;
        }
        List<OmTargetDO> targetDOList = sourceList.stream()
                .peek(item -> {
                    item.setId(null);
                    item.setVersionId(targetVersionId);
                    item.setAcceptStatus(AcceptStatusEnum.WAITING);// 需要设置验证状态
                })
                .collect(Collectors.toList());
        return omTargetService.saveBatch(targetDOList);
    }
}
