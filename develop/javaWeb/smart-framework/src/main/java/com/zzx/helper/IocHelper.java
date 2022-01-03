package com.zzx.helper;

import com.zzx.annotation.Inject;
import com.zzx.util.ArrayUtil;
import com.zzx.util.CollectionUtil;
import com.zzx.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

public final class IocHelper {
    static {
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)) {
            /* 遍历Bean Map */
            for (Map.Entry<Class<?>, Object>beanEntry : beanMap.entrySet()) {
                /* 从BeanMap中获取Bean类与实例 */
                Class<?>beanClass = beanEntry.getKey();
                Object beanInstance = beanEntry.getValue();
                /* 获取Bean类定义的所有成员变量（简称Bean Field) */
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFields)) {
                    /* 遍历Bean Field */
                    for (Field beanField : beanFields) {
                        /* 判断当前BeanField是否带有Inject注解 */
                        if (beanField.isAnnotationPresent(Inject.class)) {
                            /* 在BeanMap中获取BeanField对应的实例 */
                            Class<?>beanFieldClass = beanField.getType();
                            Object beanFieldInstance = beanMap.get(beanFieldClass);
                            if (beanFieldInstance != null) {
                                /* 通过反射初始化BeanField的值 */
                                ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
