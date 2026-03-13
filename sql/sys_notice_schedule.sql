-- ----------------------------
-- 通知公告表添加定时发布字段
-- ----------------------------

-- 添加发布时间字段
ALTER TABLE sys_notice ADD COLUMN publish_time DATETIME NULL COMMENT '发布时间' AFTER status;

-- 添加结束时间字段
ALTER TABLE sys_notice ADD COLUMN end_time DATETIME NULL COMMENT '结束时间' AFTER publish_time;

-- 添加发布状态字段（用于区分草稿、待发布、已发布、已过期）
ALTER TABLE sys_notice ADD COLUMN publish_status CHAR(1) DEFAULT '0' COMMENT '发布状态（0草稿 1待发布 2已发布 3已过期）' AFTER end_time;

-- 添加索引优化时间范围查询
ALTER TABLE sys_notice ADD INDEX idx_publish_time (publish_time);
ALTER TABLE sys_notice ADD INDEX idx_end_time (end_time);
ALTER TABLE sys_notice ADD INDEX idx_publish_status (publish_status);

-- 更新现有数据，将已启用的公告设置为已发布状态
UPDATE sys_notice SET publish_status = '2' WHERE status = '0';
UPDATE sys_notice SET publish_status = '0' WHERE status = '1';

-- 添加字典数据：发布状态
INSERT INTO sys_dict_type VALUES(20, '公告发布状态', 'sys_notice_publish_status', '0', 'admin', sysdate(), '', null, '公告发布状态列表');

INSERT INTO sys_dict_data VALUES(100, 1, '草稿', '0', 'sys_notice_publish_status', '', 'info', 'N', '0', 'admin', sysdate(), '', null, '草稿状态');
INSERT INTO sys_dict_data VALUES(101, 2, '待发布', '1', 'sys_notice_publish_status', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '待发布状态');
INSERT INTO sys_dict_data VALUES(102, 3, '已发布', '2', 'sys_notice_publish_status', '', 'success', 'Y', '0', 'admin', sysdate(), '', null, '已发布状态');
INSERT INTO sys_dict_data VALUES(103, 4, '已过期', '3', 'sys_notice_publish_status', '', 'danger', 'N', '0', 'admin', sysdate(), '', null, '已过期状态');
