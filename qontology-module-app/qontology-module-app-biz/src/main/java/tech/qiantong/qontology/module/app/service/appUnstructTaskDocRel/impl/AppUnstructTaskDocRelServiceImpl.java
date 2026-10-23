package tech.qiantong.qontology.module.app.service.appUnstructTaskDocRel.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskDocRel.vo.AppUnstructTaskDocRelPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskDocRel.vo.AppUnstructTaskDocRelRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskDocRel.vo.AppUnstructTaskDocRelSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskDocRel.AppUnstructTaskDocRelDO;
import tech.qiantong.qontology.module.app.dal.mapper.appUnstructTaskDocRel.AppUnstructTaskDocRelMapper;
import tech.qiantong.qontology.module.app.service.appUnstructTaskDocRel.IAppUnstructTaskDocRelService;
import tech.qiantong.qontology.common.exception.ServiceException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务文件关联Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppUnstructTaskDocRelServiceImpl
        extends ServiceImpl<AppUnstructTaskDocRelMapper, AppUnstructTaskDocRelDO>
        implements IAppUnstructTaskDocRelService {
    @Resource
    private AppUnstructTaskDocRelMapper appUnstructTaskDocRelMapper;

    /** 获得任务文件关联分页列表。 */
    @Override
    public PageResult<AppUnstructTaskDocRelDO> getAppUnstructTaskDocRelPage(AppUnstructTaskDocRelPageReqVO pageReqVO) {
        return appUnstructTaskDocRelMapper.selectPage(pageReqVO);
    }

    /** 创建任务文件关联。 */
    @Override
    public Long createAppUnstructTaskDocRel(AppUnstructTaskDocRelSaveReqVO createReqVO) {
        AppUnstructTaskDocRelDO relationDO = BeanUtils.toBean(createReqVO, AppUnstructTaskDocRelDO.class);
        appUnstructTaskDocRelMapper.insert(relationDO);
        return relationDO.getId();
    }

    /** 更新任务文件关联。 */
    @Override
    public int updateAppUnstructTaskDocRel(AppUnstructTaskDocRelSaveReqVO updateReqVO) {
        AppUnstructTaskDocRelDO updateObj = BeanUtils.toBean(updateReqVO, AppUnstructTaskDocRelDO.class);
        return appUnstructTaskDocRelMapper.updateById(updateObj);
    }

    /** 删除任务文件关联。 */
    @Override
    public int removeAppUnstructTaskDocRel(Collection<Long> idList) {
        return appUnstructTaskDocRelMapper.deleteByIds(idList);
    }

    /** 获得任务文件关联详情。 */
    @Override
    public AppUnstructTaskDocRelDO getAppUnstructTaskDocRelById(Long id) {
        return appUnstructTaskDocRelMapper.selectById(id);
    }

    /** 获得全部任务文件关联列表。 */
    @Override
    public List<AppUnstructTaskDocRelDO> getAppUnstructTaskDocRelList() {
        return appUnstructTaskDocRelMapper.selectList();
    }

    /** 获得全部任务文件关联Map。 */
    @Override
    public Map<Long, AppUnstructTaskDocRelDO> getAppUnstructTaskDocRelMap() {
        return appUnstructTaskDocRelMapper.selectList().stream().collect(Collectors.toMap(
                AppUnstructTaskDocRelDO::getId,
                relationDO -> relationDO,
                (existing, replacement) -> existing));
    }

    /** 导入任务文件关联数据。 */
    @Override
    public String importAppUnstructTaskDocRel(List<AppUnstructTaskDocRelRespVO> importExcelList,
                                              boolean isUpdateSupport, String operName) {
        if (importExcelList == null || importExcelList.isEmpty()) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        for (AppUnstructTaskDocRelRespVO respVO : importExcelList) {
            AppUnstructTaskDocRelDO relationDO = BeanUtils.toBean(respVO, AppUnstructTaskDocRelDO.class);
            AppUnstructTaskDocRelDO existing = respVO.getId() == null
                    ? null : appUnstructTaskDocRelMapper.selectById(respVO.getId());
            if (existing != null) {
                if (!isUpdateSupport) {
                    throw new ServiceException("数据已存在，ID为 " + respVO.getId());
                }
                relationDO.setUpdateBy(operName);
                appUnstructTaskDocRelMapper.updateById(relationDO);
            } else {
                relationDO.setCreateBy(operName);
                appUnstructTaskDocRelMapper.insert(relationDO);
            }
            successNum++;
        }
        return "恭喜您，数据已全部导入成功！共 " + successNum + " 条。";
    }

    /** 根据任务编号获得文件关联列表。 */
    @Override
    public List<AppUnstructTaskDocRelDO> findByTaskId(Long taskId) {
        return appUnstructTaskDocRelMapper.selectList(new LambdaQueryWrapper<AppUnstructTaskDocRelDO>()
                .eq(AppUnstructTaskDocRelDO::getTaskId, taskId));
    }

    /** 根据任务编号删除文件关联。 */
    @Override
    public int removeByTaskId(Long taskId) {
        return appUnstructTaskDocRelMapper.delete(new LambdaQueryWrapper<AppUnstructTaskDocRelDO>()
                .eq(AppUnstructTaskDocRelDO::getTaskId, taskId));
    }

    /** 编辑任务时物理删除旧关联，避免逻辑删除记录占用唯一键。 */
    @Override
    public int deletePhysicallyByTaskId(Long taskId) {
        return appUnstructTaskDocRelMapper.deleteByTaskId(taskId);
    }
}
