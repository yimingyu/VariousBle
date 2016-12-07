package android.yimingyu.net.blesrv.util;

import android.util.Log;

import static android.yimingyu.net.blesrv.SrvCfg.LOG_Enabled;
import static android.yimingyu.net.blesrv.SrvCfg.SERVICE_TAG;

/**
 * Author：Mingyu Yi on 2016/11/3 19:53
 * Email：461072496@qq.com
 */
public class LogUtil {
    public static void d(String message) {
        d(SERVICE_TAG,message);
    }
    public static void d(String tag,String message) {
        if(LOG_Enabled) Log.d(tag, message);
    }
    public static void e(String message) {
        e(SERVICE_TAG,message);
    }
    public static void e(String tag,String message) {
        if(LOG_Enabled) Log.e(tag, message);
    }
    public static void i(String message) {
        i(SERVICE_TAG,message);
    }
    public static void i(String tag,String message) {
        if(LOG_Enabled) Log.i(tag, message);
    }
    public static void v(String message) {
        v(SERVICE_TAG,message);
    }
    public static void v(String tag,String message) {
        if(LOG_Enabled) Log.v(tag, message);
    }
    public static void w(String message) {
        w(SERVICE_TAG,message);
    }
    public static void w(String tag,String message) {
        if(LOG_Enabled) Log.w(tag, message);
    }
    public static void wtf(String message) {
        wtf(SERVICE_TAG,message);
    }
    public static void wtf(String tag,String message) {
        if(LOG_Enabled) Log.wtf(tag, message);
    }
}
