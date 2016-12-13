package android.yimingyu.net.blesrv;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.yimingyu.net.blesrv.util.LogUtil;
import android.yimingyu.net.btevent.base.GeneralActions;
import android.yimingyu.net.btevent.base.SrvEvent;
import android.yimingyu.net.btevent.base.SrvEvent_DEVICE_FOUND;
import android.yimingyu.net.btevent.base.UiEvent;
import android.yimingyu.net.btevent.bpm.EVENT_UI_BPM;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

/**
 * Author：Mingyu Yi on 2016/11/8 14:32
 * Email：461072496@qq.com
 */
public class BluetoothService extends Service{
    private final IBinder iBinder=new LocalBinder();
    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }
    private boolean allowRebind=true;
    public IBinder onBind(Intent intent) {
        return iBinder;
    }
    public boolean onUnbind(Intent intent) {
        return allowRebind;
    }
    public boolean environmentInitialed=false;
    @Override
    public void onCreate() {
        environmentInitialed=initialEnvironment();
        initialData();
        EventBus.getDefault().register(this);
        if(autoDiscovery) startScan();
        LogUtil.e("BLE服务启动");
    }
    @Override
    public void onDestroy() {
        LogUtil.e("BLE服务销毁");
        EventBus.getDefault().unregister(this);
        if(isReceiverRegistered){
            unregisterReceiver(mReceiver);
        }
    }

    public BluetoothManager bluetoothManager;
    public BluetoothAdapter bluetoothAdapter;

    public MyBroadcastReceiver mReceiver;
    public boolean isReceiverRegistered;
    public IntentFilter mFilter;
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                String name=device.getName();
                String type= SrvCfg.getDeviceTypeByName(name);
                if(EventBus.getDefault().hasSubscriberForEvent(SrvEvent.class)) {
                    EventBus.getDefault().post(new SrvEvent_DEVICE_FOUND(GeneralActions.ACTION_DEVICE_FOUND, type, device.getAddress(), name, rssi));
                }
                if(type.equals(SrvCfg.DEVICE_TYPE_BPM)){
//                    Log.e(TAG,"找到血压计，开始自动连接");
//                    connect(device.getAddress());
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                EventBus.getDefault().post(new SrvEvent(GeneralActions.ACTION_DISCOVERY_FINISH));
                if(cycleDiscovery) startScan();
            }
        }
    }
    public void startScan(){
        if(!isReceiverRegistered){
            registerReceiver(mReceiver,mFilter);
            isReceiverRegistered=true;
        }
        stopScan();
        bluetoothAdapter.startDiscovery();
    }
    public void stopScan(){
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
    }
    public static boolean autoDiscovery=true;
    public static boolean cycleDiscovery=true;

    public boolean initialEnvironment() {
        if(!bleSupported()){
            LogUtil.e("不支持低功功耗蓝牙");
            return false;
        }
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                LogUtil.e("不支持蓝牙");
                return false;
            }
        }
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            LogUtil.e("无法获取蓝牙适配器");
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
        return true;
    }
    public void initialData(){
        context=getApplicationContext();
        mReceiver=new MyBroadcastReceiver();
        mFilter = new IntentFilter();
        mFilter.addAction(BluetoothDevice.ACTION_FOUND);
        mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    }
    public Context context;

    public HashMap<String,GattMgr> gattServices=new HashMap<>();
    public boolean connect(String address){
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            LogUtil.e("没有找到蓝牙"+address);
            return false;
        }
        GattMgr mgr=gattServices.get(address);
        if(mgr!=null){
            if(mgr.connectStatus==2) {
                LogUtil.e(address + "已经处于连接状态");
                return true;
            }else if(mgr.connectStatus==1){
                LogUtil.e("正在努力连接中"+address);
                return false;
            }
            mgr.close();  //之所以不用已存在的gatt是因为太慢
        }else {
            mgr= SrvCfg.getMgrByDevice(device);
            if(mgr==null) {
                LogUtil.e("没有得到GattMgr");
                return false;
            }
        }
        BluetoothGatt gatt=device.connectGatt(this,false,mgr);
        mgr.setBluetoothGatt(gatt);
        mgr.connectStatus=1;
        gattServices.put(address,mgr);


        List<BluetoothDevice> list=bluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
        LogUtil.e("GattServer连接了"+list.size());
        for(BluetoothDevice d:list){
            LogUtil.e("连接了"+d.getName()+" "+d.getAddress());
        }
        return false;
    }
    public void disconnect(String address){
        try {
            gattServices.get(address).disconnect();
        }catch (Exception e){
        }
    }

    /**
     * 智能手环上的单模ble属于支持ble但是不支持蓝牙
     * http://blog.csdn.net/cnbloger/article/details/41382653
     * 也就是说，设备有可能只支持蓝牙不支持BLE，也有可能只支持BLE不支持蓝牙
     */
    public boolean bleSupported() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(UiEvent uiEvent){
        if(!dealNormalEvent(uiEvent)){
            GattMgr mgr=gattServices.get(uiEvent.address);
            if(mgr!=null) {
                mgr.dealUiEvent(uiEvent);
            }else {
                LogUtil.e(uiEvent.address,"设备没有连接");
                mgr= SrvCfg.getMgrByEvent(uiEvent);
                if(mgr!=null) mgr.dealUiEventIfNotConnected(uiEvent);
            }

        }
    }
    private boolean dealNormalEvent(UiEvent uiEvent){
        if(uiEvent==null||uiEvent.action==null) return true;
        switch (uiEvent.action){
            case GeneralActions.ACTION_CONNECT:
                connect(uiEvent.address);
                break;
            case GeneralActions.ACTION_DISCONNECT:
                disconnect(uiEvent.address);
                break;
            case GeneralActions.ACTION_START_SCAN:
                startScan();
                break;
            case GeneralActions.ACTION_STOP_SCAN:
                stopScan();
                break;
            default:
                return false;
        }
        return true;
    }
}
