package com.fc.v2.service.impl;

import org.springframework.stereotype.Service;

import com.fc.v2.mapper.auto.TMarrCertBillMapper;
import com.fc.v2.model.auto.TMarrCertBill;
import com.fc.v2.service.ITMarrCertBillService;

/**
 * 婚姻登记记录证明逐级核准单 Service业务层处理（approval-chain 形状：多阶段签批）
 *
 * @author fuce
 * @date 2026-09-14
 */
@Service
public class TMarrCertBillServiceImpl implements ITMarrCertBillService {

    private static final int MAX_NODE = 2;
    private static final int MODE_OR = 0;
    private static final int MODE_AND = 1;
    private static final int STATUS_RUNNING = 0;
    private static final int STATUS_PASS = 1;
    private static final int STATUS_VETO = 2;

    @javax.annotation.Resource
    private TMarrCertBillMapper marrCertBillMapper;

    @Override
    public TMarrCertBill selectTMarrCertBillById(Long id) {
        return this.marrCertBillMapper.selectById(id);
    }

    @Override
    public TMarrCertBill approve(Long id, String approver, String comment) {
        TMarrCertBill r = this.marrCertBillMapper.selectById(id);
        if (r == null || approver == null || approver.trim().isEmpty()) {
            return null;
        }
        r.setNodeNo(Integer.valueOf((r.getNodeNo() == null ? 0 : r.getNodeNo()) + 1));
        r.setStatus(Integer.valueOf(r.getNodeNo() >= MAX_NODE ? STATUS_PASS : STATUS_RUNNING));
        this.marrCertBillMapper.updateById(r);
        return r;
    }

    @Override
    public TMarrCertBill reject(Long id, String approver, String comment) {
        TMarrCertBill r = this.marrCertBillMapper.selectById(id);
        if (r == null) {
            return null;
        }
        this.marrCertBillMapper.updateById(r);
        return r;
    }

    @Override
    public TMarrCertBill rollback(Long id, String comment) {
        TMarrCertBill r = this.marrCertBillMapper.selectById(id);
        if (r == null) {
            return null;
        }
        int node = r.getNodeNo() == null ? 0 : r.getNodeNo();
        r.setNodeNo(Integer.valueOf(Math.max(0, node - 1)));
        this.marrCertBillMapper.updateById(r);
        return r;
    }
}
