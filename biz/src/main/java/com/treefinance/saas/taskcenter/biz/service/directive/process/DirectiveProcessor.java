package com.treefinance.saas.taskcenter.biz.service.directive.process;

import com.treefinance.saas.taskcenter.common.enums.EDirective;

/**
 * 指令处理器
 * 
 * @author yh-treefinance
 * @date 2017/7/6.
 */
public interface DirectiveProcessor {

    /**
     * 获取指定处理的指令
     *
     * @return 指令
     */
    EDirective getSpecifiedDirective();

    /**
     * 处理指令
     *
     * @param context 指令信息上下文
     */
    void process(DirectiveContext context);
}
