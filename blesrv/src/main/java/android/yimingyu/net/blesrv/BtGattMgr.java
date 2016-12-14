package android.yimingyu.net.blesrv;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.yimingyu.net.blesrv.util.BluetoothUtil;
import android.yimingyu.net.blesrv.util.LogUtil;
import android.yimingyu.net.btevent.base.GeneralActions;
import android.yimingyu.net.btevent.base.SrvEvent;
import android.yimingyu.net.btevent.base.UiEvent;

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


    public boolean defaultTryReconnect(){
        return false;
    }
    private boolean tryReconnect=defaultTryReconnect();

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

        if(tryReconnect) tryReconnect=false;
    }
    protected void onDisconnect(BluetoothGatt gatt, int status, int newState){
        connectStatus=STATE_DISCONNECTED;
        LogUtil.e(DEVICE_TYPE,  gatt.getDevice().getName()+" "+address +"已经断开，状态"+status+" "+newState);
        if(tryReconnect){         //如果默认不重连或者没有成功连接过，继续尝试重连
            tryReconnect();       //这里不用connect()是因为connect()方法不触发回调
        }else {
            EventBus.getDefault().post(new SrvEvent(GeneralActions.ACTION_DEVICE_DISCONNECTED,DEVICE_TYPE,address,status));
            close();
        }
    }

    public void tryReconnect(){
        EventBus.getDefault().post(new UiEvent(GeneralActions.ACTION_CONNECT,address));
    }

    @Override
    public int connect(BluetoothDevice device, Context context) {
        tryReconnect=defaultTryReconnect();
        return super.connect(device, context);
    }

    public int connect(){
        tryReconnect=defaultTryReconnect();
        return super.connect();
    }
    public int disconnect(){
        tryReconnect=false;
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
