package com.km66.wechatrobot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.km66.wechatrobot.common.utils.GenerateErpRequestSign;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ikook on 2020/12/16 9:27 上午.
 */
public class ErpRespUtils {

    public static JSONObject getErpResult(String wxCode, String erpUrl) {
        String timeStamp = String.valueOf(DateUtil.parse(DateUtil.now()).getTime() / 1000);
        Map<String, Object> params = new HashMap<>();
//        params.put("parts_id", String.join(",", erpParts));
        params.put("weixin", wxCode);
        params.put("appId", "YP00001");
        params.put("timeStamp", timeStamp);
        params.put("method", "get-parts-list-v2");
        params.put("sign", GenerateErpRequestSign.generateSign("get-parts-list-v2", timeStamp));

        System.out.println("参数：" + JSONObject.toJSONString(params));
        System.out.println("erpUrl = " + erpUrl);
        String resultErp = HttpUtil.post(erpUrl + "/app-store/get-parts-list-v2", params);
        System.out.println("resultErp = " + resultErp);

        return JSONObject.parseObject(resultErp);
    }


    public static void main(String[] args) {
        System.out.println(getErpResult("uap88888", "http://api.1uap.com"));
    }

}
