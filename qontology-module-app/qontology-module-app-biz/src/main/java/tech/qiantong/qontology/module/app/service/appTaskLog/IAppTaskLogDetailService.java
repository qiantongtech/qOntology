package tech.qiantong.qontology.module.app.service.appTaskLog;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog.AppTaskLogDetailDO;
import java.util.*;

/** 抽取任务执行日志详情 Service 接口。 */
public interface IAppTaskLogDetailService extends IService<AppTaskLogDetailDO> {
    /** 获得日志详情分页列表。 */
    PageResult<AppTaskLogDetailDO> getAppTaskLogDetailPage(AppTaskLogDetailPageReqVO pageReqVO);
    /** 创建日志详情。 */
    Long createAppTaskLogDetail(AppTaskLogDetailSaveReqVO createReqVO);
    /** 更新日志详情。 */
    int updateAppTaskLogDetail(AppTaskLogDetailSaveReqVO updateReqVO);
    /** 删除日志详情。 */
    int removeAppTaskLogDetail(Collection<Long> idList);
    /** 获得日志详情。 */
    AppTaskLogDetailDO getAppTaskLogDetailById(Long id);
    /** 获得全部日志详情列表。 */
    List<AppTaskLogDetailDO> getAppTaskLogDetailList();
    /** 获得全部日志详情 Map。 */
    Map<Long, AppTaskLogDetailDO> getAppTaskLogDetailMap();
}
