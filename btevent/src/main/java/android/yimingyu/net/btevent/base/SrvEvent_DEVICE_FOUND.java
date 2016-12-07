package android.yimingyu.net.btevent.base;

/**
 * Author：Mingyu Yi on 2016/12/6 23:46
 * Email：461072496@qq.com
 */
public class SrvEvent_DEVICE_FOUND extends SrvEvent{
    public String name;
    public int rssi;

    public SrvEvent_DEVICE_FOUND(String action, String deviceType, String address, String name, int rssi) {
        super(action, deviceType, address);
        this.name = name;
        this.rssi = rssi;
    }
}
