package tech.qiantong.qontology.module.basic.api.dto.file;

import lombok.Data;
import java.io.Serializable;

/** 文件管理响应 DTO。 */
@Data
public class BasicFileRespDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 文件 ID。 */
    private Long id;
    /** 文件名称。 */
    private String fileName;
    /** 文件存储地址。 */
    private String filePath;
}
