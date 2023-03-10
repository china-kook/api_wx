package com.km66.wechatrobot.api;

import cn.hutool.core.net.URLEncoder;
import cn.hutool.http.HttpUtil;
import com.km66.framework.core.utils.configkit.AppKit;
import com.km66.wechatrobot.common.utils.Dict;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ikook on 2020/11/2 8:37 下午.
 */
@Component
public class WxApi {
    //主动调用发送接口
    private static final String API_URL = AppKit.getStr("API_URL", "wxApiSendUrl");

    /**
     * 发送文字消息(好友或者群)
     *
     * @param robWxid 登录账号id，用哪个账号去发送这条消息
     * @param toWxid  对方的id，可以是群或者好友id
     * @param msg     消息内容
     */
    public void sendTextMsg(String robWxid, String toWxid, String msg) {
        Dict data = new Dict();
        data.put("type", 100);
//        data.put("msg", new String(msg.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
        System.out.println(msg);
        data.put("msg", URLEncoder.createDefault().encode(msg, StandardCharsets.UTF_8));
        System.out.println(data.getStr("msg"));
        data.put("to_wxid", toWxid);
        data.put("robot_wxid", robWxid);

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        HttpUtil.post(API_URL, result);
    }

    /**
     * 发送群消息并@艾特某人
     *
     * @param robWxid 账户id，用哪个账号去发送这条消息
     * @param toWxid  群id
     * @param atWxid  艾特的id，群成员的id
     * @param atName  艾特的昵称，群成员的昵称
     * @param msg     消息内容
     */
    public void sendGroupAtMsg(String robWxid, String toWxid, String atWxid, String atName, String msg) throws UnsupportedEncodingException {
        Dict data = new Dict();
        data.put("type", 102);
        System.out.println(msg);
        data.put("msg", URLEncoder.createDefault().encode(msg, StandardCharsets.UTF_8));
        System.out.println(data.getStr("msg"));
        data.put("to_wxid", toWxid);
        data.put("at_wxid", atWxid);
        data.put("at_name", atName);
        data.put("robot_wxid", robWxid);

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        HttpUtil.post(API_URL, result);
    }

    /**
     * 发送图片消息
     *
     * @param robWxid 账户id，用哪个账号去发送这条消息
     * @param toWxid  对方的id，可以是群或者好友id
     * @param path    图片的绝对路径
     */
    public void sendImageMsg(String robWxid, String toWxid, String path) {
        Dict data = new Dict();
        data.put("type", 102);
        data.put("msg", path);
        data.put("to_wxid", toWxid);
        data.put("robot_wxid", robWxid);

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        HttpUtil.post(API_URL, result);
    }

    /**
     * 发送视频消息
     *
     * @param robWxid 账户id，用哪个账号去发送这条消息
     * @param toWxid  对方的id，可以是群或者好友id
     * @param path    视频的绝对路径
     */
    public void sendVideoMsg(String robWxid, String toWxid, String path) {
        Dict data = new Dict();
        data.put("type", 104);
        data.put("msg", path);
        data.put("to_wxid", toWxid);
        data.put("robot_wxid", robWxid);

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        HttpUtil.post(API_URL, result);
    }

    /**
     * 发送文件消息
     *
     * @param robWxid 账户id，用哪个账号去发送这条消息
     * @param toWxid  对方的id，可以是群或者好友id
     * @param path    文件的绝对路径
     */
    public void sendFileMsg(String robWxid, String toWxid, String path) {
        Dict data = new Dict();
        data.put("type", 105);
        data.put("msg", path);
        data.put("to_wxid", toWxid);
        data.put("robot_wxid", robWxid);

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        HttpUtil.post(API_URL, result);
    }

    /**
     * 发送动态表情
     *
     * @param robWxid 账户id，用哪个账号去发送这条消息
     * @param toWxid  对方的id，可以是群或者好友id
     * @param path    动态表情文件（通常是gif）的绝对路径
     */
    public void sendEmojiMsg(String robWxid, String toWxid, String path) {
        Dict data = new Dict();
        data.put("type", 106);
        data.put("msg", path);
        data.put("to_wxid", toWxid);
        data.put("robot_wxid", robWxid);

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        HttpUtil.post(API_URL, result);
    }

    /**
     * 发送分享链接
     *
     * @param robWxid   账户id，用哪个账号去发送这条消息
     * @param toWxid    对方的id，可以是群或者好友id
     * @param title     链接标题
     * @param text      链接内容
     * @param targetUrl 跳转链接
     * @param picUrl    图片链接
     */
    public void sendLinkMsg(String robWxid, String toWxid, String title, String text, String targetUrl, String picUrl) {

        Dict link = new Dict();
        link.put("title", title);
        link.put("text", text);
        link.put("url", targetUrl);
        link.put("pic", picUrl);

        Dict data = new Dict();
        data.put("type", 107);

        // 另一个库
//        data.put("title", title);
//        data.put("text", text);
//        data.put("target_url", targetUrl);
//        data.put("pic_pic", picUrl);
//        data.put("icon_url", picUrl);

        data.put("msg", link);
        data.put("to_wxid", toWxid);
        data.put("robot_wxid", robWxid);

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        HttpUtil.post(API_URL, result);
    }

    /**
     * 发送音乐分享
     *
     * @param robWxid 账户id，用哪个账号去发送这条消息
     * @param toWxid  对方的id，可以是群或者好友id
     * @param name    歌曲名字
     */
    public void sendMusicMsg(String robWxid, String toWxid, String name) {
        Dict data = new Dict();
        data.put("type", 108);
        data.put("msg", name);
        data.put("to_wxid", toWxid);
        data.put("robot_wxid", robWxid);

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        HttpUtil.post(API_URL, result);
    }

    /**
     * 取指定登录账号的昵称
     *
     * @param robWxid 账户id
     * @return 账号昵称
     */
    public String getRobotName(String robWxid) {
        Dict data = new Dict();
        data.put("type", 201);
        data.put("robot_wxid", robWxid);

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        return HttpUtil.post(API_URL, result);
    }

    public String getRobotAccountList() {
        Dict data = new Dict();
        data.put("type", 203);

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);

        return HttpUtil.post(API_URL, result);
    }

}
