package tech.qiantong.qontology.module.om.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.core.domain.model.LoginUser;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.api.service.IOmTargetApiService;
import tech.qiantong.qontology.module.om.api.service.IProjectRepeatable;
import tech.qiantong.qontology.module.om.api.service.ProjectCopyContext;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetDO;
import tech.qiantong.qontology.module.om.enums.AcceptStatusEnum;
import tech.qiantong.qontology.module.om.service.omtarget.IOmTargetService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OmTargetApiServiceImpl implements IOmTargetApiService, IProjectRepeatable {

    @Resource
    private IOmTargetService omTargetService;

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
        LambdaQueryWrapper<OmTargetDO> queryWrapper = Wrappers.lambdaQuery(OmTargetDO.class)
                .eq(OmTargetDO::getVersionId, sourceVersionId);
        List<OmTargetDO> sourceList = omTargetService.list(queryWrapper);
        if (CollUtil.isEmpty(sourceList)) {
            return true;
        }
        // 生成副本插入，避免直接改写查询结果导致旧ID丢失（副本与源按下标一一对应）
        LoginUser currentUser = context.getCurrentUser();
        Date now = new Date();
        List<OmTargetDO> newTargets = sourceList.stream()
                .map(item -> {
                    OmTargetDO target = BeanUtils.toBean(item, OmTargetDO.class);
                    target.setId(null);
                    target.setVersionId(targetVersionId);
                    target.setAcceptStatus(AcceptStatusEnum.WAITING);// 需要设置验证状态
                    target.setCreatorId(currentUser.getUserId());
                    target.setCreateBy(currentUser.getUser().getNickName());
                    target.setCreateTime(now);
                    return target;
                })
                .collect(Collectors.toList());
        boolean saved = omTargetService.saveBatch(newTargets);

        // 登记 om_target 的 旧ID->新ID 映射，供 om_object_type_target 等关联表重映射使用
        Map<Long, Long> idMapping = new HashMap<>(sourceList.size());
        for (int i = 0; i < sourceList.size(); i++) {
            idMapping.put(sourceList.get(i).getId(), newTargets.get(i).getId());
        }
        context.putIdMapping(ProjectCopyContext.TABLE_OM_TARGET, idMapping);
        return saved;
    }

    /**
     * om_target 需先于引用它的关联表复制，故赋予较高的执行顺序。
     */
    @Override
    public int copyOrder() {
        return 100;
    }
}
