-- marr 婚姻登记与婚姻家庭辅导服务管理 -- schema (chen-013)
-- 列名与基线实体契约（@TableName/@TableField）逐列对齐，改列必须同步实体。
-- 库：chen_013

CREATE TABLE IF NOT EXISTS t_marr_cert_bill (
  id bigint NOT NULL COMMENT '主键',
  bill_no varchar(64) DEFAULT NULL COMMENT '婚姻登记记录证明核准单号',
  node_no int DEFAULT NULL COMMENT '当前所在层 0..3',
  sign_mode int DEFAULT NULL COMMENT '同层核准方式 0任一人 1两名点齐',
  need_count int DEFAULT NULL COMMENT '本层应签人数',
  sign_count int DEFAULT NULL COMMENT '本层已签人数',
  status int DEFAULT NULL COMMENT '报批情形 0在核 1已核讫 2已打回',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='婚姻登记记录证明逐级核准单';

CREATE TABLE IF NOT EXISTS t_marr_due_task (
  id bigint NOT NULL COMMENT '主键',
  item_no varchar(64) DEFAULT NULL COMMENT '到期催办事项单号',
  due_at datetime DEFAULT NULL COMMENT '本项届满之日那天的最后一刻',
  amount decimal(12,2) DEFAULT NULL COMMENT '单条提前催办的自然日数',
  content varchar(255) DEFAULT NULL COMMENT '催办事由与收信人记要',
  status int DEFAULT NULL COMMENT '条目情形 0待发 1已发 2发不出去',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='冷静期与领证期到期催办条目';

CREATE TABLE IF NOT EXISTS t_marr_fix_card (
  id bigint NOT NULL COMMENT '主键',
  biz_no varchar(64) DEFAULT NULL COMMENT '婚姻登记差错更正事务卡号',
  stage int DEFAULT NULL COMMENT '当前关口 0..5',
  status int DEFAULT NULL COMMENT '卡的落定 0在办 1已转出 2已结案',
  content varchar(255) DEFAULT NULL COMMENT '纠错记事',
  last_action varchar(64) DEFAULT NULL COMMENT '最近一次过口动作',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='婚姻登记差错更正事务卡';

CREATE TABLE IF NOT EXISTS t_marr_img_card (
  id bigint NOT NULL COMMENT '主键',
  bill_no varchar(64) DEFAULT NULL COMMENT '影像件建档卡号',
  node_no int DEFAULT NULL COMMENT '本件影像版本号',
  site_id int DEFAULT NULL COMMENT '所属卷宗基础档案',
  site_no varchar(64) DEFAULT NULL COMMENT '婚姻卷宗档案编号',
  qty decimal(12,2) DEFAULT NULL COMMENT '本卡合并后在册影像页数(页)',
  fine_amt decimal(12,2) DEFAULT NULL COMMENT '本年度新上件页数(页)',
  content varchar(255) DEFAULT NULL COMMENT '出证水印字样',
  grade_level int DEFAULT NULL COMMENT '影像完整度档',
  status int DEFAULT NULL COMMENT '进展 0待核对 1已核对 2已压死',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='卷宗影像件建档卡';

CREATE TABLE IF NOT EXISTS t_marr_report_row (
  id bigint NOT NULL COMMENT '主键',
  batch_no varchar(64) DEFAULT NULL COMMENT '登记统计报送册号',
  row_no int DEFAULT NULL COMMENT '原册内行次',
  item_code varchar(64) DEFAULT NULL COMMENT '报表项代号',
  qty decimal(12,2) DEFAULT NULL COMMENT '本行填报登记对数(对)',
  status int DEFAULT NULL COMMENT '行进展 0待核 1已汇纳 2挂驳回',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='登记统计报送册核收转存行';

CREATE TABLE IF NOT EXISTS t_marr_risk_line (
  id bigint NOT NULL COMMENT '主键',
  rule_code varchar(64) DEFAULT NULL COMMENT '辅导风险判据代号',
  rule_name varchar(128) DEFAULT NULL COMMENT '判据名称',
  th1_max decimal(12,2) DEFAULT NULL COMMENT '一档风险分值封顶值',
  th2_max decimal(12,2) DEFAULT NULL COMMENT '二档风险分值封顶值',
  th3_max decimal(12,2) DEFAULT NULL COMMENT '三档风险分值封顶值',
  eff_start datetime DEFAULT NULL COMMENT '启用之日',
  eff_end datetime DEFAULT NULL COMMENT '交棒之日(不含)',
  priority int DEFAULT NULL COMMENT '抢档顺位(数值越大越优先)',
  status int DEFAULT NULL COMMENT '判据的情形 0现行 1已停用',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='婚姻家庭辅导风险评估判据线';

CREATE TABLE IF NOT EXISTS t_marr_vol_doc (
  id bigint NOT NULL COMMENT '主键',
  site_no varchar(64) DEFAULT NULL COMMENT '婚姻卷宗档案编号',
  site_name varchar(128) DEFAULT NULL COMMENT '卷宗基础档案名称',
  site_type varchar(32) DEFAULT NULL COMMENT '卷宗类别类',
  road_name varchar(128) DEFAULT NULL COMMENT '承办处(省—市—区县)',
  th1_max decimal(12,2) DEFAULT NULL COMMENT '在册影像页数一档上限(页)',
  th2_max decimal(12,2) DEFAULT NULL COMMENT '二档上限(页)',
  th3_max decimal(12,2) DEFAULT NULL COMMENT '三档上限(页)',
  status int DEFAULT NULL COMMENT '档案情形 0在库 1已封卷',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='婚姻卷宗基础档案';

-- 初始档案数据：预置两条卷宗基础档案，一条在册、一条已封卷。
-- id=0 为在册卷宗，可继续挂用；id=1 为已封卷卷宗，仅供翻查。
-- 在册影像页数上限按小、中、大三档取 5/12/30 页。
INSERT IGNORE INTO t_marr_vol_doc (id, site_no, site_name, site_type, road_name, th1_max, th2_max, th3_max, status, del_flag, create_by, create_time)
VALUES (0, 'MH00', '江原县民政局婚姻管理处结婚卷宗基础档案', '结婚', '江原省—江原市—临川县', 5.00, 12.00, 30.00, 0, 0, 'seed', NOW()),
       (1, 'MH01', '江原县民政局婚姻管理处离婚卷宗基础档案（已封卷）', '离婚', '江原省—江原市—临川县', 5.00, 12.00, 30.00, 1, 0, 'seed', NOW());

-- 初始判据数据：四个事类各管各的。FX02 留一版已交棒的旧版（status=1 已让位），
-- 示范补做的活如何翻回去；FX04 系早年纸质评分表誊入，第三格未填=三档不设界。
-- 交接口径只有一套：旧条管到交棒之日前一刻，新条从交棒之日零点起算（eff_end 不含）；
-- 取用只认 [eff_start, eff_end) 窗口，不认 status 记号，否则旧版永远翻不回来。
INSERT IGNORE INTO t_marr_risk_line (id, rule_code, rule_name, th1_max, th2_max, th3_max, eff_start, eff_end, priority, status, del_flag, create_by, create_time)
VALUES (0, 'FX01', '离婚调适', 30.00, 60.00, 90.00, '2025-01-01 00:00:00', NULL, 10, 0, 0, 'seed', NOW()),
       (1, 'FX02', '家庭暴力倾向', 20.00, 50.00, 80.00, '2024-01-01 00:00:00', '2025-03-01 00:00:00', 10, 1, 0, 'seed', NOW()),
       (2, 'FX02', '家庭暴力倾向', 25.00, 55.00, 85.00, '2025-03-01 00:00:00', NULL, 10, 0, 0, 'seed', NOW()),
       (3, 'FX03', '未成年人受影响程度', 30.00, 70.00, 95.00, '2025-01-01 00:00:00', NULL, 10, 0, 0, 'seed', NOW()),
       (4, 'FX04', '财产纠纷激化', 40.00, 75.00, NULL, '2025-01-01 00:00:00', NULL, 10, 0, 0, 'seed', NOW());

