package cloud.catfish.mbg.controller;

import ${ModelName};
import cloud.catfish.mbg.service;
import ${responseModel};
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import cloud.catfish.api.CommonPage;
import cloud.catfish.api.CommonPageReq;

/**
 * @description: ${tableRemark}控制层
 */
@Tag(name = "UmsAdmin API", description = "UmsAdmin相关的增删改查操作")
@RestController
@RequestMapping("/UmsAdmin")
@Slf4j
public class ${ControllerSimpleName} {

    @Autowired
    private IUmsAdmin umsAdmin;

    @Operation(summary = "分页条件查询")
    @PostMapping("/page")
    public CommonResult<CommonPage<UmsAdmin>> page(@RequestBody CommonPageReq<UmsAdmin> req) {
        List<UmsAdmin> lst = umsAdmin.page(req.getCurrentPage(), req.getPageSize());
        return CommonResult.success(CommonPage.restPage(lst));
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/{id}")
    public CommonResult<UmsAdmin> getById(@PathVariable("id") Long id) {
        return CommonResult.success(umsAdmin.selectByPrimaryKey(id));
    }

    @Operation(summary = "根据主键更新")
    @PutMapping("/updateById")
    public CommonResult<Boolean> updateByPrimaryKeySelective(@RequestBody UmsAdmin record) {
        Boolean success = umsAdmin.updateByPrimaryKeySelective(record);
        return success ? CommonResult.success(success) : CommonResult.failed();
    }

    @Operation(summary = "根据条件更新")
    @PostMapping("/updateByExample")
    public CommonResult<Integer> updateByExampleSelective(@RequestBody UmsAdmin record) {
        Integer rowsAffected = umsAdmin.updateByExampleSelective(record);
        log.info("Update {} row(s) by condition.", rowsAffected);
        return CommonResult.success(rowsAffected);
    }

    @Operation(summary = "根据主键删除")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deleteByPrimaryKey(@PathVariable("id") Long id) {
        Boolean success = umsAdmin.deleteByPrimaryKey(id);
        return success ? CommonResult.success(success) : CommonResult.failed();
    }

    @Operation(summary = "根据条件删除")
    @PostMapping("/deleteByExample")
    public CommonResult<Integer> deleteByExample(@RequestBody UmsAdmin record) {
        Integer rowsAffected = umsAdmin.deleteByExample(record);
        log.info("Delete {} row(s) by condition.", rowsAffected);
        return CommonResult.success(rowsAffected);
    }

    @Operation(summary = "新增")
    @PostMapping("/insert")
    public CommonResult<Boolean> insertSelective(@RequestBody UmsAdmin record) {
        Boolean success = umsAdmin.insertSelective(record);
        return success ? CommonResult.success(success) : CommonResult.failed();
    }

    @Operation(summary = "Excel导入")
    @PostMapping("/import")
    public CommonResult<Boolean> importExcel(@RequestBody UmsAdmin record) {
        Boolean success = umsAdmin.insertSelective(record);
        return success ? CommonResult.success(success) : CommonResult.failed();
    }

    @Operation(summary = "导出Excel")
    @PostMapping("/export")
    public CommonResult<Boolean> exportExcel(@RequestBody UmsAdmin record) {
        Boolean success = umsAdmin.insertSelective(record);
        return success ? CommonResult.success(success) : CommonResult.failed();
    }

}
