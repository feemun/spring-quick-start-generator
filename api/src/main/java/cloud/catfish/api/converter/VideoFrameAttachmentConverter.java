package cloud.catfish.api.converter;

import cloud.catfish.api.domain.VideoFrameAttachment;
import cloud.catfish.api.vo.VideoFrameAttachmentVO;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoFrameAttachmentConverter {

    /**
     * Converts a domain model to a VO.
     * 
     * @param videoframeattachment the domain model to convert
     * @return the corresponding VO, or null if input is null
     */
    VideoFrameAttachmentVO toVo(VideoFrameAttachment videoframeattachment);

    /**
     * Converts a VO to a domain model.
     * 
     * @param videoframeattachmentvo the VO to convert
     * @return the corresponding domain model, or null if input is null
     */
    VideoFrameAttachment toDomain(VideoFrameAttachmentVO videoframeattachmentvo);

    /**
     * Converts a list of domain models to VOs.
     * 
     * @param videoframeattachments the list of domain models to convert
     * @return the list of corresponding VOs, or null if input is null
     */
    List<VideoFrameAttachmentVO> toVoList(List<VideoFrameAttachment> videoframeattachments);

    /**
     * Converts a list of VOs to domain models.
     * 
     * @param videoframeattachmentvos the list of VOs to convert
     * @return the list of corresponding domain models, or null if input is null
     */
    List<VideoFrameAttachment> toDomainList(List<VideoFrameAttachmentVO> videoframeattachmentvos);
}
