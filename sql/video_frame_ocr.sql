-- OCR识别结果表
-- 用于存储 PaddleOCR 对 video_frame 表中图片识别出的文本信息
CREATE TABLE `video_frame_ocr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `video_frame_id` bigint(20) NOT NULL COMMENT '关联视频帧ID (video_frame.id)',
  `text_content` text NOT NULL COMMENT '识别到的文本内容',
  `confidence` decimal(6,5) NOT NULL COMMENT '识别置信度 (0.00000-1.00000)',
  `box_points` json DEFAULT NULL COMMENT '文本框坐标点 (JSON格式: [[x1,y1],[x2,y2],[x3,y3],[x4,y4]])',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_video_frame_id` (`video_frame_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频帧OCR识别结果表';

-- 插入测试数据
-- 假设 video_frame 表中 id=1 的图片识别出 3 个文本块
INSERT INTO `video_frame_ocr` (`video_frame_id`, `text_content`, `confidence`, `box_points`, `created_at`) VALUES
(1, 'Spring Boot 教程', 0.99821, '[[100, 50], [400, 50], [400, 100], [100, 100]]', NOW()),
(1, '快速入门指南', 0.98567, '[[120, 120], [380, 120], [380, 160], [120, 160]]', NOW()),
(1, 'Author: Catfish', 0.95432, '[[50, 900], [200, 900], [200, 930], [50, 930]]', NOW());

-- 假设 video_frame 表中 id=2 的图片识别出 2 个文本块
INSERT INTO `video_frame_ocr` (`video_frame_id`, `text_content`, `confidence`, `box_points`, `created_at`) VALUES
(2, '环境配置', 0.99123, '[[50, 50], [200, 50], [200, 80], [50, 80]]', NOW()),
(2, 'Install JDK 17', 0.97890, '[[60, 100], [300, 100], [300, 130], [60, 130]]', NOW());

-- 假设 video_frame 表中 id=3 的图片识别出 1 个文本块
INSERT INTO `video_frame_ocr` (`video_frame_id`, `text_content`, `confidence`, `box_points`, `created_at`) VALUES
(3, 'Maven Dependency', 0.96789, '[[80, 200], [350, 200], [350, 240], [80, 240]]', NOW());

-- 假设 video_frame 表中 id=5 的图片识别出 4 个文本块 (复杂场景)
INSERT INTO `video_frame_ocr` (`video_frame_id`, `text_content`, `confidence`, `box_points`, `created_at`) VALUES
(5, 'ERROR: Connection Refused', 0.99999, '[[300, 400], [800, 400], [800, 450], [300, 450]]', NOW()),
(5, 'Check your network settings', 0.88765, '[[320, 460], [700, 460], [700, 490], [320, 490]]', NOW()),
(5, 'Retry', 0.92345, '[[400, 600], [500, 600], [500, 640], [400, 640]]', NOW()),
(5, 'Cancel', 0.91234, '[[600, 600], [700, 600], [700, 640], [600, 640]]', NOW());
