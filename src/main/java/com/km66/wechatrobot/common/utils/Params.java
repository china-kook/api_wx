package com.km66.wechatrobot.common.utils;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.util.TypeUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * http 请求参数实体类
 */
public class Params extends LinkedHashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static Map<String, Object> map = new HashMap<>();
    private HttpServletRequest request;

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public Params(HttpServletRequest request) {
        this.request = request;
        saveParams();
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

    public List<String> getArr(String key) {
        List<String> list = new ArrayList<>();
        this.keySet().forEach(k -> {
            if (k.contains(key)) {
                list.add(Convert.toStr(this.get(k)));
            }
        });
        return list;
    }

    public Params(Map<String, ?> map) {
        if (map != null)
            this.putAll(map);
    }

    /**
     * 根据类型获取实体类
     *
     * @param clazz 数据类型
     * @return
     * @date 2017年12月18日
     * @author Sir丶雨轩
     */
    public <T> T getEntity(Class<T> clazz) {
        return TypeUtils.castToJavaBean(this, clazz);
    }

    public String[] getArr(String key, String... sp) {
        String tmp = this.getStr(key);
        if (!tmp.contains(sp.length == 0 ? "," : sp[0])) {
            return new String[]{tmp};
        } else {
            return tmp.split(sp.length == 0 ? "," : sp[0]);
        }
    }

    /**
     * 将本次请求参数放置map中
     *
     * @date 2017年12月13日
     * @author Sir丶雨轩
     */
    private void saveParams() {
        this.clear();// 清空之前的参数
        Enumeration<String> em = request.getParameterNames();
        while (em.hasMoreElements()) {
            String key = em.nextElement();
            String value = request.getParameter(key);
            this.put(key, value);
        }

    }

    public Params putE(String key, Object Object) {
        this.put(key, Object);
        return this;
    }

    public Params removeE(String key) {
        this.remove(key);
        return this;
    }

    public Params removeS(String... key) {
        for (String str : key) {
            if (this.containsKey(str))
                this.remove(str);
        }
        return this;
    }

    /**
     * 安全的获取int类型数据
     *
     * @param key
     * @return
     * @date 2017年12月18日
     * @author Sir丶雨轩
     */
    public Integer getInt(String key) {
        int result = 0;
        try {
            result = Integer.parseInt(this.get(key).toString());
        } catch (Exception ignored) {
        }
        return result;
    }

    /**
     * 安全的获取boolean类型数据
     *
     * @param key
     * @return
     * @date 2017年12月18日
     * @author Sir丶雨轩
     */
    public Boolean getBool(String key) {
        Boolean result = Boolean.FALSE;
        try {
            result = Boolean.valueOf(this.getStr(key));
        } catch (Exception ignored) {
        }
        return result;
    }

    /**
     * 安全的获取string类型数据
     *
     * @param key
     * @return
     * @date 2017年12月18日
     * @author Sir丶雨轩
     */
    public String getStr(String key) {
        String result = new String("");
        try {
            result = this.get(key).toString();
        } catch (Exception ignored) {
        }
        return result;
    }

    /**
     * 安全的获取long类型数据
     *
     * @param key
     * @return
     * @date 2017年12月18日
     * @author Sir丶雨轩
     */
    public Long getLong(String key) {
        long result = 0L;
        try {
            result = Long.parseLong(this.get(key).toString());
        } catch (Exception ignored) {
        }
        return result;
    }


}
