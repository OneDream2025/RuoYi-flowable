-- 为通知公告表添加定时发布字段
ALTER TABLE sys_notice ADD COLUMN publish_time datetime DEFAULT NULL COMMENT '发布时间';
ALTER TABLE sys_notice ADD COLUMN end_time datetime DEFAULT NULL COMMENT '结束时间';

-- 为定时发布字段添加索引，便于定时任务查询
CREATE INDEX idx_publish_time ON sys_notice(publish_time);
CREATE INDEX idx_end_time ON sys_notice(end_time);

-- 添加公告发布状态字典类型
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark) 
VALUES ('公告发布状态', 'sys_notice_publish_status', '0', 'admin', sysdate(), '公告发布状态列表');

-- 添加公告发布状态字典数据
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) 
VALUES 
(1, '正常', '0', 'sys_notice_publish_status', '', 'primary', 'Y', '0', 'admin', sysdate(), '正常状态'),
(2, '关闭', '1', 'sys_notice_publish_status', '', 'danger', 'N', '0', 'admin', sysdate(), '关闭状态'),
(3, '待发布', '2', 'sys_notice_publish_status', '', 'info', 'N', '0', 'admin', sysdate(), '待发布状态'),
(4, '已过期', '3', 'sys_notice_publish_status', '', 'warning', 'N', '0', 'admin', sysdate(), '已过期状态');
