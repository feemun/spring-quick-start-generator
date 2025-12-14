-- 人脸识别结果表
-- 用于存储对 video_frame 表中图片进行人脸识别的结果
CREATE TABLE `video_frame_face` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `video_frame_id` bigint(20) NOT NULL COMMENT '关联视频帧ID (video_frame.id)',
  `face_token` varchar(64) DEFAULT NULL COMMENT '人脸唯一标识(用于跨帧追踪)',
  `person_name` varchar(64) DEFAULT NULL COMMENT '识别出的人员姓名(如果是已知人员)',
  `confidence` decimal(6,5) NOT NULL COMMENT '识别置信度 (0.00000-1.00000)',
  `age` int(11) DEFAULT NULL COMMENT '预测年龄',
  `gender` tinyint(4) DEFAULT '0' COMMENT '预测性别: 0-未知, 1-男, 2-女',
  `box_points` json DEFAULT NULL COMMENT '人脸坐标框 (JSON格式: [x, y, w, h] 或 [[x1,y1],[x2,y2]...])',
  `features` text DEFAULT NULL COMMENT '人脸特征向量(JSON数组或Base64，用于比对)',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_video_frame_id` (`video_frame_id`),
  KEY `idx_face_token` (`face_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频帧人脸识别结果表';

-- 插入测试数据
-- 场景1: ID=1 的图片中识别出 1 个人 (已知人员)
INSERT INTO `video_frame_face` (`video_frame_id`, `face_token`, `person_name`, `confidence`, `age`, `gender`, `box_points`, `created_at`) VALUES
(1, 'face_001_abc', 'Elon Musk', 0.99234, 50, 1, '[100, 200, 150, 150]', NOW());

-- 场景2: ID=2 的图片中识别出 2 个人 (未知人员)
INSERT INTO `video_frame_face` (`video_frame_id`, `face_token`, `person_name`, `confidence`, `age`, `gender`, `box_points`, `created_at`) VALUES
(2, 'face_002_xyz', 'Unknown', 0.95678, 25, 2, '[50, 100, 120, 120]', NOW()),
(2, 'face_003_def', 'Unknown', 0.93456, 30, 1, '[300, 100, 130, 130]', NOW());

-- 场景3: ID=3 的图片中识别出多人 (会议场景)
INSERT INTO `video_frame_face` (`video_frame_id`, `face_token`, `person_name`, `confidence`, `age`, `gender`, `box_points`, `created_at`) VALUES
(3, 'face_004_ghi', 'Alice', 0.88765, 28, 2, '[10, 50, 60, 60]', NOW()),
(3, 'face_005_jkl', 'Bob', 0.85432, 35, 1, '[80, 60, 70, 70]', NOW()),
(3, 'face_006_mno', 'Charlie', 0.91234, 40, 1, '[160, 55, 65, 65]', NOW());
