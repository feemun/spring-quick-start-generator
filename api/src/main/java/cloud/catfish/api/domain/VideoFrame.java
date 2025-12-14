package cloud.catfish.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class VideoFrame implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "关联视频ID")
    private Long videoId;

    @Schema(title = "图片存储路径")
    private String filePath;

    @Schema(title = "帧序号")
    private Integer frameIndex;

    @Schema(title = "视频时间戳(毫秒)")
    private Long timestampMs;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Serial
    private static final long serialVersionUID = 1L;
}