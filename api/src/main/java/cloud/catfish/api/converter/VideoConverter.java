package cloud.catfish.api.converter;

import cloud.catfish.api.domain.Video;
import cloud.catfish.api.vo.VideoVO;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoConverter {

    /**
     * Converts a domain model to a VO.
     * 
     * @param video the domain model to convert
     * @return the corresponding VO, or null if input is null
     */
    VideoVO toVo(Video video);

    /**
     * Converts a VO to a domain model.
     * 
     * @param videovo the VO to convert
     * @return the corresponding domain model, or null if input is null
     */
    Video toDomain(VideoVO videovo);

    /**
     * Converts a list of domain models to VOs.
     * 
     * @param videos the list of domain models to convert
     * @return the list of corresponding VOs, or null if input is null
     */
    List<VideoVO> toVoList(List<Video> videos);

    /**
     * Converts a list of VOs to domain models.
     * 
     * @param videovos the list of VOs to convert
     * @return the list of corresponding domain models, or null if input is null
     */
    List<Video> toDomainList(List<VideoVO> videovos);
}
