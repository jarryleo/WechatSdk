package cn.leo.sdk.wechat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;

/**
 * @author : ling luo
 * @date : 2021/6/8
 * @description : File URI权限赋予
 */
public class UriUtil {

    /**
     * 给文件赋予别的应用访问权限
     *
     * @param context     上下文
     * @param file        文件
     * @param packageName 别的应用包名
     * @return Uri
     */
    public static Uri grantUri(Context context, File file, String packageName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Uri uri = WechatFileProvider.getUriForFile(context,
                    context.getPackageName() + ".WxFileProvider", file);
            if (packageName != null) {
                context.grantUriPermission(packageName, uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            return uri;
        }
        return Uri.fromFile(file);
    }
}
