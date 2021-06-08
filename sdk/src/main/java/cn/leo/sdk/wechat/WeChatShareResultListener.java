package cn.leo.sdk.wechat;

/**
 * @author : ling luo
 * @date : 2021/6/3
 * @description : 微信分享结果回调
 */
public interface WeChatShareResultListener {

    void onSuccess();

    void onCancel();

    void onError(String error);
}
