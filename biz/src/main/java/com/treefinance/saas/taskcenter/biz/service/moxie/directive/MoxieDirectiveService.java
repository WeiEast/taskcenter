package com.treefinance.saas.taskcenter.biz.service.moxie.directive;


import com.treefinance.saas.taskcenter.common.model.moxie.MoxieDirectiveDTO;

/**
 * 魔蝎指令处理Service
 */
public interface MoxieDirectiveService {
    /**
     * 处理指令
     *
     * @param moxieDirectiveDTO
     */
    void process(MoxieDirectiveDTO moxieDirectiveDTO);
}
