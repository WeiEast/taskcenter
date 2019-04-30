package com.treefinance.saas.taskcenter.biz.mq;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.taskcenter.biz.mq.model.DeliveryAddressMessage;
import com.treefinance.saas.taskcenter.biz.service.DeliveryAddressService;
import com.treefinance.saas.taskcenter.share.mq.ConsumeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class DeliveryAddressMessageListener extends AbstractRocketMqMessageListener {

    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @Override
    public ConsumeSetting getConsumeSetting() {
        ConsumeSetting consumeSetting = new ConsumeSetting();
        consumeSetting.setGroup("grap-data");
        consumeSetting.setTopic("ecommerce_trade_address");
        consumeSetting.setTags("ecommerce");

        return consumeSetting;
    }

    @Override
    protected void handleMessage(String msgBody) {
        logger.info("消费收货地址消息数据==>{}", msgBody);
        DeliveryAddressMessage addressMessage = JSON.parseObject(msgBody, DeliveryAddressMessage.class);
        deliveryAddressService.callback(addressMessage);
    }
}
