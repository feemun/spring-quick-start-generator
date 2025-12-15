package cloud.catfish.api.dto.external.face;

import lombok.Data;
import java.util.List;

@Data
public class FaceRecognitionResponseDTO {
    private FaceRecognitionResultDTO result;

    @Data
    public static class FaceRecognitionResultDTO {
        private List<FaceRecognitionFaceDTO> faces;
        private String image;
    }

    @Data
    public static class FaceRecognitionFaceDTO {
        private List<Integer> bbox;
        private String label;
        private Double score;
    }
}
