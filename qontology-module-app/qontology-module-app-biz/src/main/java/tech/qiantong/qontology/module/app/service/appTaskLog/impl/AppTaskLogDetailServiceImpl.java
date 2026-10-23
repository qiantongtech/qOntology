package tech.qiantong.qontology.module.app.service.appTaskLog.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog.AppTaskLogDetailDO;
import tech.qiantong.qontology.module.app.dal.mapper.appTaskLog.AppTaskLogDetailMapper;
import tech.qiantong.qontology.module.app.service.appTaskLog.IAppTaskLogDetailService;
import java.util.*;
import java.util.stream.Collectors;

/** 抽取任务执行日志详情 Service 业务层处理。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppTaskLogDetailServiceImpl extends ServiceImpl<AppTaskLogDetailMapper, AppTaskLogDetailDO>
        implements IAppTaskLogDetailService {
    /** 获得日志详情分页列表。 */
    @Override
    public PageResult<AppTaskLogDetailDO> getAppTaskLogDetailPage(AppTaskLogDetailPageReqVO reqVO) {
        return baseMapper.selectPage(reqVO);
    }
    /** 创建日志详情。 */
    @Override
    public Long createAppTaskLogDetail(AppTaskLogDetailSaveReqVO reqVO) {
        AppTaskLogDetailDO entity = BeanUtils.toBean(reqVO, AppTaskLogDetailDO.class);
        baseMapper.insert(entity);
        return entity.getId();
    }
    /** 更新日志详情。 */
    @Override
    public int updateAppTaskLogDetail(AppTaskLogDetailSaveReqVO reqVO) {
        return baseMapper.updateById(BeanUtils.toBean(reqVO, AppTaskLogDetailDO.class));
    }
    /** 删除日志详情。 */
    @Override
    public int removeAppTaskLogDetail(Collection<Long> ids) {
        return baseMapper.deleteByIds(ids);
    }
    /** 获得日志详情。 */
    @Override
    public AppTaskLogDetailDO getAppTaskLogDetailById(Long id) {
        return baseMapper.selectById(id);
    }
    /** 获得全部日志详情列表。 */
    @Override
    public List<AppTaskLogDetailDO> getAppTaskLogDetailList() {
        return baseMapper.selectList();
    }
    /** 获得全部日志详情 Map。 */
    @Override
    public Map<Long, AppTaskLogDetailDO> getAppTaskLogDetailMap() {
        return getAppTaskLogDetailList().stream().collect(Collectors.toMap(
                AppTaskLogDetailDO::getId, item -> item, (existing, replacement) -> existing));
    }
}
