package cloud.catfish.admin.controller.video;

import cloud.catfish.admin.external.PaddleOcrExternalService;
import cloud.catfish.api.dto.external.paddleocr.PaddleOcrResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@Tag(name = "ExternalController", description = "Mock External APIs")
@RequestMapping("/external")
public class ExternalOCRController {

    private static final String OCR_RESULT_FILE_PATH = "c:/Users/feemu/Documents/GitHub/spring-quick-start-generator/json/ocrResults.json";

    @Autowired
    private PaddleOcrExternalService paddleOcrExternalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Operation(summary = "Mock PaddleOCR Prediction", description = "Returns static OCR result from file")
    @PostMapping(value = "/paddleocr/prediction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaddleOcrResponseDTO> mockPaddleOcrPrediction() {
        log.info("Mocking PaddleOCR prediction request");
        File file = new File(OCR_RESULT_FILE_PATH);
        if (!file.exists()) {
            log.error("Mock OCR file not found at: {}", OCR_RESULT_FILE_PATH);
            return ResponseEntity.notFound().build();
        }
        try {
            PaddleOcrResponseDTO response = objectMapper.readValue(file, PaddleOcrResponseDTO.class);
            if (response.getResult() != null && response.getResult().getOcrResults() != null && !response.getResult().getOcrResults().isEmpty()) {
                var firstResult = response.getResult().getOcrResults().get(0).getPrunedResult();
                log.info("Loaded {} OCR results.", response.getResult().getOcrResults().size());
                if (firstResult != null) {
                     log.info("First result recText count: {}", firstResult.getRecText() != null ? firstResult.getRecText().size() : "null");
                     log.info("First result recScore count: {}", firstResult.getRecScore() != null ? firstResult.getRecScore().size() : "null");
                }
            }
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error reading/parsing mock OCR file", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Test PaddleOCR Service", description = "Calls the internal service which calls the mock endpoint")
    @GetMapping("/test/paddleocr")
    public ResponseEntity<PaddleOcrResponseDTO> testPaddleOcr(@RequestParam(defaultValue = "http://test.image/1.jpg") String imageUrl) {
        log.info("Testing PaddleOCR service with image: {}", imageUrl);
        // Calls the service, which in turn calls the mock endpoint defined above (via localhost networking)
        PaddleOcrResponseDTO response = paddleOcrExternalService.recognize(imageUrl);
        return ResponseEntity.ok(response);
    }
}
