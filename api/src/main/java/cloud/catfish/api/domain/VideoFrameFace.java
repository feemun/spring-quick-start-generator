package cloud.catfish.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class VideoFrameFace implements Serializable {
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "关联视频帧ID (video_frame.id)")
    private Long videoFrameId;

    @Schema(title = "人脸唯一标识(用于跨帧追踪)")
    private String faceToken;

    @Schema(title = "识别出的人员姓名(如果是已知人员)")
    private String personName;

    @Schema(title = "识别置信度 (0.00000-1.00000)")
    private BigDecimal confidence;

    @Schema(title = "预测年龄")
    private Integer age;

    @Schema(title = "预测性别: 0-未知, 1-男, 2-女")
    private Boolean gender;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(title = "人脸坐标框 (JSON格式: [x, y, w, h] 或 [[x1,y1],[x2,y2]...])")
    private String boxPoints;

    @Schema(title = "人脸特征向量(JSON数组或Base64，用于比对)")
    private String features;

    @Serial
    private static final long serialVersionUID = 1L;
}