package tech.qiantong.qontology.module.basic.api.service.file;

import java.util.List;
import tech.qiantong.qontology.module.basic.api.dto.file.BasicFileRespDTO;

/**
 * 文件管理跨模块查询接口
 */
public interface IBasicFileApiService {

    /**
     * 根据文件 ID 批量查询文件元数据
     *
     * @param idList 文件 ID 集合
     * @return 文件元数据集合
     */
    List<BasicFileRespDTO> getFileListByIds(List<Long> idList);
}
