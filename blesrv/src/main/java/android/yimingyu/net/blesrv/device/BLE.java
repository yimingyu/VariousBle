package android.yimingyu.net.blesrv.device;

import android.yimingyu.net.blesrv.BtGattMgr;
import android.yimingyu.net.btevent.base.UiEvent;

import java.util.UUID;

import static android.yimingyu.net.blesrv.SrvCfg.DEVICE_TYPE_BLE;

/**
 * Author：Mingyu Yi on 2016/9/26 14:38
 * Email：461072496@qq.com
 */
public class BLE extends BtGattMgr {
    public BLE(String address) {
        super(address);
        setAutoNotify(false);
        setPrintServices(true);
    }

    @Override
    public String getDeviceType() {
        return DEVICE_TYPE_BLE;
    }

    @Override
    public UUID getSrvUUID() {
        return null;
    }

    public void setSrvUUID(UUID uuid){
        super.UUID_SRV=uuid;
    }

    @Override
    public UUID getReadUUID() {
        return null;
    }

    public void setReadUUID(UUID uuid){
        super.UUID_READ=uuid;
    }

    @Override
    public UUID getWriteUUID() {
        return null;
    }

    @Override
    protected void dealUiEvent(UiEvent uiEvent) {

    }

    public void setWriteUUID(UUID uuid){
        super.UUID_WRITE=uuid;
    }
}
