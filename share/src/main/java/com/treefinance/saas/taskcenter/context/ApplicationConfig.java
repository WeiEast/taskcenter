/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.taskcenter.context;

import com.treefinance.toolkit.util.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jerry
 * @since 15:27 01/08/2017
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public RedissonClient redissonClient(@Value("${core.redis.hostName}") String host, @Value("${core.redis.port:6379}") String port,
        @Value("${core.redis.password:}") String password, @Value("${core.redis.useSSL?rediss:redis}") String protocol) {
        Preconditions.notBlank("redis.host", host);

        String address = String.format("%s://%s:%s", protocol, host, port);

        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer().setClientName("merchant_center_redisson").setAddress(address).setTimeout(5000);
        if (StringUtils.isNotEmpty(password)) {
            singleServerConfig.setPassword(password);
        }

        return Redisson.create(config);
    }
}
