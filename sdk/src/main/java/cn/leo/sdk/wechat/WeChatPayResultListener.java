package cn.leo.sdk.wechat;

/**
 * @author : ling luo
 * @date : 2021/6/3
 * @description : 微信登录结果回调
 */
public interface WeChatPayResultListener {

    void onSuccess();

    void onCancel();

    void onError(String error);
}
