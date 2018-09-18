package com.treefinance.saas.taskcenter.biz.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.cglib.beans.BeanCopier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 *
 * @author luoyihua
 * @date 2017/5/10
 */
public final class BeanUtils {

    private BeanUtils() {

    }

    private static final Map<String, BeanCopier> beanCopierMap = new ConcurrentHashMap<>();

    /**
     * 基于CGLIB的bean properties 的拷贝，性能要远优于{@code org.springframework.beans.BeanUtils.copyProperties}
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            target = null;
            return;
        }

        String key = String.format("%s:%s", source.getClass().getName(), target.getClass().getName());
        if (!beanCopierMap.containsKey(key)) {
            BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
            beanCopierMap.putIfAbsent(key, beanCopier);
        }
        BeanCopier beanCopier = beanCopierMap.get(key);
        beanCopier.copy(source, target, null);
    }

    /**
     * 对象转化
     *
     * @param src
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T convert(Object src, T target) {
        copyProperties(src, target);
        return target;
    }

    public static <S, T> List<T> convertList(List<S> request, Class<T> cls) {
        List<T> result = Lists.newArrayList();
        if (request == null) return result;
        for (S obj : request) {
            try {
                T target = cls.newInstance();
                result.add(convert(obj, target));
            } catch (Exception e) {
                throw new IllegalArgumentException("对象copy失败，请检查相关module", e);
            }
        }
        return result;
    }

    public static <T> T checkNull(Supplier<T> t) {
        try {
            return t.get();
        } catch (NullPointerException e) {

        }
        return null;
    }

    public static void main(String[] args) {
        Map<String, List<String>> map = Maps.newHashMap();
        map.put("1", Lists.newArrayList("1"));
        String str = checkNull(() -> map.get("2").get(0));
        String str1 = checkNull(() -> map.get("1").get(1));
        System.out.println(str);
        if (str == null)
            return;
    }
}
