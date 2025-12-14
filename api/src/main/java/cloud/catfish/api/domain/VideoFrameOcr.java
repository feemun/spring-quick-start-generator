package cloud.catfish.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class VideoFrameOcr implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "关联视频帧ID (video_frame.id)")
    private Long videoFrameId;

    @Schema(title = "识别置信度 (0.00000-1.00000)")
    private BigDecimal confidence;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(title = "识别到的文本内容")
    private String textContent;

    @Schema(title = "文本框坐标点 (JSON格式: [[x1,y1],[x2,y2],[x3,y3],[x4,y4]])")
    private String boxPoints;

    @Serial
    private static final long serialVersionUID = 1L;
}