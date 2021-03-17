package com.km66.wechatrobot.common.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

import java.util.*;

/**
 * Created by ikook on 2020/3/13 3:57 下午.
 */
public class GenerateErpRequestSign {

    private static final String APP_ID = "YP00001";
    private static final String APP_KEY = "YP00001";

    public static String generateSign(String method, String timeStamp) {

        Map<String, Object> param = new HashMap<>();
        param.put("appId", APP_ID);
        param.put("appKey", APP_KEY);
        param.put("timeStamp", timeStamp);
        param.put("method", method);

        Map<String, Object> sortParam = sortByKey(param);

        StringBuilder signParam = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortParam.entrySet()) {
            signParam.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        signParam.deleteCharAt(signParam.length() - 1);

        System.out.println(signParam);

        Digester md5 = new Digester(DigestAlgorithm.MD5);

        return md5.digestHex(signParam.toString()).toUpperCase();
    }

    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }
}
