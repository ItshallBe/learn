package com.zzx;

import com.zzx.helper.BeanHelper;
import com.zzx.helper.ClassHelper;
import com.zzx.helper.ControllerHelper;
import com.zzx.helper.IocHelper;
import com.zzx.util.ClassUtil;
import com.zzx.util.CollectionUtil;

public final class HelperLoader {
    public static void init() {
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };
        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName(), false);
        }
    }
}
