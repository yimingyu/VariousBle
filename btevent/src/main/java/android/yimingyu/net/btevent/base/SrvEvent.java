package android.yimingyu.net.btevent.base;

/**
 * Author：Mingyu Yi on 2016/11/3 11:04
 * Email：461072496@qq.com
 */
public class SrvEvent{
    public String action;
    public String deviceType;
    public String address;
    public Object data;

    public SrvEvent(String action){
        this(action,null);
    }

    public SrvEvent(String action,Object data) {
        this(action,null,null,data);
    }

    public SrvEvent(String action, String deviceType, String address) {
        this(action,deviceType,address,null);
    }

    public SrvEvent(String action, String deviceType, String address, Object data) {
        this.action = action;
        this.deviceType = deviceType;
        this.address = address;
        this.data = data;
    }
}
