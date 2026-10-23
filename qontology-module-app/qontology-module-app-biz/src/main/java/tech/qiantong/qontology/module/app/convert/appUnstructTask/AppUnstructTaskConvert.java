package tech.qiantong.qontology.module.app.convert.appUnstructTask;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTask.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTask.AppUnstructTaskDO;
import java.util.List;

/** 非结构化抽取任务对象转换器。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppUnstructTaskConvert {
    AppUnstructTaskConvert INSTANCE = Mappers.getMapper(AppUnstructTaskConvert.class);

    /** 将分页请求转换为数据对象。 */
    AppUnstructTaskDO convertToDO(AppUnstructTaskPageReqVO pageReqVO);

    /** 将保存请求转换为数据对象。 */
    AppUnstructTaskDO convertToDO(AppUnstructTaskSaveReqVO saveReqVO);

    /** 将数据对象转换为响应对象。 */
    AppUnstructTaskRespVO convertToRespVO(AppUnstructTaskDO taskDO);

    /** 将数据对象列表转换为响应对象列表。 */
    List<AppUnstructTaskRespVO> convertToRespVOList(List<AppUnstructTaskDO> taskDOList);
}
