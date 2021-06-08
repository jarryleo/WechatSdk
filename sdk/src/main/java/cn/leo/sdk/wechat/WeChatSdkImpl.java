package cn.leo.sdk.wechat;

import android.app.Application;

import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @author : ling luo
 * @date : 2021/6/3
 * @description : 微信sdk 实现
 */
final class WeChatSdkImpl {

    private static IWXAPI sWxApi = null;

    /**
     * 初始化sdk
     */
    void init(Application application, String appId) {
        sWxApi = WXAPIFactory.createWXAPI(application, appId, true);
        sWxApi.registerApp(appId);
    }

    IWXAPI getWxApi() {
        if (sWxApi == null){
            throw new NullPointerException("微信sdk尚未初始化");
        }
        return sWxApi;
    }

}
