package android.yimingyu.net.variousble.adapter.device;

/**
 * Author：Mingyu Yi on 2016/12/7 10:25
 * Email：461072496@qq.com
 */
public class Device {
    public String name;
    public String type;
    public String mac;
    public int rssi;

    public Device(String name, String type, String mac, int rssi) {
        this.name = name;
        this.type = type;
        this.mac = mac;
        this.rssi = rssi;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Device) return ((Device) obj).mac.equals(this.mac);
        return false;
    }
}
