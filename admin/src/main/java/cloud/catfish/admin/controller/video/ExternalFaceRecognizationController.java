package cloud.catfish.admin.controller.video;

import cloud.catfish.admin.external.FaceRecognitionExternalService;
import cloud.catfish.api.dto.external.face.FaceRecognitionResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@Tag(name = "外部人脸识别接口调用与测试", description = "Mock External Face Recognition APIs")
@RequestMapping("/external")
public class ExternalFaceRecognizationController {

    private static final String FACE_RESULT_FILE_PATH = "c:/Users/feemu/Documents/GitHub/spring-quick-start-generator/json/faceRecognizationResult.json";

    @Autowired
    private FaceRecognitionExternalService faceRecognitionExternalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Operation(summary = "Mock Face Recognition Prediction", description = "Returns static Face Recognition result from file")
    @PostMapping(value = "/face/prediction", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FaceRecognitionResponseDTO> mockFacePrediction() {
        log.info("Mocking Face Recognition prediction request");
        File file = new File(FACE_RESULT_FILE_PATH);
        if (!file.exists()) {
            log.error("Mock Face file not found at: {}", FACE_RESULT_FILE_PATH);
            return ResponseEntity.notFound().build();
        }
        try {
            FaceRecognitionResponseDTO response = objectMapper.readValue(file, FaceRecognitionResponseDTO.class);
            if (response.getResult() != null && response.getResult().getFaces() != null) {
                log.info("Loaded {} faces.", response.getResult().getFaces().size());
            }
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error reading/parsing mock Face file", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Test Face Recognition Service", description = "Calls the internal service which calls the mock endpoint")
    @GetMapping("/test/face")
    public ResponseEntity<FaceRecognitionResponseDTO> testFaceRecognition(@RequestParam(defaultValue = "http://test.image/1.jpg") String imageUrl) {
        log.info("Testing Face Recognition service with image: {}", imageUrl);
        FaceRecognitionResponseDTO response = faceRecognitionExternalService.recognize(imageUrl);
        return ResponseEntity.ok(response);
    }
}
