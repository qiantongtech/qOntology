package tech.qiantong.qontology.module.om.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.core.domain.model.LoginUser;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.dal.dataobject.dataType.OmDataTypeDO;
import tech.qiantong.qontology.module.om.dal.mapper.dataType.OmDataTypeMapper;
import tech.qiantong.qontology.module.om.api.service.IProjectRepeatable;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OmDataTypeApiServiceImpl implements IProjectRepeatable {

    @Resource
    private OmDataTypeMapper omDataTypeMapper;

    /**
     * 复制
     *
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @param currentUser 当前用户
     * @return 是否复制成功
     */
    @Override
    public Boolean copy(Long sourceVersionId, Long targetVersionId, LoginUser currentUser) {
        // 查询源版本下的全部数据类型
        LambdaQueryWrapper<OmDataTypeDO> queryWrapper = Wrappers.lambdaQuery(OmDataTypeDO.class)
                .eq(OmDataTypeDO::getVersionId, sourceVersionId);
        List<OmDataTypeDO> sourceList = omDataTypeMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(sourceList)) {
            return true;
        }
        // 逐条复制为目标版本的新数据类型：主键置空由数据库重新生成，版本指向目标版本
        List<OmDataTypeDO> targetList = sourceList.stream()
                .map(item -> {
                    OmDataTypeDO dataTypeDO = BeanUtils.toBean(item, OmDataTypeDO.class);
                    dataTypeDO.setId(null);
                    dataTypeDO.setVersionId(targetVersionId);
                    dataTypeDO.setCreatorId(currentUser.getUserId());
                    dataTypeDO.setCreateBy(currentUser.getUser().getNickName());
                    dataTypeDO.setCreateTime(new Date());
                    return dataTypeDO;
                })
                .collect(Collectors.toList());
        return omDataTypeMapper.insertBatch(targetList);
    }
}
