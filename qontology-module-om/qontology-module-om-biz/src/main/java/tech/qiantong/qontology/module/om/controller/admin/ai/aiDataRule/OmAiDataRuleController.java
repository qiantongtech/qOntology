package tech.qiantong.qontology.module.om.controller.admin.ai.aiDataRule;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiDataRule.vo.OmAiDataRulePageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiDataRule.vo.OmAiDataRuleRespVO;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiDataRule.vo.OmAiDataRuleSaveReqVO;
import tech.qiantong.qontology.module.om.enums.ai.OmAiCandidateTypeEnum;
import tech.qiantong.qontology.module.om.service.ai.aiDataRule.IOmAiDataRuleService;
import tech.qiantong.qontology.module.om.service.ai.aiModeling.IOmAiModelingService;

/** AI 候选数据规则接口。 */
@Tag(name = "AI候选数据规则")
@RestController
@RequestMapping("/om/aiDataRule")
public class OmAiDataRuleController {
    @Resource
    private IOmAiDataRuleService service;
    @Resource
    private IOmAiModelingService modelingService;

    @GetMapping("/list")
    public CommonResult<PageResult<OmAiDataRuleRespVO>> list(OmAiDataRulePageReqVO request) {
        return CommonResult.success(BeanUtils.toBean(service.getPage(request), OmAiDataRuleRespVO.class));
    }

    @GetMapping("/{id}")
    public CommonResult<OmAiDataRuleRespVO> get(@PathVariable Long id) {
        return CommonResult.success(BeanUtils.toBean(service.get(id), OmAiDataRuleRespVO.class));
    }

    @PostMapping
    @Log(title = "AI候选数据规则", businessType = BusinessType.INSERT)
    public CommonResult<Long> add(@Valid @RequestBody OmAiDataRuleSaveReqVO request) {
        return CommonResult.success(service.create(request));
    }

    @PutMapping
    @Log(title = "AI候选数据规则", businessType = BusinessType.UPDATE)
    public CommonResult<Integer> edit(@Valid @RequestBody OmAiDataRuleSaveReqVO request) {
        return CommonResult.toAjax(service.update(request));
    }

    @DeleteMapping("/{ids}")
    @Log(title = "AI候选数据规则", businessType = BusinessType.DELETE)
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(service.remove(Arrays.asList(ids)));
    }

    @GetMapping("/{id}/evidence")
    public CommonResult<List<Map<String, Object>>> evidence(@PathVariable Long id) {
        return CommonResult.success(modelingService.evidence(OmAiCandidateTypeEnum.DATA_RULE.getCode(), id));
    }

    @PostMapping("/{id}/adopt")
    public CommonResult<Integer> adopt(@PathVariable Long id) {
        return CommonResult.success(modelingService.adopt(OmAiCandidateTypeEnum.DATA_RULE.getCode(), id));
    }
}
