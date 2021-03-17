package com.km66.wechatrobot.support.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.km66.framework.core.basis.Record;
import com.km66.framework.core.utils.configkit.CoreKit;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 调用大数据服务
 */
@Slf4j
public class BigDataServiceUtil {

    /**
     * 请求大数据接口返回Bean类型结果
     *
     * @param url    请求地址
     * @param record 请求参数
     */
    @SuppressWarnings("unchecked")
    public static <T> T postGetBean(String url, Record record, Class<? extends Map<String, Object>> bean) {
        Map<String, Object> data = null;
        try {
            data = bean.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assert data != null;
        data.putAll(Objects.requireNonNull(postGetMap(url, record)));
        return (T) data;
    }

    public static <T> T postGetBeanByAppInfo(String url, String appId, String appKey, Record record, Class<? extends Map<String, Object>> bean) {
        Map<String, Object> data = null;
        try {
            data = bean.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        assert data != null;
        data.putAll(Objects.requireNonNull(postGetMapByAppInfo(url, appId, appKey, record)));
        return (T) data;
    }

    /**
     * 请求大数据接口返回Map类型结果
     *
     * @param url    请求地址
     * @param record 请求参数
     */
    public static Map<String, Object> postGetMap(String url, Record record) {
        String body = post(url, record);
        if (StrUtil.isBlank(body)) {
            return null;
        }
        JSONObject json = JSONObject.parseObject(body);
        Map<String, Object> data = Maps.newHashMapWithExpectedSize(json.size());
        data.putAll(json);
        return data;
    }

    public static Map<String, Object> postGetMapByAppInfo(String url, String appId, String appKey, Record record) {
        String body = postByAppInfo(url, appId, appKey, record);
        if (StrUtil.isBlank(body)) {
            return null;
        }
        JSONObject json = JSONObject.parseObject(body);
        Map<String, Object> data = Maps.newHashMapWithExpectedSize(json.size());
        data.putAll(json);
        return data;
    }

    /**
     * 请求大数据接口
     *
     * @param url    接口地址
     * @param record 请求参数
     */
    public static String post(String url, Record record) {

        String path = StrUtil.format("{}{}", CoreKit.getStr("basePath", "bigdata"), url);

        HttpRequest httpRequest = HttpUtil.createPost(path);
        // 创建Headers
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(4);
        headers.put("appId", CoreKit.getStr("appId", "bigdata"));

        long timestamp = new Date().getTime();
        headers.put("signature", getSign(record, timestamp));
        headers.put("timestamp", Convert.toStr(timestamp));
        httpRequest.addHeaders(headers);
        httpRequest.form(record);

        HttpResponse httpResponse = httpRequest.execute();

        if (httpResponse.isOk()) {
            String body = httpResponse.body();
            log.info(body);
            if (StrUtil.isBlank(body) || !JSONUtil.isJson(body)) {
                return StrUtil.EMPTY;
            }
            return body;
        }
        return StrUtil.EMPTY;
    }

    public static String postByAppInfo(String url, String appId, String appKey, Record record) {

        String path = StrUtil.format("{}{}", CoreKit.getStr("basePath", "bigdata"), url);

        HttpRequest httpRequest = HttpUtil.createPost(path);
        // 创建Headers
        Map<String, String> headers = Maps.newHashMapWithExpectedSize(4);
        headers.put("appId", appId);

        long timestamp = new Date().getTime();
        headers.put("signature", getSignByAppInfo(record, appKey, timestamp));
        headers.put("timestamp", Convert.toStr(timestamp));
        httpRequest.addHeaders(headers);
        httpRequest.form(record);

        HttpResponse httpResponse = httpRequest.execute();

        if (httpResponse.isOk()) {
            String body = httpResponse.body();
            log.info(body);
            if (StrUtil.isBlank(body) || !JSONUtil.isJson(body)) {
                return StrUtil.EMPTY;
            }
            return body;
        }
        return StrUtil.EMPTY;
    }

    /**
     * 获取密钥值
     */
    private static String getSign(Record record, long timestamp) {
        String one = StrUtil.format("{}{}", CoreKit.getStr("appKey", "bigdata"), timestamp);

        List<String> keys = new ArrayList<>(record.keySet());
        Collections.sort(keys);
        StringBuffer urlParam = new StringBuffer();

        keys.forEach(key -> {
            if (record.get(key) != null) {
                urlParam.append(key);
                urlParam.append(record.getStr(key));
            }

        });

        String paramUrlStr = ReUtil.replaceAll(urlParam.toString(), "\\s", "");

        Digester digester = new Digester(DigestAlgorithm.MD5);
        one = digester.digestHex(digester.digestHex(one.toUpperCase()).toUpperCase() + paramUrlStr.toUpperCase()).toUpperCase();
        return one;
    }

    private static String getSignByAppInfo(Record record, String appKey, long timestamp) {
        String one = StrUtil.format("{}{}", appKey, timestamp);

        List<String> keys = new ArrayList<>(record.keySet());
        Collections.sort(keys);
        StringBuffer urlParam = new StringBuffer();

        keys.forEach(key -> {
            if (record.get(key) != null) {
                urlParam.append(key);
                urlParam.append(record.getStr(key));
            }

        });

        String paramUrlStr = ReUtil.replaceAll(urlParam.toString(), "\\s", "");

        Digester digester = new Digester(DigestAlgorithm.MD5);
        one = digester.digestHex(digester.digestHex(one.toUpperCase()).toUpperCase() + paramUrlStr.toUpperCase()).toUpperCase();
        return one;
    }

}
