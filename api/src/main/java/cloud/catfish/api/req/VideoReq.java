package cloud.catfish.api.req;

import cloud.catfish.api.BaseRequestParam;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
@Schema(description = "cloud.catfish.api.domain.Video Request Parameters")
public class VideoReq extends BaseRequestParam {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "视频流名称")
    @Size(max = 255, message = "name cannot exceed 255 characters")
    private String name;

    @Schema(description = "视频流地址 (RTSP/RTMP/HLS/HTTP-FLV等)")
    @Size(max = 255, message = "streamUrl cannot exceed 255 characters")
    private String streamUrl;

    @Schema(description = "类型: 1-实时流, 2-本地文件, 3-网络文件")
    private Boolean type;

    @Schema(description = "状态: 0-已停止, 1-正在拉流/监控中, 2-连接异常")
    private Boolean status;

    @Schema(description = "分辨率 (如 1920x1080)")
    @Size(max = 255, message = "resolution cannot exceed 255 characters")
    private String resolution;

    @Schema(description = "帧率 (FPS)")
    private Integer frameRate;

    @Schema(description = "描述信息")
    @Size(max = 255, message = "description cannot exceed 255 characters")
    private String description;

    @Schema(description = "创建时间 start range")
    private LocalDateTime createdAtStart;

    @Schema(description = "创建时间 end range")
    private LocalDateTime createdAtEnd;

    @Schema(description = "更新时间 start range")
    private LocalDateTime updatedAtStart;

    @Schema(description = "更新时间 end range")
    private LocalDateTime updatedAtEnd;

}
