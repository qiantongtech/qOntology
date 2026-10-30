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
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmRelationTargetDO;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmRelationTargetMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 对象类型关系与建模目标关联表（om_relation_target）复制。
 *
 * <p>该表的 target_id 引用 om_target.id、relation_id 引用 om_object_type_relation.id，
 * 两张父表在版本复制时都会重新生成主键，因此需依据 {@link ProjectCopyContext} 中登记的
 * 「旧ID-&gt;新ID」映射，对两列做一一对应重映射。必须在 om_target 与
 * om_object_type_relation 复制完成之后执行。</p>
 */
@Slf4j
@Service
public class OmRelationTargetApiServiceImpl implements IProjectRepeatable {

    @Resource
    private OmRelationTargetMapper omRelationTargetMapper;

    @Override
    public Boolean copy(ProjectCopyContext context) {
        Long sourceVersionId = context.getSourceVersionId();
        Long targetVersionId = context.getTargetVersionId();
        LoginUser currentUser = context.getCurrentUser();

        // 读取两张父表已登记的映射
        Map<Long, Long> targetMapping = context.getIdMapping(ProjectCopyContext.TABLE_OM_TARGET);
        Map<Long, Long> relationMapping = context.getIdMapping(ProjectCopyContext.TABLE_OM_OBJECT_TYPE_RELATION);

        // 查询源版本下的全部关系-目标关联记录
        LambdaQueryWrapper<OmRelationTargetDO> queryWrapper = Wrappers.lambdaQuery(OmRelationTargetDO.class)
                .eq(OmRelationTargetDO::getVersionId, sourceVersionId);
        List<OmRelationTargetDO> sourceList = omRelationTargetMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(sourceList)) {
            return true;
        }

        Date now = new Date();
        List<OmRelationTargetDO> targetList = new ArrayList<>(sourceList.size());
        for (OmRelationTargetDO item : sourceList) {
            // 外键为空视为无关联，原样保留；非空但映射不到说明父表未复制成功，跳过并告警
            Long newTargetId = remap(item.getTargetId(), targetMapping);
            Long newRelationId = remap(item.getRelationId(), relationMapping);
            if ((item.getTargetId() != null && newTargetId == null)
                    || (item.getRelationId() != null && newRelationId == null)) {
                log.warn("项目版本复制跳过无法映射的关系-目标关联记录, sourceLinkId={}, targetId={}, relationId={}, "
                                + "newTargetId={}, newRelationId={}",
                        item.getId(), item.getTargetId(), item.getRelationId(), newTargetId, newRelationId);
                continue;
            }
            OmRelationTargetDO link = BeanUtils.toBean(item, OmRelationTargetDO.class);
            link.setId(null);
            link.setVersionId(targetVersionId);
            link.setTargetId(newTargetId);
            link.setRelationId(newRelationId);
            link.setCreatorId(currentUser.getUserId());
            link.setCreateBy(currentUser.getUser().getNickName());
            link.setCreateTime(now);
            targetList.add(link);
        }
        if (CollUtil.isEmpty(targetList)) {
            return true;
        }
        return omRelationTargetMapper.insertBatch(targetList);
    }

    /**
     * 旧ID为空时返回 null（无关联），否则按映射表转换。
     */
    private Long remap(Long oldId, Map<Long, Long> mapping) {
        return oldId == null ? null : mapping.get(oldId);
    }

    /**
     * 依赖 om_target(100) 与 om_object_type_relation(400) 的ID映射，必须最后执行。
     */
    @Override
    public int copyOrder() {
        return 500;
    }
}
