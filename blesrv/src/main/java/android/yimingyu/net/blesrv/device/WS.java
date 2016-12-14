package android.yimingyu.net.blesrv.device;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.yimingyu.net.blesrv.BtGattMgr;
import android.yimingyu.net.blesrv.util.DataUtil;
import android.yimingyu.net.blesrv.util.LogUtil;
import android.yimingyu.net.btevent.base.UiEvent;

import java.util.UUID;
import static android.yimingyu.net.blesrv.SrvCfg.DEVICE_TYPE_WS;

/**
 * Author：Mingyu Yi on 2016/9/26 14:38
 * Email：461072496@qq.com
 */
public class WS extends BtGattMgr {
    protected static final UUID UUID_SRV_WS = UUID
            .fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    protected static  final UUID UUID_READ_WS = UUID
            .fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    protected static final UUID UUID_WRITE_WS = UUID
            .fromString("0000fff1-0000-1000-8000-00805f9b34fb");

    private static final String ElectronicScale="88:1B:99:04:0E:31";


    private static byte[] userData=new byte[] { (byte)0xFE,(byte)0x01, (byte)0x00,
            (byte)0x00, (byte)0xAF,(byte)0x19, (byte)0x01,(byte)0xB6 };



    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] bytes=characteristic.getValue();
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
        }
        String tmp=sb.toString();
        LogUtil.e("体重秤发出数据:"+tmp);
        if("fd31000000000031".equals(tmp)){
            writeChar(userData);
        }else if("fd31000000000033".equals(tmp)){

        }else{
            String[] result=parseData(tmp);
        }
    }
    public static String[] parseData(String content) {
        byte[] data = DataUtil.hexStrToByteArr(content);

        // 设别类型
        int v = data[0] & 0xFF;
        String typeRec = "脂肪秤";
        if (v == 0xcf) {
            typeRec = "脂肪秤";
        } else if (v == 0xce) {
            typeRec = "人体秤";
        } else if (v == 0xcb) {
            typeRec = "婴儿秤";
        } else if (v == 0xca) {
            typeRec = "厨房秤";
        }

        // 等级和组号
        int level = (data[1] >> 4) & 0xf;
        int group = data[1] & 0xf;

        String levelRec = "普通";
        if (level == 0) {
            levelRec = "普通";
        } else if (level == 1) {
            levelRec = "业余";
        } else if (level == 2) {
            levelRec = "专业";
        }

        // 性别
        int sex = (data[2] >> 7) & 0x1;
        String secRec = "";
        if (sex == 1) {
            secRec = "男";
        } else {
            secRec = "女";
        }
        // 年龄
        int age = data[2] & 0x7f;

        // 身高
        int height = data[3] & 0xFF;

        // 体重
        int weight = (data[4] << 8) | (data[5] & 0xff);
        float scale = (float) 0.1;
        if (v == 0xcf) {
            scale = (float) 0.1;
        } else if (v == 0xce) {
            scale = (float) 0.1;
        } else if (v == 0xcb) {
            scale = (float) 0.01;
        } else if (v == 0xca) {
            scale = (float) 0.001;
        }

        float weightRec = scale * weight;

        if (weightRec < 0) {
            weightRec *= -1;
        }

        // 脂肪
        int zhifang = (data[6] << 8) | (data[7] & 0xff);

        float zhifangRate = (float) (zhifang * 0.1);
        // 骨骼
        int guge = data[8] & 0xff;

        float gugeRate = (float) ((guge * 0.1) / weightRec) * 100;

        // 肌肉含量
        int jirou = (data[9] << 8) | (data[10] & 0xff);
        float jirouRate = (float) (jirou * 0.1);

        // 内脏脂肪等级
        int neizang = data[11] & 0xff;
        int neizanglevel = neizang * 1;

        // 水份含量
        int water = data[12] << 8 | data[13];
        float waterRate = (float) (water * 0.1);

        // 热量含量
        int hot = data[14] << 8 | (data[15] & 0xff);

        String[] rec = new String[]{"体重:" + (weightRec < 0 ? -weightRec : weightRec) + "Kg", "骨骼:" + (gugeRate < 0 ? -gugeRate : gugeRate) + "%", "脂肪:"
                + (zhifangRate < 0 ? -zhifangRate : zhifangRate) + "%", "肌肉:" + (jirouRate < 0 ? -jirouRate : jirouRate) + "%", "水分:" +(waterRate < 0 ? -waterRate : waterRate)
                + "%", "内脏脂肪:" + (neizanglevel < 0 ? -neizanglevel : neizanglevel), "BMR:" + (hot < 0 ? -hot : hot) + "kcal"};

        return rec;
    }


    public WS(String address) {
        super(address);
        setPrintServices(true);
    }

    @Override
    public String getDeviceType() {
        return DEVICE_TYPE_WS;
    }

    @Override
    public UUID getSrvUUID() {
        return UUID_SRV_WS;
    }

    @Override
    public UUID getReadUUID() {
        return UUID_READ_WS;
    }

    @Override
    public UUID getWriteUUID() {
        return UUID_WRITE_WS;
    }

    @Override
    protected void dealUiEvent(UiEvent uiEvent) {
        writeChar(userData);
    }
}
