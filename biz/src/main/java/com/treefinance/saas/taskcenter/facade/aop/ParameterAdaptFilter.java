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

package com.treefinance.saas.taskcenter.facade.aop;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.treefinance.toolkit.util.reflect.Reflections;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * hessian序列化和反序列化过程中一些特殊参数的适配和修正
 * <p>
 * 比如Byte,Short类型序列化当成整型处理，针对list<Byte>类型参数会被反序列化成List<Integer>
 * </p>
 *
 * @author Jerry
 * @date 2018/12/17 16:08
 */
@Activate(group = {Constants.PROVIDER, Constants.CONSUMER}, order = Integer.MAX_VALUE)
public class ParameterAdaptFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParameterAdaptFilter.class);

    private static final String CUSTOM_BEAN_PACKAGE = "com.treefinance.saas.taskcenter.facade";

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            Class<?>[] parameterTypes = invocation.getParameterTypes();
            if (ArrayUtils.isNotEmpty(parameterTypes)) {
                Object[] arguments = invocation.getArguments();
                for (int i = 0; i < parameterTypes.length; i++) {
                    final Object argument = arguments[i];
                    if (argument == null) {
                        continue;
                    }

                    final Class<?> parameterType = parameterTypes[i];
                    fixFieldValueWithByteList(argument, parameterType);
                }
            }
        } catch (Exception e) {
            LOGGER.error("[dubbo] Parameter adapt error! invocation: {}, invoker: {}", invocation, invoker, e);
        }

        final Result result = invoker.invoke(invocation);

        final Object value = result.getValue();
        if (value != null) {
            fixFieldValueWithByteList(value, value.getClass());
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private void fixFieldValueWithByteList(Object argument, Class<?> parameterType) {
        try {
            Package pkg = parameterType.getPackage();
            if (pkg != null && pkg.getName().startsWith(CUSTOM_BEAN_PACKAGE)) {
                List<Field> fields = Reflections.getFields(parameterType);
                for (Field field : fields) {
                    if (field.getType() == List.class) {
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType && ((ParameterizedType)genericType).getActualTypeArguments()[0] == Byte.class) {
                            field.setAccessible(true);
                            List<Integer> list = (List<Integer>)field.get(argument);
                            if (CollectionUtils.isNotEmpty(list)) {
                                List<Byte> result = list.stream().map(Integer::byteValue).collect(Collectors.toList());
                                field.set(argument, result);
                            }
                        }
                    }
                }
            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOGGER.warn("Error reading and adapt field in special parameter! parameterType: {}, value: {}", parameterType, argument, e);
        }
    }

}
