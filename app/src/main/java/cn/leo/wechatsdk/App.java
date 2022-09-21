package cn.leo.wechatsdk;

import android.app.Application;

import cn.leo.sdk.wechat.WeChatSdk;

/**
 * @author : ling luo
 * @date : 2021/6/8
 * @description : application
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        WeChatSdk.init(this,"你的微信appId");
    }
}
