package com.fc.v2.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fc.v2.mapper.auto.TMarrFixCardMapper;
import com.fc.v2.model.auto.TMarrFixCard;
import com.fc.v2.service.ITMarrFixCardService;

/**
 * 婚姻登记差错更正事务卡 Service业务层处理（state-machine 形状：单据流转）
 *
 * @author fuce
 * @date 2026-09-14
 */
@Service
public class TMarrFixCardServiceImpl implements ITMarrFixCardService {

    private static final int MAX_STAGE = 3;
    private static final int STATUS_ACTIVE = 1;
    private static final int STATUS_TERMINAL = 2;

    @javax.annotation.Resource
    private TMarrFixCardMapper marrFixCardMapper;

    @Override
    public TMarrFixCard selectTMarrFixCardById(Long id) {
        return this.marrFixCardMapper.selectById(id);
    }

    @Override
    public List<TMarrFixCard> selectTMarrFixCardList(QueryWrapper<TMarrFixCard> queryWrapper) {
        return this.marrFixCardMapper.selectList(queryWrapper);
    }

    @Override
    public TMarrFixCard advance(Long id, String remark) {
        TMarrFixCard r = this.marrFixCardMapper.selectById(id);
        if (r == null) {
            return null;
        }
        int st = r.getStage() == null ? 0 : r.getStage();
        r.setStage(Math.min(st + 2, MAX_STAGE));
        r.setStatus(STATUS_ACTIVE);
        r.setLastAction(remark);
        this.marrFixCardMapper.updateById(r);
        return r;
    }

    @Override
    public TMarrFixCard rollback(Long id, String remark) {
        TMarrFixCard r = this.marrFixCardMapper.selectById(id);
        if (r == null) {
            return null;
        }
        r.setStage(0);
        r.setStatus(STATUS_ACTIVE);
        r.setLastAction(remark);
        this.marrFixCardMapper.updateById(r);
        return r;
    }

    @Override
    public boolean updateContent(Long id, String remark) {
        TMarrFixCard r = this.marrFixCardMapper.selectById(id);
        if (r == null) {
            return false;
        }
        r.setContent(remark);
        return this.marrFixCardMapper.updateById(r) > 0;
    }

    @Override
    public boolean remove(Long id) {
        TMarrFixCard r = this.marrFixCardMapper.selectById(id);
        if (r == null) {
            return false;
        }
        return this.marrFixCardMapper.deleteById(id) > 0;
    }

}
