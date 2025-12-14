package cloud.catfish.mbg.mapper;

import java.util.List;

import cloud.catfish.api.domain.VideoFrame;
import cloud.catfish.mbg.example.VideoFrameExample;
import org.apache.ibatis.annotations.Param;

public interface VideoFrameMapper {
    long countByExample(VideoFrameExample example);

    int deleteByExample(VideoFrameExample example);

    int deleteByPrimaryKey(Long id);

    int insert(VideoFrame row);

    int insertSelective(VideoFrame row);

    List<VideoFrame> selectByExample(VideoFrameExample example);

    VideoFrame selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") VideoFrame row, @Param("example") VideoFrameExample example);

    int updateByExample(@Param("row") VideoFrame row, @Param("example") VideoFrameExample example);

    int updateByPrimaryKeySelective(VideoFrame row);

    int updateByPrimaryKey(VideoFrame row);

    /**
     * 批量插入记录
     * @param records 要插入的记录列表
     * @return 插入的记录数
     */
    int batchInsert(List<VideoFrame> records);
}