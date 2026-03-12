CREATE TABLE `sys_post_history` (
  `history_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '历史记录ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `operation_type` char(1) NOT NULL COMMENT '操作类型（1新增 2修改 3删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `change_reason` varchar(200) DEFAULT NULL COMMENT '变更原因',
  PRIMARY KEY (`history_id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='岗位信息历史表';
