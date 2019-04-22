package com.treefinance.saas.taskcenter.biz.service.directive.process.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.directive.process.AbstractDirectiveProcessor;
import com.treefinance.saas.taskcenter.biz.service.directive.process.DirectiveContext;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import org.springframework.stereotype.Component;

/**
 * 基础指令处理
 * 
 * @author yh-treefinance
 * @date 2017/7/6.
 */
@Component
public class DefaultDirectiveProcessor extends AbstractDirectiveProcessor {

    @Override
    public EDirective getSpecifiedDirective() {
        return null;
    }

    @Override
    protected void doProcess(DirectiveContext context) {
        logger.info("处理基础指令消息：{}", JSON.toJSONString(context));
    }

}
