/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.saas.taskcenter.share.mq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.treefinance.saas.taskcenter.context.config.MqConfig;
import com.treefinance.saas.taskcenter.exception.UnexpectedException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel.CLUSTERING;

/**
 * @author Jerry
 * @date 2018/12/10 14:45
 */
@Component
public class ConsumerStartListener implements ApplicationListener<ApplicationContextEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerStartListener.class);

    private List<DefaultMQPushConsumer> consumers;

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            ApplicationContext applicationContext = event.getApplicationContext();
            Map<String, BizMqMessageListener> listenerMap = applicationContext.getBeansOfType(BizMqMessageListener.class);
            if (MapUtils.isNotEmpty(listenerMap)) {
                MqConfig mqConfig = applicationContext.getBean(MqConfig.class);
                consumers = listenerMap.values().stream().map(listener -> {
                    ConsumeSetting consumeSetting = listener.getConsumeSetting();

                    DefaultMQPushConsumer consumer;
                    try {
                        consumer = new DefaultMQPushConsumer(consumeSetting.getGroup());
                        consumer.setNamesrvAddr(mqConfig.getNamesrvAddr());
                        consumer.subscribe(consumeSetting.getTopic(), consumeSetting.getTags());
                        consumer.setMessageModel(CLUSTERING);
                        consumer.registerMessageListener(listener);
                        consumer.start();
                    } catch (MQClientException e) {
                        throw new UnexpectedException(
                            "Error start mq consumer[" + consumeSetting.getGroup() + "], " + "topic: " + consumeSetting.getTopic() + ", tags: " + consumeSetting.getTags(), e);
                    }
                    LOGGER.info("Started mq consumer[{}]. name-server:{}, topic:{}, tag:{}", consumeSetting.getGroup(), mqConfig.getNamesrvAddr(), consumeSetting.getTopic(),
                        consumeSetting.getTags());

                    return consumer;
                }).collect(Collectors.toList());
            }
        } else if (event instanceof ContextClosedEvent) {
            if (CollectionUtils.isNotEmpty(consumers)) {
                for (DefaultMQPushConsumer consumer : consumers) {
                    try {
                        consumer.shutdown();
                        LOGGER.info("Closed mq consumer[{}]", consumer.getConsumerGroup());
                    } catch (Exception e) {
                        LOGGER.error("Error shutdown mq consumer[{}]", consumer.getConsumerGroup(), e);
                    }
                }
            }
        }

    }
}
