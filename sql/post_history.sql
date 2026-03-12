-- ----------------------------
-- 岗位变更历史记录表
-- ----------------------------
drop table if exists sys_post_history;
create table sys_post_history
(
  history_id        bigint(20)      not null auto_increment    comment '历史记录ID',
  post_id           bigint(20)      not null                   comment '岗位ID',
  post_code         varchar(64)     not null                   comment '岗位编码',
  post_name         varchar(50)     not null                   comment '岗位名称',
  post_sort         int(4)          not null                   comment '显示顺序',
  status            char(1)         not null                   comment '状态（0正常 1停用）',
  remark            varchar(500)    default null               comment '备注',
  version           int(4)          default 1                  comment '版本号',
  change_type       char(1)         not null                   comment '变更类型（1新增 2修改 3删除 4回滚）',
  change_by         varchar(64)     default ''                 comment '变更人',
  change_time       datetime                                   comment '变更时间',
  primary key (history_id),
  key idx_post_id (post_id),
  key idx_change_time (change_time)
) engine=innodb comment = '岗位变更历史记录表';

-- ----------------------------
-- 岗位变更对比字段表
-- ----------------------------
drop table if exists sys_post_change_detail;
create table sys_post_change_detail
(
  detail_id         bigint(20)      not null auto_increment    comment '详情ID',
  history_id        bigint(20)      not null                   comment '历史记录ID',
  post_id           bigint(20)      not null                   comment '岗位ID',
  field_name        varchar(50)     not null                   comment '字段名称',
  field_label       varchar(100)    default ''                 comment '字段标签',
  old_value         varchar(500)    default null               comment '旧值',
  new_value         varchar(500)    default null               comment '新值',
  primary key (detail_id),
  key idx_history_id (history_id),
  key idx_post_id (post_id)
) engine=innodb comment = '岗位变更对比字段表';

-- ----------------------------
-- 初始化菜单权限 - 岗位变更历史
-- ----------------------------
-- 岗位管理下增加历史记录菜单（parent_id=104是岗位管理，order_num=6表示排在第6个）
-- 这是一个隐藏菜单，用于权限控制，实际页面通过按钮跳转
insert into sys_menu values('2000', '变更历史', '104', '6', 'history', '', '', '', 1, 0, 'C', '0', '0', 'system:post:history', 'log', 'admin', sysdate(), '', null, '岗位变更历史菜单');

-- 岗位变更历史按钮权限
insert into sys_menu values('2001', '历史查询', '2000', '1', '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:history:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2002', '历史对比', '2000', '2', '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:history:compare', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2003', '版本回滚', '2000', '3', '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:history:rollback', '#', 'admin', sysdate(), '', null, '');

-- 给超级管理员角色添加权限
insert into sys_role_menu values ('1', '2000');
insert into sys_role_menu values ('1', '2001');
insert into sys_role_menu values ('1', '2002');
insert into sys_role_menu values ('1', '2003');

-- 给普通角色添加权限
insert into sys_role_menu values ('2', '2000');
insert into sys_role_menu values ('2', '2001');
insert into sys_role_menu values ('2', '2002');
insert into sys_role_menu values ('2', '2003');

-- ----------------------------
-- 添加变更类型字典
-- ----------------------------
insert into sys_dict_type values(100, '岗位变更类型', 'sys_post_change_type', '0', 'admin', sysdate(), '', null, '岗位变更类型列表');

insert into sys_dict_data values(100, 1, '新增', '1', 'sys_post_change_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '新增操作');
insert into sys_dict_data values(101, 2, '修改', '2', 'sys_post_change_type', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '修改操作');
insert into sys_dict_data values(102, 3, '删除', '3', 'sys_post_change_type', '', 'danger', 'N', '0', 'admin', sysdate(), '', null, '删除操作');
insert into sys_dict_data values(103, 4, '回滚', '4', 'sys_post_change_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '回滚操作');
