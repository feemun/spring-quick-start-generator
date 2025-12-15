package cloud.catfish.api.converter;

import cloud.catfish.api.domain.VideoFrameFace;
import cloud.catfish.api.dto.external.face.FaceRecognitionResponseDTO;
import cloud.catfish.api.vo.VideoFrameFaceVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class VideoFrameFaceConverter {

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Converts a domain model to a VO.
     * 
     * @param videoframeface the domain model to convert
     * @return the corresponding VO, or null if input is null
     */
    public abstract VideoFrameFaceVO toVo(VideoFrameFace videoframeface);

    /**
     * Converts a VO to a domain model.
     * 
     * @param videoframefacevo the VO to convert
     * @return the corresponding domain model, or null if input is null
     */
    public abstract VideoFrameFace toDomain(VideoFrameFaceVO videoframefacevo);

    /**
     * Converts a list of domain models to VOs.
     * 
     * @param videoframefaces the list of domain models to convert
     * @return the list of corresponding VOs, or null if input is null
     */
    public abstract List<VideoFrameFaceVO> toVoList(List<VideoFrameFace> videoframefaces);

    /**
     * Converts a list of VOs to domain models.
     * 
     * @param videoframefacevos the list of VOs to convert
     * @return the list of corresponding domain models, or null if input is null
     */
    public abstract List<VideoFrameFace> toDomainList(List<VideoFrameFaceVO> videoframefacevos);

    /**
     * Converts Face Recognition response DTO to a list of VideoFrameFace domain models.
     *
     * @param videoFrameId the ID of the video frame
     * @param responseDTO  the Face Recognition response DTO
     * @return the list of corresponding domain models
     */
    public List<VideoFrameFace> toDomainList(Long videoFrameId, FaceRecognitionResponseDTO responseDTO) {
        if (responseDTO == null || responseDTO.getResult() == null || responseDTO.getResult().getFaces() == null) {
            return Collections.emptyList();
        }

        List<VideoFrameFace> resultList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (FaceRecognitionResponseDTO.FaceRecognitionFaceDTO faceDTO : responseDTO.getResult().getFaces()) {
            VideoFrameFace face = new VideoFrameFace();
            face.setVideoFrameId(videoFrameId);
            face.setPersonName(faceDTO.getLabel());
            
            // Convert score to confidence
            if (faceDTO.getScore() != null) {
                face.setConfidence(BigDecimal.valueOf(faceDTO.getScore()));
            }

            // Convert bbox to boxPoints (JSON string)
            if (faceDTO.getBbox() != null) {
                try {
                    face.setBoxPoints(objectMapper.writeValueAsString(faceDTO.getBbox()));
                } catch (JsonProcessingException e) {
                    // Log error or set empty list? Setting empty list for safety.
                    face.setBoxPoints("[]");
                }
            }

            face.setCreatedAt(now);
            resultList.add(face);
        }

        return resultList;
    }
}
