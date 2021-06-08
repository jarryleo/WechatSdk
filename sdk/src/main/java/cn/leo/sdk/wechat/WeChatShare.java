package cn.leo.sdk.wechat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.leo.sdk.BuildConfig;

/**
 * @author : ling luo
 * @date : 2021/6/4
 * @description : 微信分享和处理结果
 */
final class WeChatShare {

    /**
     * 缩略图尺寸
     */
    private static final int THUMB_SIZE = 100;

    static WeChatShareResultListener sWeChatShareResultListener = null;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * 分享到微信
     */
    static void shareToWechat(
            IWXAPI api,
            String title,
            String description,
            String contentUrl,
            String imageUrl,
            WeChatShareResultListener weChatShareResultListener
    ) {
        sWeChatShareResultListener = weChatShareResultListener;
        share(api, title, description, contentUrl, imageUrl, SendMessageToWX.Req.WXSceneSession);
    }

    /**
     * 分享到朋友圈
     */
    static void shareToMoment(
            IWXAPI api,
            String title,
            String description,
            String contentUrl,
            String imageUrl,
            WeChatShareResultListener weChatShareResultListener
    ) {
        sWeChatShareResultListener = weChatShareResultListener;
        share(api, title, description, contentUrl, imageUrl, SendMessageToWX.Req.WXSceneTimeline);
    }

    /**
     * 分享到微信小程序
     */
    static void shareToMiniProgram(
            IWXAPI api,
            String title,
            String description,
            String path,
            String username,
            String contentUrl,
            String imageUrl,
            WeChatShareResultListener weChatShareResultListener
    ) {
        sWeChatShareResultListener = weChatShareResultListener;
        WXMiniProgramObject wxMiniProgramObject = new WXMiniProgramObject();
        wxMiniProgramObject.userName = username;
        wxMiniProgramObject.path = path;
        if (BuildConfig.DEBUG) {
            wxMiniProgramObject.miniprogramType = WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW;
        } else {
            wxMiniProgramObject.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;
        }
        wxMiniProgramObject.webpageUrl = contentUrl; //网页链接
        WXMediaMessage wxMediaMessage = new WXMediaMessage(wxMiniProgramObject);
        wxMediaMessage.title = title;
        wxMediaMessage.description = description;
        if (imageUrl.startsWith("http")) {
            executor.execute(() -> {
                shareNetImage(wxMediaMessage, imageUrl);
            });
        }
        startShare(api, wxMediaMessage, SendMessageToWX.Req.WXSceneSession);
    }

    private static void share(
            IWXAPI api,
            String title,
            String description,
            String contentUrl,
            String imageUrl,
            int shareScene
    ) {
        WXMediaMessage wxMediaMessage = new WXMediaMessage();
        wxMediaMessage.title = title;
        wxMediaMessage.description = description;
        // 区分本地图片和网络图片
        if (!imageUrl.startsWith("http") && (imageUrl.endsWith(".png") || imageUrl.endsWith(".jpg"))) {
            //本地图片分享
            executor.execute(() -> {
                shareLocalImage(wxMediaMessage, imageUrl);
            });
        } else {
            WXWebpageObject mediaObject = new WXWebpageObject();
            mediaObject.webpageUrl = contentUrl;
            wxMediaMessage.mediaObject = mediaObject;
            //网络图片分享
            if (imageUrl.startsWith("http")) {
                executor.execute(() -> {
                    shareNetImage(wxMediaMessage, imageUrl);
                });
            }
        }
        startShare(api, wxMediaMessage, shareScene);
    }

    private static void shareLocalImage(WXMediaMessage wxMediaMessage, String imagePath) {
        File imageFile = new File(imagePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(imageFile);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            if (Build.VERSION.SDK_INT >= 21) {
                //android 11 适配
                Uri uri = UriUtil.grantUri(WeChatSdk.sContext,
                        imageFile, "com.tencent.mm");
                String contentPath = uri.toString();
                WXImageObject wxObj = new WXImageObject();
                wxObj.setImagePath(contentPath);
                wxMediaMessage.mediaObject = wxObj;
            } else {
                wxMediaMessage.mediaObject = new WXImageObject(bitmap);
            }
            Bitmap thumbBmp =
                    Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
            wxMediaMessage.thumbData = bmpToByteArray(thumbBmp, 95);
            bitmap.recycle();
            thumbBmp.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void shareNetImage(WXMediaMessage wxMediaMessage, String imageUrl) {
        InputStream imageStream = getImageStream(imageUrl);
        if (imageStream != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
            wxMediaMessage.thumbData = bmpToByteArray(scaledBitmap, 95);
            bitmap.recycle();
            scaledBitmap.recycle();
        } else {
            Log.e("WechatShare", "分享失败：图片路径无法解析");
        }
    }

    private static void startShare(IWXAPI api, WXMediaMessage mediaMessage, int shareScene) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "webpage_" + System.currentTimeMillis();
        req.message = mediaMessage;
        req.scene = shareScene;
        api.sendReq(req);
    }

    private static byte[] bmpToByteArray(Bitmap bitmap, int quality) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output);
        return output.toByteArray();
    }

    private static InputStream getImageStream(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10 * 1000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return conn.getInputStream();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 处理微信分享结果
     */
    static void executeShareResp(BaseResp baseResp) {
        if (sWeChatShareResultListener == null) return;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                sWeChatShareResultListener.onSuccess();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                sWeChatShareResultListener.onCancel();
                break;
            default:
                sWeChatShareResultListener.onError(baseResp.errStr);
                break;
        }
        sWeChatShareResultListener = null;
    }
}
