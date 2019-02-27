package com.treefinance.saas.taskcenter.biz.service.directive.process;

import com.treefinance.saas.taskcenter.dto.DirectiveDTO;

/**
 * 指令消息处理器
 * 
 * @author yh-treefinance
 * @date 2017/7/6.
 */
public interface DirectiveProcessor {

    /**
     * 处理消息
     *
     * @param directiveDTO 指令对象
     */
    void process(DirectiveDTO directiveDTO);
}
