package cloud.catfish.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "cloud.catfish.api.domain.VideoFrameAttachment")
public record VideoFrameAttachmentVO(
    @Schema(description = "主键ID")
    Long id,
    @Schema(description = "关联视频ID")
    Long videoId,
    @Schema(description = "关联视频帧ID")
    Long videoFrameId,
    @Schema(description = "附件名称 (通常基于视频名+时间点生成)")
    String fileName,
    @Schema(description = "附件文件路径 (即原关键帧图片的路径，或者是处理后的PDF/图片路径)")
    String filePath,
    @Schema(description = "文件大小(字节)")
    Long fileSize,
    @Schema(description = "附件类型: 1-关键帧图片, 2-生成的PDF, 3-其他")
    Boolean attachmentType,
    @Schema(description = "下载次数")
    Integer downloadCount,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    LocalDateTime createdAt,
    @Schema(description = "附件描述 (可存储OCR识别出的摘要文本)")
    String description
) {}
