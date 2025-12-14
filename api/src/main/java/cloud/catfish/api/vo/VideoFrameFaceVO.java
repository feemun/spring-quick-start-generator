package cloud.catfish.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "cloud.catfish.api.domain.VideoFrameFace")
public record VideoFrameFaceVO(
    @Schema(description = "主键ID")
    Long id,
    @Schema(description = "关联视频帧ID (video_frame.id)")
    Long videoFrameId,
    @Schema(description = "人脸唯一标识(用于跨帧追踪)")
    String faceToken,
    @Schema(description = "识别出的人员姓名(如果是已知人员)")
    String personName,
    @Schema(description = "识别置信度 (0.00000-1.00000)")
    BigDecimal confidence,
    @Schema(description = "预测年龄")
    Integer age,
    @Schema(description = "预测性别: 0-未知, 1-男, 2-女")
    Boolean gender,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    LocalDateTime createdAt,
    @Schema(description = "人脸坐标框 (JSON格式: [x, y, w, h] 或 [[x1,y1],[x2,y2]...])")
    String boxPoints,
    @Schema(description = "人脸特征向量(JSON数组或Base64，用于比对)")
    String features
) {}
