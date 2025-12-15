package cloud.catfish.api.converter;

import cloud.catfish.api.domain.VideoFrameOcr;
import cloud.catfish.api.dto.external.paddleocr.PaddleOcrResponseDTO;
import cloud.catfish.api.vo.VideoFrameOcrVO;
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
public abstract class VideoFrameOcrConverter {

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Converts a domain model to a VO.
     * 
     * @param videoframeocr the domain model to convert
     * @return the corresponding VO, or null if input is null
     */
    public abstract VideoFrameOcrVO toVo(VideoFrameOcr videoframeocr);

    /**
     * Converts a VO to a domain model.
     * 
     * @param videoframeocrvo the VO to convert
     * @return the corresponding domain model, or null if input is null
     */
    public abstract VideoFrameOcr toDomain(VideoFrameOcrVO videoframeocrvo);

    /**
     * Converts a list of domain models to VOs.
     * 
     * @param videoframeocrs the list of domain models to convert
     * @return the list of corresponding VOs, or null if input is null
     */
    public abstract List<VideoFrameOcrVO> toVoList(List<VideoFrameOcr> videoframeocrs);

    /**
     * Converts a list of VOs to domain models.
     * 
     * @param videoframeocrvos the list of VOs to convert
     * @return the list of corresponding domain models, or null if input is null
     */
    public abstract List<VideoFrameOcr> toDomainList(List<VideoFrameOcrVO> videoframeocrvos);

    /**
     * Converts PaddleOCR response DTO to a list of VideoFrameOcr domain models.
     *
     * @param videoFrameId the ID of the video frame
     * @param responseDTO  the PaddleOCR response DTO
     * @return the list of corresponding domain models
     */
    public List<VideoFrameOcr> toDomainList(Long videoFrameId, PaddleOcrResponseDTO responseDTO) {
        if (responseDTO == null || responseDTO.getResult() == null || responseDTO.getResult().getOcrResults() == null) {
            return Collections.emptyList();
        }

        List<VideoFrameOcr> resultList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (PaddleOcrResponseDTO.PaddleOcrResultDTO resultDTO : responseDTO.getResult().getOcrResults()) {
            PaddleOcrResponseDTO.PaddleOcrPrunedResultDTO pruned = resultDTO.getPrunedResult();
            if (pruned == null) {
                continue;
            }

            List<String> texts = pruned.getRecText();
            List<Double> scores = pruned.getRecScore();
            List<List<List<Integer>>> polys = pruned.getDtPolys();

            if (texts == null || scores == null || polys == null) {
                continue;
            }

            int size = Math.min(texts.size(), Math.min(scores.size(), polys.size()));

            for (int i = 0; i < size; i++) {
                VideoFrameOcr ocr = new VideoFrameOcr();
                ocr.setVideoFrameId(videoFrameId);
                ocr.setTextContent(texts.get(i));
                
                // Convert confidence score
                Double score = scores.get(i);
                if (score != null) {
                    ocr.setConfidence(BigDecimal.valueOf(score));
                }

                // Convert box points to JSON string
                List<List<Integer>> points = polys.get(i);
                try {
                    ocr.setBoxPoints(objectMapper.writeValueAsString(points));
                } catch (JsonProcessingException e) {
                    // Log error or handle silently? Assuming silent for now as this is a converter
                    ocr.setBoxPoints("[]");
                }

                ocr.setCreatedAt(now);
                resultList.add(ocr);
            }
        }

        return resultList;
    }
}
