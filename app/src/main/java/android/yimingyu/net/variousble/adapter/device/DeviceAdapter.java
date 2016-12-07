package android.yimingyu.net.variousble.adapter.device;

import android.content.Context;
import android.view.View;
import android.yimingyu.net.blesrv.util.LogUtil;
import android.yimingyu.net.btevent.base.GeneralActions;
import android.yimingyu.net.btevent.base.UiEvent;
import android.yimingyu.net.variousble.adapter.BetterAdapter;
import android.yimingyu.net.variousble.adapter.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：Mingyu Yi on 2016/12/7 10:17
 * Email：461072496@qq.com
 */
public class DeviceAdapter extends BetterAdapter{
    private List<Device> devices=new ArrayList<>();
    public int indexOf(Device device){
        return devices.indexOf(device);
    }
    public boolean addDevice(Device device) {
        int index=indexOf(device);
        if (index==-1) {
            devices.add(device);
            notifyDataSetChanged();
            return true;
        }else{
            devices.set(index, device);
            notifyDataSetChanged();
            return false;
        }
    }
    public boolean clearDevice(){
        if(devices.size()<1) return false;
        devices.clear();
        notifyDataSetChanged();
        return true;
    }


    public DeviceAdapter(Context context) {
        super(context);
    }

    @Override
    public ViewHolder getViewHolder() {
        return new DeviceHolder();
    }

    @Override
    public void renderView(int index, View view) {
        final Device device=devices.get(index);
        DeviceHolder holder=(DeviceHolder)view.getTag();
        holder.deviceMac.setText("地址："+device.mac);
        holder.deviceName.setText(device.name);
        holder.deviceRssi.setText("信号："+device.rssi+"dp");
        holder.deviceType.setText("类型："+device.type);
        holder.connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new UiEvent(GeneralActions.ACTION_CONNECT,device.mac));
            }
        });
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
