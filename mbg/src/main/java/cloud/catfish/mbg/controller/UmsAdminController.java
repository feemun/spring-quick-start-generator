package cloud.catfish.mbg.controller;

import cloud.catfish.mbg.model.UmsAdmin;
import cloud.catfish.mbg.service.IUmsAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import cloud.catfish.api.CommonPageReq;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.annotation.Resource;

@Tag(name = "UmsAdmin API", description = "UmsAdmin相关的增删改查操作")
@RestController
@RequestMapping("/UmsAdmin")
@Slf4j
public class UmsAdminController {

    @Resource
    private IUmsAdminService umsAdmin;

    @Operation(summary = "分页条件查询", description = "根据条件分页查询UmsAdmin列表")
    @PostMapping("/search")
    public ResponseEntity<Page<UmsAdmin>> search(@Valid @RequestBody CommonPageReq<UmsAdmin> req) {
        List<UmsAdmin> lst = umsAdmin.page(req.getCurrentPage(), req.getPageSize(), req.getCondition());
        PageInfo<UmsAdmin> pageInfo = new PageInfo<>(lst);
        
        Page<UmsAdmin> page = new PageImpl<>(
            pageInfo.getList(),
            PageRequest.of(pageInfo.getPageNum() - 1, pageInfo.getPageSize()),
            pageInfo.getTotal()
        );
        
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "根据主键查询", description = "根据ID查询单个UmsAdmin")
    @GetMapping("/{id}")
    public ResponseEntity<UmsAdmin> getById(@PathVariable("id") Long id) {
        UmsAdmin result = umsAdmin.selectByPrimaryKey(id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "根据主键更新", description = "更新UmsAdmin信息")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id, @Valid @RequestBody UmsAdmin record) {
        record.setId(id); // 确保ID一致
        Boolean success = umsAdmin.updateByPrimaryKeySelective(record);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "根据条件批量更新", description = "根据条件批量更新UmsAdmin信息")
    @PutMapping("/batch")
    public ResponseEntity<Integer> batchUpdate(@Valid @RequestBody UmsAdmin record) {
        Integer rowsAffected = umsAdmin.updateByExampleSelective(record);
        log.info("Updated {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "根据主键删除", description = "删除单个UmsAdmin")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        Boolean success = umsAdmin.deleteByPrimaryKey(id);
        if (success) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "根据条件批量删除", description = "根据条件批量删除UmsAdmin")
    @DeleteMapping("/batch")
    public ResponseEntity<Integer> batchDelete(@Valid @RequestBody UmsAdmin record) {
        Integer rowsAffected = umsAdmin.deleteByExample(record);
        log.info("Deleted {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "创建新记录", description = "创建新的UmsAdmin")
    @PostMapping
    public ResponseEntity<UmsAdmin> create(@Valid @RequestBody UmsAdmin record) {
        Boolean success = umsAdmin.insertSelective(record);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Excel批量导入", description = "通过Excel文件批量导入UmsAdmin数据")
    @PostMapping("/import")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
                return ResponseEntity.badRequest().body("文件格式不正确，仅支持.xlsx和.xls格式");
            }
            
            // 这里应该添加Excel文件解析和数据导入的逻辑
            // 示例：List<UmsAdmin> records = excelService.parseExcel(file, UmsAdmin.class);
            // Boolean success = umsAdmin.batchInsert(records);
            
            return ResponseEntity.ok("导入成功");
        } catch (Exception e) {
            log.error("Import Excel failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("导入Excel失败: " + e.getMessage());
        }
    }

    @Operation(summary = "导出Excel", description = "导出UmsAdmin数据到Excel文件")
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response, @RequestParam(required = false) UmsAdmin condition) {
        try {
            // 设置响应头信息
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String filename = new String("UmsAdmin数据.xlsx".getBytes("UTF-8"), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            
            // 这里应该添加数据查询和Excel导出的逻辑
            // 如果condition不为空，则按条件查询
            // 示例：List<UmsAdmin> records = condition != null ? 
            //     umsAdmin.selectByExample(condition) : umsAdmin.selectAll();
            // excelService.exportExcel(response, records, "UmsAdmin数据");
            
            // 导出逻辑实现...
        } catch (Exception e) {
            log.error("Export Excel failed: {}", e.getMessage(), e);
            try {
                response.reset();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"导出Excel失败: " + e.getMessage() + "\"}");
            } catch (Exception ex) {
                log.error("Error sending export failure response", ex);
            }
        }
    }

}
