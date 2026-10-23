package tech.qiantong.qontology.module.app.service.appTaskLog.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo.AppTaskLogDetailPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo.AppTaskLogPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo.AppTaskLogSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog.AppTaskLogDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog.AppTaskLogDetailDO;
import tech.qiantong.qontology.module.app.dal.mapper.appTaskLog.AppTaskLogDetailMapper;
import tech.qiantong.qontology.module.app.dal.mapper.appTaskLog.AppTaskLogMapper;
import tech.qiantong.qontology.module.app.extEnum.AppLogStatusEnum;
import tech.qiantong.qontology.module.app.extEnum.AppTaskTypeEnum;
import tech.qiantong.qontology.module.app.service.appTaskLog.IAppTaskLogService;

import java.util.Date;
import java.util.List;

/**
 * 抽取任务执行日志Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppTaskLogServiceImpl extends ServiceImpl<AppTaskLogMapper, AppTaskLogDO>
        implements IAppTaskLogService {
    @Resource
    private AppTaskLogMapper appTaskLogMapper;
    @Resource
    private AppTaskLogDetailMapper appTaskLogDetailMapper;

    /** 获得抽取任务执行日志分页列表。 */
    @Override
    public PageResult<AppTaskLogDO> getAppTaskLogPage(AppTaskLogPageReqVO pageReqVO) {
        return appTaskLogMapper.selectPage(pageReqVO);
    }

    /** 获得抽取任务执行日志步骤分页列表。 */
    @Override
    public PageResult<AppTaskLogDetailDO> getLogDetailPage(AppTaskLogDetailPageReqVO pageReqVO) {
        return appTaskLogDetailMapper.selectPage(pageReqVO);
    }

    /** 创建抽取任务执行日志。 */
    @Override
    public Long createAppTaskLog(AppTaskLogSaveReqVO createReqVO) {
        AppTaskLogDO taskLogDO = BeanUtils.toBean(createReqVO, AppTaskLogDO.class);
        appTaskLogMapper.insert(taskLogDO);
        return taskLogDO.getId();
    }

    /** 获得抽取任务执行日志详情。 */
    @Override
    public AppTaskLogDO getAppTaskLogById(Long id) {
        return appTaskLogMapper.selectById(id);
    }

    /** 开始执行日志。 */
    @Override
    public Long startInvoke(Long workspaceId, Long taskId, String taskName, AppTaskTypeEnum taskType, String createBy, Long creatorId) {
        AppTaskLogDO taskLogDO = new AppTaskLogDO();
        taskLogDO.setWorkspaceId(workspaceId);
        taskLogDO.setTaskId(taskId);
        taskLogDO.setTaskName(taskName);
        taskLogDO.setTaskType(taskType.getValue());
        taskLogDO.setStatus(AppLogStatusEnum.SUCCESS.getValue());
        taskLogDO.setStartTime(new Date());
        // 异步线程中无法获取登录用户，由调用方带入任务创建人
        taskLogDO.setCreateBy(createBy);
        taskLogDO.setCreatorId(creatorId);
        taskLogDO.setValidFlag(true);
        taskLogDO.setDelFlag(false);
        appTaskLogMapper.insert(taskLogDO);
        return taskLogDO.getId();
    }

    /** 记录日志步骤并刷新日志结束时间。 */
    @Override
    public void recordStep(Long logId, String step) {
        AppTaskLogDO taskLogDO = appTaskLogMapper.selectById(logId);
        if (taskLogDO == null) {
            throw new ServiceException("获取日志对象出错");
        }
        String stepText = limitText(step);
        AppTaskLogDetailDO detailDO = new AppTaskLogDetailDO();
        detailDO.setLogId(logId);
        detailDO.setWorkspaceId(taskLogDO.getWorkspaceId());
        detailDO.setStep(stepText);
        detailDO.setValidFlag(true);
        detailDO.setDelFlag(false);
        appTaskLogDetailMapper.insert(detailDO);
        updateEndTime(logId);
    }

    /** 结束执行日志。 */
    @Override
    public void endInvoke(Long logId, AppLogStatusEnum status, String errorMsg) {
        update(Wrappers.<AppTaskLogDO>lambdaUpdate()
                .set(AppTaskLogDO::getStatus, status.getValue())
                .set(AppTaskLogDO::getErrorMsg, limitText(errorMsg))
                .set(AppTaskLogDO::getEndTime, new Date())
                .eq(AppTaskLogDO::getId, logId));
    }

    /** 删除抽取任务执行日志。 */
    @Override
    public Integer removeAppTaskById(List<Long> idList) {
        return appTaskLogMapper.deleteByIds(idList);
    }

    /** 更新日志结束时间。 */
    private void updateEndTime(Long logId) {
        update(Wrappers.<AppTaskLogDO>lambdaUpdate()
                .set(AppTaskLogDO::getEndTime, new Date())
                .eq(AppTaskLogDO::getId, logId));
    }

    /** 将日志文本限制在数据库字段允许的长度内。 */
    private String limitText(String text) {
        String value = text == null ? "" : text;
        return value.length() > 2000 ? value.substring(0, 2000) : value;
    }
}
