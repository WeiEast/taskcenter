package com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.moxie.directive.process.MoxieAbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.context.enums.moxie.EMoxieDirective;
import com.treefinance.saas.taskcenter.dto.moxie.MoxieDirectiveDTO;
import org.springframework.stereotype.Component;

/**
 * 基础指令处理
 * Created by yh-treefinance on 2017/7/6.
 */
@Component
public class MoxieBaseDirectiveProcessor extends MoxieAbstractDirectiveProcessor {

    @Override
    protected void doProcess(EMoxieDirective directive, MoxieDirectiveDTO directiveDTO) {
        logger.info("处理魔蝎基础指令消息：{}", JSON.toJSONString(directiveDTO));
    }

}
