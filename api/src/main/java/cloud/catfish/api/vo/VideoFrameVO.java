package cloud.catfish.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "cloud.catfish.api.domain.VideoFrame")
public record VideoFrameVO(
    @Schema(description = "主键ID")
    Long id,
    @Schema(description = "关联视频ID")
    Long videoId,
    @Schema(description = "图片存储路径")
    String filePath,
    @Schema(description = "帧序号")
    Integer frameIndex,
    @Schema(description = "视频时间戳(毫秒)")
    Long timestampMs,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    LocalDateTime createdAt
) {}
