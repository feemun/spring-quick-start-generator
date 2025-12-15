package cloud.catfish.admin.external;

import cloud.catfish.api.dto.external.face.FaceRecognitionResponseDTO;

/**
 * External service for Face Recognition.
 */
public interface FaceRecognitionExternalService {

    /**
     * Call Face Recognition external API to recognize faces in an image.
     *
     * @param imageUrl The URL or path of the image to recognize.
     * @return The Face Recognition response DTO.
     */
    FaceRecognitionResponseDTO recognize(String imageUrl);
}
