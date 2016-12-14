package android.yimingyu.net.blesrv.device;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.yimingyu.net.blesrv.BtGattMgr;
import android.yimingyu.net.blesrv.util.DataUtil;
import android.yimingyu.net.blesrv.util.LogUtil;
import android.yimingyu.net.btevent.base.GeneralActions;
import android.yimingyu.net.btevent.base.UiEvent;
import android.yimingyu.net.btevent.bpm.Event_SRV_BPM;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.UUID;

import static android.yimingyu.net.blesrv.SrvCfg.DEVICE_TYPE_BPM;
import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_START_TEST;
import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_STOP_TEST;
import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_VOICE_LOOP;
import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_VOICE_OFF;
import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_VOICE_ON;
import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_VOICE_SET;
import static android.yimingyu.net.btevent.bpm.Event_SRV_BPM.ACTION_RESULT_AP;
import static android.yimingyu.net.btevent.bpm.Event_SRV_BPM.ACTION_RESULT_BL;

/**
 * Author：Mingyu Yi on 2016/9/26 10:01
 * Email：461072496@qq.com
 */
public class BPM extends BtGattMgr {
    public static final String SRV_BPM = "000018f0-0000-1000-8000-00805f9b34fb";
    public static UUID UUID_SRV_BPM = UUID.fromString(SRV_BPM);
    public static final String READ_BPM = "00002af0-0000-1000-8000-00805f9b34fb";
    public static UUID UUID_READ_BPM = UUID.fromString(READ_BPM);
    public static final String WRITE_BPM = "00002af1-0000-1000-8000-00805f9b34fb";
    public static UUID UUID_WRITE_BPM = UUID.fromString(WRITE_BPM);

    //当前测试设备的名字以BPM-开头，地址是00:15:83:00:3D:84
    private static final String BPM_="00:15:83:00:3D:84";

    private static final byte COMMAND_BASE_FLAG= DataUtil.xorAll(new byte[]{0x02,0x40,(byte) 0xdc,0x01},1);
    private static final byte[] COMMAND_START_TEST={0x02,0x40,(byte) 0xdc,0x01,(byte) 0xa1,0x3c};
    private static final byte[] COMMAND_STOP_TEST={0x02,0x40,(byte) 0xdc,0x01,(byte) 0xa2,0x3f};
    private static final byte[] COMMAND_VOICE_OFF={0x02,0x40,(byte) 0xdc,0x01,(byte) 0xa3,0x3e};
    private static final byte[] COMMAND_VOICE_ON={0x02,0x40,(byte) 0xdc,0x01,(byte) 0xa4,0x39};
    private static final byte[] COMMAND_VOICE_LOOP={0x02,0x40,(byte) 0xdc,0x01,(byte) 0xa5,(byte) (COMMAND_BASE_FLAG^(byte)0xa5)};


    private static final HashMap<Integer,String> errCode=new HashMap<>();
    static {
        errCode.put(1,"传感器信号异常");
        errCode.put(2,"测量不出结果");
        errCode.put(3,"测量结果异常");
        errCode.put(4,"腕带过松或漏气");
        errCode.put(5,"腕带过紧或气路堵塞");
        errCode.put(6,"测量中压力干扰严重");
        errCode.put(7,"压力超过300");
    }

    public BPM(String address) {
        super(address);
    }

    @Override
    public String getDeviceType() {
        return DEVICE_TYPE_BPM;
    }

    @Override
    public UUID getSrvUUID() {
        return UUID_SRV_BPM;
    }

    @Override
    public UUID getReadUUID() {
        return UUID_READ_BPM;
    }

    @Override
    public UUID getWriteUUID() {
        return UUID_WRITE_BPM;
    }


    @Override
    protected void dealUiEvent(UiEvent uiEvent) {
        if(connectStatus!= BluetoothProfile.STATE_CONNECTED){
            LogUtil.e(DEVICE_TYPE,address+"没有连接");
            return;
        }
        switch (uiEvent.action){
            case ACTION_START_TEST:
                switchTest(true);
                break;
            case ACTION_STOP_TEST:
                switchTest(false);
                break;
            case ACTION_VOICE_ON:
                switchVoicePrompt(true);
                break;
            case ACTION_VOICE_OFF:
                switchVoicePrompt(false);
                break;
            case ACTION_VOICE_LOOP:
                startVoiceLoop();
                break;
            case ACTION_VOICE_SET:
                setVoiceType((int)uiEvent.data);
                break;
        }
    }


    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] bytes=characteristic.getValue();
        if(bytes.length<4) {
            return;
        }
        if(bytes[3]==2){
            short ap= DataUtil.getShort(bytes,4);
            EventBus.getDefault().post(new Event_SRV_BPM(ACTION_RESULT_AP,DEVICE_TYPE,address,ap));
        }else if(bytes[3]==3){
            EventBus.getDefault().post(new Event_SRV_BPM(ACTION_RESULT_BL,DEVICE_TYPE,address,(int)bytes[6]));
        }else if(bytes[3]==12){
            if(DataUtil.isBitSet(bytes[4],5)){
                EventBus.getDefault().post(new Event_SRV_BPM(GeneralActions.ACTION_RESULT_ERR,DEVICE_TYPE,address,errCode.get((int)bytes[12])));
            }else {
                short hbp= DataUtil.getShort(bytes,5);
                short lbp= DataUtil.getShort(bytes,7);
                short br= DataUtil.getShort(bytes,11);
                EventBus.getDefault().post(new Event_SRV_BPM(GeneralActions.ACTION_RESULT_OK,DEVICE_TYPE,address,hbp,lbp,br));
            }
        }
    }

    public boolean isTesting=false;
    public void switchTest(boolean enable){
        if(enable==isTesting) return;
        boolean result=writeChar(enable?COMMAND_START_TEST:COMMAND_STOP_TEST);
        if(result){
            isTesting=enable;
        }else {
            LogUtil.e(DEVICE_TYPE,(enable?"开始":"停止")+"血压测试失败，血压计没有连接");
        }
    }
    public void switchVoicePrompt(boolean enable){
        boolean result=writeChar(enable?COMMAND_VOICE_ON:COMMAND_VOICE_OFF);
        LogUtil.e(DEVICE_TYPE,(enable?"开始":"停止")+"语音提示，结果"+result);
    }
    public void startVoiceLoop(){
        boolean result=writeChar(COMMAND_VOICE_LOOP);
        LogUtil.e(DEVICE_TYPE,"开始循环切换语音，结果"+result);
    }
    public void setVoiceType(int number){
        byte flag=(byte) (COMMAND_BASE_FLAG^(byte)0xa6);
        flag=(byte) (flag^(byte)number);
        byte[] command={0x02,0x40,(byte) 0xdc,0x01,(byte) 0xa6,(byte) number,flag};
        boolean result=writeChar(command);
        LogUtil.e(DEVICE_TYPE,"将语音类型切换为： "+number+" 结果"+result);
    }

}
