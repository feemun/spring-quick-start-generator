package cloud.catfish.mbg.controller;

import cloud.catfish.api.domain.VideoFrame;
import cloud.catfish.api.req.VideoFrameReq;
import cloud.catfish.api.vo.VideoFrameVO;
import cloud.catfish.mbg.service.VideoFrameService;
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

@Tag(name = "cloud.catfish.api.domain.VideoFrame API", description = "VideoFrame相关的增删改查操作")
@RestController
@RequestMapping("/cloud.catfish.api.domain.VideoFrame")
@Slf4j
public class VideoFrameController {

    @Resource
    private VideoFrameService videoFrameService;

    @Operation(summary = "分页条件查询", description = "根据条件分页查询VideoFrame列表")
    @GetMapping("/search")
    public ResponseEntity<PageResult<VideoFrameVO>> search(@Valid @ModelAttribute VideoFrameReq req) {
        return ResponseEntity.ok(videoFrameService.page(req));
    }

    @Operation(summary = "根据主键查询", description = "根据ID查询单个VideoFrame")
    @GetMapping("/{id}")
    public ResponseEntity<VideoFrameVO> selectByPrimaryKey(@PathVariable Long id) {
        VideoFrameVO result = videoFrameService.selectByPrimaryKey(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "根据主键更新", description = "更新VideoFrame信息")
    @PutMapping("")
    public ResponseEntity<Boolean> updateByPrimaryKeySelective(@Valid @RequestBody VideoFrame record) {
        Boolean success = videoFrameService.updateByPrimaryKeySelective(record);
        log.info(success ? "Successfully updated cloud.catfish.api.domain.VideoFrame" : "Failed to update cloud.catfish.api.domain.VideoFrame");
        return ResponseEntity.ok(success);
    }

    @Operation(summary = "根据条件批量更新", description = "根据指定条件批量更新VideoFrame信息")
    @PutMapping("/batch")
    public ResponseEntity<Integer> batchUpdate(
            @Valid @RequestBody VideoFrame updateData,
            @Valid @ModelAttribute VideoFrameReq condition) {
        if (condition == null) {
            throw new IllegalArgumentException("批量更新必须指定明确的条件");
        }
        Integer rowsAffected = videoFrameService.updateByExampleSelective(updateData, condition);
        log.info("Updated {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "根据主键删除", description = "删除单个VideoFrame")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteByPrimaryKey(@PathVariable Long id) {
        Boolean success = videoFrameService.deleteByPrimaryKey(id);
        log.info(success ? "Successfully deleted cloud.catfish.api.domain.VideoFrame with primary key: id"
                        : "Failed to delete cloud.catfish.api.domain.VideoFrame with primary key: id");
        return ResponseEntity.ok(success);
    }

    @Operation(summary = "根据条件批量删除", description = "根据条件批量删除VideoFrame")
    @DeleteMapping("/batch")
    public ResponseEntity<Integer> batchDelete(@Valid @ModelAttribute VideoFrameReq condition) {
        Integer rowsAffected = videoFrameService.deleteByExample(condition);
        log.info("Deleted {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "创建新记录", description = "创建新的VideoFrame")
    @PostMapping
    public ResponseEntity<VideoFrame> create(@Valid @RequestBody VideoFrame record) {
        // 使用Twitter Snowflake算法生成ID
        Long snowflakeId = SnowflakeUtil.nextId();
        record.setId(snowflakeId);
        
        videoFrameService.insertSelective(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @Operation(summary = "Excel批量导入", description = "通过Excel文件批量导入VideoFrame数据")
    @PostMapping("/import")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file)  throws Exception {
        // 委托给服务层处理所有业务逻辑
        String result = videoFrameService.importExcel(file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "导出Excel", description = "导出VideoFrame数据到Excel文件")
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response, @RequestParam(required = false) VideoFrame condition) throws Exception {
        // 设置响应头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = new String("VideoFrame数据.xlsx".getBytes("UTF-8"), "ISO-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        
        // 这里应该添加数据查询和Excel导出的逻辑
        // 如果condition不为空，则按条件查询
        // 示例：List<cloud.catfish.api.domain.VideoFrame> records = condition != null ?
        //     videoFrameService.selectByExample(condition) : videoFrameService.selectAll();
        // excelService.exportExcel(response, records, "VideoFrame数据");
        
        // 导出逻辑实现...
    }

}
