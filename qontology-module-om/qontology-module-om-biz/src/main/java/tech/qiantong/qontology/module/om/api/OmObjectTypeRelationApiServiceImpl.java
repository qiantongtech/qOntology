package tech.qiantong.qontology.module.om.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.core.domain.model.LoginUser;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.api.service.IProjectRepeatable;
import tech.qiantong.qontology.module.om.api.service.ProjectCopyContext;
import tech.qiantong.qontology.module.om.dal.dataobject.relation.OmObjectTypeRelationDO;
import tech.qiantong.qontology.module.om.dal.mapper.relation.OmObjectTypeRelationMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象类型关系表（om_object_type_relation）复制。
 *
 * <p>该表的 start_type_id 与 end_type_id 均引用 om_object_type.id，而对象类型在版本复制时
 * 会重新生成主键，因此需依据 {@link ProjectCopyContext} 中登记的 om_object_type「旧ID->新ID」
 * 映射，对起点、终点两列做一一对应重映射。必须在 om_object_type 复制完成之后执行。</p>
 */
@Slf4j
@Service
public class OmObjectTypeRelationApiServiceImpl implements IProjectRepeatable {

    @Resource
    private OmObjectTypeRelationMapper omObjectTypeRelationMapper;

    @Override
    public Boolean copy(ProjectCopyContext context) {
        Long sourceVersionId = context.getSourceVersionId();
        Long targetVersionId = context.getTargetVersionId();
        LoginUser currentUser = context.getCurrentUser();

        // 读取对象类型已登记的映射；缺失说明依赖表尚未复制，无法安全重映射
        Map<Long, Long> objectTypeMapping = context.getIdMapping(ProjectCopyContext.TABLE_OM_OBJECT_TYPE);

        // 查询源版本下的全部对象类型关系
        LambdaQueryWrapper<OmObjectTypeRelationDO> queryWrapper = Wrappers.lambdaQuery(OmObjectTypeRelationDO.class)
                .eq(OmObjectTypeRelationDO::getVersionId, sourceVersionId);
        List<OmObjectTypeRelationDO> sourceList = omObjectTypeRelationMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(sourceList)) {
            return true;
        }

        Date now = new Date();
        List<OmObjectTypeRelationDO> targetList = new ArrayList<>(sourceList.size());
        // 与 targetList 下标对齐地记录旧主键（循环中存在跳过，不能直接用 sourceList 下标对应）
        List<Long> oldIds = new ArrayList<>(sourceList.size());
        for (OmObjectTypeRelationDO item : sourceList) {
            Long newStartTypeId = objectTypeMapping.get(item.getStartTypeId());
            Long newEndTypeId = objectTypeMapping.get(item.getEndTypeId());
            // 起点、终点对象类型必须均已被复制；任一无法映射则跳过该条并记录告警，避免悬挂关系
            if (newStartTypeId == null || newEndTypeId == null) {
                log.warn("项目版本复制跳过无法映射的关系记录, sourceRelationId={}, startTypeId={}, endTypeId={}, "
                                + "newStartTypeId={}, newEndTypeId={}",
                        item.getId(), item.getStartTypeId(), item.getEndTypeId(), newStartTypeId, newEndTypeId);
                continue;
            }
            OmObjectTypeRelationDO relation = BeanUtils.toBean(item, OmObjectTypeRelationDO.class);
            relation.setId(null);
            relation.setVersionId(targetVersionId);
            relation.setStartTypeId(newStartTypeId);
            relation.setEndTypeId(newEndTypeId);
            relation.setCreatorId(currentUser.getUserId());
            relation.setCreateBy(currentUser.getUsername());
            relation.setCreateTime(now);
            targetList.add(relation);
            oldIds.add(item.getId());
        }
        if (CollUtil.isEmpty(targetList)) {
            return true;
        }
        boolean saved = omObjectTypeRelationMapper.insertBatch(targetList);

        // 登记 om_object_type_relation 的 旧ID->新ID 映射，供 om_relation_target 重映射 relation_id 使用
        Map<Long, Long> idMapping = new HashMap<>(oldIds.size());
        for (int i = 0; i < oldIds.size(); i++) {
            idMapping.put(oldIds.get(i), targetList.get(i).getId());
        }
        context.putIdMapping(ProjectCopyContext.TABLE_OM_OBJECT_TYPE_RELATION, idMapping);
        return saved;
    }

    /**
     * 仅依赖 om_object_type(200) 的ID映射，排在对象类型复制之后即可。
     */
    @Override
    public int copyOrder() {
        return 400;
    }
}
