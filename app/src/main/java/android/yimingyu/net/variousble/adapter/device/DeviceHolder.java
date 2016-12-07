package android.yimingyu.net.variousble.adapter.device;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.yimingyu.net.variousble.R;
import android.yimingyu.net.variousble.adapter.ViewHolder;

class DeviceHolder extends ViewHolder {
        public Button connect;
        public TextView deviceName;
        public TextView deviceMac;
        public TextView deviceRssi;
        public TextView deviceType;

    @Override
    public int getHolderLayout() {
        return R.layout.list_item_device;
    }

    @Override
    public void findView(View layoutView) {
        connect=(Button) layoutView.findViewById(R.id.btn_connect);
        deviceName=(TextView) layoutView.findViewById(R.id.device_name);
        deviceMac=(TextView) layoutView.findViewById(R.id.device_address);
        deviceRssi =(TextView) layoutView.findViewById(R.id.device_rssi);
        deviceType=(TextView)layoutView.findViewById(R.id.device_type);
    }
}