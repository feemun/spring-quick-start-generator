package cloud.catfish.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "cloud.catfish.api.domain.Video")
public record VideoVO(
    @Schema(description = "主键ID")
    Long id,
    @Schema(description = "视频流名称")
    String name,
    @Schema(description = "视频流地址 (RTSP/RTMP/HLS/HTTP-FLV等)")
    String streamUrl,
    @Schema(description = "类型: 1-实时流, 2-本地文件, 3-网络文件")
    Boolean type,
    @Schema(description = "状态: 0-已停止, 1-正在拉流/监控中, 2-连接异常")
    Boolean status,
    @Schema(description = "分辨率 (如 1920x1080)")
    String resolution,
    @Schema(description = "帧率 (FPS)")
    Integer frameRate,
    @Schema(description = "描述信息")
    String description,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    LocalDateTime updatedAt
) {}
