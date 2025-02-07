package cloud.catfish.mbg.controller;

import cloud.catfish.api.CommonPage;
import cloud.catfish.api.CommonPageReq;
import cloud.catfish.api.CommonResult;
import cloud.catfish.mbg.model.UmsAdmin;
import cloud.catfish.mbg.service.UmsAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 后台用户表控制层
 */
@Tag(name = "UmsAdmin API", description = "UmsAdmin相关的增删改查操作")
@RestController
@RequestMapping("umsAdmin")
@Slf4j
public class UmsAdminController {

    @Autowired
    private UmsAdminService umsAdminService;

    @Operation(summary = "分页条件查询")
    @PostMapping("/page")
    public CommonResult<CommonPage<UmsAdmin>> page(@RequestBody CommonPageReq<UmsAdmin> req) {
        List<UmsAdmin> lst = umsAdminService.page(req.getCurrentPage(), req.getPageSize());
        return CommonResult.success(CommonPage.restPage(lst));
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/{id}")
    public CommonResult<UmsAdmin> getById(@PathVariable("id") Long id) {
        return CommonResult.success(umsAdminService.selectByPrimaryKey(id));
    }

    @Operation(summary = "根据主键更新")
    @PutMapping("/updateById")
    public CommonResult<Boolean> updateByPrimaryKeySelective(@RequestBody UmsAdmin record) {
        Boolean success = umsAdminService.updateByPrimaryKeySelective(record);
        return success ? CommonResult.success(success) : CommonResult.failed();
    }

    @Operation(summary = "根据条件更新")
    @PostMapping("/updateByExample")
    public CommonResult<Integer> updateByExampleSelective(@RequestBody UmsAdmin record) {
        Integer rowsAffected = umsAdminService.updateByExampleSelective(record);
        log.info("Update {} row(s) by condition.", rowsAffected);
        return CommonResult.success(rowsAffected);
    }

    @Operation(summary = "根据主键删除")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> deleteByPrimaryKey(@PathVariable("id") Long id) {
        Boolean success = umsAdminService.deleteByPrimaryKey(id);
        return success ? CommonResult.success(success) : CommonResult.failed();
    }

    @Operation(summary = "根据条件删除")
    @PostMapping("/deleteByExample")
    public CommonResult<Integer> deleteByExample(@RequestBody UmsAdmin record) {
        Integer rowsAffected = umsAdminService.deleteByExample(record);
        log.info("Delete {} row(s) by condition.", rowsAffected);
        return CommonResult.success(rowsAffected);
    }

    @Operation(summary = "新增")
    @PostMapping("/insert")
    public CommonResult<Boolean> insertSelective(@RequestBody UmsAdmin record) {
        Boolean success = umsAdminService.insertSelective(record);
        return success ? CommonResult.success(success) : CommonResult.failed();
    }


}
