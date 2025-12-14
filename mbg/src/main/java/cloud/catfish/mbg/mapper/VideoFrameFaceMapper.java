package cloud.catfish.mbg.mapper;

import java.util.List;

import cloud.catfish.api.domain.VideoFrameFace;
import cloud.catfish.mbg.example.VideoFrameFaceExample;
import org.apache.ibatis.annotations.Param;

public interface VideoFrameFaceMapper {
    long countByExample(VideoFrameFaceExample example);

    int deleteByExample(VideoFrameFaceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(VideoFrameFace row);

    int insertSelective(VideoFrameFace row);

    List<VideoFrameFace> selectByExampleWithBLOBs(VideoFrameFaceExample example);

    List<VideoFrameFace> selectByExample(VideoFrameFaceExample example);

    VideoFrameFace selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") VideoFrameFace row, @Param("example") VideoFrameFaceExample example);

    int updateByExampleWithBLOBs(@Param("row") VideoFrameFace row, @Param("example") VideoFrameFaceExample example);

    int updateByExample(@Param("row") VideoFrameFace row, @Param("example") VideoFrameFaceExample example);

    int updateByPrimaryKeySelective(VideoFrameFace row);

    int updateByPrimaryKeyWithBLOBs(VideoFrameFace row);

    int updateByPrimaryKey(VideoFrameFace row);

    /**
     * 批量插入记录
     * @param records 要插入的记录列表
     * @return 插入的记录数
     */
    int batchInsert(List<VideoFrameFace> records);
}