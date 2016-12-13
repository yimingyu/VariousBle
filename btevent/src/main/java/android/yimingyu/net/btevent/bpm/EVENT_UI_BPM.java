package android.yimingyu.net.btevent.bpm;

import android.yimingyu.net.btevent.base.UiEvent;

/**
 * Author：Mingyu Yi on 2016/11/3 11:04
 * Email：461072496@qq.com
 */
public class EVENT_UI_BPM extends UiEvent {
    public static final String ACTION_START_TEST="ACTION_START_TEST";
    public static final String ACTION_STOP_TEST="ACTION_STOP_TEST";
    public static final String ACTION_VOICE_ON="ACTION_VOICE_ON";
    public static final String ACTION_VOICE_OFF="ACTION_VOICE_OFF";
    public static final String ACTION_VOICE_LOOP="ACTION_VOICE_LOOP";
    public static final String ACTION_VOICE_SET="ACTION_VOICE_SET";


    public EVENT_UI_BPM(String action, String address) {
        super(action, address);
    }

    public EVENT_UI_BPM(String action, String address, Object data) {
        super(action, address, data);
    }
}
