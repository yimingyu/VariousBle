package android.yimingyu.net.blesrv.util;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.Arrays;
import java.util.UUID;

import static android.bluetooth.BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
import static android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;


public class BluetoothUtil {
    public static void printServices(BluetoothGatt gatt, String tag) {
        if (gatt != null) {
            String tmp;
            int unknownService=0;
            for (BluetoothGattService service : gatt.getServices()) {
                tmp= UUIDs.lookup(service.getUuid().toString());
                if(tmp==null)  tmp="service"+(unknownService++);
                LogUtil.e(tag, "服务："+tmp+": " + service.getUuid());

                int unknownChar=0;
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    tmp=UUIDs.lookup(characteristic.getUuid().toString());
                    if(tmp==null)  tmp="characteristic"+(unknownChar++);
                    LogUtil.e(tag, "    特性："+tmp+": " + characteristic.getUuid()
                            + "------value: " + Arrays.toString(characteristic.getValue())
                            + "------properties: " + characteristic.getProperties());

                    int unknownDescriptor=0;
                    for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                        tmp=UUIDs.lookup(descriptor.getUuid().toString());
                        if(tmp==null)  tmp="descriptor"+(unknownDescriptor++);
                        LogUtil.e(tag, "        描述符："+tmp+": "
                                + "------value: " + Arrays.toString(descriptor.getValue()));
                    }
                }
            }
        }
    }

    public static boolean enableNotification(BluetoothGatt gatt,UUID uuid_srv, UUID uuid_read,boolean enable) {
        try {
            BluetoothGattCharacteristic gattChar=gatt.getService(uuid_srv).getCharacteristic(uuid_read);
            gatt.setCharacteristicNotification(gattChar, enable);
            BluetoothGattDescriptor gatDes = gattChar.getDescriptor(UUIDs.UUID_CCC);
            gatDes.setValue(enable? ENABLE_NOTIFICATION_VALUE:DISABLE_NOTIFICATION_VALUE);
            return gatt.writeDescriptor(gatDes);
        }catch (Exception e){
            return false;
        }
    }
    public static boolean writeChar(BluetoothGatt gatt,UUID uuid_srv, UUID uuid_write,byte[] data) {
        try {
            BluetoothGattCharacteristic gattChar=gatt.getService(uuid_srv).getCharacteristic(uuid_write);
            gattChar.setValue(data);
            return gatt.writeCharacteristic(gattChar);
        }catch (Exception e){
            return false;
        }
    }



    public static boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            return (Boolean) BluetoothGatt.class.getMethod("refresh").invoke(gatt);
        } catch (Exception e) {
            return false;
        }
    }


    public static String parseScanRecord(String scanRecord) {
        int end = 0;
        String name = null;
        String data = null;
        String type = null;
        while (end <= scanRecord.length()) {
            String length = scanRecord.substring(end, end + 2);
            int len = Integer.parseInt(length, 16) * 2;
            if (len == 0) {
                break;
            }
            if (end + 4 < scanRecord.length()) {
                type = scanRecord.substring(end + 2, end + 4);
            }
            if (end + len + 2 < scanRecord.length()) {
                data = scanRecord.substring(end + 4, end + 4 + len - 2);
            }
            if (("09").equals(type)) {
                name = new String(DataUtil.hexStrToByteArr(data));
            }
            end = end + len + 2;
        }
        return name;
    }
}