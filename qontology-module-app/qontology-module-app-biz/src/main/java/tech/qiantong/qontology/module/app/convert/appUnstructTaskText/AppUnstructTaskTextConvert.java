package tech.qiantong.qontology.module.app.convert.appUnstructTaskText;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskText.AppUnstructTaskTextDO;
import java.util.List;

/** 任务文件段落关联对象转换器。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppUnstructTaskTextConvert {
    AppUnstructTaskTextConvert INSTANCE = Mappers.getMapper(AppUnstructTaskTextConvert.class);

    /** 将分页请求转换为数据对象。 */
    AppUnstructTaskTextDO convertToDO(AppUnstructTaskTextPageReqVO pageReqVO);

    /** 将保存请求转换为数据对象。 */
    AppUnstructTaskTextDO convertToDO(AppUnstructTaskTextSaveReqVO saveReqVO);

    /** 将数据对象转换为响应对象。 */
    AppUnstructTaskTextRespVO convertToRespVO(AppUnstructTaskTextDO textDO);

    /** 将数据对象列表转换为响应对象列表。 */
    List<AppUnstructTaskTextRespVO> convertToRespVOList(List<AppUnstructTaskTextDO> textDOList);
}
