package android.yimingyu.net.btevent.base;


/**
 * Author：Mingyu Yi on 2016/11/3 11:04
 * Email：461072496@qq.com
 */
public class UiEvent{
    public String action;
    public String address;
    public Object data;

    public UiEvent(String action) {
        this(action,null);
    }

    public UiEvent(String action, String address) {
        this(action,address,null);
    }

    public UiEvent(String action, String address, Object data) {
        this.action=action;
        this.address=address;
        this.data = data;
    }

    @Override
    public String toString() {
        return "UiEvent{" +
                "action='" + action + '\'' +
                ", address='" + address + '\'' +
                ", data=" + data +
                '}';
    }
}
