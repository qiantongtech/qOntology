package tech.qiantong.qontology.module.app.convert.appUnstructTaskDocRel;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskDocRel.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskDocRel.AppUnstructTaskDocRelDO;
import java.util.List;

/** 任务文件关联对象转换器。 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppUnstructTaskDocRelConvert {
    AppUnstructTaskDocRelConvert INSTANCE = Mappers.getMapper(AppUnstructTaskDocRelConvert.class);

    /** 将分页请求转换为数据对象。 */
    AppUnstructTaskDocRelDO convertToDO(AppUnstructTaskDocRelPageReqVO pageReqVO);

    /** 将保存请求转换为数据对象。 */
    AppUnstructTaskDocRelDO convertToDO(AppUnstructTaskDocRelSaveReqVO saveReqVO);

    /** 将数据对象转换为响应对象。 */
    AppUnstructTaskDocRelRespVO convertToRespVO(AppUnstructTaskDocRelDO docRelDO);

    /** 将数据对象列表转换为响应对象列表。 */
    List<AppUnstructTaskDocRelRespVO> convertToRespVOList(List<AppUnstructTaskDocRelDO> docRelDOList);
}
