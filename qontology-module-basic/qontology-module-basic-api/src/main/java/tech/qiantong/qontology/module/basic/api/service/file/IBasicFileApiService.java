package tech.qiantong.qontology.module.basic.api.service.file;

import tech.qiantong.qontology.module.basic.api.dto.file.BasicFileRespDTO;
import java.util.List;

/** 文件管理跨模块 API。 */
public interface IBasicFileApiService {
    /** 根据文件 ID 批量查询文件元数据。 */
    List<BasicFileRespDTO> getFileListByIds(List<Long> idList);
}
