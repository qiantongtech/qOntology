package tech.qiantong.qontology.module.om.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.core.domain.model.LoginUser;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.api.service.ProjectCopyContext;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeMapper;
import tech.qiantong.qontology.module.om.api.service.IProjectRepeatable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OmObjectTypeApiServiceImpl implements IProjectRepeatable {

    @Resource
    private OmObjectTypeMapper omObjectTypeMapper;

    /**
     * 复制
     *
     * @param context 项目版本复制上下文
     * @return 是否复制成功
     */
    @Override
    public Boolean copy(ProjectCopyContext context) {
        Long sourceVersionId = context.getSourceVersionId();
        Long targetVersionId = context.getTargetVersionId();
        LoginUser currentUser = context.getCurrentUser();
        // 查询源版本下的全部对象类型（含树形结构）
        LambdaQueryWrapper<OmObjectTypeDO> queryWrapper = Wrappers.lambdaQuery(OmObjectTypeDO.class)
                .eq(OmObjectTypeDO::getVersionId, sourceVersionId);
        List<OmObjectTypeDO> sourceList = omObjectTypeMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(sourceList)) {
            return true;
        }

        Date now = new Date();
        // 第一遍：先复制出新记录，主键置空由数据库重新生成，版本指向目标版本；
        // 此时 parentId / ancestors 仍为源旧值，待拿到新主键后再重映射
        List<OmObjectTypeDO> targetList = sourceList.stream()
                .map(item -> {
                    OmObjectTypeDO typeDO = BeanUtils.toBean(item, OmObjectTypeDO.class);
                    typeDO.setId(null);
                    typeDO.setVersionId(targetVersionId);
                    typeDO.setCreatorId(currentUser.getUserId());
                    typeDO.setCreateBy(currentUser.getUser().getNickName());
                    typeDO.setCreateTime(now);
                    return typeDO;
                })
                .collect(Collectors.toList());
        // 批量插入后，各新记录的自增主键会被回填到 targetList
        if (!omObjectTypeMapper.insertBatch(targetList)) {
            return false;
        }

        // 建立 源ID -> 新ID 的映射（sourceList 与 targetList 按下标一一对应）
        Map<Long, Long> oldIdToNewId = new HashMap<>(sourceList.size());
        for (int i = 0; i < sourceList.size(); i++) {
            oldIdToNewId.put(sourceList.get(i).getId(), targetList.get(i).getId());
        }
        // 登记 om_object_type 的 旧ID->新ID 映射，供 om_object_type_target 等关联表重映射使用
        context.putIdMapping(ProjectCopyContext.TABLE_OM_OBJECT_TYPE, oldIdToNewId);

        // 第二遍：将父级id 与 祖级列表中的旧 ID 重映射为新 ID
        List<OmObjectTypeDO> updateList = new ArrayList<>(targetList.size());
        for (OmObjectTypeDO typeDO : targetList) {
            OmObjectTypeDO update = new OmObjectTypeDO();
            update.setId(typeDO.getId());

            // 父级id：顶级（0/null）保持不变，其余映射到新的父级id
            Long oldParentId = typeDO.getParentId();
            Long newParentId = (oldParentId == null || oldParentId == 0L)
                    ? 0L : oldIdToNewId.getOrDefault(oldParentId, 0L);
            update.setParentId(newParentId);

            // 祖级列表：逐段将旧 ID 替换为新 ID，保留开头的顶级标记 0
            String ancestors = typeDO.getAncestors();
            if (ancestors != null && !ancestors.isEmpty()) {
                String newAncestors = Arrays.stream(ancestors.split(","))
                        .map(seg -> {
                            String trimmed = seg.trim();
                            if (trimmed.isEmpty() || "0".equals(trimmed)) {
                                return trimmed;
                            }
                            Long mapped = oldIdToNewId.get(Long.valueOf(trimmed));
                            return mapped == null ? trimmed : String.valueOf(mapped);
                        })
                        .collect(Collectors.joining(","));
                update.setAncestors(newAncestors);
            }
            updateList.add(update);
        }
        return omObjectTypeMapper.updateBatch(updateList);
    }

    /**
     * 需先于 om_object_type_target 复制，以便其读取到对象类型的ID映射。
     */
    @Override
    public int copyOrder() {
        return 200;
    }
}
