package cloud.catfish.api.req;

import cloud.catfish.api.BaseRequestParam;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "cloud.catfish.api.domain.VideoFrameFace Request Parameters")
public class VideoFrameFaceReq extends BaseRequestParam {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "关联视频帧ID (video_frame.id)")
    private Long videoFrameId;

    @Schema(description = "人脸唯一标识(用于跨帧追踪)")
    @Size(max = 255, message = "faceToken cannot exceed 255 characters")
    private String faceToken;

    @Schema(description = "识别出的人员姓名(如果是已知人员)")
    @Size(max = 255, message = "personName cannot exceed 255 characters")
    private String personName;

    @Schema(description = "识别置信度 (0.00000-1.00000)")
    private BigDecimal confidence;

    @Schema(description = "预测年龄")
    private Integer age;

    @Schema(description = "预测性别: 0-未知, 1-男, 2-女")
    private Boolean gender;

    @Schema(description = "创建时间 start range")
    private LocalDateTime createdAtStart;

    @Schema(description = "创建时间 end range")
    private LocalDateTime createdAtEnd;

    @Schema(description = "人脸坐标框 (JSON格式: [x, y, w, h] 或 [[x1,y1],[x2,y2]...])")
    @Size(max = 255, message = "boxPoints cannot exceed 255 characters")
    private String boxPoints;

    @Schema(description = "人脸特征向量(JSON数组或Base64，用于比对)")
    @Size(max = 255, message = "features cannot exceed 255 characters")
    private String features;

}
