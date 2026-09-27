package com.fc.v2.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fc.v2.common.base.BaseController;
import com.fc.v2.common.domain.AjaxResult;
import com.fc.v2.common.domain.ResultTable;
import com.fc.v2.common.log.Log;
import com.fc.v2.model.auto.TMarrImgCard;
import com.fc.v2.service.ITMarrImgCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * 卷宗影像件建档卡 Controller
 *
 * @author fuce
 * @date 2026-09-12
 */
@Api(value = "卷宗影像件建档卡")
@Controller
@RequestMapping("/MarrImgCardController")
public class MarrImgCardController extends BaseController {

    private final String prefix = "admin/marrImgCard";

    @Autowired
    private ITMarrImgCardService marrImgCardService;

    @ApiOperation(value = "分页跳转", notes = "分页跳转")
    @GetMapping("/view")
    @RequiresPermissions("marr:marrImgCard:view")
    public String view(ModelMap model) {
        return prefix + "/list";
    }

    @Log(title = "卷宗影像件建档卡集合查询", action = "list")
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/list")
    @RequiresPermissions("marr:marrImgCard:list")
    @ResponseBody
    public ResultTable list(TMarrImgCard record) {
        QueryWrapper<TMarrImgCard> queryWrapper = new QueryWrapper<TMarrImgCard>();
        startPage();
        com.github.pagehelper.PageInfo<TMarrImgCard> page =
                new com.github.pagehelper.PageInfo<TMarrImgCard>(marrImgCardService.selectTMarrImgCardList(queryWrapper));
        return pageTable(page.getList(), page.getTotal());
    }

    @Log(title = "卷宗影像件建档卡新增", action = "add")
    @ApiOperation(value = "新增", notes = "新增")
    @PostMapping("/add")
    @RequiresPermissions("marr:marrImgCard:add")
    @ResponseBody
    public AjaxResult add(TMarrImgCard record) {
        return toAjax(marrImgCardService.insertTMarrImgCard(record));
    }

    @Log(title = "卷宗影像件建档卡修改", action = "edit")
    @ApiOperation(value = "修改保存", notes = "修改保存")
    @PostMapping("/edit")
    @RequiresPermissions("marr:marrImgCard:edit")
    @ResponseBody
    public AjaxResult editSave(TMarrImgCard record) {
        return toAjax(marrImgCardService.updateTMarrImgCard(record));
    }

    @Log(title = "卷宗影像件建档卡删除", action = "remove")
    @ApiOperation(value = "删除", notes = "删除")
    @DeleteMapping("/remove")
    @RequiresPermissions("marr:marrImgCard:remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(marrImgCardService.deleteTMarrImgCardByIds(ids));
    }
}
