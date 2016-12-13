package android.yimingyu.net.btevent.bpm;

import android.yimingyu.net.btevent.base.SrvEvent;

/**
 * Author：Mingyu Yi on 2016/12/6 23:01
 * Email：461072496@qq.com
 */
public class Event_SRV_BPM extends SrvEvent{
    public static final String ACTION_RESULT_AP="ACTION_RESULT_AP";    //AtmosphericPressure
    public static final String ACTION_RESULT_BL="ACTION_RESULT_BL";    //BatteryLevel

    public Event_SRV_BPM(String action, String deviceType, String address, Object data) {
        super(action, deviceType, address, data);
    }

    public int high;
    public int low;
    public int rate;
    public Event_SRV_BPM(String action, String deviceType, String address,int hbp,int lbp,int br) {
        super(action, deviceType, address);
        high=hbp;
        low=lbp;
        rate=br;
    }
}
