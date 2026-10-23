package tech.qiantong.qontology.module.app.service.appUnstructTaskText.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText.vo.AppUnstructTaskTextPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText.vo.AppUnstructTaskTextRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText.vo.AppUnstructTaskTextSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskText.AppUnstructTaskTextDO;
import tech.qiantong.qontology.module.app.dal.mapper.appUnstructTaskText.AppUnstructTaskTextMapper;
import tech.qiantong.qontology.module.app.service.appUnstructTaskText.IAppUnstructTaskTextService;
import tech.qiantong.qontology.common.exception.ServiceException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务文件段落关联Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppUnstructTaskTextServiceImpl
        extends ServiceImpl<AppUnstructTaskTextMapper, AppUnstructTaskTextDO>
        implements IAppUnstructTaskTextService {
    @Resource
    private AppUnstructTaskTextMapper appUnstructTaskTextMapper;

    /** 获得任务文件段落关联分页列表。 */
    @Override
    public PageResult<AppUnstructTaskTextDO> getAppUnstructTaskTextPage(AppUnstructTaskTextPageReqVO pageReqVO) {
        return appUnstructTaskTextMapper.selectPage(pageReqVO);
    }

    /** 创建任务文件段落关联。 */
    @Override
    public Long createAppUnstructTaskText(AppUnstructTaskTextSaveReqVO createReqVO) {
        AppUnstructTaskTextDO textDO = BeanUtils.toBean(createReqVO, AppUnstructTaskTextDO.class);
        appUnstructTaskTextMapper.insert(textDO);
        return textDO.getId();
    }

    /** 更新任务文件段落关联。 */
    @Override
    public int updateAppUnstructTaskText(AppUnstructTaskTextSaveReqVO updateReqVO) {
        AppUnstructTaskTextDO updateObj = BeanUtils.toBean(updateReqVO, AppUnstructTaskTextDO.class);
        return appUnstructTaskTextMapper.updateById(updateObj);
    }

    /** 删除任务文件段落关联。 */
    @Override
    public int removeAppUnstructTaskText(Collection<Long> idList) {
        return appUnstructTaskTextMapper.deleteByIds(idList);
    }

    /** 获得任务文件段落关联详情。 */
    @Override
    public AppUnstructTaskTextDO getAppUnstructTaskTextById(Long id) {
        return appUnstructTaskTextMapper.selectById(id);
    }

    /** 获得全部任务文件段落关联列表。 */
    @Override
    public List<AppUnstructTaskTextDO> getAppUnstructTaskTextList() {
        return appUnstructTaskTextMapper.selectList();
    }

    /** 获得全部任务文件段落关联Map。 */
    @Override
    public Map<Long, AppUnstructTaskTextDO> getAppUnstructTaskTextMap() {
        return appUnstructTaskTextMapper.selectList().stream().collect(Collectors.toMap(
                AppUnstructTaskTextDO::getId,
                textDO -> textDO,
                (existing, replacement) -> existing));
    }

    /** 导入任务文件段落关联数据。 */
    @Override
    public String importAppUnstructTaskText(List<AppUnstructTaskTextRespVO> importExcelList,
                                            boolean isUpdateSupport, String operName) {
        if (importExcelList == null || importExcelList.isEmpty()) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        for (AppUnstructTaskTextRespVO respVO : importExcelList) {
            AppUnstructTaskTextDO textDO = BeanUtils.toBean(respVO, AppUnstructTaskTextDO.class);
            AppUnstructTaskTextDO existing = respVO.getId() == null
                    ? null : appUnstructTaskTextMapper.selectById(respVO.getId());
            if (existing != null) {
                if (!isUpdateSupport) {
                    throw new ServiceException("数据已存在，ID为 " + respVO.getId());
                }
                textDO.setUpdateBy(operName);
                appUnstructTaskTextMapper.updateById(textDO);
            } else {
                textDO.setCreateBy(operName);
                appUnstructTaskTextMapper.insert(textDO);
            }
            successNum++;
        }
        return "恭喜您，数据已全部导入成功！共 " + successNum + " 条。";
    }

    /** 根据任务编号删除段落。 */
    @Override
    public int removeByTaskId(Long taskId) {
        return appUnstructTaskTextMapper.deleteByTaskId(taskId);
    }
}
