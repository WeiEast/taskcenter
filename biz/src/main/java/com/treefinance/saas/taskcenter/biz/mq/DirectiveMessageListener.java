package com.treefinance.saas.taskcenter.biz.mq;

import com.treefinance.saas.taskcenter.biz.mq.model.DirectiveMessage;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectivePacket;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveService;
import com.treefinance.saas.taskcenter.common.enums.EDirective;
import com.treefinance.saas.taskcenter.context.config.MqConfig;
import com.treefinance.saas.taskcenter.share.mq.ConsumeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class DirectiveMessageListener extends AbstractJsonMessageListener<DirectiveMessage> {

    @Autowired
    private MqConfig mqConfig;
    @Autowired
    private DirectiveService directiveService;

    @Override
    public ConsumeSetting getConsumeSetting() {
        ConsumeSetting consumeSetting = new ConsumeSetting();
        consumeSetting.setGroup(mqConfig.getDirectiveGroupName());
        consumeSetting.setTopic(mqConfig.getConsumeDirectiveTopic());
        consumeSetting.setTags(mqConfig.getConsumeDirectiveTag());

        return consumeSetting;
    }

    @Override
    protected void processMessage(@Nonnull DirectiveMessage message) {
        DirectivePacket directivePacket = new DirectivePacket();
        directivePacket.setDirective(EDirective.directiveOf(message.getDirective()));
        directivePacket.setDirectiveId(message.getDirectiveId());
        directivePacket.setTaskId(message.getTaskId());
        directivePacket.setRemark(message.getRemark());

        directiveService.process(directivePacket);
    }
}
