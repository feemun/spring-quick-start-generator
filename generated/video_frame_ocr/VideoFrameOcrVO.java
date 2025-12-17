package cloud.catfish.mbg.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "VideoFrameOcr")
public record VideoFrameOcrVO(
    @Schema(description = "主键ID")
    Long id,
    @Schema(description = "关联视频帧ID (video_frame.id)")
    Long videoFrameId,
    @Schema(description = "识别置信度 (0.00000-1.00000)")
    BigDecimal confidence,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    LocalDateTime createdAt,
    @Schema(description = "识别到的文本内容")
    String textContent,
    @Schema(description = "文本框坐标点 (JSON格式: [[x1,y1],[x2,y2],[x3,y3],[x4,y4]])")
    String boxPoints
) {}
