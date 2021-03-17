package com.km66.wechatrobot.common.utils;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Dict extends HashMap<String, Object> {

    private static final long serialVersionUID = -7589597368091882102L;

    public Dict() {
    }

    private Dict(Map<String, Object> map) {
        super(map);
    }

    public static Dict create() {
        return new Dict();
    }

    public static Dict create(Map<String, Object> map) {
        return new Dict(map);
    }


    public static Dict create(HttpServletRequest request) {
        Dict dict = new Dict();
        Map<String, String[]> properties = request.getParameterMap();
        Iterator<?> entries = properties.entrySet().iterator();
        Entry<?, ?> entry;
        String name = "";
        String value = "";
        while (entries.hasNext()) {
            entry = (Entry<?, ?>) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else {
                value = valueObj.toString();
            }
            dict.put(name, value);
        }
        return dict;
    }

    public <T> T getBean(Class<T> clazz) {
        Object obj = null;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
            Method method = clazz.getMethod("putAll", Map.class);
            method.invoke(obj, this);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return (T) obj;
    }

    @SuppressWarnings("unchecked")
    public Dict getDict(String key) {
        return Dict.create((Map<String, Object>) this.get(key));
    }

    public Dict set(String key, Object value) {
        this.put(key, value);
        return this;
    }


    public Long getLong(String key) {
        return Convert.toLong(this.get(key));
    }

    public Date getDate(String key) {
        return new Date(getLong(key));
    }

    public int getInt(String key) {
        return Convert.toInt(this.get(key));
    }

    public Double getDouble(String key) {
        return Convert.toDouble(this.get(key));
    }

    public String getStr(String key) {
        return Convert.toStr(this.get(key));
    }

    public boolean extis(String key) {
        return !StrUtil.isBlank(getStr(key));
    }

    public char[] getChar(String key) {
        return getStr(key).toCharArray();
    }

    public Float getFloat(String key) {
        return Convert.toFloat(this.get(key));
    }

    public BigDecimal getBigDecimal(String key) {
        return Convert.toBigDecimal(this.get(key));
    }

    public Boolean getBool(String key) {
        return this.get(key) == null ? false : Convert.toBool(this.get(key));
    }

    public Boolean isBlank(String key) {
        if (get(key) == null) {
            return true;
        }
        if (get(key).getClass() == String.class) {
            return StrUtil.isBlank(getStr(key));
        }
        return false;
    }

    public Boolean isNotBlank(String key) {
        return !isBlank(key);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    /**
     * 获取分页参数
     *
     * @return
     */
    public Page getPage() {
        return (Page) this.get("__queryPage__");
    }

    public Timestamp getTimestamp(String name) {
        Date date = Convert.toDate(this.get(name), null);
        return this.get(name) == null ? null : (date == null ? null : new Timestamp(date.getTime()));
    }

}
