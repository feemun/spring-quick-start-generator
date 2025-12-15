package cloud.catfish.admin.external.impl;

import cloud.catfish.admin.external.FaceRecognitionExternalService;
import cloud.catfish.api.dto.external.face.FaceRecognitionResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FaceRecognitionExternalServiceImpl implements FaceRecognitionExternalService {

    @Autowired
    private Environment environment;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public FaceRecognitionResponseDTO recognize(String imageUrl) {
        log.info("Calling Face Recognition for image: {}", imageUrl);
        try {
            Map<String, String> request = new HashMap<>();
            request.put("image", imageUrl);

            String serverPort = environment.getProperty("server.port", "8080");
            String targetUrl = "http://localhost:" + serverPort + "/external/face/prediction";
            log.info("Targeting Mock Face Recognition URL: {}", targetUrl);

            return restTemplate.postForObject(targetUrl, request, FaceRecognitionResponseDTO.class);
        } catch (Exception e) {
            log.error("Error calling Face Recognition service", e);
            throw new RuntimeException("Failed to call Face Recognition service", e);
        }
    }
}
