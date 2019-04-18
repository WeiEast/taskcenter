package com.treefinance.saas.taskcenter.biz.mq;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.service.directive.DirectiveService;
import com.treefinance.saas.taskcenter.context.config.MqConfig;
import com.treefinance.saas.taskcenter.dto.DirectiveDTO;
import com.treefinance.saas.taskcenter.share.mq.ConsumeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class DirectiveMessageListener extends AbstractRocketMqMessageListener {

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
    protected void handleMessage(String msgBody) {
        logger.info("消费指令消息数据==>{}", msgBody);
        DirectiveDTO directiveDTO = JSON.parseObject(msgBody, DirectiveDTO.class);
        directiveService.process(directiveDTO);
    }
}
