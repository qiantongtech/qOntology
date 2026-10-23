package tech.qiantong.qontology.module.basic.api.file;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.api.dto.file.BasicFileRespDTO;
import tech.qiantong.qontology.module.basic.api.service.file.IBasicFileApiService;
import tech.qiantong.qontology.module.basic.service.file.IBasicFileService;
import java.util.List;

/** 文件管理跨模块 API 实现。 */
@Service
public class BasicFileApiServiceImpl implements IBasicFileApiService {
    @Resource
    private IBasicFileService basicFileService;
    /** 根据文件 ID 批量查询文件元数据。 */
    @Override
    public List<BasicFileRespDTO> getFileListByIds(List<Long> idList) {
        return BeanUtils.toBean(basicFileService.listByIds(idList), BasicFileRespDTO.class);
    }
}
