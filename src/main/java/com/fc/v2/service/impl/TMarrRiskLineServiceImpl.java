package com.fc.v2.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fc.v2.mapper.auto.TMarrRiskLineMapper;
import com.fc.v2.model.auto.TMarrRiskLine;
import com.fc.v2.model.custom.MarrRiskQuery;
import com.fc.v2.model.custom.MarrRiskVerdict;
import com.fc.v2.service.ITMarrRiskLineService;

/**
 * 婚姻家庭辅导风险评估判据线 Service业务层处理（rule-eval 形状：只取不算改，无增删改）
 *
 * 时段比较一律在 Java 内做（{@link #standsAt}）：JDBC 的 serverTimezone 与本机时区
 * 不对称，把 java.util.Date 作 SQL 参数绑回去会整体偏移，使"切换当日"这类边界随机错档
 * （实测踩到）；判据读回内存后比较就没有这个问题。判据是版本不多的参照表，
 * 全量读回再过滤代价可忽略，换来的是交接当日口径只此一处、可直接单测。
 *
 * @author fuce
 * @date 2026-09-14
 */
@Service
public class TMarrRiskLineServiceImpl implements ITMarrRiskLineService {

    /** 分值成体统区间 [0, 100]：负数、四位九这类越界的不硬算，退回辅导员手上重填 */
    static final BigDecimal SCORE_MIN = BigDecimal.ZERO;
    static final BigDecimal SCORE_MAX = new BigDecimal("100");

    /**
     * 抢档口径（同一份评估被多条判据同时够着时）：顺位大的先说话；
     * 顺位也相同，取代号在后的那一条；再相同（同代号多版同日均够着）
     * 按启用之日新的在先、主键大的在先——保证今天算和明天算是同一个结果。
     */
    static final Comparator<TMarrRiskLine> PRIORITY_THEN_CODE_DESC = Comparator
            .comparing((TMarrRiskLine r) -> r.getPriority() == null ? 0 : r.getPriority(),
                    Comparator.reverseOrder())
            .thenComparing(r -> r.getRuleCode() == null ? "" : r.getRuleCode(),
                    Comparator.reverseOrder())
            .thenComparing(TMarrRiskLine::getEffStart,
                    Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(TMarrRiskLine::getId,
                    Comparator.nullsLast(Comparator.reverseOrder()));

    @javax.annotation.Resource
    private TMarrRiskLineMapper marrRiskLineMapper;

    @Override
    public TMarrRiskLine selectTMarrRiskLineById(Long id) {
        if (id == null) {
            return null;
        }
        return this.marrRiskLineMapper.selectOne(new QueryWrapper<TMarrRiskLine>()
                .eq("id", id).eq("del_flag", 0));
    }

    @Override
    public int evaluate(String ruleCode, BigDecimal input, Date at) {
        return evaluate(new MarrRiskQuery(ruleCode, input, at)).getLevel();
    }

    @Override
    public MarrRiskVerdict evaluate(MarrRiskQuery query) {
        if (query == null || query.getRuleCode() == null || query.getRuleCode().trim().isEmpty()
                || query.getAt() == null) {
            // 代号或评估之日缺失：认不出任何判据，直说无判据可依
            return MarrRiskVerdict.noRule();
        }
        if (!scoreSane(query.getInput())) {
            // 分值本身不成体统：别硬算，送回辅导员手上重填
            return MarrRiskVerdict.invalidScore();
        }
        TMarrRiskLine rule = findRule(query.getRuleCode(), query.getAt());
        if (rule == null) {
            return MarrRiskVerdict.noRule();
        }
        // 档位连同代号、该条启用之日一块压在结论上
        return MarrRiskVerdict.ok(levelOf(rule, query.getInput()), rule.getRuleCode(),
                rule.getEffStart());
    }

    @Override
    public int evaluateTop(BigDecimal input, Date at) {
        if (at == null || !scoreSane(input)) {
            return 0;
        }
        List<TMarrRiskLine> avail = listAvailable(at);
        if (avail.isEmpty()) {
            return 0;
        }
        return levelOf(avail.get(0), input);
    }

    @Override
    public List<TMarrRiskLine> listAvailable(Date at) {
        List<TMarrRiskLine> standing = new ArrayList<TMarrRiskLine>();
        if (at == null) {
            return standing;
        }
        for (TMarrRiskLine r : this.marrRiskLineMapper
                .selectList(new QueryWrapper<TMarrRiskLine>().eq("del_flag", 0))) {
            if (standsAt(r, at)) {
                standing.add(r);
            }
        }
        standing.sort(PRIORITY_THEN_CODE_DESC);
        return standing;
    }

    @Override
    public List<TMarrRiskLine> listVersions(String ruleCode) {
        if (ruleCode == null || ruleCode.trim().isEmpty()) {
            return new ArrayList<TMarrRiskLine>();
        }
        return this.marrRiskLineMapper.selectList(new QueryWrapper<TMarrRiskLine>()
                .eq("del_flag", 0).eq("rule_code", ruleCode)
                .orderByDesc("eff_start").orderByDesc("id"));
    }

    @Override
    public boolean usable(Long id, Date at) {
        if (id == null || at == null) {
            return false;
        }
        TMarrRiskLine r = this.marrRiskLineMapper.selectById(id);
        return r != null && r.getDelFlag() != null && r.getDelFlag() == 0 && standsAt(r, at);
    }

    @Override
    public int countAvailable(Date at) {
        return listAvailable(at).size();
    }

    /**
     * 定位 at 当日站着的那一条：只认窗口够着的——去年腊月的活按去年腊月站着的那条算，
     * 翻不到就直说无判据可依，隔三差五邻近日子上的那几条不许拿来顶。
     * 多条同时够着按抢档口径定一条。情形记号（现行/已停用）仅供人读，取用只认窗口，
     * 否则补做的活永远翻不回已让位的旧版。
     */
    private TMarrRiskLine findRule(String ruleCode, Date at) {
        List<TMarrRiskLine> hit = new ArrayList<TMarrRiskLine>();
        for (TMarrRiskLine r : this.marrRiskLineMapper.selectList(new QueryWrapper<TMarrRiskLine>()
                .eq("del_flag", 0).eq("rule_code", ruleCode))) {
            if (standsAt(r, at)) {
                hit.add(r);
            }
        }
        if (hit.isEmpty()) {
            return null;
        }
        hit.sort(PRIORITY_THEN_CODE_DESC);
        return hit.get(0);
    }

    /**
     * 交接口径（全系统只此一套）：启用之日当天即站着；交棒之日不含——
     * 旧条管到交棒前一刻，新条从交棒之日零点起算；交棒之日空着=尚未交棒，现行。
     */
    static boolean standsAt(TMarrRiskLine rule, Date at) {
        if (rule == null || at == null || rule.getEffStart() == null) {
            return false;
        }
        if (rule.getEffStart().after(at)) {
            return false;
        }
        return rule.getEffEnd() == null || rule.getEffEnd().after(at);
    }

    /** 分值成体统才给算：没填的、负数、过百（四位九这类）一并退回重填 */
    static boolean scoreSane(BigDecimal input) {
        return input != null
                && input.compareTo(SCORE_MIN) >= 0
                && input.compareTo(SCORE_MAX) <= 0;
    }

    /**
     * 算档：挨着界就算高档——与一档界一般高记二档，与二档界一般高记三档；
     * 越过三档那道界的也记三档，没有四档；第三格空着=不设界，顺着二档往下走，
     * 不是没人认领的断档。
     */
    static int levelOf(TMarrRiskLine rule, BigDecimal input) {
        if (rule.getTh1Max() != null && input.compareTo(rule.getTh1Max()) < 0) {
            return 1;
        }
        if (rule.getTh2Max() != null && input.compareTo(rule.getTh2Max()) < 0) {
            return 2;
        }
        return 3;
    }
}
