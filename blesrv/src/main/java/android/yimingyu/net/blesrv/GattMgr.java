package android.yimingyu.net.blesrv;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.yimingyu.net.blesrv.util.BluetoothUtil;
import android.yimingyu.net.blesrv.util.LogUtil;
import android.yimingyu.net.btevent.base.UiEvent;

import java.util.UUID;


/**
 * Author：Mingyu Yi on 2016/6/29 09:49
 * Email：461072496@qq.com
 */
public abstract class GattMgr extends BluetoothGattCallback{
    public String DEVICE_TYPE = getDeviceType();  //设备类型，如血压计、体重秤等
    public UUID UUID_SRV =getSrvUUID();
    public UUID UUID_READ =getReadUUID();
    public UUID UUID_WRITE =getWriteUUID();

    public abstract String getDeviceType();
    public abstract UUID getSrvUUID();
    public abstract UUID getReadUUID();
    public abstract UUID getWriteUUID();

    protected abstract void dealUiEvent(UiEvent uiEvent);
    protected void dealUiEventIfNotConnected(UiEvent uiEvent){};

    public String address;
    private BluetoothGatt bluetoothGatt;
    public GattMgr(String address) {
        this.address = address;
    }
    public void setBluetoothGatt(BluetoothGatt bluetoothGatt){
        this.bluetoothGatt = bluetoothGatt;
    }
    public int connectStatus=0;  //连接状态：0未连接、1连接中、2已连接、3断开中
    public int connect(){
        if(connectStatus==0) bluetoothGatt.connect();
        return connectStatus;
    }
    public void disconnect(){
        if(connectStatus==2) {
            bluetoothGatt.disconnect();
            connectStatus=3;
        }
    }
    public void close(){
        connectStatus=0;
        bluetoothGatt.close();
    }
    public boolean enableNotification(boolean enable){
        return BluetoothUtil.enableNotification(bluetoothGatt, UUID_SRV, UUID_READ,enable);
    }
    public boolean writeChar(byte[] data){
        return BluetoothUtil.writeChar(bluetoothGatt,UUID_SRV,UUID_WRITE,data);
    }

}
