package com.treefinance.saas.taskcenter.biz.mq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.treefinance.saas.taskcenter.service.impl.AbstractService;
import com.treefinance.saas.taskcenter.share.mq.BizMqMessageListener;

import java.util.List;

import static com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus.CONSUME_SUCCESS;

/**
 * 抽象消息监听
 * 
 * @author yh-treefinance
 * @date 2017/12/19.
 */
public abstract class AbstractRocketMqMessageListener extends AbstractService implements BizMqMessageListener {

    @Override
    public final ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        long start = System.currentTimeMillis();
        MessageExt msg = msgs.get(0);
        String message = new String(msg.getBody());
        try {
            handleMessage(message);
            return CONSUME_SUCCESS;
        } catch (Throwable cause) {
            if (msg.getReconsumeTimes() > 0) {
                logger.error("丢弃该消息, 因为重试一次之后, 消费消息仍然出错!!! msgBody={}", message, cause);
                return CONSUME_SUCCESS;
            }
            logger.error("消费消息时出错,即将再重试一次!!! msgBody={}", message, cause);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        } finally {
            logger.info("消费MQ消息,耗时{}ms: topic={},tag={},msgId={},body={}", (System.currentTimeMillis() - start), msg.getTopic(), msg.getTags(), msg.getMsgId(), message);
        }
    }

    /**
     * 消费处理消息
     *
     * @param msgBody mq消息内容
     */
    protected abstract void handleMessage(String msgBody);
}
