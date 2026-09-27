package com.fc.v2.model.custom;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 辅导风险判定结论：某次评估落到哪一档、认哪一条判据，全以此对象的给回为准，
 * 页面上不许先摆一个结果再去凑。
 * 落定时在结论上压三样：档位(1..3)、判据代号、该条启用之日；
 * 未落定只讲原因——分值不成体统退回辅导员重填，或该评估之日无判据可依。
 * 结论一旦给回就是一句话：同一份评估今天算和明天算是同一个结果，
 * 谁也不得事后手工再盖一遍。
 *
 * @author fuce
 * @date 2026-09-27
 */
public class MarrRiskVerdict implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 收场：OK 落定；INVALID_SCORE 分值不成体统退回重填；NO_RULE 该评估之日无判据可依 */
    public enum State {
        OK, INVALID_SCORE, NO_RULE
    }

    /** 收场 */
    private State state;

    /** 落定的档位 1..3；未落定为 0 */
    private int level;

    /** 认下的判据代号；未认下为 null */
    private String ruleCode;

    /** 认下那条判据的启用之日；未认下为 null */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effStart;

    public MarrRiskVerdict() {
    }

    public MarrRiskVerdict(State state, int level, String ruleCode, Date effStart) {
        this.state = state;
        this.level = level;
        this.ruleCode = ruleCode;
        this.effStart = effStart;
    }

    /** 落定：档位连同判据代号、启用之日一块压上 */
    public static MarrRiskVerdict ok(int level, String ruleCode, Date effStart) {
        return new MarrRiskVerdict(State.OK, level, ruleCode, effStart);
    }

    /** 分值不成体统（没填、负数、过百），退回辅导员手上重填，不硬算 */
    public static MarrRiskVerdict invalidScore() {
        return new MarrRiskVerdict(State.INVALID_SCORE, 0, null, null);
    }

    /** 该评估之日没有站着的判据——直说无判据可依，邻近日子上的不许拿来顶 */
    public static MarrRiskVerdict noRule() {
        return new MarrRiskVerdict(State.NO_RULE, 0, null, null);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public Date getEffStart() {
        return effStart;
    }

    public void setEffStart(Date effStart) {
        this.effStart = effStart;
    }
}
