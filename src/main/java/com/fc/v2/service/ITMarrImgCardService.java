package com.fc.v2.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.fc.v2.model.auto.TMarrImgCard;

import java.util.List;

/**
 * 卷宗影像件建档卡 Service接口
 *
 * @author fuce
 * @date 2026-09-12
 */
public interface ITMarrImgCardService {

    /** 按主键查询 */
    TMarrImgCard selectTMarrImgCardById(Long id);

    /** 按条件查询列表（分页由调用方统一处理） */
    List<TMarrImgCard> selectTMarrImgCardList(Wrapper<TMarrImgCard> queryWrapper);

    /** 新增 */
    int insertTMarrImgCard(TMarrImgCard record);

    /** 修改 */
    int updateTMarrImgCard(TMarrImgCard record);

    /** 批量删除 */
    int deleteTMarrImgCardByIds(String ids);

    /** 按主键删除 */
    int deleteTMarrImgCardById(Long id);
}
