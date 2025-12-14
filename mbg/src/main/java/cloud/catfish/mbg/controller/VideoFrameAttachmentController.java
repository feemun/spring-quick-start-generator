package cloud.catfish.mbg.controller;

import cloud.catfish.api.domain.VideoFrameAttachment;
import cloud.catfish.api.req.VideoFrameAttachmentReq;
import cloud.catfish.api.vo.VideoFrameAttachmentVO;
import cloud.catfish.mbg.service.VideoFrameAttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.annotation.Resource;
import cloud.catfish.common.util.SnowflakeUtil;
import cloud.catfish.api.PageResult;

@Tag(name = "cloud.catfish.api.domain.VideoFrameAttachment API", description = "VideoFrameAttachment相关的增删改查操作")
@RestController
@RequestMapping("/cloud.catfish.api.domain.VideoFrameAttachment")
@Slf4j
public class VideoFrameAttachmentController {

    @Resource
    private VideoFrameAttachmentService videoFrameAttachmentService;

    @Operation(summary = "分页条件查询", description = "根据条件分页查询VideoFrameAttachment列表")
    @GetMapping("/search")
    public ResponseEntity<PageResult<VideoFrameAttachmentVO>> search(@Valid @ModelAttribute VideoFrameAttachmentReq req) {
        return ResponseEntity.ok(videoFrameAttachmentService.page(req));
    }

    @Operation(summary = "根据主键查询", description = "根据ID查询单个VideoFrameAttachment")
    @GetMapping("/{id}")
    public ResponseEntity<VideoFrameAttachmentVO> selectByPrimaryKey(@PathVariable Long id) {
        VideoFrameAttachmentVO result = videoFrameAttachmentService.selectByPrimaryKey(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "根据主键更新", description = "更新VideoFrameAttachment信息")
    @PutMapping("")
    public ResponseEntity<Boolean> updateByPrimaryKeySelective(@Valid @RequestBody VideoFrameAttachment record) {
        Boolean success = videoFrameAttachmentService.updateByPrimaryKeySelective(record);
        log.info(success ? "Successfully updated cloud.catfish.api.domain.VideoFrameAttachment" : "Failed to update cloud.catfish.api.domain.VideoFrameAttachment");
        return ResponseEntity.ok(success);
    }

    @Operation(summary = "根据条件批量更新", description = "根据指定条件批量更新VideoFrameAttachment信息")
    @PutMapping("/batch")
    public ResponseEntity<Integer> batchUpdate(
            @Valid @RequestBody VideoFrameAttachment updateData,
            @Valid @ModelAttribute VideoFrameAttachmentReq condition) {
        if (condition == null) {
            throw new IllegalArgumentException("批量更新必须指定明确的条件");
        }
        Integer rowsAffected = videoFrameAttachmentService.updateByExampleSelective(updateData, condition);
        log.info("Updated {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "根据主键删除", description = "删除单个VideoFrameAttachment")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteByPrimaryKey(@PathVariable Long id) {
        Boolean success = videoFrameAttachmentService.deleteByPrimaryKey(id);
        log.info(success ? "Successfully deleted cloud.catfish.api.domain.VideoFrameAttachment with primary key: id"
                        : "Failed to delete cloud.catfish.api.domain.VideoFrameAttachment with primary key: id");
        return ResponseEntity.ok(success);
    }

    @Operation(summary = "根据条件批量删除", description = "根据条件批量删除VideoFrameAttachment")
    @DeleteMapping("/batch")
    public ResponseEntity<Integer> batchDelete(@Valid @ModelAttribute VideoFrameAttachmentReq condition) {
        Integer rowsAffected = videoFrameAttachmentService.deleteByExample(condition);
        log.info("Deleted {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "创建新记录", description = "创建新的VideoFrameAttachment")
    @PostMapping
    public ResponseEntity<VideoFrameAttachment> create(@Valid @RequestBody VideoFrameAttachment record) {
        // 使用Twitter Snowflake算法生成ID
        Long snowflakeId = SnowflakeUtil.nextId();
        record.setId(snowflakeId);
        
        videoFrameAttachmentService.insertSelective(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @Operation(summary = "Excel批量导入", description = "通过Excel文件批量导入VideoFrameAttachment数据")
    @PostMapping("/import")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file)  throws Exception {
        // 委托给服务层处理所有业务逻辑
        String result = videoFrameAttachmentService.importExcel(file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "导出Excel", description = "导出VideoFrameAttachment数据到Excel文件")
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response, @RequestParam(required = false) VideoFrameAttachment condition) throws Exception {
        // 设置响应头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = new String("VideoFrameAttachment数据.xlsx".getBytes("UTF-8"), "ISO-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        
        // 这里应该添加数据查询和Excel导出的逻辑
        // 如果condition不为空，则按条件查询
        // 示例：List<cloud.catfish.api.domain.VideoFrameAttachment> records = condition != null ?
        //     videoFrameAttachmentService.selectByExample(condition) : videoFrameAttachmentService.selectAll();
        // excelService.exportExcel(response, records, "VideoFrameAttachment数据");
        
        // 导出逻辑实现...
    }

}
