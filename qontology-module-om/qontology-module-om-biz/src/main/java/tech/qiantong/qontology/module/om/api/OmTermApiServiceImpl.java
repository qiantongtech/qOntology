package tech.qiantong.qontology.module.om.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.core.domain.model.LoginUser;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.dal.dataobject.term.OmTermDO;
import tech.qiantong.qontology.module.om.dal.mapper.term.OmTermMapper;
import tech.qiantong.qontology.module.om.api.service.IProjectRepeatable;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OmTermApiServiceImpl implements IProjectRepeatable {

    @Resource
    private OmTermMapper omTermMapper;

    /**
     * 复制
     *
     * @param sourceVersionId 源版本ID
     * @param targetVersionId 目标版本ID
     * @return 是否复制成功
     */
    @Override
    public Boolean copy(Long sourceVersionId, Long targetVersionId, LoginUser currentUser) {
        // 查询源版本下的全部术语
        LambdaQueryWrapper<OmTermDO> queryWrapper = Wrappers.lambdaQuery(OmTermDO.class)
                .eq(OmTermDO::getVersionId, sourceVersionId);
        List<OmTermDO> sourceList = omTermMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(sourceList)) {
            return true;
        }
        // 逐条复制为目标版本的新术语：主键置空由数据库重新生成，版本指向目标版本，工作区保持不变
        List<OmTermDO> targetList = sourceList.stream()
                .map(item -> {
                    OmTermDO termDO = BeanUtils.toBean(item, OmTermDO.class);
                    termDO.setId(null);
                    termDO.setVersionId(targetVersionId);
                    termDO.setCreatorId(currentUser.getUserId());
                    termDO.setCreateBy(currentUser.getUsername());
                    termDO.setCreateTime(new Date());
                    return termDO;
                })
                .collect(Collectors.toList());
        return omTermMapper.insertBatch(targetList);
    }
}
