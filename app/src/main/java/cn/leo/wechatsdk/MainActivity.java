package cn.leo.wechatsdk;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.leo.sdk.wechat.WeChatLoginResultListener;
import cn.leo.sdk.wechat.WeChatSdk;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wechatLogin();
            }
        });

    }

    private void wechatLogin() {
        WeChatSdk.login(new WeChatLoginResultListener() {
            @Override
            public void onSuccess(String code) {
                Toast.makeText(MainActivity.this, "登陆成功：" + code, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "用户取消登陆", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "登陆错误：" + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}