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
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmObjectTypeTargetDO;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmObjectTypeTargetMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 建模目标与对象类型关联表（om_object_type_target）复制。
 *
 * <p>该表同时引用 om_object_type 与 om_target，两张父表在版本复制时都会重新生成主键，
 * 因此需要依据 {@link ProjectCopyContext} 中登记的「旧ID->新ID」映射，对 object_type_id
 * 与 target_id 两列做一一对应重映射。必须在两张父表复制完成之后执行。</p>
 */
@Slf4j
@Service
public class OmObjectTypeTargetApiServiceImpl implements IProjectRepeatable {

    @Resource
    private OmObjectTypeTargetMapper omObjectTypeTargetMapper;

    @Override
    public Boolean copy(ProjectCopyContext context) {
        Long sourceVersionId = context.getSourceVersionId();
        Long targetVersionId = context.getTargetVersionId();
        LoginUser currentUser = context.getCurrentUser();

        // 读取两张父表已登记的映射；任一缺失说明依赖表尚未复制，无法安全重映射
        Map<Long, Long> objectTypeMapping = context.getIdMapping(ProjectCopyContext.TABLE_OM_OBJECT_TYPE);
        Map<Long, Long> targetMapping = context.getIdMapping(ProjectCopyContext.TABLE_OM_TARGET);

        // 查询源版本下的全部关联记录
        LambdaQueryWrapper<OmObjectTypeTargetDO> queryWrapper = Wrappers.lambdaQuery(OmObjectTypeTargetDO.class)
                .eq(OmObjectTypeTargetDO::getVersionId, sourceVersionId);
        List<OmObjectTypeTargetDO> sourceList = omObjectTypeTargetMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(sourceList)) {
            return true;
        }

        Date now = new Date();
        List<OmObjectTypeTargetDO> targetList = new ArrayList<>(sourceList.size());
        for (OmObjectTypeTargetDO item : sourceList) {
            Long newObjectTypeId = objectTypeMapping.get(item.getObjectTypeId());
            Long newTargetId = targetMapping.get(item.getTargetId());
            // 两端关联对象必须均已被复制；任一无法映射则跳过该条并记录告警，避免脏关联
            if (newObjectTypeId == null || newTargetId == null) {
                log.warn("项目版本复制跳过无法映射的关联记录, sourceLinkId={}, objectTypeId={}, targetId={}, "
                                + "newObjectTypeId={}, newTargetId={}",
                        item.getId(), item.getObjectTypeId(), item.getTargetId(), newObjectTypeId, newTargetId);
                continue;
            }
            OmObjectTypeTargetDO link = BeanUtils.toBean(item, OmObjectTypeTargetDO.class);
            link.setId(null);
            link.setVersionId(targetVersionId);
            link.setObjectTypeId(newObjectTypeId);
            link.setTargetId(newTargetId);
            link.setCreatorId(currentUser.getUserId());
            link.setCreateBy(currentUser.getUser().getNickName());
            link.setCreateTime(now);
            targetList.add(link);
        }
        if (CollUtil.isEmpty(targetList)) {
            return true;
        }
        return omObjectTypeTargetMapper.insertBatch(targetList);
    }

    /**
     * 依赖 om_target(100) 与 om_object_type(200) 的ID映射，必须最后执行。
     */
    @Override
    public int copyOrder() {
        return 300;
    }
}
