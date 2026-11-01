package tech.qiantong.qontology.module.om.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.core.domain.model.LoginUser;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.api.service.IProjectRepeatable;
import tech.qiantong.qontology.module.om.api.service.ProjectCopyContext;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmDataRuleDO;
import tech.qiantong.qontology.module.om.dal.mapper.constraint.OmDataRuleMapper;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据规则项目版本复制服务
 */
@Service
public class OmDataRuleApiServiceImpl implements IProjectRepeatable {

    @Resource
    private OmDataRuleMapper omDataRuleMapper;

    /**
     * 将源版本的数据规则复制到目标版本
     *
     * @param context 项目版本复制上下文，承载源/目标版本、操作用户及跨表ID映射
     * @return 是否复制成功
     */
    @Override
    public Boolean copy(ProjectCopyContext context) {
        Long sourceVersionId = context.getSourceVersionId();
        Long targetVersionId = context.getTargetVersionId();
        LoginUser currentUser = context.getCurrentUser();
        // 按源版本查询，由逻辑删除机制排除已删除记录。
        LambdaQueryWrapper<OmDataRuleDO> queryWrapper = Wrappers.lambdaQuery(OmDataRuleDO.class)
                .eq(OmDataRuleDO::getVersionId, sourceVersionId);
        List<OmDataRuleDO> sourceList = omDataRuleMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(sourceList)) {
            return true;
        }
        // 沿用现有复制约定：重新生成主键，更新版本和创建信息，其余配置原样复制。
        List<OmDataRuleDO> targetList = sourceList.stream()
                .map(item -> {
                    OmDataRuleDO target = BeanUtils.toBean(item, OmDataRuleDO.class);
                    target.setId(null);
                    target.setVersionId(targetVersionId);
                    target.setCreatorId(currentUser.getUserId());
                    target.setCreateBy(currentUser.getUser().getNickName());
                    target.setCreateTime(new Date());
                    return target;
                })
                .collect(Collectors.toList());
        return omDataRuleMapper.insertBatch(targetList);
    }
}
