package com.fc.v2.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fc.v2.model.auto.TMarrRiskLine;
import com.fc.v2.model.custom.MarrRiskQuery;
import com.fc.v2.model.custom.MarrRiskVerdict;

/**
 * 婚姻家庭辅导风险评估判据线 Service接口（rule-eval 形状：规则求值，无增删改）
 *
 * 判据取用与算档都由这里的方法直接给；模块本身不配新建、修改、删除这些动作。
 * 交接口径全系统只有一套：判据管 [启用之日, 交棒之日) 这一段，交棒当日不含——
 * 旧条管到交棒前一刻，新条从交棒之日零点起算；辅导室自己核的与处里抽查的不得各讲各的。
 *
 * @author fuce
 * @date 2026-09-14
 */
public interface ITMarrRiskLineService {

    /** 按主键查询规则（翻查详情用；逻辑删除的不出） */
    TMarrRiskLine selectTMarrRiskLineById(Long id);

    /**
     * 单规则求值：按 ruleCode 在 at 当日站着的那一版判档（1..3）。
     * 挨着界就算高档：与一档界一般高记二档，与二档界一般高记三档；
     * 越过三档那道界的也记三档（没有四档）；第三格空着=不设界，顺着二档往下走。
     * 无判据可依 / 分值不成体统（负数、过百）/ 参数缺失一律返回 0。
     */
    int evaluate(String ruleCode, BigDecimal input, Date at);

    /**
     * 同名重载（扩用法）：给回压了判据代号与启用之日的完整结论——
     * 落到哪一档、认哪一条判据，全以此给回为准。
     * 原先 evaluate(String, BigDecimal, Date) 的入参与返回照旧可用，二者同一套口径。
     */
    MarrRiskVerdict evaluate(MarrRiskQuery query);

    /** 多规则求值：at 当日可用判据中抢档顺位最高者的档位（1..3）；无可用判据或分值越界返回 0 */
    int evaluateTop(BigDecimal input, Date at);

    /** at 当日站着的判据（抢档口径排序：顺位降序、代号降序、启用之日降序、主键降序） */
    List<TMarrRiskLine> listAvailable(Date at);

    /** 同一判据代号的历代版本（启用之日倒序，新的在前，一页页翻回去）；需分页时按惯例在调用处 PageHelper.startPage */
    List<TMarrRiskLine> listVersions(String ruleCode);

    /** 单条判据在 at 当日是否站着（批量导入/报表等旁路复用，口径与定位/列表一致） */
    boolean usable(Long id, Date at);

    /** at 当日站着的判据条数（列表页角标 / 看板） */
    int countAvailable(Date at);
}
