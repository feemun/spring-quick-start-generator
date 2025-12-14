package cloud.catfish.admin.service;

import cloud.catfish.admin.domain.vo.VideoFrameDetailVO;
import cloud.catfish.admin.domain.vo.VideoStatisticsVO;

import java.util.List;

public interface VideosService {
    /**
     * 获取指定视频流的统计信息
     * @param videoId 视频ID
     * @return 统计信息
     */
    VideoStatisticsVO getStatistics(Long videoId);

    /**
     * 获取指定视频流的最近N张解析图片及详情
     * @param videoId 视频ID
     * @param limit 限制数量
     * @return 视频帧详情列表
     */
    List<VideoFrameDetailVO> getRecentFrames(Long videoId, int limit);
}
