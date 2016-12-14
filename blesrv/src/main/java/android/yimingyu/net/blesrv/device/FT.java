package android.yimingyu.net.blesrv.device;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.yimingyu.net.blesrv.BtGattMgr;
import android.yimingyu.net.blesrv.util.LogUtil;
import android.yimingyu.net.btevent.base.UiEvent;

import java.util.UUID;

import static android.yimingyu.net.blesrv.SrvCfg.DEVICE_TYPE_FT;

/**
 * Author：Mingyu Yi on 2016/9/30 15:16
 * Email：461072496@qq.com
 */
public class FT extends BtGattMgr {
    public static final String SRV_FT = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static UUID UUID_SRV_FT = UUID.fromString(SRV_FT);
    public static final String READ_FT = "0000ffe4-0000-1000-8000-00805f9b34fb";
    public static UUID UUID_READ_FT = UUID.fromString(READ_FT);
    public static final String WRITE_FT = "0000ffe4-0000-1000-8000-00805f9b34fb";
    public static UUID UUID_WRITE_FT = UUID.fromString(WRITE_FT);

    //测试设备的名称AET-WD，地址是7C:EC:79:C1:C1:51
    private static final String AET_WD="7C:EC:79:C1:C1:51";


    public FT(String address) {
        super(address);
    }


    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        StringBuffer sb = new StringBuffer(data.length);
        for (int i = 0; i < data.length; i++) {
            sb.append(Integer.toHexString(0xFF & data[i]).toUpperCase()+",");
        }
        LogUtil.e("收到消息："+sb.toString());
        String t ="";

        if (0xE0 == (0xFF & data[9]) &&0x00 == (0xFF & data[10])){
            t = "0.0";
            // 温度过低，显示LO
        }else  if (0xF0 == (0xFF & data[9]) &&0x00 == (0xFF & data[10])){
            t = "99.0";
            // 温度过高，显示HI
        }else{
            float temperature = (float)data[9] + (float)data[10] / 100;

            double res = Math.floor(temperature * 10) / 10;

            t = res+"";
        }
        LogUtil.e("当前温度是："+t);
    }

    @Override
    public String getDeviceType() {
        return DEVICE_TYPE_FT;
    }

    @Override
    public UUID getSrvUUID() {
        return UUID_SRV_FT;
    }

    @Override
    public UUID getReadUUID() {
        return UUID_READ_FT;
    }

    @Override
    public UUID getWriteUUID() {
        return UUID_WRITE_FT;
    }

    @Override
    protected void dealUiEvent(UiEvent uiEvent) {

    }
}
