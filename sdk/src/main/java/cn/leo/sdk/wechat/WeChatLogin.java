package cn.leo.sdk.wechat;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;

/**
 * @author : ling luo
 * @date : 2021/6/4
 * @description : 微信登陆请求和结果处理
 */

final class WeChatLogin {

    static WeChatLoginResultListener sWeChatLoginResultListener = null;

    static void login(IWXAPI api, WeChatLoginResultListener weChatLoginResultListener) {
        sWeChatLoginResultListener = weChatLoginResultListener;
        SendAuth.Req req = new SendAuth.Req();
        //应用授权作用域，如获取用户个人信息则填写 snsapi_userinfo
        req.scope = "snsapi_userinfo";
        req.state = "wx_login";  //非必须
        api.sendReq(req);
    }

    /**
     * 处理微信登录结果
     */
    static void executeLoginResp(SendAuth.Resp baseResp) {
        if (sWeChatLoginResultListener == null) return;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                sWeChatLoginResultListener.onSuccess(baseResp.code);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                sWeChatLoginResultListener.onCancel();
                break;
            default:
                sWeChatLoginResultListener.onError(baseResp.errStr);
                break;
        }
        sWeChatLoginResultListener = null;
    }

}
