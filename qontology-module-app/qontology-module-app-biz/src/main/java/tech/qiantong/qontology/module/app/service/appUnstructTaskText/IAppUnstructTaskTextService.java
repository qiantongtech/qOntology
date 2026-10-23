package tech.qiantong.qontology.module.app.service.appUnstructTaskText;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskText.AppUnstructTaskTextDO;
import java.util.*;

/** 任务文件段落关联 Service 接口。 */
public interface IAppUnstructTaskTextService extends IService<AppUnstructTaskTextDO> {
    /** 获得任务文件段落关联分页列表。 */
    PageResult<AppUnstructTaskTextDO> getAppUnstructTaskTextPage(AppUnstructTaskTextPageReqVO pageReqVO);
    /** 创建任务文件段落关联。 */
    Long createAppUnstructTaskText(AppUnstructTaskTextSaveReqVO createReqVO);
    /** 更新任务文件段落关联。 */
    int updateAppUnstructTaskText(AppUnstructTaskTextSaveReqVO updateReqVO);
    /** 删除任务文件段落关联。 */
    int removeAppUnstructTaskText(Collection<Long> idList);
    /** 获得任务文件段落关联详情。 */
    AppUnstructTaskTextDO getAppUnstructTaskTextById(Long id);
    /** 获得全部任务文件段落关联列表。 */
    List<AppUnstructTaskTextDO> getAppUnstructTaskTextList();
    /** 获得全部任务文件段落关联 Map。 */
    Map<Long, AppUnstructTaskTextDO> getAppUnstructTaskTextMap();
    /** 导入任务文件段落关联。 */
    String importAppUnstructTaskText(List<AppUnstructTaskTextRespVO> importExcelList,
                                     boolean isUpdateSupport, String operName);
    /** 根据任务 ID 删除旧段落。 */
    int removeByTaskId(Long taskId);
}
