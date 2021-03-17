package com.km66.wechatrobot.controller;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.km66.framework.core.basis.Record;
import com.km66.framework.core.utils.configkit.AppKit;
import com.km66.wechatrobot.api.WxApi;
import com.km66.wechatrobot.common.utils.ImageUtils;
import com.km66.wechatrobot.common.utils.Params;
import com.km66.wechatrobot.support.model.RespEntity;
import com.km66.wechatrobot.support.utils.BigDataServiceUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by ikook on 2020/11/2 8:46 下午.
 */
@RestController
public class CallWx {

    @Resource
    private WxApi wxApi;

    private static final String baseUrl = AppKit.getStr("testBaseUrl", "baseUrl");

    private static final String url = AppKit.getStr("URL", "wxApiSendUrl");

    @PostMapping("callback")
    public void callbackWx(@RequestParam Map<String, Object> param) throws Exception {

        Params params = new Params(param);

        String wxType = params.getStr("type"); // 数据类型
        String msgType = params.getStr("msg_type"); // 消息类型
        String msg = params.getStr("msg"); // 发送内容
        String toWxid = params.getStr("from_wxid"); // 1级来源（比如发消息的人的ID）
        String fromName = params.getStr("from_name"); // 1级来源昵称（比如发消息的人昵称）
        String final_from_wxid = params.getStr("final_from_wxid");  // 2级来源id（群消息事件下，1级来源为群id，2级来源为发消息的成员id，私聊事件下都一样）
        String final_nickname = params.getStr("final_from_name"); // 2级来源昵称
        String robot_wxid = params.getStr("robot_wxid");  // 当前登录的账号（机器人）标识id
        String parameters = params.getStr("parameters");  // 附加参数（暂未用到，请忽略）
        String ws_time = params.getStr("time");  // 请求时间(时间戳10位版本)

        String file_url = params.getStr("file_url");


        String result = URLDecoder.decode(new String(wxApi.getRobotAccountList().getBytes(StandardCharsets.UTF_8)), "UTF-8");
        result = result.replace("\"[","[");
        result = result.replace("]\"","]");

        System.out.println(result);

        JSONObject wxObject = JSONObject.parseObject(result);
        JSONArray wxArray = wxObject.getJSONArray("data");
        String wxCode = "";
        String wxNickname = "";

        for (int i = 0; i < wxArray.size(); i++) {

            JSONObject jsonObject1 = wxArray.getJSONObject(i);

            if (jsonObject1.getString("robot_wxid").equals(robot_wxid)) {
                wxCode = jsonObject1.getString("wx_num");
//                wxCode = "uap88888";
                wxNickname = jsonObject1.getString("nickname");
            }
        }

        String actionType = "1"; // 请求类型。1 为直接 vin ，2 为图片扫描

        RespEntity respInfoEntity = AppInfoUtils.getInfo(wxCode);

        if (respInfoEntity.getCode() != 0) {
            return;
        }

        Map<String, Object> resultInfo = respInfoEntity.getData(Map.class);

        JSONObject erpResult = ErpRespUtils.getErpResult(wxCode, resultInfo.get("erpUrl").toString());

        if (1000 != erpResult.getInteger("errCode")) {
            return;
        }

        if ("3".equals(msgType)) {
            Record record = Record.create();

            System.out.println("file_url: " + file_url);
            System.out.println("url: " + url);

            String pattern = "http://((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
            file_url = Pattern.compile(pattern).matcher(file_url).replaceAll(url);

            System.out.println("替换后的 file_url: " + file_url);

            URL url = new URL(file_url);
            //打开链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            byte[] data = readInputStream(inStream);

            record.put("photo", Base64.encode(ImageUtils.compressPicForScale(data, data.length, "1", 0.6)));

            inStream.close();

            record.put("wxId", robot_wxid);
            record.put("wxCode", wxCode);
            record.put("wxNickname", wxNickname);
            RespEntity respEntity = BigDataServiceUtil.postGetBeanByAppInfo("/vinByOpenApi/scanner",
                    resultInfo.get("appId").toString(), resultInfo.get("appKey").toString(), record, RespEntity.class);
            if (respEntity.getCode() == 0) {
                msg = respEntity.getData().toString();
            }
        }

        // 是否为 VIN
        if (msg.length() == 17 && Pattern.matches("[0-9|a-zA-Z]+", msg)) {

            Map<String, String> urlParam = new HashMap<>();
            urlParam.put("wxid", wxCode);
            urlParam.put("origWxId", robot_wxid);
            urlParam.put("wxCode", wxCode);
            urlParam.put("wxNickname", wxNickname);
            urlParam.put("vin", msg);
            urlParam.put("actionType", actionType);

            String url = baseUrl +  "param=" + URLEncoder.encode(JSON.toJSONString(urlParam));

            if ("200".equals(wxType)) {
                wxApi.sendGroupAtMsg(robot_wxid, toWxid, final_from_wxid, final_nickname, "");
            }

            if ("3".equals(msgType)) {
                wxApi.sendTextMsg(robot_wxid, toWxid, msg);
            }

            wxApi.sendLinkMsg(robot_wxid, toWxid, "配件列表", "点击查看详情", url, "http://dmsimg.66km.com/dms/share_pic_zhangdan.png");

        }
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[102400000];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

}
