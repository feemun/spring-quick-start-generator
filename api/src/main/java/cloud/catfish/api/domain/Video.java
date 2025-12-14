package cloud.catfish.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class Video implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "视频流名称")
    private String name;

    @Schema(title = "视频流地址 (RTSP/RTMP/HLS/HTTP-FLV等)")
    private String streamUrl;

    @Schema(title = "类型: 1-实时流, 2-本地文件, 3-网络文件")
    private Boolean type;

    @Schema(title = "状态: 0-已停止, 1-正在拉流/监控中, 2-连接异常")
    private Boolean status;

    @Schema(title = "分辨率 (如 1920x1080)")
    private String resolution;

    @Schema(title = "帧率 (FPS)")
    private Integer frameRate;

    @Schema(title = "描述信息")
    private String description;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(title = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;
}