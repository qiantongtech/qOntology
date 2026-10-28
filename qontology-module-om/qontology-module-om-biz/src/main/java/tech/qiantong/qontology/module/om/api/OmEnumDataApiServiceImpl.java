package tech.qiantong.qontology.module.om.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.core.domain.model.LoginUser;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumDataDO;
import tech.qiantong.qontology.module.om.dal.mapper.enumeration.OmEnumDataMapper;
import tech.qiantong.qontology.module.om.api.service.IProjectRepeatable;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OmEnumDataApiServiceImpl implements IProjectRepeatable {

    @Resource
    private OmEnumDataMapper omEnumDataMapper;

    /**
     * 复制
     *
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @param currentUser     当前用户
     * @return 是否复制成功
     */
    @Override
    public Boolean copy(Long sourceVersionId, Long targetVersionId, LoginUser currentUser) {
        // 查询源版本下的全部枚举数据
        LambdaQueryWrapper<OmEnumDataDO> queryWrapper = Wrappers.lambdaQuery(OmEnumDataDO.class)
                .eq(OmEnumDataDO::getVersionId, sourceVersionId);
        List<OmEnumDataDO> sourceList = omEnumDataMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(sourceList)) {
            return true;
        }
        // 逐条复制为目标版本的新枚举数据：主键置空由数据库重新生成，版本指向目标版本，
        // 所属类型按 enumType 编码关联，不随主键变化，故无需重映射
        List<OmEnumDataDO> targetList = sourceList.stream()
                .map(item -> {
                    OmEnumDataDO enumDataDO = BeanUtils.toBean(item, OmEnumDataDO.class);
                    enumDataDO.setEnumCode(null);
                    enumDataDO.setVersionId(targetVersionId);
                    enumDataDO.setCreatorId(currentUser.getUserId());
                    enumDataDO.setCreateBy(currentUser.getUser().getNickName());
                    enumDataDO.setCreateTime(new Date());
                    return enumDataDO;
                })
                .collect(Collectors.toList());
        return omEnumDataMapper.insertBatch(targetList);
    }
}
