package cn.leo.sdk.wechat;

/**
 * @author : ling luo
 * @date : 2021/6/3
 * @description : 微信登录结果回调
 */
public interface WeChatLoginResultListener {

    /**
     * @param code 用户身份标识
     */
    void onSuccess(String code);

    void onCancel();

    void onError(String error);
}
