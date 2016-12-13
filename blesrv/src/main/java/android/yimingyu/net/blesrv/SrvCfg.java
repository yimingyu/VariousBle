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
    public static String getDeviceTypeByName(String name){
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
        String type=getDeviceTypeByName(device.getName());
        return getMgrByType(type,device.getAddress());
    }




    private static final HashMap<String,Boolean> autoConnect=new HashMap<>();
    static {
        autoConnect.put("00:15:83:00:3D:84",true);
        autoConnect.put("7C:EC:79:C1:C1:51",true);
        autoConnect.put("88:1B:99:04:0E:31",true);
    }



}
