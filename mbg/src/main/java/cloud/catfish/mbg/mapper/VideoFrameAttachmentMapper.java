package cloud.catfish.mbg.mapper;

import java.util.List;

import cloud.catfish.api.domain.VideoFrameAttachment;
import cloud.catfish.mbg.example.VideoFrameAttachmentExample;
import org.apache.ibatis.annotations.Param;

public interface VideoFrameAttachmentMapper {
    long countByExample(VideoFrameAttachmentExample example);

    int deleteByExample(VideoFrameAttachmentExample example);

    int deleteByPrimaryKey(Long id);

    int insert(VideoFrameAttachment row);

    int insertSelective(VideoFrameAttachment row);

    List<VideoFrameAttachment> selectByExampleWithBLOBs(VideoFrameAttachmentExample example);

    List<VideoFrameAttachment> selectByExample(VideoFrameAttachmentExample example);

    VideoFrameAttachment selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") VideoFrameAttachment row, @Param("example") VideoFrameAttachmentExample example);

    int updateByExampleWithBLOBs(@Param("row") VideoFrameAttachment row, @Param("example") VideoFrameAttachmentExample example);

    int updateByExample(@Param("row") VideoFrameAttachment row, @Param("example") VideoFrameAttachmentExample example);

    int updateByPrimaryKeySelective(VideoFrameAttachment row);

    int updateByPrimaryKeyWithBLOBs(VideoFrameAttachment row);

    int updateByPrimaryKey(VideoFrameAttachment row);

    /**
     * 批量插入记录
     * @param records 要插入的记录列表
     * @return 插入的记录数
     */
    int batchInsert(List<VideoFrameAttachment> records);
}