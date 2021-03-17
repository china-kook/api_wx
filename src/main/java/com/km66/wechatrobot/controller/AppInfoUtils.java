package com.km66.wechatrobot.controller;

import com.km66.framework.core.basis.Record;
import com.km66.wechatrobot.support.model.RespEntity;
import com.km66.wechatrobot.support.utils.BigDataServiceUtil;

/**
 * Created by ikook on 2020/11/23 9:11 上午.
 */
public class AppInfoUtils {

    public static RespEntity getInfo(String wxCode) {
        Record getInfoRecord = new Record();
        getInfoRecord.put("wxid", wxCode);

       return BigDataServiceUtil.postGetBean("/wechatApi/getReqInfo", getInfoRecord, RespEntity.class);

    }

}
