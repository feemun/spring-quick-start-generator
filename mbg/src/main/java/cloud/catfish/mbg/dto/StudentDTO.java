package cloud.catfish.mbg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class StudentDTO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "生日")
    private java.time.LocalDateTime birth;

    @Schema(description = "创建人")
    private Long createdBy;

}
