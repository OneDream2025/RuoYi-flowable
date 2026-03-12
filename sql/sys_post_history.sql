-- ----------------------------
-- 岗位变更历史表
-- ----------------------------
DROP TABLE IF EXISTS sys_post_history;
CREATE TABLE sys_post_history (
  history_id      BIGINT(20)      NOT NULL AUTO_INCREMENT    COMMENT '历史记录ID',
  post_id         BIGINT(20)      NOT NULL                   COMMENT '岗位ID',
  post_code       VARCHAR(64)     NOT NULL                   COMMENT '岗位编码',
  post_name       VARCHAR(50)     NOT NULL                   COMMENT '岗位名称',
  post_sort       INT(4)          NOT NULL                   COMMENT '显示顺序',
  status          CHAR(1)         NOT NULL                   COMMENT '状态（0正常 1停用）',
  remark          VARCHAR(500)    DEFAULT NULL               COMMENT '备注',
  version         INT(11)         DEFAULT 1                  COMMENT '版本号',
  change_type     CHAR(1)         NOT NULL                   COMMENT '变更类型（1新增 2修改 3删除）',
  change_content  TEXT            DEFAULT NULL               COMMENT '变更内容（JSON格式）',
  before_data     TEXT            DEFAULT NULL               COMMENT '变更前数据（JSON格式）',
  after_data      TEXT            DEFAULT NULL               COMMENT '变更后数据（JSON格式）',
  oper_by         VARCHAR(64)     DEFAULT ''                 COMMENT '操作人',
  oper_time       DATETIME        NOT NULL                   COMMENT '操作时间',
  oper_ip         VARCHAR(128)    DEFAULT ''                 COMMENT '操作IP',
  PRIMARY KEY (history_id),
  INDEX idx_post_id (post_id),
  INDEX idx_oper_time (oper_time)
) ENGINE=INNODB COMMENT = '岗位变更历史表';

-- ----------------------------
-- 菜单权限配置
-- ----------------------------
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) 
VALUES ('岗位变更历史', 1, 6, 'postHistory', 'system/postHistory/history', 1, 0, 'C', '0', '0', 'system:postHistory:list', 'history', 'admin', sysdate(), '岗位变更历史菜单');

SET @parentId = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) 
VALUES ('岗位变更历史查询', @parentId, 1, '', '', 1, 0, 'F', '0', '0', 'system:postHistory:query', '#', 'admin', sysdate(), '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) 
VALUES ('岗位变更回滚', @parentId, 2, '', '', 1, 0, 'F', '0', '0', 'system:postHistory:rollback', '#', 'admin', sysdate(), '');
