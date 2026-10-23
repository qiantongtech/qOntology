package tech.qiantong.qontology.module.app.service.appUnstructTaskDocRel;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskDocRel.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskDocRel.AppUnstructTaskDocRelDO;
import java.util.*;

/** 任务文件关联 Service 接口。 */
public interface IAppUnstructTaskDocRelService extends IService<AppUnstructTaskDocRelDO> {
    /** 获得任务文件关联分页列表。 */
    PageResult<AppUnstructTaskDocRelDO> getAppUnstructTaskDocRelPage(AppUnstructTaskDocRelPageReqVO pageReqVO);
    /** 创建任务文件关联。 */
    Long createAppUnstructTaskDocRel(AppUnstructTaskDocRelSaveReqVO createReqVO);
    /** 更新任务文件关联。 */
    int updateAppUnstructTaskDocRel(AppUnstructTaskDocRelSaveReqVO updateReqVO);
    /** 删除任务文件关联。 */
    int removeAppUnstructTaskDocRel(Collection<Long> idList);
    /** 获得任务文件关联详情。 */
    AppUnstructTaskDocRelDO getAppUnstructTaskDocRelById(Long id);
    /** 获得全部任务文件关联列表。 */
    List<AppUnstructTaskDocRelDO> getAppUnstructTaskDocRelList();
    /** 获得全部任务文件关联 Map。 */
    Map<Long, AppUnstructTaskDocRelDO> getAppUnstructTaskDocRelMap();
    /** 导入任务文件关联。 */
    String importAppUnstructTaskDocRel(List<AppUnstructTaskDocRelRespVO> importExcelList,
                                       boolean isUpdateSupport, String operName);
    /** 根据任务 ID 获得文件关联列表。 */
    List<AppUnstructTaskDocRelDO> findByTaskId(Long taskId);
    /** 根据任务 ID 删除文件关联。 */
    int removeByTaskId(Long taskId);
    /** 编辑任务时物理清理旧文件关联，释放任务-文件唯一键。 */
    int deletePhysicallyByTaskId(Long taskId);
}
