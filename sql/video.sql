-- 视频流信息表 (原视频表修改)
-- 支持存储实时视频流 URL
CREATE TABLE `video` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(255) NOT NULL COMMENT '视频流名称',
  `stream_url` varchar(1024) NOT NULL COMMENT '视频流地址 (RTSP/RTMP/HLS/HTTP-FLV等)',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '类型: 1-实时流, 2-本地文件, 3-网络文件',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态: 0-已停止, 1-正在拉流/监控中, 2-连接异常',
  `resolution` varchar(20) DEFAULT NULL COMMENT '分辨率 (如 1920x1080)',
  `frame_rate` int(11) DEFAULT NULL COMMENT '帧率 (FPS)',
  `description` varchar(500) DEFAULT NULL COMMENT '描述信息',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频流信息表';

-- 插入视频流测试数据
-- 1. 交通路口监控 (RTSP流)
INSERT INTO `video` (`name`, `stream_url`, `type`, `status`, `resolution`, `frame_rate`, `description`, `created_at`, `updated_at`) VALUES
('十字路口监控-北向', 'rtsp://admin:123456@192.168.1.100:554/h264/ch1/main/av_stream', 1, 1, '1920x1080', 25, '主干道交通状况监控', NOW(), NOW());

-- 2. 工厂车间监控 (HLS流)
INSERT INTO `video` (`name`, `stream_url`, `type`, `status`, `resolution`, `frame_rate`, `description`, `created_at`, `updated_at`) VALUES
('A区装配线', 'http://hls.example.com/live/factory_a/index.m3u8', 1, 1, '1280x720', 30, '实时生产线监控', NOW(), NOW());

-- 3. 网络测试视频 (HTTP-FLV)
INSERT INTO `video` (`name`, `stream_url`, `type`, `status`, `resolution`, `frame_rate`, `description`, `created_at`, `updated_at`) VALUES
('测试视频流', 'http://flv.example.com/live/test.flv', 1, 0, '1920x1080', 60, '用于系统连通性测试', NOW(), NOW());

-- 4. 本地演示文件 (兼容旧数据格式)
INSERT INTO `video` (`name`, `stream_url`, `type`, `status`, `resolution`, `frame_rate`, `description`, `created_at`, `updated_at`) VALUES
('产品演示视频', 'file:///data/videos/demo_v2.mp4', 2, 0, '1920x1080', 30, '2025春季发布会演示', NOW(), NOW());
