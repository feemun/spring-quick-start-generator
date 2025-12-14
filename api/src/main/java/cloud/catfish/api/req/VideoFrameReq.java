package cloud.catfish.api.req;

import cloud.catfish.api.BaseRequestParam;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
@Schema(description = "cloud.catfish.api.domain.VideoFrame Request Parameters")
public class VideoFrameReq extends BaseRequestParam {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "关联视频ID")
    private Long videoId;

    @Schema(description = "图片存储路径")
    @Size(max = 255, message = "filePath cannot exceed 255 characters")
    private String filePath;

    @Schema(description = "帧序号")
    private Integer frameIndex;

    @Schema(description = "视频时间戳(毫秒)")
    private Long timestampMs;

    @Schema(description = "创建时间 start range")
    private LocalDateTime createdAtStart;

    @Schema(description = "创建时间 end range")
    private LocalDateTime createdAtEnd;

}
