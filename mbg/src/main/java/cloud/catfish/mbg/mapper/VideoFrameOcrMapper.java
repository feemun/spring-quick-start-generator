package cloud.catfish.mbg.mapper;

import java.util.List;

import cloud.catfish.api.domain.VideoFrameOcr;
import cloud.catfish.mbg.example.VideoFrameOcrExample;
import org.apache.ibatis.annotations.Param;

public interface VideoFrameOcrMapper {
    long countByExample(VideoFrameOcrExample example);

    int deleteByExample(VideoFrameOcrExample example);

    int deleteByPrimaryKey(Long id);

    int insert(VideoFrameOcr row);

    int insertSelective(VideoFrameOcr row);

    List<VideoFrameOcr> selectByExampleWithBLOBs(VideoFrameOcrExample example);

    List<VideoFrameOcr> selectByExample(VideoFrameOcrExample example);

    VideoFrameOcr selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") VideoFrameOcr row, @Param("example") VideoFrameOcrExample example);

    int updateByExampleWithBLOBs(@Param("row") VideoFrameOcr row, @Param("example") VideoFrameOcrExample example);

    int updateByExample(@Param("row") VideoFrameOcr row, @Param("example") VideoFrameOcrExample example);

    int updateByPrimaryKeySelective(VideoFrameOcr row);

    int updateByPrimaryKeyWithBLOBs(VideoFrameOcr row);

    int updateByPrimaryKey(VideoFrameOcr row);

    /**
     * 批量插入记录
     * @param records 要插入的记录列表
     * @return 插入的记录数
     */
    int batchInsert(List<VideoFrameOcr> records);
}