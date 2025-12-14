package cloud.catfish.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class VideoFrameAttachment implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "关联视频ID")
    private Long videoId;

    @Schema(title = "关联视频帧ID")
    private Long videoFrameId;

    @Schema(title = "附件名称 (通常基于视频名+时间点生成)")
    private String fileName;

    @Schema(title = "附件文件路径 (即原关键帧图片的路径，或者是处理后的PDF/图片路径)")
    private String filePath;

    @Schema(title = "文件大小(字节)")
    private Long fileSize;

    @Schema(title = "附件类型: 1-关键帧图片, 2-生成的PDF, 3-其他")
    private Boolean attachmentType;

    @Schema(title = "下载次数")
    private Integer downloadCount;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(title = "附件描述 (可存储OCR识别出的摘要文本)")
    private String description;

    @Serial
    private static final long serialVersionUID = 1L;
}