package android.yimingyu.net.blesrv;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.yimingyu.net.blesrv.util.BluetoothUtil;
import android.yimingyu.net.btevent.base.UiEvent;

import java.util.UUID;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTING;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTING;


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

    protected void dealUiEvent(UiEvent uiEvent){
    };

    public String address;
    private BluetoothGatt bluetoothGatt;
    protected Context context;
    public GattMgr(String address) {
        this.address = address;
    }
    public int connectStatus= STATE_DISCONNECTED;  //连接状态：0未连接、1连接中、2已连接、3断开中

    public int connect(BluetoothDevice device, Context context){//个人觉得这个方法比BluetoothGatt.connect()方法快
        this.context=context;
        connectStatus=STATE_CONNECTING;
        bluetoothGatt=device.connectGatt(context,false,this);
        return connectStatus;
    }
    public int connect(){
        if(connectStatus!=STATE_CONNECTING) {
            bluetoothGatt.connect();
            connectStatus=STATE_CONNECTING;
        }
        return connectStatus;
    }
    public int disconnect(){
        if(connectStatus==STATE_CONNECTED) {
            bluetoothGatt.disconnect();
            connectStatus=STATE_DISCONNECTING;
        }
        return connectStatus;
    }
    public void close(){
        if(connectStatus==STATE_DISCONNECTED) return;
        connectStatus=STATE_DISCONNECTED;
        bluetoothGatt.close();
    }
    public boolean enableNotification(boolean enable){
        return BluetoothUtil.enableNotification(bluetoothGatt, UUID_SRV, UUID_READ,enable);
    }
    public boolean writeChar(byte[] data){
        return BluetoothUtil.writeChar(bluetoothGatt,UUID_SRV,UUID_WRITE,data);
    }

}
