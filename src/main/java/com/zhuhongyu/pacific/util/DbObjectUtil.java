package com.zhuhongyu.pacific.util;

import com.mongodb.DBObject;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class DbObjectUtil {
    public static <T> void dbObject2Bean(DBObject dbObject, T bean)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (bean == null) {  //测试已通过
            return;
        }
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            String varName = field.getName();
            Object object = dbObject.get(varName);
            if (object != null) {
                BeanUtils.setProperty(bean, varName, object);
            }
        }
    }
}
