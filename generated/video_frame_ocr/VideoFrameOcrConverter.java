package cloud.catfish.mbg.converter;

import org.mapstruct.Mapper;
import cloud.catfish.mbg.model.VideoFrameOcr;
import cloud.catfish.mbg.vo.VideoFrameOcrVO;
import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoFrameOcrConverter {

    /**
     * Converts a domain model to a VO.
     * 
     * @param videoframeocr the domain model to convert
     * @return the corresponding VO, or null if input is null
     */
    VideoFrameOcrVO toVo(VideoFrameOcr videoframeocr);

    /**
     * Converts a VO to a domain model.
     * 
     * @param videoframeocrvo the VO to convert
     * @return the corresponding domain model, or null if input is null
     */
    VideoFrameOcr toDomain(VideoFrameOcrVO videoframeocrvo);

    /**
     * Converts a list of domain models to VOs.
     * 
     * @param videoframeocrs the list of domain models to convert
     * @return the list of corresponding VOs, or null if input is null
     */
    List<VideoFrameOcrVO> toVoList(List<VideoFrameOcr> videoframeocrs);

    /**
     * Converts a list of VOs to domain models.
     * 
     * @param videoframeocrvos the list of VOs to convert
     * @return the list of corresponding domain models, or null if input is null
     */
    List<VideoFrameOcr> toDomainList(List<VideoFrameOcrVO> videoframeocrvos);
}
