package cloud.catfish.api.dto.external.paddleocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PaddleOcrResponseDTO {
    private String logId;
    private Integer errorCode;
    private String errorMsg;
    private PaddleOcrResultWrapperDTO result;

    @Data
    public static class PaddleOcrResultWrapperDTO {
        private List<PaddleOcrResultDTO> ocrResults;
    }

    @Data
    public static class PaddleOcrResultDTO {
        private PaddleOcrPrunedResultDTO prunedResult;
    }

    @Data
    public static class PaddleOcrPrunedResultDTO {
        @JsonProperty("model_settings")
        private PaddleOcrModelSettingsDTO modelSettings;

        @JsonProperty("doc_preprocessor_res")
        private PaddleOcrDocPreprocessorResDTO docPreprocessorRes;

        @JsonProperty("dt_polys")
        private List<List<List<Integer>>> dtPolys;

        @JsonProperty("rec_texts")
        private List<String> recText;

        @JsonProperty("rec_scores")
        private List<Double> recScore;
    }

    @Data
    public static class PaddleOcrModelSettingsDTO {
        @JsonProperty("use_doc_preprocessor")
        private Boolean useDocPreprocessor;

        @JsonProperty("use_textline_orientation")
        private Boolean useTextlineOrientation;

        @JsonProperty("use_doc_orientation_classify")
        private Boolean useDocOrientationClassify;

        @JsonProperty("use_doc_unwarping")
        private Boolean useDocUnwarping;
    }

    @Data
    public static class PaddleOcrDocPreprocessorResDTO {
        @JsonProperty("model_settings")
        private PaddleOcrModelSettingsDTO modelSettings;

        private Integer angle;
    }
}
