package com.fc.v2.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fc.v2.model.auto.TMarrRiskLine;

/**
 * 判据线纯逻辑单测（不依赖 Spring/数据库）：
 * 算档边界、分值成体统、交接口径、抢档排序。
 */
public class TMarrRiskLineServiceImplTest {

    private static TMarrRiskLine rule(String th1, String th2, String th3) {
        TMarrRiskLine r = new TMarrRiskLine();
        r.setTh1Max(th1 == null ? null : new BigDecimal(th1));
        r.setTh2Max(th2 == null ? null : new BigDecimal(th2));
        r.setTh3Max(th3 == null ? null : new BigDecimal(th3));
        return r;
    }

    private static Date day(String yyyyMMdd) throws Exception {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(yyyyMMdd + " 00:00:00");
    }

    @Test
    public void 挨着界就算高档() {
        TMarrRiskLine r = rule("10", "20", "30");
        assertEquals(1, TMarrRiskLineServiceImpl.levelOf(r, new BigDecimal("9.99")));
        // 与一档那道界一般高 -> 记二档
        assertEquals(2, TMarrRiskLineServiceImpl.levelOf(r, new BigDecimal("10")));
        assertEquals(2, TMarrRiskLineServiceImpl.levelOf(r, new BigDecimal("19.99")));
        // 与二档那道界一般高 -> 记三档
        assertEquals(3, TMarrRiskLineServiceImpl.levelOf(r, new BigDecimal("20")));
        assertEquals(3, TMarrRiskLineServiceImpl.levelOf(r, new BigDecimal("25")));
    }

    @Test
    public void 越过三档那道界也记三档_没有四档() {
        TMarrRiskLine r = rule("10", "20", "30");
        assertEquals(3, TMarrRiskLineServiceImpl.levelOf(r, new BigDecimal("30")));
        assertEquals(3, TMarrRiskLineServiceImpl.levelOf(r, new BigDecimal("30.01")));
        assertEquals(3, TMarrRiskLineServiceImpl.levelOf(r, new BigDecimal("100")));
    }

    @Test
    public void 第三格空着等于不设界_顺着二档往下走() {
        TMarrRiskLine legacy = rule("10", "20", null);
        assertEquals(2, TMarrRiskLineServiceImpl.levelOf(legacy, new BigDecimal("15")));
        assertEquals(3, TMarrRiskLineServiceImpl.levelOf(legacy, new BigDecimal("20")));
        assertEquals(3, TMarrRiskLineServiceImpl.levelOf(legacy, new BigDecimal("100")));
    }

    @Test
    public void 分值不成体统的退回重填() {
        assertFalse(TMarrRiskLineServiceImpl.scoreSane(null));
        assertFalse(TMarrRiskLineServiceImpl.scoreSane(new BigDecimal("-0.01")));
        assertFalse(TMarrRiskLineServiceImpl.scoreSane(new BigDecimal("100.01")));
        assertFalse(TMarrRiskLineServiceImpl.scoreSane(new BigDecimal("9999")));
        assertTrue(TMarrRiskLineServiceImpl.scoreSane(BigDecimal.ZERO));
        assertTrue(TMarrRiskLineServiceImpl.scoreSane(new BigDecimal("100")));
    }

    @Test
    public void 交接口径_启用当日含_交棒当日不含() throws Exception {
        TMarrRiskLine old = rule("10", "20", "30");
        old.setEffStart(day("2024-01-01"));
        old.setEffEnd(day("2025-03-01"));

        assertTrue(TMarrRiskLineServiceImpl.standsAt(old, day("2024-01-01")));
        assertTrue(TMarrRiskLineServiceImpl.standsAt(old, day("2024-12-20")));
        // 旧条管到交棒前一刻：2025-02-28 仍站着
        assertTrue(TMarrRiskLineServiceImpl.standsAt(old, day("2025-02-28")));
        // 交棒当日不含：2025-03-01 零点起让位
        assertFalse(TMarrRiskLineServiceImpl.standsAt(old, day("2025-03-01")));
        // 启用之前不站着
        assertFalse(TMarrRiskLineServiceImpl.standsAt(old, day("2023-12-31")));
    }

    @Test
    public void 交棒之日空着等于现行() throws Exception {
        TMarrRiskLine cur = rule("10", "20", "30");
        cur.setEffStart(day("2025-03-01"));
        cur.setEffEnd(null);
        assertTrue(TMarrRiskLineServiceImpl.standsAt(cur, day("2025-03-01")));
        assertTrue(TMarrRiskLineServiceImpl.standsAt(cur, day("2026-09-27")));
        assertFalse(TMarrRiskLineServiceImpl.standsAt(cur, day("2025-02-28")));
    }

    @Test
    public void 抢档口径_顺位大的先_顺位同取代号在后_再同取启用新_再同取主键大() throws Exception {
        TMarrRiskLine a = rule("10", "20", "30");
        a.setId(1L);
        a.setRuleCode("FX01");
        a.setPriority(10);
        a.setEffStart(day("2025-01-01"));

        // 顺位更大者先说话
        TMarrRiskLine b = rule("10", "20", "30");
        b.setId(2L);
        b.setRuleCode("FX02");
        b.setPriority(20);
        b.setEffStart(day("2025-01-01"));

        // 顺位相同，代号在后的先说话
        TMarrRiskLine c = rule("10", "20", "30");
        c.setId(3L);
        c.setRuleCode("FX09");
        c.setPriority(10);
        c.setEffStart(day("2025-01-01"));

        // 顺位、代号都相同，启用之日新的在先
        TMarrRiskLine d = rule("10", "20", "30");
        d.setId(4L);
        d.setRuleCode("FX09");
        d.setPriority(10);
        d.setEffStart(day("2025-06-01"));

        // 三者皆同，主键大的在先
        TMarrRiskLine e = rule("10", "20", "30");
        e.setId(5L);
        e.setRuleCode("FX09");
        e.setPriority(10);
        e.setEffStart(day("2025-06-01"));

        List<TMarrRiskLine> all = new ArrayList<TMarrRiskLine>();
        all.add(a);
        all.add(e);
        all.add(c);
        all.add(b);
        all.add(d);
        all.sort(TMarrRiskLineServiceImpl.PRIORITY_THEN_CODE_DESC);

        assertEquals(Long.valueOf(2L), all.get(0).getId());
        assertEquals(Long.valueOf(5L), all.get(1).getId());
        assertEquals(Long.valueOf(4L), all.get(2).getId());
        assertEquals(Long.valueOf(3L), all.get(3).getId());
        assertEquals(Long.valueOf(1L), all.get(4).getId());
    }
}
