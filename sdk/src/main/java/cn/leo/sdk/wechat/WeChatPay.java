package cn.leo.sdk.wechat;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;

/**
 * @author : ling luo
 * @date : 2021/6/4
 * @description : 微信支付和处理结果
 */
final class WeChatPay {

    static WeChatPayResultListener sWeChatPayResultListener = null;

    /**
     * @param partnerId 商户号
     * @param prepayId  预支付交易会话ID
     * @param nonceStr  随机字符串
     * @param timeStamp 时间戳
     * @param sign      签名
     */
    static void pay(
            IWXAPI api,
            String partnerId,
            String prepayId,
            String nonceStr,
            String timeStamp,
            String sign,
            WeChatPayResultListener weChatPayResultListener
    ) {
        sWeChatPayResultListener = weChatPayResultListener;
        PayReq request = new PayReq();
        request.appId = WeChatSdk.sAppId;
        request.partnerId = partnerId;
        request.prepayId = prepayId;
        request.packageValue = "Sign=WXPay";
        request.nonceStr = nonceStr;
        request.sign = sign;
        request.timeStamp = timeStamp;
        api.sendReq(request);
    }

    /**
     * 处理微信支付结果
     */
    static void executePayResp(BaseResp baseResp) {
        if (sWeChatPayResultListener == null) return;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                sWeChatPayResultListener.onSuccess();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                sWeChatPayResultListener.onCancel();
                break;
            default:
                sWeChatPayResultListener.onError(baseResp.errStr);
                break;
        }
        sWeChatPayResultListener = null;
    }
}
