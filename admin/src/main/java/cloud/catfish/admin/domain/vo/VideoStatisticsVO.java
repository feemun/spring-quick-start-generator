package cloud.catfish.admin.domain.vo;

import lombok.Data;

@Data
public class VideoStatisticsVO {
    private Long videoId;
    private String videoName;
    private Integer frameCount;
    private Integer attachmentCount;
    private Integer ocrCount;
    private Integer faceCount;
}
