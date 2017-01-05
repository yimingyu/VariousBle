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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

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
                String address=device.getAddress();
                String type= SrvCfg.getDeviceType(device);
                if(EventBus.getDefault().hasSubscriberForEvent(SrvEvent.class)) {
                    EventBus.getDefault().post(new SrvEvent_DEVICE_FOUND(GeneralActions.ACTION_DEVICE_FOUND, type, address, name, rssi));
                }
                if(SrvCfg.needAutoConnect(type,address)){
                    LogUtil.e("找到"+type+" "+address+"开始自动连接");
                    connect(device.getAddress());
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
        if(address==null||!BluetoothAdapter.checkBluetoothAddress(address)){
            LogUtil.e("蓝牙地址"+address+"不合法");
            return false;
        }
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        String name=device.getName();
        GattMgr mgr=gattServices.get(address);
        if(mgr!=null){
            if(mgr.connectStatus==BluetoothProfile.STATE_CONNECTED) {
                LogUtil.e("蓝牙"+name+" "+address + "已经处于连接状态");
                return true;
            }else if(mgr.connectStatus==BluetoothProfile.STATE_CONNECTING){
                LogUtil.e("正在努力连接"+name+" "+address);
                return false;
            }
            mgr.close();  //之所以不用已存在的gatt是因为太慢
        }else {
            mgr= SrvCfg.getMgrByDevice(device);
            if(mgr==null) {
                LogUtil.e("未知错误，没有得到"+name+" "+address +"对应的GattMgr");
                return false;
            }
        }
        mgr.connect(device,this);
        gattServices.put(address,mgr);
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
            boolean notUsed=mgr==null;
            /***
             * 如果mgr为空表明设备还没有连接过，例如血压计还没连接，用户却直接点击开始测量，服务收到这种UiEvent
             * 可以不做任何处理，也可以人性化的自动连接血压计并开始测量，但是这种情况不应该出现，即没有连接血压计之前，不应该让
             * 用户看到操作界面。
             */
            if(notUsed) mgr=SrvCfg.getMgrByEvent(uiEvent);
            if(mgr!=null) {
                mgr.dealUiEvent(uiEvent);
                if(notUsed) gattServices.put(uiEvent.address,mgr);
            }else {
                LogUtil.e("未知错误，没有得到"+uiEvent +"对应的GattMgr");
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
