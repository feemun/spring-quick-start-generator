-- 视频附件表
-- 用于存储识别出文本的关键帧，作为可下载的附件（如讲义、笔记截图等）
CREATE TABLE `video_frame_attachment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `video_id` bigint(20) NOT NULL COMMENT '关联视频ID',
  `video_frame_id` bigint(20) NOT NULL COMMENT '关联视频帧ID',
  `file_name` varchar(255) NOT NULL COMMENT '附件名称 (通常基于视频名+时间点生成)',
  `file_path` varchar(512) NOT NULL COMMENT '附件文件路径 (即原关键帧图片的路径，或者是处理后的PDF/图片路径)',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小(字节)',
  `attachment_type` tinyint(4) DEFAULT '1' COMMENT '附件类型: 1-关键帧图片, 2-生成的PDF, 3-其他',
  `description` text DEFAULT NULL COMMENT '附件描述 (可存储OCR识别出的摘要文本)',
  `download_count` int(11) DEFAULT '0' COMMENT '下载次数',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_video_id` (`video_id`),
  KEY `idx_video_frame_id` (`video_frame_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频附件表';

-- 插入测试数据
-- 假设从 video_id=1 的视频中，提取了两个关键帧作为附件
INSERT INTO `video_frame_attachment` (`video_id`, `video_frame_id`, `file_name`, `file_path`, `file_size`, `attachment_type`, `description`, `created_at`) VALUES
(1, 1, 'demo_intro_00s_slide.jpg', '/data/images/1/frame_000.jpg', 153600, 1, 'Spring Boot 教程封面', NOW()),
(1, 2, 'demo_intro_01s_slide.jpg', '/data/images/1/frame_001.jpg', 168200, 1, '快速入门指南 - 核心概念', NOW());
