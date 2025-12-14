package cloud.catfish.admin.service.impl;

import cloud.catfish.admin.domain.vo.VideoFrameDetailVO;
import cloud.catfish.admin.domain.vo.VideoStatisticsVO;
import cloud.catfish.admin.service.VideosService;
import cloud.catfish.api.domain.*;
import cloud.catfish.mbg.example.*;
import cloud.catfish.mbg.mapper.*;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VideosServiceImpl implements VideosService {

    @Resource
    private VideoMapper videoMapper;
    @Resource
    private VideoFrameMapper videoFrameMapper;
    @Resource
    private VideoFrameAttachmentMapper videoFrameAttachmentMapper;
    @Resource
    private VideoFrameOcrMapper videoFrameOcrMapper;
    @Resource
    private VideoFrameFaceMapper videoFrameFaceMapper;

    @Override
    public VideoStatisticsVO getStatistics(Long videoId) {
        Video video = videoMapper.selectByPrimaryKey(videoId);
        if (video == null) {
            return null;
        }

        VideoStatisticsVO vo = new VideoStatisticsVO();
        vo.setVideoId(video.getId());
        vo.setVideoName(video.getName());

        // 1. 统计帧数
        long frameCount = countFrames(video.getId());
        vo.setFrameCount((int) frameCount);

        if (frameCount > 0) {
            // 获取该视频下的所有帧ID
            List<Long> frameIds = getFrameIds(video.getId());

            // 2. 统计附件数
            long attachmentCount = countAttachments(video.getId());
            vo.setAttachmentCount((int) attachmentCount);

            // 3. 统计 OCR 识别数
            if (!frameIds.isEmpty()) {
                long ocrCount = countOcr(frameIds);
                vo.setOcrCount((int) ocrCount);

                // 4. 统计人脸识别数
                long faceCount = countFaces(frameIds);
                vo.setFaceCount((int) faceCount);
            } else {
                vo.setOcrCount(0);
                vo.setFaceCount(0);
            }
        } else {
            vo.setAttachmentCount(0);
            vo.setOcrCount(0);
            vo.setFaceCount(0);
        }

        return vo;
    }

    @Override
    public List<VideoFrameDetailVO> getRecentFrames(Long videoId, int limit) {
        // 1. 获取最近的 N 张帧
        VideoFrameExample frameExample = new VideoFrameExample();
        frameExample.createCriteria().andVideoIdEqualTo(videoId);
        frameExample.setOrderByClause("id DESC"); // 按 ID 倒序，即最新的帧
        
        List<VideoFrame> frames = videoFrameMapper.selectByExample(frameExample);
        
        // 截取前 N 个
        if (frames.size() > limit) {
            frames = frames.subList(0, limit);
        }

        if (frames.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> frameIds = frames.stream().map(VideoFrame::getId).collect(Collectors.toList());

        // 2. 批量查询 OCR
        VideoFrameOcrExample ocrExample = new VideoFrameOcrExample();
        ocrExample.createCriteria().andVideoFrameIdIn(frameIds);
        List<VideoFrameOcr> allOcrs = videoFrameOcrMapper.selectByExample(ocrExample);
        Map<Long, List<VideoFrameOcr>> ocrMap = allOcrs.stream()
                .collect(Collectors.groupingBy(VideoFrameOcr::getVideoFrameId));

        // 3. 批量查询附件
        VideoFrameAttachmentExample attachmentExample = new VideoFrameAttachmentExample();
        attachmentExample.createCriteria().andVideoFrameIdIn(frameIds);
        List<VideoFrameAttachment> allAttachments = videoFrameAttachmentMapper.selectByExample(attachmentExample);
        Map<Long, List<VideoFrameAttachment>> attachmentMap = allAttachments.stream()
                .collect(Collectors.groupingBy(VideoFrameAttachment::getVideoFrameId));

        // 4. 批量查询人脸
        VideoFrameFaceExample faceExample = new VideoFrameFaceExample();
        faceExample.createCriteria().andVideoFrameIdIn(frameIds);
        List<VideoFrameFace> allFaces = videoFrameFaceMapper.selectByExample(faceExample);
        Map<Long, List<VideoFrameFace>> faceMap = allFaces.stream()
                .collect(Collectors.groupingBy(VideoFrameFace::getVideoFrameId));

        // 5. 组装结果
        List<VideoFrameDetailVO> result = new ArrayList<>();
        for (VideoFrame frame : frames) {
            VideoFrameDetailVO vo = new VideoFrameDetailVO();
            vo.setFrameInfo(frame);
            
            List<VideoFrameOcr> ocrs = ocrMap.getOrDefault(frame.getId(), Collections.emptyList());
            vo.setOcrList(ocrs);
            vo.setOcrCount(ocrs.size());
            
            List<VideoFrameAttachment> attachments = attachmentMap.getOrDefault(frame.getId(), Collections.emptyList());
            vo.setAttachmentList(attachments);
            vo.setAttachmentCount(attachments.size());
            
            List<VideoFrameFace> faces = faceMap.getOrDefault(frame.getId(), Collections.emptyList());
            vo.setFaceList(faces);
            vo.setFaceCount(faces.size());
            
            result.add(vo);
        }

        return result;
    }

    private long countFrames(Long videoId) {
        VideoFrameExample frameExample = new VideoFrameExample();
        frameExample.createCriteria().andVideoIdEqualTo(videoId);
        return videoFrameMapper.countByExample(frameExample);
    }

    private List<Long> getFrameIds(Long videoId) {
        VideoFrameExample frameExample = new VideoFrameExample();
        frameExample.createCriteria().andVideoIdEqualTo(videoId);
        List<VideoFrame> frames = videoFrameMapper.selectByExample(frameExample);
        return frames.stream().map(VideoFrame::getId).collect(Collectors.toList());
    }

    private long countAttachments(Long videoId) {
        VideoFrameAttachmentExample attachmentExample = new VideoFrameAttachmentExample();
        attachmentExample.createCriteria().andVideoIdEqualTo(videoId);
        return videoFrameAttachmentMapper.countByExample(attachmentExample);
    }

    private long countOcr(List<Long> frameIds) {
        VideoFrameOcrExample ocrExample = new VideoFrameOcrExample();
        ocrExample.createCriteria().andVideoFrameIdIn(frameIds);
        return videoFrameOcrMapper.countByExample(ocrExample);
    }

    private long countFaces(List<Long> frameIds) {
        VideoFrameFaceExample faceExample = new VideoFrameFaceExample();
        faceExample.createCriteria().andVideoFrameIdIn(frameIds);
        return videoFrameFaceMapper.countByExample(faceExample);
    }
}
