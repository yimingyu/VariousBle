package android.yimingyu.net.blesrv;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.yimingyu.net.blesrv.util.BluetoothUtil;
import android.yimingyu.net.blesrv.util.LogUtil;
import android.yimingyu.net.btevent.base.GeneralActions;
import android.yimingyu.net.btevent.base.SrvEvent;

import org.greenrobot.eventbus.EventBus;

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




    private boolean tryReconnect=false;
    public void setTryReconnect(boolean bl){
        tryReconnect=bl;
    }

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        String address=gatt.getDevice().getAddress();
        String name=gatt.getDevice().getName();
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            if(status==133){
                connectStatus = 0;
                LogUtil.e(DEVICE_TYPE+" "+name+" "+address+"连接虽然成功但是status=133相于当断开了连接");
                EventBus.getDefault().post(new SrvEvent(GeneralActions.ACTION_DEVICE_DISCONNECTED,DEVICE_TYPE,address,status));
                if(tryReconnect){
                    connect();
                }else {
                    close();
                }
            }else {
                connectStatus = 2;
                EventBus.getDefault().post(new SrvEvent(GeneralActions.ACTION_DEVICE_CONNECTED, DEVICE_TYPE, address));
                LogUtil.e(DEVICE_TYPE, name+" "+address + "连接成功，" + (gatt.discoverServices() ? "开始" : "但是无法") + "搜索服务");
            }
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            connectStatus=0;
            EventBus.getDefault().post(new SrvEvent(GeneralActions.ACTION_DEVICE_DISCONNECTED,DEVICE_TYPE,address));
            LogUtil.e(DEVICE_TYPE,  name+" "+address +"已经断开连接，具体"+status+" "+newState);
            if(tryReconnect){
                connect();
            }else {
                close();
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        String address=gatt.getDevice().getAddress();
        if (status == BluetoothGatt.GATT_SUCCESS) {
            LogUtil.e(DEVICE_TYPE,address+"服务搜索完成\n");
            if(printServices) BluetoothUtil.printServices(gatt, DEVICE_TYPE);
            if(autoNotify) BluetoothUtil.enableNotification(gatt, UUID_SRV, UUID_READ,true);
        } else {
            LogUtil.e(DEVICE_TYPE, address+"服务搜索失败: " + status);
        }
    }
}
