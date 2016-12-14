package android.yimingyu.net.blesrv;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.yimingyu.net.blesrv.util.BluetoothUtil;
import android.yimingyu.net.blesrv.util.LogUtil;
import android.yimingyu.net.btevent.base.GeneralActions;
import android.yimingyu.net.btevent.base.SrvEvent;

import org.greenrobot.eventbus.EventBus;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

/**
 * Author：Mingyu Yi on 2016/11/3 20:15
 * Email：461072496@qq.com
 */
public abstract class BtGattMgr extends GattMgr{
    private boolean printServices=false;  //是否打印所有服务
    private boolean autoNotify=true;  //是否服务搜索完成后自动接收通知
    public BtGattMgr(String address) {
        super(address);
    }
    public void setPrintServices(boolean printServices){
        this.printServices=printServices;
    }
    public void setAutoNotify(boolean autoNotify){
        this.autoNotify=autoNotify;
    }


    public int defaultRetryTimes(){ //-1无限重连,0不重连,其他值代表重连次数
        return 0;
    }
    public int retryTimes= defaultRetryTimes();
    public boolean cancelRetryOnceConnected(){
        return true;
    }

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (newState == STATE_CONNECTED) {
            if(status==133){
                onDisconnect(gatt,status,newState);
            }else {
                onConnect(gatt);
            }
        } else if (newState == STATE_DISCONNECTED) {
            onDisconnect(gatt,status,newState);
        }
    }

    protected void onConnect(BluetoothGatt gatt){
        connectStatus = STATE_CONNECTED;
        EventBus.getDefault().post(new SrvEvent(GeneralActions.ACTION_DEVICE_CONNECTED, DEVICE_TYPE, address));
        LogUtil.e(DEVICE_TYPE, gatt.getDevice().getName()+" "+address + "连接成功，" + (gatt.discoverServices() ? "开始" : "但是无法") + "搜索服务");

        if(cancelRetryOnceConnected()) retryTimes=0;
    }
    protected void onDisconnect(BluetoothGatt gatt, int status, int newState){
        connectStatus=STATE_DISCONNECTED;
        String name=gatt.getDevice().getName();
        LogUtil.e(DEVICE_TYPE,  name+" "+address +"已经断开，状态"+status+" "+newState);
        if(retryTimes!=0){         //如果默认不重连或者没有成功连接过，继续尝试重连
            super.connect(gatt.getDevice(),context);      //这里不用connect()是因为connect()方法不触发回调
//            super.connect();
            retryTimes--;
            LogUtil.e(DEVICE_TYPE,name+" "+address+"连接失败，将"+(retryTimes<0?"不断重连":("重连"+retryTimes+"次")));
        }else {
            EventBus.getDefault().post(new SrvEvent(GeneralActions.ACTION_DEVICE_DISCONNECTED,DEVICE_TYPE,address,status));
            close();
            LogUtil.e(DEVICE_TYPE,name+" "+address+"连接失败并不再重连");
        }
    }

    @Override
    public int connect(BluetoothDevice device, Context context) {
        retryTimes=defaultRetryTimes();
        return super.connect(device, context);
    }

    public int connect(){
        retryTimes=defaultRetryTimes();
        return super.connect();
    }

    public int disconnect(){  //主动断开表示不需要继续尝试重连
        retryTimes=0;
        return super.disconnect();
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        String name=gatt.getDevice().getName();
        if (status == BluetoothGatt.GATT_SUCCESS) {
            LogUtil.e(DEVICE_TYPE,name+" "+address+"服务搜索完成");
            if(printServices) BluetoothUtil.printServices(gatt, DEVICE_TYPE);
            if(autoNotify) BluetoothUtil.enableNotification(gatt, UUID_SRV, UUID_READ,true);
        } else {
            LogUtil.e(DEVICE_TYPE, name+" "+address+"服务搜索失败: " + status);
        }
    }
}
