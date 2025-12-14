package cloud.catfish.api.converter;

import cloud.catfish.api.domain.VideoFrameFace;
import cloud.catfish.api.vo.VideoFrameFaceVO;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoFrameFaceConverter {

    /**
     * Converts a domain model to a VO.
     * 
     * @param videoframeface the domain model to convert
     * @return the corresponding VO, or null if input is null
     */
    VideoFrameFaceVO toVo(VideoFrameFace videoframeface);

    /**
     * Converts a VO to a domain model.
     * 
     * @param videoframefacevo the VO to convert
     * @return the corresponding domain model, or null if input is null
     */
    VideoFrameFace toDomain(VideoFrameFaceVO videoframefacevo);

    /**
     * Converts a list of domain models to VOs.
     * 
     * @param videoframefaces the list of domain models to convert
     * @return the list of corresponding VOs, or null if input is null
     */
    List<VideoFrameFaceVO> toVoList(List<VideoFrameFace> videoframefaces);

    /**
     * Converts a list of VOs to domain models.
     * 
     * @param videoframefacevos the list of VOs to convert
     * @return the list of corresponding domain models, or null if input is null
     */
    List<VideoFrameFace> toDomainList(List<VideoFrameFaceVO> videoframefacevos);
}
