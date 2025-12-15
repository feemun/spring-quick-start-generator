package cloud.catfish.admin.external;

import cloud.catfish.api.dto.external.paddleocr.PaddleOcrResponseDTO;

public interface PaddleOcrExternalService {
    /**
     * Call PaddleOCR external API to recognize text in an image.
     *
     * @param imageUrl The URL or path of the image to recognize.
     * @return The OCR response DTO.
     */
    PaddleOcrResponseDTO recognize(String imageUrl);
}
