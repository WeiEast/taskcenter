package com.treefinance.saas.taskcenter.biz.service.moxie.directive.process;


import com.treefinance.saas.taskcenter.common.model.moxie.MoxieDirectiveDTO;

public interface MoxieDirectiveProcessor {

    /**
     * 处理消息
     *
     * @param directiveDTO
     */
    void process(MoxieDirectiveDTO directiveDTO);
}
