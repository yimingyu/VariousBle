package android.yimingyu.net.variousble;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.yimingyu.net.blesrv.BluetoothService;
import android.yimingyu.net.blesrv.device.BPM;
import android.yimingyu.net.blesrv.util.LogUtil;
import android.yimingyu.net.btevent.base.GeneralActions;
import android.yimingyu.net.btevent.base.SrvEvent;
import android.yimingyu.net.btevent.base.SrvEvent_DEVICE_FOUND;
import android.yimingyu.net.btevent.base.UiEvent;
import android.yimingyu.net.btevent.bpm.Event_SRV_BPM;
import android.yimingyu.net.variousble.adapter.device.Device;
import android.yimingyu.net.variousble.adapter.device.DeviceAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ListView deviceList;
    DeviceAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_bpm).setOnClickListener(this);
        findViewById(R.id.btn_start_scan).setOnClickListener(this);
        findViewById(R.id.btn_stop_scan).setOnClickListener(this);
        deviceList=(ListView)findViewById(R.id.scan_devices_list);


        startService(new Intent(getApplicationContext(), BluetoothService.class));

        EventBus.getDefault().register(this);
        adapter=new DeviceAdapter(this);
        deviceList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_bpm:
                startActivity(new Intent(this,Activity_BPM.class));
                break;
            case R.id.btn_start_scan:
                adapter.clearDevice();
                EventBus.getDefault().post(new UiEvent(GeneralActions.ACTION_START_SCAN));
                break;
            case R.id.btn_stop_scan:
                EventBus.getDefault().post(new UiEvent(GeneralActions.ACTION_STOP_SCAN));
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SrvEvent srvEvent){
        if(srvEvent==null) return;
        switch (srvEvent.action){
            case GeneralActions.ACTION_DEVICE_FOUND:
                SrvEvent_DEVICE_FOUND event=(SrvEvent_DEVICE_FOUND)srvEvent;
                Device device=new Device(event.name,event.deviceType,event.address,event.rssi);
                adapter.addDevice(device);
                break;
        }
    }


}