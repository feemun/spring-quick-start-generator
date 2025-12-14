package cloud.catfish.api.req;

import cloud.catfish.api.BaseRequestParam;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
@Schema(description = "cloud.catfish.api.domain.VideoFrameAttachment Request Parameters")
public class VideoFrameAttachmentReq extends BaseRequestParam {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "关联视频ID")
    private Long videoId;

    @Schema(description = "关联视频帧ID")
    private Long videoFrameId;

    @Schema(description = "附件名称 (通常基于视频名+时间点生成)")
    @Size(max = 255, message = "fileName cannot exceed 255 characters")
    private String fileName;

    @Schema(description = "附件文件路径 (即原关键帧图片的路径，或者是处理后的PDF/图片路径)")
    @Size(max = 255, message = "filePath cannot exceed 255 characters")
    private String filePath;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "附件类型: 1-关键帧图片, 2-生成的PDF, 3-其他")
    private Boolean attachmentType;

    @Schema(description = "下载次数")
    private Integer downloadCount;

    @Schema(description = "创建时间 start range")
    private LocalDateTime createdAtStart;

    @Schema(description = "创建时间 end range")
    private LocalDateTime createdAtEnd;

    @Schema(description = "附件描述 (可存储OCR识别出的摘要文本)")
    @Size(max = 255, message = "description cannot exceed 255 characters")
    private String description;

}
