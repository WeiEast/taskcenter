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

import com.treefinance.toolkit.util.reflect.Reflections;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Jerry
 * @date 2019-05-07 17:44
 */
public abstract class AbstractValueAdapterFilter {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final Pattern CUSTOM_BEAN_PACKAGE_PATTERN = Pattern.compile("^com\\.(treefinance|datatrees|treefintech)\\.saas\\..*");

    protected void fixFieldValueWithByteList(Object argument, Class<?> parameterType) {
        LOGGER.info("检查dubbo序列化对象 >> {}", parameterType);
        try {
            if (isCustomBean(parameterType)) {
                adaptFieldValueWithByteList(argument, parameterType);
            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOGGER.warn("Error reading and adapt field in special parameter! parameterType: {}, value: {}", parameterType, argument, e);
        }
    }

    @SuppressWarnings("unchecked")
    private void adaptFieldValueWithByteList(Object argument, Class<?> parameterType) throws IllegalAccessException {
        List<Field> fields = Reflections.getFields(parameterType);
        for (Field field : fields) {
            Object fieldValue = null;
            Class<?> fieldType = field.getType();
            if (fieldType.isPrimitive() || fieldType == String.class || Number.class.isAssignableFrom(fieldType) || fieldType == Date.class) {
                continue;
            }

            if (fieldType == Object.class) {
                field.setAccessible(true);
                fieldValue = field.get(argument);
                if (fieldValue == null) {
                    continue;
                }

                fieldType = fieldValue.getClass();
            }

            if (Collection.class.isAssignableFrom(fieldType)) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    final Type actualType = ((ParameterizedType)genericType).getActualTypeArguments()[0];
                    if (actualType == Byte.class) {
                        LOGGER.info("重置dubbo序列化对象字段 >> {}, 类型：List<Byte>", field.getName());
                        if (fieldValue == null) {
                            field.setAccessible(true);
                            fieldValue = field.get(argument);
                        }
                        Collection<Integer> list = (Collection<Integer>)fieldValue;
                        if (CollectionUtils.isNotEmpty(list)) {
                            List<Byte> result = list.stream().map(Integer::byteValue).collect(Collectors.toList());
                            field.set(argument, result);
                        }
                    } else if (isCustomBean((Class<?>)actualType)) {
                        if (fieldValue == null) {
                            field.setAccessible(true);
                            fieldValue = field.get(argument);
                        }
                        for (Object item : (Collection)fieldValue) {
                            if (item != null) {
                                adaptFieldValueWithByteList(item, (Class<?>)actualType);
                            }
                        }
                    }
                }
            } else if (isCustomBean(fieldType)) {
                if (fieldValue == null) {
                    field.setAccessible(true);
                    fieldValue = field.get(argument);
                }
                if (fieldValue != null) {
                    adaptFieldValueWithByteList(fieldValue, fieldType);
                }
            } else if (fieldType.isArray()) {
                final Class<?> componentType = fieldType.getComponentType();
                if (isCustomBean(componentType)) {
                    if (fieldValue == null) {
                        field.setAccessible(true);
                        fieldValue = field.get(argument);
                    }
                    for (Object item : (Object[])fieldValue) {
                        if (item != null) {
                            adaptFieldValueWithByteList(item, componentType);
                        }
                    }
                }
            }
        }
    }

    private boolean isCustomBean(Class<?> parameterType) {
        Package pkg = parameterType.getPackage();
        return pkg != null && CUSTOM_BEAN_PACKAGE_PATTERN.matcher(pkg.getName()).matches();
    }
}
