package cloud.catfish.mbg.request;

import lombok.Data;
import cloud.catfish.common.param.BaseRequestParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;

@Data
@Schema(description = "VideoFrameOcr Request Parameters")
public class VideoFrameOcrReq extends BaseRequestParam {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "关联视频帧ID (video_frame.id)")
    private Long videoFrameId;

    @Schema(description = "识别置信度 (0.00000-1.00000)")
    private BigDecimal confidence;

    @Schema(description = "创建时间 start range")
    private LocalDateTime createdAtStart;

    @Schema(description = "创建时间 end range")
    private LocalDateTime createdAtEnd;

    @Schema(description = "识别到的文本内容")
    @Size(max = 255, message = "textContent cannot exceed 255 characters")
    private String textContent;

    @Schema(description = "文本框坐标点 (JSON格式: [[x1,y1],[x2,y2],[x3,y3],[x4,y4]])")
    @Size(max = 255, message = "boxPoints cannot exceed 255 characters")
    private String boxPoints;

}
