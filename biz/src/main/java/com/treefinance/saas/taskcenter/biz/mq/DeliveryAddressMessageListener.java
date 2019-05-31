package com.treefinance.saas.taskcenter.biz.mq;

import com.treefinance.saas.taskcenter.biz.mq.model.DeliveryAddressMessage;
import com.treefinance.saas.taskcenter.biz.service.DeliveryAddressService;
import com.treefinance.saas.taskcenter.share.mq.ConsumeSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * Created by luoyihua on 2017/4/26.
 */
@Service
public class DeliveryAddressMessageListener extends AbstractJsonMessageListener<DeliveryAddressMessage> {

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
    protected void processMessage(@Nonnull DeliveryAddressMessage message) {
        deliveryAddressService.callback(message);
    }
}
