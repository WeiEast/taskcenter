package com.treefinance.saas.taskcenter.biz.service.directive;

/**
 * 指令消息处理Service
 * 
 * @author Jerry
 * @date 2017/7/5.
 */
public interface DirectiveService {
    /**
     * 处理指令信息
     *
     * @param directivePacket 指令信息数据包
     */
    void process(DirectivePacket directivePacket);
}
