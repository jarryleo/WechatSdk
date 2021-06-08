package cn.leo.sdk.wechat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WeChatEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private final IWXAPI mWxApi = WeChatSdk.wechatSdk.getWxApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWxApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWxApi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        //请求
    }

    @Override
    public void onResp(BaseResp baseResp) {
        //结果
        if (baseResp == null) {
            finish();
            return;
        }
        Log.d("WechatEntryActivity ", "onResp baseResp.type = " + baseResp.getType());
        switch (baseResp.getType()) {
            //微信登录
            case ConstantsAPI.COMMAND_SENDAUTH:
                if (baseResp instanceof SendAuth.Resp) {
                    WeChatLogin.executeLoginResp((SendAuth.Resp) baseResp);
                }
                break;
            //微信分享
            case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                WeChatShare.executeShareResp(baseResp);
                break;
            //微信支付
            case ConstantsAPI.COMMAND_PAY_BY_WX:
                WeChatPay.executePayResp(baseResp);
                break;
        }
        finish();
    }
}