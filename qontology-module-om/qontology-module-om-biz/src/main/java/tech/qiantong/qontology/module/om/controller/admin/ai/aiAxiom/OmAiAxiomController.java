package tech.qiantong.qontology.module.om.controller.admin.ai.aiAxiom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
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
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAxiom.vo.OmAiAxiomPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAxiom.vo.OmAiAxiomRespVO;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiAxiom.vo.OmAiAxiomSaveReqVO;
import tech.qiantong.qontology.module.om.enums.ai.OmAiCandidateTypeEnum;
import tech.qiantong.qontology.module.om.service.ai.aiAxiom.IOmAiAxiomService;
import tech.qiantong.qontology.module.om.service.ai.aiModeling.IOmAiModelingService;

/** AI候选模型公理接口。 */
@Tag(name = "AI候选模型公理")
@RestController
@RequestMapping("/om/aiAxiom")
public class OmAiAxiomController {

    @Resource
    private IOmAiAxiomService service;
    @Resource
    private IOmAiModelingService modelingService;

    /** 查询候选模型公理。 */
    @Operation(summary = "查询候选模型公理")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmAiAxiomRespVO>> list(OmAiAxiomPageReqVO reqVO) {
        return CommonResult.success(BeanUtils.toBean(service.getPage(reqVO), OmAiAxiomRespVO.class));
    }

    /** 获取候选模型公理详情。 */
    @GetMapping("/{id}")
    public CommonResult<OmAiAxiomRespVO> get(@PathVariable Long id) {
        return CommonResult.success(BeanUtils.toBean(service.get(id), OmAiAxiomRespVO.class));
    }

    /** 新增候选模型公理。 */
    @PostMapping
    @Log(title = "AI候选模型公理", businessType = BusinessType.INSERT)
    public CommonResult<Long> add(@Valid @RequestBody OmAiAxiomSaveReqVO reqVO) {
        return CommonResult.success(service.create(reqVO));
    }

    /** 修改候选模型公理。 */
    @PutMapping
    @Log(title = "AI候选模型公理", businessType = BusinessType.UPDATE)
    public CommonResult<Integer> edit(@Valid @RequestBody OmAiAxiomSaveReqVO reqVO) {
        return CommonResult.toAjax(service.update(reqVO));
    }

    /** 删除候选模型公理。 */
    @DeleteMapping("/{ids}")
    @Log(title = "AI候选模型公理", businessType = BusinessType.DELETE)
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(service.remove(Arrays.asList(ids)));
    }

    /** 查询候选模型公理的证据。 */
    @GetMapping("/{id}/evidence")
    public CommonResult<List<Map<String, Object>>> evidence(@PathVariable Long id) {
        return CommonResult.success(modelingService.evidence(OmAiCandidateTypeEnum.AXIOM.getCode(), id));
    }

    /** 采纳候选模型公理。 */
    @PostMapping("/{id}/adopt")
    public CommonResult<Integer> adopt(@PathVariable Long id) {
        return CommonResult.success(modelingService.adopt(OmAiCandidateTypeEnum.AXIOM.getCode(), id));
    }
}
