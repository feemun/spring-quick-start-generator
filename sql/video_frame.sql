-- 抽取的图片表
-- 1. frame_index (帧序号)
-- - 含义 ：表示该图片是视频中的 第几帧 。
-- - 作用 ：视频本质上是由连续播放的一系列静态图片组成的，每一张图片就是一“帧”。这个字段记录了该图片在视频序列中的顺序编号（例如：第 0 帧、第 1 帧、第 100 帧等）。
-- - 用途 ：
--   - 排序 ：确保抽取出来的图片能按照视频播放的原始顺序排列。
--   - 定位 ：结合视频的帧率（FPS），可以计算出它大概的时间点，或者用于重新合成视频。
-- 2. timestamp_ms (视频时间戳 - 毫秒)
-- - 含义 ：表示该图片出现在视频的 具体时刻 （距离视频开始经过了多少毫秒）。
-- - 作用 ：记录这张截图是在视频播放到哪个时间点截取的。例如 1500 表示这张图是在视频播放到 1.5 秒时出现的画面。
-- - 用途 ：
--   - 精确检索 ：用户可能想跳转到视频的特定时间点查看画面，或者点击图片跳转到播放器的对应进度。
--   - 时间对应 ：相比于帧序号，时间戳更符合人类对视频进度的认知（例如“第 5 分 30 秒的画面”）。
CREATE TABLE `video_frame` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `video_id` bigint(20) NOT NULL COMMENT '关联视频ID',
  `file_path` varchar(512) NOT NULL COMMENT '图片存储路径',
  `frame_index` int(11) NOT NULL COMMENT '帧序号',
  `timestamp_ms` bigint(20) NOT NULL COMMENT '视频时间戳(毫秒)',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频抽取帧信息表';

-- 插入视频1 (demo_intro.mp4) 的抽取帧测试数据
-- 假设策略是每秒抽取一张，视频帧率为 30fps
INSERT INTO `video_frame` (`video_id`, `file_path`, `frame_index`, `timestamp_ms`, `created_at`) VALUES
(1, '/data/images/1/frame_000.jpg', 0, 0, NOW()),
(1, '/data/images/1/frame_001.jpg', 30, 1000, NOW()),
(1, '/data/images/1/frame_002.jpg', 60, 2000, NOW()),
(1, '/data/images/1/frame_003.jpg', 90, 3000, NOW()),
(1, '/data/images/1/frame_004.jpg', 120, 4000, NOW()),
(1, '/data/images/1/frame_005.jpg', 150, 5000, NOW()),
(1, '/data/images/1/frame_006.jpg', 180, 6000, NOW()),
(1, '/data/images/1/frame_007.jpg', 210, 7000, NOW()),
(1, '/data/images/1/frame_008.jpg', 240, 8000, NOW()),
(1, '/data/images/1/frame_009.jpg', 270, 9000, NOW()),
(1, '/data/images/1/frame_010.jpg', 300, 10000, NOW());
