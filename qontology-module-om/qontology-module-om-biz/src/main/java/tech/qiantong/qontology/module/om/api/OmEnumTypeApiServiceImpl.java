package tech.qiantong.qontology.module.om.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.core.domain.model.LoginUser;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumTypeDO;
import tech.qiantong.qontology.module.om.dal.mapper.enumeration.OmEnumTypeMapper;
import tech.qiantong.qontology.module.om.api.service.IProjectRepeatable;
import tech.qiantong.qontology.module.om.api.service.ProjectCopyContext;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OmEnumTypeApiServiceImpl implements IProjectRepeatable {

    @Resource
    private OmEnumTypeMapper omEnumTypeMapper;

    /**
     * 复制
     *
     * @param context 项目版本复制上下文，承载源/目标版本、操作用户及跨表ID映射
     * @return 是否复制成功
     */
    @Override
    public Boolean copy(ProjectCopyContext context) {
        Long sourceVersionId = context.getSourceVersionId();
        Long targetVersionId = context.getTargetVersionId();
        LoginUser currentUser = context.getCurrentUser();
        // 查询源版本下的全部枚举类型
        LambdaQueryWrapper<OmEnumTypeDO> queryWrapper = Wrappers.lambdaQuery(OmEnumTypeDO.class)
                .eq(OmEnumTypeDO::getVersionId, sourceVersionId);
        List<OmEnumTypeDO> sourceList = omEnumTypeMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(sourceList)) {
            return true;
        }
        // 逐条复制为目标版本的新枚举类型：主键置空由数据库重新生成，版本指向目标版本，工作区保持不变
        List<OmEnumTypeDO> targetList = sourceList.stream()
                .map(item -> {
                    OmEnumTypeDO enumTypeDO = BeanUtils.toBean(item, OmEnumTypeDO.class);
                    enumTypeDO.setEnumId(null);
                    enumTypeDO.setVersionId(targetVersionId);
                    enumTypeDO.setCreatorId(currentUser.getUserId());
                    enumTypeDO.setCreateBy(currentUser.getUser().getNickName());
                    enumTypeDO.setCreateTime(new Date());
                    return enumTypeDO;
                })
                .collect(Collectors.toList());
        return omEnumTypeMapper.insertBatch(targetList);
    }
}
