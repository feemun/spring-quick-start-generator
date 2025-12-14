package cloud.catfish.api.converter;

import cloud.catfish.api.domain.VideoFrame;
import cloud.catfish.api.vo.VideoFrameVO;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoFrameConverter {

    /**
     * Converts a domain model to a VO.
     * 
     * @param videoframe the domain model to convert
     * @return the corresponding VO, or null if input is null
     */
    VideoFrameVO toVo(VideoFrame videoframe);

    /**
     * Converts a VO to a domain model.
     * 
     * @param videoframevo the VO to convert
     * @return the corresponding domain model, or null if input is null
     */
    VideoFrame toDomain(VideoFrameVO videoframevo);

    /**
     * Converts a list of domain models to VOs.
     * 
     * @param videoframes the list of domain models to convert
     * @return the list of corresponding VOs, or null if input is null
     */
    List<VideoFrameVO> toVoList(List<VideoFrame> videoframes);

    /**
     * Converts a list of VOs to domain models.
     * 
     * @param videoframevos the list of VOs to convert
     * @return the list of corresponding domain models, or null if input is null
     */
    List<VideoFrame> toDomainList(List<VideoFrameVO> videoframevos);
}
