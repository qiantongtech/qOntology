/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
 *
 * qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 * You may use qOntology for commercial purposes, but you may not remove, hide,
 * modify, or replace the qOntology logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qOntology as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qknow.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.app.service.appStructTask;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.RequestBody;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskSaveReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appStructTask.vo.AppStructTaskRespVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appStructTask.AppStructTask;
import tech.qiantong.qontology.module.app.dal.dataobject.appStructTask.AppStructTaskDO;

/**
 * 结构化抽取任务Service接口
 *
 * @author qknow
 * @date 2025-02-25
 */
public interface IAppStructTaskService extends IService<AppStructTaskDO> {
    /**
     * 执行抽取
     *
     * @param appStructTask
     * @return
     */
    public AjaxResult executeExtraction(AppStructTaskPageReqVO appStructTask);

    public AjaxResult selectByTaskId(Long taskId, Integer extractType);

    public AjaxResult getAttributeInformation(List<String> list);

    /**
     * 发布
     *
     * @param structTaskDO
     * @return
     */
    public AjaxResult releaseByTaskId(AppStructTaskDO structTaskDO);

    /**
     * 取消发布
     *
     * @param structTaskDO
     * @return
     */
    public AjaxResult cancelReleaseByTaskId(AppStructTaskDO structTaskDO);

    /**
     * 新建结构化抽取任务
     *
     * @param appStructTask
     * @return
     */
    public AjaxResult addDataMapping(AppStructTask appStructTask);

    public AjaxResult updateDataMapping(AppStructTask appStructTask);

    /**
     * 获得结构化抽取任务分页列表
     *
     * @param pageReqVO 分页请求
     * @return 结构化抽取任务分页列表
     */
    PageResult<AppStructTaskDO> getAppStructTaskPage(AppStructTaskPageReqVO pageReqVO);

    /**
     * 创建结构化抽取任务
     *
     * @param createReqVO 结构化抽取任务信息
     * @return 结构化抽取任务编号
     */
    Long createAppStructTask(AppStructTaskSaveReqVO createReqVO);

    /**
     * 更新结构化抽取任务
     *
     * @param updateReqVO 结构化抽取任务信息
     */
    int updateAppStructTask(AppStructTaskSaveReqVO updateReqVO) throws SchedulerException;

    /**
     * 删除结构化抽取任务
     *
     * @param idList 结构化抽取任务编号
     */
    int removeAppStructTask(Collection<Long> idList) throws SchedulerException;

    /**
     * 获得结构化抽取任务详情
     *
     * @param id 结构化抽取任务编号
     * @return 结构化抽取任务
     */
    AppStructTaskDO getAppStructTaskById(Long id);

    AjaxResult getInfo(Long id);

    /**
     * 获得全部结构化抽取任务列表
     *
     * @return 结构化抽取任务列表
     */
    List<AppStructTaskDO> getAppStructTaskList();

    /**
     * 获得全部结构化抽取任务 Map
     *
     * @return 结构化抽取任务 Map
     */
    ConcurrentHashMap<Long, AppStructTaskDO> getAppStructTaskMap();


    /**
     * 导入结构化抽取任务数据
     *
     * @param importExcelList 结构化抽取任务数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    String importAppStructTask(List<AppStructTaskRespVO> importExcelList, boolean isUpdateSupport, String operName);



    /**
     * 定时任务立即执行一次
     *
     * @param appStructTask
     * @return
     */
    void  runStructTask(AppStructTask appStructTask) throws Exception;
}

