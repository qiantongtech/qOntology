package tech.qiantong.qontology.module.app.convert.appTaskLog;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog.*;
import java.util.List;

/** 抽取任务执行日志对象转换器。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppTaskLogConvert {
    AppTaskLogConvert INSTANCE = Mappers.getMapper(AppTaskLogConvert.class);

    /** 将分页请求转换为数据对象。 */
    AppTaskLogDO convertToDO(AppTaskLogPageReqVO pageReqVO);

    /** 将日志保存请求转换为数据对象。 */
    AppTaskLogDO convertToDO(AppTaskLogSaveReqVO saveReqVO);

    /** 将日志数据对象转换为响应对象。 */
    AppTaskLogRespVO convertToRespVO(AppTaskLogDO taskLogDO);

    /** 将日志列表转换为响应对象列表。 */
    List<AppTaskLogRespVO> convertToRespVOList(List<AppTaskLogDO> taskLogDOList);
}
