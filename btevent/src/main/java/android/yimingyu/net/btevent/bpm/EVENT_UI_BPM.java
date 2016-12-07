package android.yimingyu.net.btevent.bpm;

import android.yimingyu.net.btevent.base.UiEvent;

/**
 * Author：Mingyu Yi on 2016/11/3 11:04
 * Email：461072496@qq.com
 */
public class EVENT_UI_BPM extends UiEvent {
    public String deviceType="血压计";
    public EVENT_UI_BPM(String action, String address) {
        super(action, address);
    }

    public EVENT_UI_BPM(String action, String address, Object data) {
        super(action, address, data);
    }
}
