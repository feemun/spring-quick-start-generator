package cloud.catfish.admin.controller.video;

import cloud.catfish.admin.domain.vo.VideoFrameDetailVO;
import cloud.catfish.admin.domain.vo.VideoStatisticsVO;
import cloud.catfish.admin.service.VideosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@Tag(name = "视频流统计 API", description = "提供视频流相关的统计信息")
@RestController
@RequestMapping("/admin/videos")
@Slf4j
public class VideosController {

    @Resource
    private VideosService videosService;

    @Operation(summary = "获取指定视频流统计信息", description = "获取指定视频流的统计信息，包括帧数、附件数、OCR识别数、人脸识别数")
    @GetMapping("/{id}/statistics")
    public ResponseEntity<VideoStatisticsVO> getStatistics(@PathVariable("id") Long id) {
        VideoStatisticsVO statistics = videosService.getStatistics(id);
        if (statistics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "获取指定视频流的最近解析图片及详情", description = "根据id查询某个视频的最近的N张解析的图片，并且包含每张图片的统计信息，关联的ocr信息列表，附件信息列表，人脸识别信息列表")
    @GetMapping("/{id}/frames/recent")
    public ResponseEntity<List<VideoFrameDetailVO>> getRecentFrames(@Parameter(description = "视频ID") @PathVariable("id") Long id,
                                                                    @Parameter(description = "限制数量，默认10") @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<VideoFrameDetailVO> frames = videosService.getRecentFrames(id, limit);
        return ResponseEntity.ok(frames);
    }
}
