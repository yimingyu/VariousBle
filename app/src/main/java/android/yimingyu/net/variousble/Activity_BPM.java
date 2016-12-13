package android.yimingyu.net.variousble;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.yimingyu.net.blesrv.util.LogUtil;
import android.yimingyu.net.btevent.base.GeneralActions;
import android.yimingyu.net.btevent.bpm.EVENT_UI_BPM;
import android.yimingyu.net.btevent.bpm.Event_SRV_BPM;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_START_TEST;
import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_STOP_TEST;
import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_VOICE_LOOP;
import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_VOICE_OFF;
import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_VOICE_ON;
import static android.yimingyu.net.btevent.bpm.EVENT_UI_BPM.ACTION_VOICE_SET;
import static android.yimingyu.net.btevent.bpm.Event_SRV_BPM.ACTION_RESULT_AP;
import static android.yimingyu.net.btevent.bpm.Event_SRV_BPM.ACTION_RESULT_BL;

public class Activity_BPM extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUST_CODE = 1;
    int voiceNumber = 1;

    public void onClick(View view) {
        String address="00:15:83:00:3D:84";
        switch (view.getId()) {
            case R.id.btn_start_test:
                EventBus.getDefault().post(new EVENT_UI_BPM(ACTION_START_TEST,address));
                return;
            case R.id.btn_stop_test:
                EventBus.getDefault().post(new EVENT_UI_BPM(ACTION_STOP_TEST,address));
                return;
            case R.id.btn_start_voice:
                EventBus.getDefault().post(new EVENT_UI_BPM(ACTION_VOICE_ON,address));
                return;
            case R.id.btn_stop_voice:
                EventBus.getDefault().post(new EVENT_UI_BPM(ACTION_VOICE_OFF,address));
                return;
            case R.id.btn_loop_voice:
                EventBus.getDefault().post(new EVENT_UI_BPM(ACTION_VOICE_LOOP,address));
                return;
            case R.id.btn_set_voice:
                voiceNumber++;
                if (voiceNumber > 3) voiceNumber = 1;
                EventBus.getDefault().post(new EVENT_UI_BPM(ACTION_VOICE_SET,address,voiceNumber));
                return;
            case R.id.btn_connect:
                EventBus.getDefault().post(new EVENT_UI_BPM(GeneralActions.ACTION_CONNECT,address));
                break;
            case R.id.btn_disconnect:
                EventBus.getDefault().post(new EVENT_UI_BPM(GeneralActions.ACTION_DISCONNECT,address));
                break;
        }
    }

    TextView tv_ap;
    TextView tv_bl;
    TextView tv_h;
    TextView tv_l;
    TextView tv_r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpm);
        setTitle("设置");
        findViewById(R.id.btn_start_test).setOnClickListener(this);
        findViewById(R.id.btn_stop_test).setOnClickListener(this);
        findViewById(R.id.btn_start_voice).setOnClickListener(this);
        findViewById(R.id.btn_stop_voice).setOnClickListener(this);
        findViewById(R.id.btn_loop_voice).setOnClickListener(this);
        findViewById(R.id.btn_set_voice).setOnClickListener(this);
        findViewById(R.id.btn_connect).setOnClickListener(this);
        findViewById(R.id.btn_disconnect).setOnClickListener(this);

        tv_ap=(TextView)findViewById(R.id.tv_bpm_ap);
        tv_bl=(TextView)findViewById(R.id.tv_bpm_bl);
        tv_h=(TextView)findViewById(R.id.tv_bpm_h);
        tv_l=(TextView)findViewById(R.id.tv_bpm_l);
        tv_r=(TextView)findViewById(R.id.tv_bpm_r);

        EventBus.getDefault().register(this);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event_SRV_BPM eventSrvBpm){
        if(eventSrvBpm==null) return;
        switch (eventSrvBpm.action){
            case GeneralActions.ACTION_RESULT_OK:
                tv_h.setText("高压:"+eventSrvBpm.high);
                tv_l.setText("低压:"+eventSrvBpm.low);
                tv_r.setText("心率:"+eventSrvBpm.rate);
                break;
            case GeneralActions.ACTION_RESULT_ERR:
                LogUtil.e("测量失败，原因"+eventSrvBpm.data);
                break;
            case ACTION_RESULT_AP:
                tv_ap.setText("充气压："+eventSrvBpm.data);
                break;
            case ACTION_RESULT_BL:
                tv_bl.setText("电量："+eventSrvBpm.data);
                break;
        }
    }
}
