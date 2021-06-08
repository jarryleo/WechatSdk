package cn.leo.sdk.wechat;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.tencent.mm.opensdk.constants.Build;

/**
 * @author : ling luo
 * @date : 2021/6/3
 * @description : 微信sdk
 */
public class WeChatSdk {

    static final WeChatSdkImpl wechatSdk = new WeChatSdkImpl();

    static String sAppId = "";

    @SuppressLint("StaticFieldLeak")
    static Context sContext = null;

    /**
     * 初始化微信sdk
     *
     * @param application application
     * @param appId       微信appId
     */
    public static void init(Application application, String appId) {
        sAppId = appId;
        sContext = application;
        wechatSdk.init(application, appId);
    }

    /**
     * 微信登陆
     *
     * @param weChatLoginResultListener 登陆结果回调
     */
    public static void login(WeChatLoginResultListener weChatLoginResultListener) {
        WeChatLogin.login(wechatSdk.getWxApi(), weChatLoginResultListener);
    }

    /**
     * 微信支付
     *
     * @param partnerId               商户号
     * @param prepayId                预支付交易会话ID
     * @param nonceStr                随机字符串
     * @param timeStamp               时间戳
     * @param sign                    签名
     * @param weChatPayResultListener 支付结果
     */
    public static void pay(
            String partnerId,
            String prepayId,
            String nonceStr,
            String timeStamp,
            String sign,
            WeChatPayResultListener weChatPayResultListener) {
        WeChatPay.pay(
                wechatSdk.getWxApi(),
                partnerId,
                prepayId,
                nonceStr,
                timeStamp,
                sign,
                weChatPayResultListener);
    }

    /**
     * 分享到微信
     *
     * @param title                     标题
     * @param description               描述
     * @param contentUrl                内容链接
     * @param imageUrl                  图片路径，可以是文件路径或者网络图片 url
     * @param weChatShareResultListener 分享结果回调
     */
    public static void shareToWechat(
            String title,
            String description,
            String contentUrl,
            String imageUrl,
            WeChatShareResultListener weChatShareResultListener
    ) {
        WeChatShare.shareToWechat(wechatSdk.getWxApi(),
                title, description, contentUrl,
                imageUrl, weChatShareResultListener);
    }

    /**
     * 分享到微信朋友圈
     *
     * @param title                     标题
     * @param description               描述
     * @param contentUrl                内容链接
     * @param imageUrl                  图片路径，可以是文件路径或者网络图片 url
     * @param weChatShareResultListener 分享结果回调
     */
    public static void shareToMoment(
            String title,
            String description,
            String contentUrl,
            String imageUrl,
            WeChatShareResultListener weChatShareResultListener
    ) {
        WeChatShare.shareToMoment(wechatSdk.getWxApi(),
                title, description, contentUrl,
                imageUrl, weChatShareResultListener);
    }


    /**
     * 分享到微信朋友圈
     *
     * @param title                     标题
     * @param description               描述
     * @param path                      小程序 页面 路径
     * @param username                  小程序 username
     * @param contentUrl                内容链接
     * @param imageUrl                  图片路径，可以是文件路径或者网络图片 url
     * @param weChatShareResultListener 分享结果回调
     */
    public static void shareToMiniProgram(
            String title,
            String description,
            String path,
            String username,
            String contentUrl,
            String imageUrl,
            WeChatShareResultListener weChatShareResultListener
    ) {
        WeChatShare.shareToMiniProgram(wechatSdk.getWxApi(),
                title, description,
                path, username, contentUrl,
                imageUrl, weChatShareResultListener);
    }


    /**
     * 是否安装有微信
     */
    public static boolean isInstallWechat() {
        return wechatSdk.getWxApi().isWXAppInstalled();
    }

    /**
     * 是否支持朋友圈
     */
    public static boolean isSupportMoment() {
        return wechatSdk.getWxApi().getWXAppSupportAPI() >= Build.TIMELINE_SUPPORTED_SDK_INT;
    }
}
