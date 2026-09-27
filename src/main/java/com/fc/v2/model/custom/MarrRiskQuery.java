package com.fc.v2.model.custom;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 辅导风险判定入参：判据代号 + 分值 + 评估之日。
 * 补做的活（去年腊月做的评估、今年开春才来讨结论）at 一律填评估当时之日，
 * 不填今天——按当时站着的那一条判据算，不按今天桌上的这条。
 *
 * @author fuce
 * @date 2026-09-27
 */
public class MarrRiskQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 辅导风险判据代号（离婚调适、家庭暴力倾向等各管各的，凭代号认领） */
    private String ruleCode;

    /** 辅导员给的分值；负数、四位九这类不成体统的不进判定，退回重填 */
    private BigDecimal input;

    /** 评估之日（判档按这一天的判据窗口取用） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date at;

    public MarrRiskQuery() {
    }

    public MarrRiskQuery(String ruleCode, BigDecimal input, Date at) {
        this.ruleCode = ruleCode;
        this.input = input;
        this.at = at;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public BigDecimal getInput() {
        return input;
    }

    public void setInput(BigDecimal input) {
        this.input = input;
    }

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }
}
