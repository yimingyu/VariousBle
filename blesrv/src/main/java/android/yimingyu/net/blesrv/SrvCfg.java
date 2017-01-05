package android.yimingyu.net.blesrv;

import android.bluetooth.BluetoothDevice;
import android.yimingyu.net.blesrv.device.BLE;
import android.yimingyu.net.blesrv.device.BPM;
import android.yimingyu.net.blesrv.device.FT;
import android.yimingyu.net.blesrv.device.WS;
import android.yimingyu.net.btevent.base.UiEvent;
import android.yimingyu.net.btevent.bpm.EVENT_UI_BPM;

import java.util.HashMap;

/**
 * Author：Mingyu Yi on 2016/9/26 11:21
 * Email：461072496@qq.com
 */
public class SrvCfg {
    public static boolean LOG_Enabled=true;
    public static String SERVICE_TAG="BleSrv";

    public static final String DEVICE_TYPE_BLE ="BLE";
    public static final String DEVICE_TYPE_BPM="血压计";  //Blood Pressure Meter
    public static final String DEVICE_TYPE_BSM="血糖仪";  //Blood Sugar Meter
    public static final String DEVICE_TYPE_WS="体重秤";   //Weight Scale
    public static final String DEVICE_TYPE_FT="体温计";   //Fever Thermometer
    public static final String DEVICE_TYPE_WB="手环";     //WristBand
    public static final String DEVICE_TYPE_LB="灯炮";     //LightBulb

    /***
     * maps1的作用是根据UiEvent得到GattMgr，通常用不到。比如血压计还没连接，用户却直接点击开始测量，服务收到这种UiEvent
     * 可以不做任何处理，也可以人性化的自动连接血压计并开始测量，但是这种情况不应该出现，即没有连接血压计之前，不应该让
     * 用户看到操作界面。
     */
    private static final HashMap<Class<? extends UiEvent>,Class<? extends GattMgr>> maps1=new HashMap<>();
    static {
        maps1.put(EVENT_UI_BPM.class,BPM.class);
    }
    private static final HashMap<String,Class<? extends GattMgr>> maps2=new HashMap<>();
    static {
        maps2.put(DEVICE_TYPE_BPM,BPM.class);
        maps2.put(DEVICE_TYPE_FT,FT.class);
        maps2.put(DEVICE_TYPE_BLE, BLE.class);
        maps2.put(DEVICE_TYPE_WS, WS.class);
    }

    public static GattMgr getMgrByEvent(UiEvent uiEvent){
        try {
            return maps1.get(uiEvent.getClass()).getDeclaredConstructor(String.class).newInstance(uiEvent.address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static GattMgr getMgrByType(String type,String address){
        try {
            return maps2.get(type).getDeclaredConstructor(String.class).newInstance(address);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getDeviceType(BluetoothDevice device){
        String address=device.getAddress();
        String type= knownDevices.get(address);
        if(type!=null) return type;
        String name=device.getName();
        if(name==null){
            return DEVICE_TYPE_BLE;
        }
        if(name.startsWith("BPM-")){
            return DEVICE_TYPE_BPM;
        }else if(name.startsWith("MH")){
            return DEVICE_TYPE_WB;
        }else if(name.startsWith("AET-")){
            return DEVICE_TYPE_FT;
        }else if(name.startsWith("Electronic Scale")){
            return DEVICE_TYPE_WS;
        }else {
            return DEVICE_TYPE_BLE;
        }
    }

    public static GattMgr getMgrByDevice(BluetoothDevice device){
        String type= getDeviceType(device);
        return getMgrByType(type,device.getAddress());
    }

    private static final HashMap<String,String> knownDevices =new HashMap<>();
    static {
        knownDevices.put("00:15:83:00:3D:84",DEVICE_TYPE_BPM);
        knownDevices.put("7C:EC:79:C1:C1:51",DEVICE_TYPE_FT);
        knownDevices.put("88:1B:99:04:0E:31",DEVICE_TYPE_WS);
        knownDevices.put("68:9E:19:0D:7B:57",DEVICE_TYPE_LB);
    }

    public static boolean needAutoConnect(String type,String address){
        return knownDevices.containsKey(address);
    }
}
