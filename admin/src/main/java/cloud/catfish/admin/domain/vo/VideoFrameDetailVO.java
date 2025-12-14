package cloud.catfish.admin.domain.vo;

import cloud.catfish.api.domain.VideoFrame;
import cloud.catfish.api.domain.VideoFrameAttachment;
import cloud.catfish.api.domain.VideoFrameFace;
import cloud.catfish.api.domain.VideoFrameOcr;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "视频帧详情VO")
public class VideoFrameDetailVO {
    @Schema(title = "视频帧基本信息")
    private VideoFrame frameInfo;

    @Schema(title = "关联OCR数量")
    private Integer ocrCount;

    @Schema(title = "关联附件数量")
    private Integer attachmentCount;

    @Schema(title = "关联人脸数量")
    private Integer faceCount;

    @Schema(title = "关联OCR信息列表")
    private List<VideoFrameOcr> ocrList;

    @Schema(title = "关联附件信息列表")
    private List<VideoFrameAttachment> attachmentList;

    @Schema(title = "关联人脸信息列表")
    private List<VideoFrameFace> faceList;
}
