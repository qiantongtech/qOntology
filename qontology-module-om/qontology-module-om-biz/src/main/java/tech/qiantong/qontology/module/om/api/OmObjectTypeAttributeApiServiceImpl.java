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
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeAttributeDO;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeAttributeMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 对象类型属性表（om_object_type_attribute）复制。
 *
 * <p>依赖说明：</p>
 * <ul>
 *   <li>object_type_id 引用 om_object_type.id，父表复制会重新生成主键，需按
 *       {@link ProjectCopyContext} 中的 om_object_type 映射重映射；</li>
 *   <li>business_type_id 需按 business_type_source 区分：取值 PROJECT 时引用当前项目的
 *       om_data_type.id（同样会被重新生成主键），需按 om_data_type 映射重映射；
 *       取值 GLOBAL（或为空）时引用全局 basic_data_type，不随版本复制，原样拷贝即可；</li>
 *   <li>enum_type 通过枚举编码字符串关联而非主键，无需重映射。</li>
 * </ul>
 */
@Slf4j
@Service
public class OmObjectTypeAttributeApiServiceImpl implements IProjectRepeatable {

    /** 业务数据类型来源：当前项目 */
    private static final String SOURCE_PROJECT = "PROJECT";

    @Resource
    private OmObjectTypeAttributeMapper omObjectTypeAttributeMapper;

    @Override
    public Boolean copy(ProjectCopyContext context) {
        Long sourceVersionId = context.getSourceVersionId();
        Long targetVersionId = context.getTargetVersionId();
        LoginUser currentUser = context.getCurrentUser();

        // 读取两张父表已登记的映射
        Map<Long, Long> objectTypeMapping = context.getIdMapping(ProjectCopyContext.TABLE_OM_OBJECT_TYPE);
        Map<Long, Long> dataTypeMapping = context.getIdMapping(ProjectCopyContext.TABLE_OM_DATA_TYPE);

        // 查询源版本下的全部对象类型属性
        LambdaQueryWrapper<OmObjectTypeAttributeDO> queryWrapper = Wrappers.lambdaQuery(OmObjectTypeAttributeDO.class)
                .eq(OmObjectTypeAttributeDO::getVersionId, sourceVersionId);
        List<OmObjectTypeAttributeDO> sourceList = omObjectTypeAttributeMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(sourceList)) {
            return true;
        }

        Date now = new Date();
        List<OmObjectTypeAttributeDO> targetList = new ArrayList<>(sourceList.size());
        for (OmObjectTypeAttributeDO item : sourceList) {
            Long newObjectTypeId = objectTypeMapping.get(item.getObjectTypeId());
            if (newObjectTypeId == null) {
                log.warn("项目版本复制跳过无法映射所属对象类型的属性, sourceAttributeId={}, objectTypeId={}",
                        item.getId(), item.getObjectTypeId());
                continue;
            }

            // 仅 PROJECT 来源需要把 business_type_id 换成新数据类型id；其余来源原样保留
            Long newBusinessTypeId = item.getBusinessTypeId();
            if (SOURCE_PROJECT.equals(item.getBusinessTypeSource()) && item.getBusinessTypeId() != null) {
                newBusinessTypeId = dataTypeMapping.get(item.getBusinessTypeId());
                if (newBusinessTypeId == null) {
                    log.warn("项目版本复制跳过无法映射业务数据类型的属性, sourceAttributeId={}, businessTypeId={}",
                            item.getId(), item.getBusinessTypeId());
                    continue;
                }
            }

            OmObjectTypeAttributeDO attribute = BeanUtils.toBean(item, OmObjectTypeAttributeDO.class);
            attribute.setId(null);
            attribute.setVersionId(targetVersionId);
            attribute.setObjectTypeId(newObjectTypeId);
            attribute.setBusinessTypeId(newBusinessTypeId);
            attribute.setCreatorId(currentUser.getUserId());
            attribute.setCreateBy(currentUser.getUser().getNickName());
            attribute.setCreateTime(now);
            targetList.add(attribute);
        }
        if (CollUtil.isEmpty(targetList)) {
            return true;
        }
        return omObjectTypeAttributeMapper.insertBatch(targetList);
    }

    /**
     * 依赖 om_object_type(200) 与 om_data_type(150) 的ID映射，排在两者之后。
     */
    @Override
    public int copyOrder() {
        return 600;
    }
}
