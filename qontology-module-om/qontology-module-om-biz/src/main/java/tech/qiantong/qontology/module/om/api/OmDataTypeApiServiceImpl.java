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
import tech.qiantong.qontology.module.om.api.service.ProjectCopyContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Boolean copy(ProjectCopyContext context) {
        Long sourceVersionId = context.getSourceVersionId();
        Long targetVersionId = context.getTargetVersionId();
        LoginUser currentUser = context.getCurrentUser();
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
        boolean saved = omDataTypeMapper.insertBatch(targetList);

        // 登记 om_data_type 的 旧ID->新ID 映射，供 om_object_type_attribute 重映射 business_type_id 使用
        // （sourceList 与 targetList 无跳过，下标严格对齐）
        Map<Long, Long> idMapping = new HashMap<>(sourceList.size());
        for (int i = 0; i < sourceList.size(); i++) {
            idMapping.put(sourceList.get(i).getId(), targetList.get(i).getId());
        }
        context.putIdMapping(ProjectCopyContext.TABLE_OM_DATA_TYPE, idMapping);
        return saved;
    }

    /**
     * 先于引用它的 om_object_type_attribute 复制，并对外提供ID映射。
     */
    @Override
    public int copyOrder() {
        return 150;
    }
}
